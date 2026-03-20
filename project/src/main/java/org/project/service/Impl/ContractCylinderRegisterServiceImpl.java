package org.project.service.Impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.mapper.CylinderMapper.ContractCylinderRegisterMapper;
import org.project.mapper.CylinderMapper.CylinderInspectLogMapper;
import org.project.mapper.unitMapper.UnitMapper;
import org.project.mapper.UserMapper;
import org.project.model.Unit.Unit;
import org.project.model.User;
import org.project.model.contract.ContractCylinderRegister;
import org.project.model.CylinderInspectLog;
import org.project.model.Dto.CylinderRegisterDto.*;
import org.project.service.ContractCylinderRegisterService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContractCylinderRegisterServiceImpl implements ContractCylinderRegisterService {

    private final ContractCylinderRegisterMapper registerMapper;
    private final CylinderInspectLogMapper inspectLogMapper;
    private final UserMapper userMapper;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long registerCylinder(CylinderRegisterDto dto, Long operatorId) {
        // 1. 校验气瓶编号是否已存在
        ContractCylinderRegister existing = registerMapper.selectByCylinderNo(dto.getCylinderNo());
        if (existing != null) {
            throw new IllegalArgumentException("气瓶编号 " + dto.getCylinderNo() + " 已登记，请勿重复登记");
        }

        // 2. 创建登记记录
        ContractCylinderRegister register = new ContractCylinderRegister();
        BeanUtils.copyProperties(dto, register);
        register.setRegisteredBy(operatorId);

        registerMapper.insert(register);

        log.info("气瓶登记成功，气瓶编号: {}, 登记ID: {}", dto.getCylinderNo(), register.getRegisterId());
        return register.getRegisterId();
    }

    @Override

    public Integer batchRegister(List<CylinderRegisterDto> dtoList, Long operatorId) {
        int successCount = 0;
        for (CylinderRegisterDto dto : dtoList) {
            try {
                registerCylinder(dto, operatorId);
                successCount++;
            } catch (Exception e) {
                log.error("气瓶登记失败，气瓶编号: {}, 错误: {}", dto.getCylinderNo(), e.getMessage());
            }
        }
        log.info("批量登记气瓶完成，成功: {}, 失败: {}", successCount, dtoList.size() - successCount);
        return successCount;
    }

    @Override
    public IPage<CylinderRegisterListDto> queryRegisterPage(CylinderRegisterQueryDto queryDto,Long userId) {
        Page<ContractCylinderRegister> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());

        User user= userMapper.selectById(userId);
        if(user.getUnitId()!=null) {
            queryDto.setUnitId(user.getUnitId());
        }
        IPage<ContractCylinderRegister> resultPage = registerMapper.selectRegisterPage(
                page,
                queryDto.getContractId(),
                queryDto.getCylinderTypeId(),
                queryDto.getCylinderNo(),
                queryDto.getRfidTag(),
                queryDto.getCylinderStatus(),
                queryDto.getUnitId()
                , queryDto.getInspectionStatus()
        );

        // 转换为DTO
        IPage<CylinderRegisterListDto> dtoPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<CylinderRegisterListDto> dtoList = resultPage.getRecords().stream()
                .map(this::convertToListDto)
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    @Override
    public CylinderRegisterDetailDto getRegisterDetail(Long registerId) {
        ContractCylinderRegister register = registerMapper.selectById(registerId);
        if (register == null) {
            throw new IllegalArgumentException("气瓶登记记录不存在");
        }

        // 重新查询以获取关联信息
        register = registerMapper.selectByCylinderNo(register.getCylinderNo());

        CylinderRegisterDetailDto dto = new CylinderRegisterDetailDto();
        BeanUtils.copyProperties(register, dto);
        dto.setCylinderStatusText(CylinderRegisterDetailDto.getCylinderStatusText(register.getCylinderStatus()));
        dto.setIsInspectDueSoon(register.isInspectDueSoon());
        dto.setIsInspectOverdue(register.isInspectOverdue());

        // 查询检验历史
        List<CylinderInspectLog> inspectLogs = inspectLogMapper.selectByRegisterId(registerId);
        dto.setInspectLogs(inspectLogs.stream().map(this::convertToInspectLogDto).collect(Collectors.toList()));

        return dto;
    }

    @Override
    public CylinderRegisterDetailDto getDetailByCylinderNo(String cylinderNo) {
        ContractCylinderRegister register = registerMapper.selectByCylinderNo(cylinderNo);
        if (register == null) {
            throw new IllegalArgumentException("气瓶编号不存在");
        }

        return getRegisterDetail(register.getRegisterId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCylinderStatus(CylinderStatusUpdateDto dto, Long operatorId) {
        ContractCylinderRegister register = registerMapper.selectById(dto.getRegisterId());
        if (register == null) {
            throw new IllegalArgumentException("气瓶登记记录不存在");
        }

        register.setCylinderStatus(dto.getCylinderStatus());
        if (dto.getRemark() != null) {
            register.setRemark(dto.getRemark());
        }

        registerMapper.updateById(register);
        log.info("气瓶状态更新成功，登记ID: {}, 新状态: {}", dto.getRegisterId(), dto.getCylinderStatus());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordInspection(CylinderInspectDto dto, Long operatorId) {
        // 1. 创建检验记录
        CylinderInspectLog inspectLog = new CylinderInspectLog();
        BeanUtils.copyProperties(dto, inspectLog);
        inspectLogMapper.insert(inspectLog);

        // 2. 更新气瓶登记记录的检验信息
        ContractCylinderRegister register = registerMapper.selectById(dto.getRegisterId());
        if (register == null) {
            throw new IllegalArgumentException("气瓶登记记录不存在");
        }

        register.setLastInspectDate(dto.getInspectDate());
        register.setNextInspectDate(dto.getNextInspectDate());

        // 根据检验结果更新状态
        if (dto.getInspectResult() == 1) {
            register.setCylinderStatus(1); // 合格 -> 正常使用
        } else if (dto.getInspectResult() == 2) {
            register.setCylinderStatus(2); // 不合格 -> 待检验
        } else if (dto.getInspectResult() == 3) {
            register.setCylinderStatus(4); // 报废 -> 已报废
        }

        registerMapper.updateById(register);
        log.info("气瓶检验记录成功，气瓶编号: {}, 检验结果: {}", dto.getCylinderNo(), dto.getInspectResult());
    }

    @Override
    public List<ContractCylinderRegister> getInspectDueSoon(Integer days) {
        return registerMapper.selectInspectDueSoon(days);
    }

    @Override
    public List<ContractCylinderRegister> getInspectOverdue() {
        return registerMapper.selectInspectOverdue();
    }

    @Override
    public List<ContractCylinderRegister> getByContractId(Long contractId) {
        return registerMapper.selectByContractId(contractId);
    }

    // ==================== 私有辅助方法 ====================

    private CylinderRegisterListDto convertToListDto(ContractCylinderRegister register) {
        CylinderRegisterListDto dto = new CylinderRegisterListDto();
        BeanUtils.copyProperties(register, dto);
        dto.setCylinderStatusText(CylinderRegisterDetailDto.getCylinderStatusText(register.getCylinderStatus()));
        dto.setIsInspectDueSoon(register.isInspectDueSoon());
        dto.setIsInspectOverdue(register.isInspectOverdue());
        return dto;
    }

    private CylinderRegisterDetailDto.InspectLogDto convertToInspectLogDto(CylinderInspectLog log) {
        CylinderRegisterDetailDto.InspectLogDto dto = new CylinderRegisterDetailDto.InspectLogDto();
        BeanUtils.copyProperties(log, dto);
        dto.setInspectResultText(CylinderRegisterDetailDto.getInspectResultText(log.getInspectResult()));
        return dto;
    }
}
