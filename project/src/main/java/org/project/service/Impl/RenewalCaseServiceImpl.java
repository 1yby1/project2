package org.project.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.project.mapper.RenewalMapper.RenewalCaseMapper;
import org.project.mapper.RenewalMapper.RenewalCaseContractMapper;
import org.project.model.renewal.RenewalCase;
import org.project.model.renewal.RenewalCaseContract;
import org.project.service.RenewalCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class RenewalCaseServiceImpl implements RenewalCaseService {

    @Autowired
    private RenewalCaseMapper renewalCaseMapper;

    @Autowired
    private RenewalCaseContractMapper renewalCaseContractMapper;

    @Override
    public IPage<RenewalCase> getPageList(int page, int size, Long unitId, Integer status) {
        Page<RenewalCase> pageParam = new Page<>(page, size);
        QueryWrapper<RenewalCase> queryWrapper = new QueryWrapper<>();

        if (unitId != null) {
            queryWrapper.eq("unit_id", unitId);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }

        queryWrapper.orderByDesc("created_at");
        return renewalCaseMapper.selectPage(pageParam, queryWrapper);
    }

    @Override
    public RenewalCase getCaseById(Long caseId) {
        return renewalCaseMapper.selectById(caseId);
    }

    @Override
    public boolean saveCase(RenewalCase renewalCase) {
        // DEBUG: 记录进入service时的金额值
        System.out.println("=== DEBUG: Service saveCase received ===");
        System.out.println("Original Amount: " + renewalCase.getOriginalAmount());
        System.out.println("Discount Amount: " + renewalCase.getDiscountAmount());
        System.out.println("Final Amount: " + renewalCase.getFinalAmount());

        // 自动生成 case_no（带重试机制处理并发）
        if (renewalCase.getCaseNo() == null || renewalCase.getCaseNo().isEmpty()) {
            renewalCase.setCaseNo(generateCaseNo());
        }

        // 设置默认值 (仅在真正为null时设置,避免覆盖前端传来的0值)
        // 注意: 移除自动设为ZERO的逻辑,让前端明确传递金额

        // 设置创建时间
        if (renewalCase.getCreatedAt() == null) {
            renewalCase.setCreatedAt(LocalDateTime.now());
        }
        if (renewalCase.getUpdatedAt() == null) {
            renewalCase.setUpdatedAt(LocalDateTime.now());
        }

        // 插入时如果遇到重复编号，重试最多3次
        int retries = 3;
        for (int i = 0; i < retries; i++) {
            try {
                return renewalCaseMapper.insert(renewalCase) > 0;
            } catch (org.springframework.dao.DuplicateKeyException e) {
                if (i < retries - 1) {
                    // 重新生成编号并重试
                    renewalCase.setCaseNo(generateCaseNo());
                } else {
                    // 最后一次重试失败，抛出异常
                    throw e;
                }
            }
        }
        return false;
    }

    /**
     * 生成续约案件编号
     * 格式: RC-YYYY-NNNN (例如: RC-2026-0001)
     * 使用时间戳作为后缀的一部分以减少冲突
     */
    private synchronized String generateCaseNo() {
        String yearMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy"));

        // 查询当年已有的最大编号
        QueryWrapper<RenewalCase> countWrapper = new QueryWrapper<>();
        countWrapper.likeRight("case_no", "RC-" + yearMonth);
        Long count = renewalCaseMapper.selectCount(countWrapper);

        // 使用计数 + 1 作为下一个编号
        int nextNumber = count.intValue() + 1;

        // 如果编号已存在，继续递增直到找到可用的编号
        String caseNo;
        int attempt = 0;
        boolean exists;
        do {
            caseNo = String.format("RC-%s-%04d", yearMonth, nextNumber + attempt);
            QueryWrapper<RenewalCase> checkWrapper = new QueryWrapper<>();
            checkWrapper.eq("case_no", caseNo);
            exists = renewalCaseMapper.selectCount(checkWrapper) > 0;
            attempt++;
        } while (exists && attempt < 100);

        return caseNo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveCaseWithContract(RenewalCase renewalCase, Long originalContractId) {
        // First save the renewal case
        boolean caseSaved = saveCase(renewalCase);

        if (caseSaved && originalContractId != null) {
            // Then create the relationship with the original contract
            RenewalCaseContract relationship = new RenewalCaseContract();
            relationship.setCaseId(renewalCase.getCaseId());
            relationship.setOriginalContractId(originalContractId);
            relationship.setCreatedAt(LocalDateTime.now());

            return renewalCaseContractMapper.insert(relationship) > 0;
        }

        return caseSaved;
    }

    @Override
    public boolean updateCase(RenewalCase renewalCase) {
        return renewalCaseMapper.updateById(renewalCase) > 0;
    }

    @Override
    public boolean updateStatus(Long caseId, Integer status) {
        RenewalCase renewalCase = new RenewalCase();
        renewalCase.setCaseId(caseId);
        renewalCase.setStatus(status);
        return renewalCaseMapper.updateById(renewalCase) > 0;
    }

    @Override
    public boolean deleteCase(Long caseId) {
        return renewalCaseMapper.deleteById(caseId) > 0;
    }
}
