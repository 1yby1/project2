package org.project.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.mapper.*;
import org.project.mapper.unitMapper.*;
import org.project.mapper.ContractMapper.*;
import org.project.mapper.unitMapper.*;
import org.project.model.*;
import org.project.model.Dto.ContractDto.*;
import org.project.model.Unit.*;
import org.project.model.contract.*;
import org.project.service.ContractNoService;
import org.project.service.ContractService;
import org.project.service.PdfGeneratorService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractMapper contractMapper;
    private final ContractCylinderLineMapper cylinderLineMapper;
    private final ContractItemMapper itemMapper;
    private final ContractFileMapper fileMapper;
    private final ContractPartySnapshotMapper partySnapshotMapper;
    private final UnitMapper unitMapper;
    private final UserMapper userMapper;
    private final UnitCylinderTypeMapper unitCylinderTypeMapper;
    private final UnitBankAccountMapper unitBankAccountMapper;
    private final ContractNoService contractNoService;
    private final PdfGeneratorService pdfGeneratorService;


    @Override
    public List<Contract> getExpiringContracts(Long unitId) {
        QueryWrapper<Contract> wrapper = new QueryWrapper<>();

        // 查询状态为6（即将到期）或7（已到期）的合同
        wrapper.in("contract_status", 6, 7);

        // 如果指定了单位ID，则只查询该单位的合同
        if (unitId != null) {
            wrapper.eq("unit_id", unitId);
        }

        // 按到期日期升序排序
        wrapper.orderByAsc("end_date");

        return contractMapper.selectList(wrapper);
    }

    @Override
    public ContractInitDto getContractInitData(Long userId) {
        // 1. 查询用户获取所属单位ID
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        
        if (user.getUnitId() == null) {
            throw new IllegalArgumentException("该用户未关联单位，无法创建合同");
        }

        // 2. 查询单位信息
        Unit unit = unitMapper.selectById(user.getUnitId());
        if (unit == null) {
            throw new IllegalArgumentException("用户所属单位不存在");
        }

        ContractInitDto initDto = new ContractInitDto();

        // 3. 查询单位经营的气瓶类型
        List<UnitCylinderType> cylinderTypes = unitCylinderTypeMapper.selectByUnitIdWithTypeName(unit.getUnitId());
        String businessScope = cylinderTypes.isEmpty() ? "" : 
            cylinderTypes.stream()
                .map(UnitCylinderType::getCylinderTypeName)
                .collect(Collectors.joining("、"));

        // 4. 查询默认银行账户
        UnitBankAccount defaultAccount = unitBankAccountMapper.selectDefaultByUnitId(unit.getUnitId());
        String bankName = defaultAccount != null ? defaultAccount.getBankName() : "";
        String bankAccount = defaultAccount != null ? defaultAccount.getBankAccountNo() : "";

        // 5. 填充甲方信息
        ContractInitDto.PartyAInfo partyAInfo = new ContractInitDto.PartyAInfo();
        partyAInfo.setUnitName(unit.getUnitName());
        partyAInfo.setSocialCreditCode(unit.getSocialCreditCode());
        partyAInfo.setUnitAddress(unit.getUnitAddress());
        partyAInfo.setBusinessScope(businessScope);
        partyAInfo.setPrincipalName(unit.getUnitPrincipalName());
        partyAInfo.setPrincipalPhone(unit.getUnitPrincipalPhone());
        partyAInfo.setBankName(bankName);
        partyAInfo.setBankAccount(bankAccount);
        initDto.setPartyAInfo(partyAInfo);

        // 6. 填充乙方信息（固定配置，对应图片中的瓶安保公司信息）
        ContractInitDto.PartyBInfo partyBInfo = new ContractInitDto.PartyBInfo();
        partyBInfo.setCompanyName("瓶安保技术有限公司");
        partyBInfo.setSocialCreditCode("91110000MA00000000");
        partyBInfo.setAddress("北京市西城区长椿街1号");
        partyBInfo.setContactName("李经理");
        partyBInfo.setContactPhone("010-65262000");
        partyBInfo.setBankName("中国银行北京分行");
        partyBInfo.setBankAccount("123456789012345");
        initDto.setPartyBInfo(partyBInfo);

        // 7. 预生成合同编号
        String preContractNo = contractNoService.generateContractNo(unit.getRegionCode(), unit.getUnitCode());
        initDto.setPreGeneratedContractNo(preContractNo);

        log.info("获取合同初始化数据成功，用户ID: {}, 单位: {}, 预生成编号: {}", userId, unit.getUnitName(), preContractNo);
        return initDto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createContract(ContractCreateDto dto, Long creatorId) {
        // 1. 校验单位是否存在
        Unit unit = unitMapper.selectById(dto.getUnitId());
        if (unit == null) {
            throw new IllegalArgumentException("单位不存在");
        }

        // 1.1 业务校验：仅允许一种气瓶类型（即仅一条气瓶行）
        if (dto.getCylinderLines() == null || dto.getCylinderLines().isEmpty()) {
            throw new IllegalArgumentException("请填写气瓶类型计价信息");
        }
        if (dto.getCylinderLines().size() != 1) {
            throw new IllegalArgumentException("每个合同仅允许一种气瓶类型，请仅保留一条气瓶行");
        }

        // 2. 生成或校验合同编号
        String contractNo;
        if (StringUtils.hasText(dto.getContractNo())) {
            // 手动输入，校验唯一性
            if (contractNoService.checkContractNoExists(dto.getContractNo())) {
                throw new IllegalArgumentException("合同编号已存在");
            }
            contractNo = dto.getContractNo();
        } else {
            // 自动生成
            contractNo = contractNoService.generateContractNo(unit.getRegionCode(), unit.getUnitCode());
        }

        // 3. 计算金额
        BigDecimal totalOriginal = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal totalFinal = BigDecimal.ZERO;

        // 计算气瓶明细金额
        for (ContractCreateDto.CylinderLineDto line : dto.getCylinderLines()) {
            BigDecimal original = line.getUnitPrice()
                    .multiply(BigDecimal.valueOf(line.getCylinderQty()))
                    .multiply(line.getYears());
            BigDecimal discount = original.multiply(BigDecimal.ONE.subtract(line.getDiscountRate()));
            BigDecimal finalAmount = original.subtract(discount);

            totalOriginal = totalOriginal.add(original);
            totalDiscount = totalDiscount.add(discount);
            totalFinal = totalFinal.add(finalAmount);
        }

        // 计算产品明细金额
        if (dto.getProductItems() != null) {
            for (ContractCreateDto.ProductItemDto item : dto.getProductItems()) {
                BigDecimal amount = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                totalFinal = totalFinal.add(amount);
            }
        }

        // 4. 创建合同主记录
        Contract contract = new Contract();
        contract.setContractNo(contractNo);
        contract.setUnitId(dto.getUnitId());
        contract.setContractType(1); // 1-初始合同
        contract.setRenewalCount(0);
        contract.setStartDate(dto.getStartDate());
        contract.setEndDate(dto.getEndDate());
        contract.setOriginalAmount(totalOriginal);
        contract.setDiscountAmount(totalDiscount);
        contract.setFinalAmount(totalFinal);
        contract.setContractStatus(1); // 1-待确认（等待管理员审核）
        contract.setRemark(dto.getRemark());
        contractMapper.insert(contract);

        Long contractId = contract.getContractId();

        // 5. 插入气瓶明细
        for (ContractCreateDto.CylinderLineDto lineDto : dto.getCylinderLines()) {
            ContractCylinderLine line = new ContractCylinderLine();
            line.setContractId(contractId);
            line.setCylinderTypeId(lineDto.getCylinderTypeId());
            line.setCylinderQty(lineDto.getCylinderQty());
            line.setUnitPrice(lineDto.getUnitPrice());
            line.setYears(lineDto.getYears());
            line.setDiscountRate(lineDto.getDiscountRate());

            BigDecimal original = lineDto.getUnitPrice()
                    .multiply(BigDecimal.valueOf(lineDto.getCylinderQty()))
                    .multiply(lineDto.getYears());
            BigDecimal discount = original.multiply(BigDecimal.ONE.subtract(lineDto.getDiscountRate()));
            BigDecimal finalAmount = original.subtract(discount);

            line.setOriginalAmount(original);
            line.setDiscountAmount(discount);
            line.setFinalAmount(finalAmount);

            cylinderLineMapper.insert(line);
        }

        // 6. 插入产品明细
        if (dto.getProductItems() != null && !dto.getProductItems().isEmpty()) {
            for (ContractCreateDto.ProductItemDto itemDto : dto.getProductItems()) {
                ContractItem item = new ContractItem();
                item.setContractId(contractId);
                item.setProductCode(itemDto.getProductCode());
                item.setProductName(itemDto.getProductName());
                item.setQuantity(itemDto.getQuantity());
                item.setUnitPrice(itemDto.getUnitPrice());
                item.setAmount(itemDto.getUnitPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
                itemMapper.insert(item);
            }
        }

        // 7. 创建甲乙双方快照
        createPartySnapshot(contractId, unit);

        // 8. 保存扫描件（如果有）
        if (StringUtils.hasText(dto.getScannedFileUrl())) {
            ContractFile file = new ContractFile();
            file.setContractId(contractId);
            file.setFileType(1); // 1-扫描件
            file.setFileName("合同扫描件.pdf");
            file.setFileUrl(dto.getScannedFileUrl());
            file.setUploadedBy(creatorId);
            fileMapper.insert(file);
        }

        log.info("合同创建成功，合同ID: {}, 合同编号: {}", contractId, contractNo);
        return contractId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateContract(ContractUpdateDto dto, Long updaterId) {
        Contract contract = contractMapper.selectById(dto.getContractId());
        if (contract == null) {
            throw new IllegalArgumentException("合同不存在");
        }

        // 校验编辑权限
        if (!contract.isEditable()) {
            throw new IllegalStateException("已作废合同不可编辑");
        }

        // 已生效合同只能编辑备注
        if (contract.isOnlyRemarkEditable()) {
            if (dto.getRemark() != null) {
                contract.setRemark(dto.getRemark());
                contractMapper.updateById(contract);
                log.info("已生效合同仅更新备注，合同ID: {}", dto.getContractId());
            }
            return;
        }

        // 待确认/已确认合同可以修改业务条款
        if (dto.getStartDate() != null) {
            contract.setStartDate(dto.getStartDate());
        }
        if (dto.getEndDate() != null) {
            contract.setEndDate(dto.getEndDate());
        }
        if (dto.getRemark() != null) {
            contract.setRemark(dto.getRemark());
        }

        // 如果修改了气瓶明细，重新计算金额
        if (dto.getCylinderLines() != null) {
            if (dto.getCylinderLines().size() != 1) {
                throw new IllegalArgumentException("每个合同仅允许一种气瓶类型，请仅保留一条气瓶行");
            }
            // 删除旧明细
            LambdaQueryWrapper<ContractCylinderLine> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ContractCylinderLine::getContractId, dto.getContractId());
            cylinderLineMapper.delete(wrapper);

            // 重新插入并计算
            BigDecimal totalOriginal = BigDecimal.ZERO;
            BigDecimal totalDiscount = BigDecimal.ZERO;
            BigDecimal totalFinal = BigDecimal.ZERO;

            for (ContractCreateDto.CylinderLineDto lineDto : dto.getCylinderLines()) {
                ContractCylinderLine line = new ContractCylinderLine();
                line.setContractId(dto.getContractId());
                line.setCylinderTypeId(lineDto.getCylinderTypeId());
                line.setCylinderQty(lineDto.getCylinderQty());
                line.setUnitPrice(lineDto.getUnitPrice());
                line.setYears(lineDto.getYears());
                line.setDiscountRate(lineDto.getDiscountRate());

                BigDecimal original = lineDto.getUnitPrice()
                        .multiply(BigDecimal.valueOf(lineDto.getCylinderQty()))
                        .multiply(lineDto.getYears());
                BigDecimal discount = original.multiply(BigDecimal.ONE.subtract(lineDto.getDiscountRate()));
                BigDecimal finalAmount = original.subtract(discount);

                line.setOriginalAmount(original);
                line.setDiscountAmount(discount);
                line.setFinalAmount(finalAmount);

                cylinderLineMapper.insert(line);

                totalOriginal = totalOriginal.add(original);
                totalDiscount = totalDiscount.add(discount);
                totalFinal = totalFinal.add(finalAmount);
            }

            contract.setOriginalAmount(totalOriginal);
            contract.setDiscountAmount(totalDiscount);
            contract.setFinalAmount(totalFinal);
        }

        contractMapper.updateById(contract);
        log.info("合同更新成功，合同ID: {}", dto.getContractId());
    }

    @Override
    public IPage<ContractListItemDto> queryContractPage(Long userId,ContractQueryDto queryDto) {
        Page<Contract> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        User user= userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        // 如果是单位用户，则强制按该用户所属单位查询；否则保留前端传入的查询条件
        if (user.getUnitId() != null) {
            Unit unit = unitMapper.selectById(user.getUnitId());
            queryDto.setUnitName(unit.getUnitName());
        }
        
        IPage<Contract> resultPage = contractMapper.selectContractPage(
                page,
                queryDto.getUnitName(),
                queryDto.getKeyword(),
                queryDto.getContractStatus(),
                queryDto.getStartDateBegin(),
                queryDto.getStartDateEnd(),
                queryDto.getEndDateBegin(),
                queryDto.getEndDateEnd()
        );

        // 转换为DTO
        IPage<ContractListItemDto> dtoPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<ContractListItemDto> dtoList = resultPage.getRecords().stream().map(this::convertToListItemDto).collect(Collectors.toList());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    @Override
    public ContractDetailDto getContractDetail(Long contractId) {
        Contract contract = contractMapper.selectContractDetailById(contractId);
        if (contract == null) {
            throw new IllegalArgumentException("合同不存在");
        }

        ContractDetailDto dto = new ContractDetailDto();
        BeanUtils.copyProperties(contract, dto);
        dto.setContractStatusText(ContractDetailDto.getContractStatusText(contract.getContractStatus()));
        dto.setRemainingDays(contract.getRemainingDays());
        dto.setIsEditable(contract.isEditable());
        dto.setIsOnlyRemarkEditable(contract.isOnlyRemarkEditable());

        // 查询气瓶明细
        List<ContractCylinderLine> lines = cylinderLineMapper.selectByContractIdWithTypeName(contractId);
        dto.setCylinderLines(lines.stream().map(this::convertToCylinderLineDetailDto).collect(Collectors.toList()));

        // 查询产品明细
        LambdaQueryWrapper<ContractItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(ContractItem::getContractId, contractId);
        List<ContractItem> items = itemMapper.selectList(itemWrapper);
        dto.setProductItems(items.stream().map(this::convertToProductItemDetailDto).collect(Collectors.toList()));

        // 查询甲乙方快照
        LambdaQueryWrapper<ContractPartySnapshot> snapshotWrapper = new LambdaQueryWrapper<>();
        snapshotWrapper.eq(ContractPartySnapshot::getContractId, contractId);
        ContractPartySnapshot snapshot = partySnapshotMapper.selectOne(snapshotWrapper);
        if (snapshot != null) {
            dto.setPartySnapshot(convertToPartySnapshotDto(snapshot));
        }

        // 查询文件
        LambdaQueryWrapper<ContractFile> fileWrapper = new LambdaQueryWrapper<>();
        fileWrapper.eq(ContractFile::getContractId, contractId);
        List<ContractFile> files = fileMapper.selectList(fileWrapper);
        dto.setFiles(files.stream().map(this::convertToFileDetailDto).collect(Collectors.toList()));

        // 查询续约链
        List<Contract> renewalChain = contractMapper.selectRenewalChain(contractId);
        dto.setRenewalChain(renewalChain.stream().map(this::convertToRenewalChainDto).collect(Collectors.toList()));

        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void terminateContract(ContractTerminateDto dto, Long operatorId) {
        Contract contract = contractMapper.selectById(dto.getContractId());
        if (contract == null) {
            throw new IllegalArgumentException("合同不存在");
        }

        if (contract.getContractStatus() == 8) {
            throw new IllegalStateException("合同已作废，无需重复操作");
        }

        // 更新状态为已作废
        contract.setContractStatus(8);
        contract.setRemark((contract.getRemark() != null ? contract.getRemark() + "\n" : "") + 
                          "【终止原因】" + dto.getTerminateReason());
        contractMapper.updateById(contract);

        log.info("合同已终止，合同ID: {}, 操作人: {}", dto.getContractId(), operatorId);
    }

    @Override
    public String downloadContractPdf(Long contractId) {
        try {
            // 获取合同详情
            ContractDetailDto contractDetail = getContractDetail(contractId);
            
            // 生成PDF
            String pdfUrl = pdfGeneratorService.generateContractPdf(contractDetail);
            
            log.info("合同PDF生成成功，合同ID: {}, PDF URL: {}", contractId, pdfUrl);
            return pdfUrl;
        } catch (Exception e) {
            log.error("生成合同PDF失败，合同ID: {}", contractId, e);
            throw new RuntimeException("生成合同PDF失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Contract> getExpiringContracts(Integer days) {
        return contractMapper.selectExpiringContracts(days);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateContractStatus(Long contractId, Integer status) {
        Contract contract = contractMapper.selectById(contractId);
        if (contract == null) {
            throw new IllegalArgumentException("合同不存在");
        }

        contract.setContractStatus(status);
        contractMapper.updateById(contract);
        log.info("合同状态更新，合同ID: {}, 新状态: {}", contractId, status);
    }

    // ==================== 合同审核流程 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectContract(Long contractId, String rejectReason, Long operatorId) {
        Contract contract = contractMapper.selectById(contractId);
        if (contract == null) {
            throw new IllegalArgumentException("合同不存在");
        }

        // 只有待确认状态的合同才能被退回
        if (contract.getContractStatus() != 1) {
            throw new IllegalStateException("只有待确认状态的合同才能退回，当前状态：" + contract.getContractStatus());
        }

        // 更新状态为已退回
        contract.setContractStatus(2);
        String remarkAppend = "\n【退回原因】" + rejectReason + "（操作时间：" + java.time.LocalDateTime.now() + "）";
        contract.setRemark((contract.getRemark() != null ? contract.getRemark() : "") + remarkAppend);
        contractMapper.updateById(contract);

        // TODO: 发送消息通知给单位用户
        log.info("合同已退回，合同ID: {}, 原因: {}, 操作人: {}", contractId, rejectReason, operatorId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmContract(Long contractId, String remark, Long operatorId) {
        Contract contract = contractMapper.selectById(contractId);
        if (contract == null) {
            throw new IllegalArgumentException("合同不存在");
        }

        // 只有待确认状态的合同才能被确认
        if (contract.getContractStatus() != 1) {
            throw new IllegalStateException("只有待确认状态的合同才能确认，当前状态：" + contract.getContractStatus());
        }

        // 更新状态为已确认/待缴费
        contract.setContractStatus(3);
        if (StringUtils.hasText(remark)) {
            String remarkAppend = "\n【管理员确认备注】" + remark;
            contract.setRemark((contract.getRemark() != null ? contract.getRemark() : "") + remarkAppend);
        }
        contractMapper.updateById(contract);

        log.info("合同已确认，进入待缴费状态，合同ID: {}, 操作人: {}", contractId, operatorId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resubmitContract(Long contractId, Long userId) {
        Contract contract = contractMapper.selectById(contractId);
        if (contract == null) {
            throw new IllegalArgumentException("合同不存在");
        }

        // 只有已退回状态的合同才能重新提交
        if (contract.getContractStatus() != 2) {
            throw new IllegalStateException("只有已退回状态的合同才能重新提交，当前状态：" + contract.getContractStatus());
        }

        // 校验用户是否有权限操作该合同（同一单位）
        User user = userMapper.selectById(userId);
        if (user == null || !user.getUnitId().equals(contract.getUnitId())) {
            throw new IllegalStateException("无权操作该合同");
        }

        // 更新状态为待确认
        contract.setContractStatus(1);
        String remarkAppend = "\n【重新提交】用户于 " + java.time.LocalDateTime.now() + " 重新提交审核";
        contract.setRemark((contract.getRemark() != null ? contract.getRemark() : "") + remarkAppend);
        contractMapper.updateById(contract);

        log.info("合同重新提交，合同ID: {}, 用户ID: {}", contractId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitPayment(Long contractId, Integer paymentMethod, java.math.BigDecimal paymentAmount,
                              String paymentVoucherUrl, String remark, Long userId) {
        Contract contract = contractMapper.selectById(contractId);
        if (contract == null) {
            throw new IllegalArgumentException("合同不存在");
        }

        // 只有已确认/待缴费状态的合同才能提交缴费
        if (contract.getContractStatus() != 3) {
            throw new IllegalStateException("只有已确认(待缴费)状态的合同才能提交缴费，当前状态：" + contract.getContractStatus());
        }

        // 校验用户是否有权限操作该合同
        User user = userMapper.selectById(userId);
        if (user == null || !user.getUnitId().equals(contract.getUnitId())) {
            throw new IllegalStateException("无权操作该合同");
        }

        // 更新状态为待审核缴费
        contract.setContractStatus(4);
        StringBuilder remarkBuilder = new StringBuilder();
        remarkBuilder.append("\n【缴费信息】");
        remarkBuilder.append("缴费方式：").append(getPaymentMethodText(paymentMethod));
        remarkBuilder.append("，金额：").append(paymentAmount);
        if (StringUtils.hasText(paymentVoucherUrl)) {
            remarkBuilder.append("，凭证：").append(paymentVoucherUrl);
        }
        if (StringUtils.hasText(remark)) {
            remarkBuilder.append("，备注：").append(remark);
        }
        remarkBuilder.append("（提交时间：").append(java.time.LocalDateTime.now()).append("）");
        contract.setRemark((contract.getRemark() != null ? contract.getRemark() : "") + remarkBuilder.toString());
        contractMapper.updateById(contract);

        log.info("缴费信息已提交，合同ID: {}, 金额: {}, 用户ID: {}", contractId, paymentAmount, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditPayment(Long contractId, Boolean approved, String auditRemark, Long operatorId) {
        Contract contract = contractMapper.selectById(contractId);
        if (contract == null) {
            throw new IllegalArgumentException("合同不存在");
        }

        // 只有待审核缴费状态的合同才能审核
        if (contract.getContractStatus() != 4) {
            throw new IllegalStateException("只有待审核缴费状态的合同才能进行缴费审核，当前状态：" + contract.getContractStatus());
        }

        if (approved) {
            // 审核通过，状态变为待签章
            contract.setContractStatus(5);
            String remarkAppend = "\n【缴费审核通过】" + (StringUtils.hasText(auditRemark) ? auditRemark : "审核通过") 
                                + "（审核时间：" + java.time.LocalDateTime.now() + "）";
            contract.setRemark((contract.getRemark() != null ? contract.getRemark() : "") + remarkAppend);
            log.info("缴费审核通过，合同已生效，合同ID: {}, 操作人: {}", contractId, operatorId);
        } else {
            // 审核不通过，状态回到待缴费
            contract.setContractStatus(3);
            String remarkAppend = "\n【缴费审核不通过】原因：" + auditRemark 
                                + "（审核时间：" + java.time.LocalDateTime.now() + "）";
            contract.setRemark((contract.getRemark() != null ? contract.getRemark() : "") + remarkAppend);
            log.info("缴费审核不通过，合同ID: {}, 原因: {}, 操作人: {}", contractId, auditRemark, operatorId);
        }

        contractMapper.updateById(contract);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void signContract(Long contractId, Long userId) {
        Contract contract = contractMapper.selectById(contractId);
        if (contract == null) {
            throw new IllegalArgumentException("合同不存在");
        }

        // 只有待签章状态的合同才能签署
        if (contract.getContractStatus() != 5) {
            throw new IllegalStateException("只有待签章状态的合同才能签署，当前状态：" + contract.getContractStatus());
        }

        // 更新状态为已生效
        contract.setContractStatus(6);
        String remarkAppend = "\n【合同已签署】合同于 " + java.time.LocalDateTime.now() + " 正式生效";
        contract.setRemark((contract.getRemark() != null ? contract.getRemark() : "") + remarkAppend);
        contractMapper.updateById(contract);

        log.info("合同已签署生效，合同ID: {}, 用户ID: {}", contractId, userId);
    }

    @Override
    public DashboardStatsDto getDashboardStats(Long userId) {
        // 获取用户信息和所属单位ID
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        Long unitId = user.getUnitId();
        LambdaQueryWrapper<Contract> queryWrapper = new LambdaQueryWrapper<>();

        // 如果用户有单位，只查询该单位的合同
        if (unitId != null) {
            queryWrapper.eq(Contract::getUnitId, unitId);
        }

        // 1. 有效合同数（状态为6-已生效）
        queryWrapper.eq(Contract::getContractStatus, 6);
        long validContracts = contractMapper.selectCount(queryWrapper);

        // 2. 待处理续约（状态为7-即将到期）
        queryWrapper.clear();
        if (unitId != null) {
            queryWrapper.eq(Contract::getUnitId, unitId);
        }
        queryWrapper.eq(Contract::getContractStatus, 7);
        long pendingRenewals = contractMapper.selectCount(queryWrapper);

        // 3. 本月新增合同
        queryWrapper.clear();
        if (unitId != null) {
            queryWrapper.eq(Contract::getUnitId, unitId);
        }
        // 获取本月第一天
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfMonth = now.withDayOfMonth(1);
        LocalDateTime firstDayOfMonthDateTime = firstDayOfMonth.atStartOfDay();
        queryWrapper.ge(Contract::getCreatedAt, firstDayOfMonthDateTime);
        long newContracts = contractMapper.selectCount(queryWrapper);

        // 4. 最近即将到期天数
        queryWrapper.clear();
        if (unitId != null) {
            queryWrapper.eq(Contract::getUnitId, unitId);
        }
        queryWrapper.in(Contract::getContractStatus, 6, 7) // 生效中或即将到期
                .orderByAsc(Contract::getEndDate)
                .last("LIMIT 1");
        Contract nearestExpiringContract = contractMapper.selectOne(queryWrapper);
        long expiringDays = 30L; // 默认30天
        if (nearestExpiringContract != null && nearestExpiringContract.getEndDate() != null) {
            LocalDate endDate = nearestExpiringContract.getEndDate();
            LocalDate currentDate = LocalDate.now();
            // 计算剩余天数
            long diff = ChronoUnit.DAYS.between(currentDate, endDate);
            expiringDays = diff;
            expiringDays = Math.max(0L, expiringDays); // 确保天数不为负
        }

        return new DashboardStatsDto(validContracts, pendingRenewals, newContracts, expiringDays);
    }

    private String getPaymentMethodText(Integer method) {
        if (method == null) return "未知";
        return switch (method) {
            case 1 -> "银行转账";
            case 2 -> "在线支付";
            case 3 -> "现金";
            default -> "其他";
        };
    }

    // ==================== 私有辅助方法 ====================

    private void createPartySnapshot(Long contractId, Unit unit) {
        ContractPartySnapshot snapshot = new ContractPartySnapshot();
        snapshot.setContractId(contractId);

        // 甲方（单位）信息
        snapshot.setPartyAUnitName(unit.getUnitName());
        snapshot.setPartyASocialCreditCode(unit.getSocialCreditCode());
        snapshot.setPartyAAddress(unit.getUnitAddress());
        snapshot.setPartyAPrincipalName(unit.getUnitPrincipalName());
        snapshot.setPartyAPrincipalPhone(unit.getUnitPrincipalPhone());

        // 乙方（瓶安保）信息 - 从配置读取
        snapshot.setPartyBCompanyName("瓶安保软件服务有限公司");
        snapshot.setPartyBPhone("025-88888888");
        snapshot.setPartyBAddress("南京市江宁区科技园XX路XX号");
        snapshot.setPartyBBankName("工商银行南京科技支行");
        snapshot.setPartyBBankAccount("8888666699990000");

        partySnapshotMapper.insert(snapshot);
    }

    private ContractListItemDto convertToListItemDto(Contract contract) {
        ContractListItemDto dto = new ContractListItemDto();
        dto.setCylinderQty(contract.getCylinderQty());
        dto.setContractId(contract.getContractId());
        dto.setContractNo(contract.getContractNo());
        dto.setUnitName(contract.getUnitName());
        dto.setCylinderTypes(contract.getCylinderTypes());
        dto.setFinalAmount(contract.getFinalAmount());
        dto.setContractStatus(contract.getContractStatus());
        dto.setContractStatusText(ContractDetailDto.getContractStatusText(contract.getContractStatus()));
        dto.setRemainingDays(contract.getRemainingDays());
        dto.setStartDate(contract.getStartDate());
        dto.setEndDate(contract.getEndDate());
        dto.setIsEditable(contract.isEditable());
        dto.setContractPeriod(contract.getStartDate() + " 至 " + contract.getEndDate());
        return dto;
    }

    private ContractDetailDto.CylinderLineDetailDto convertToCylinderLineDetailDto(ContractCylinderLine line) {
        ContractDetailDto.CylinderLineDetailDto dto = new ContractDetailDto.CylinderLineDetailDto();
        BeanUtils.copyProperties(line, dto);
        return dto;
    }

    private ContractDetailDto.ProductItemDetailDto convertToProductItemDetailDto(ContractItem item) {
        ContractDetailDto.ProductItemDetailDto dto = new ContractDetailDto.ProductItemDetailDto();
        BeanUtils.copyProperties(item, dto);
        return dto;
    }

    private ContractDetailDto.PartySnapshotDto convertToPartySnapshotDto(ContractPartySnapshot snapshot) {
        ContractDetailDto.PartySnapshotDto dto = new ContractDetailDto.PartySnapshotDto();
        BeanUtils.copyProperties(snapshot, dto);
        return dto;
    }

    private ContractDetailDto.FileDetailDto convertToFileDetailDto(ContractFile file) {
        ContractDetailDto.FileDetailDto dto = new ContractDetailDto.FileDetailDto();
        BeanUtils.copyProperties(file, dto);
        dto.setFileTypeName(ContractDetailDto.getFileTypeText(file.getFileType()));
        return dto;
    }

    private ContractDetailDto.RenewalChainDto convertToRenewalChainDto(Contract contract) {
        ContractDetailDto.RenewalChainDto dto = new ContractDetailDto.RenewalChainDto();
        BeanUtils.copyProperties(contract, dto);
        dto.setContractStatusText(ContractDetailDto.getContractStatusText(contract.getContractStatus()));
        return dto;
    }
}
