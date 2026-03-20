package org.project.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.project.mapper.ContractMapper.ContractMapper;
import org.project.mapper.ContractMapper.ContractNoSequenceMapper;
import org.project.model.contract.Contract;
import org.project.model.contract.ContractNoSequence;
import org.project.service.ContractNoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ContractNoServiceImpl implements ContractNoService {

    private final ContractNoSequenceMapper sequenceMapper;
    private final ContractMapper contractMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String generateContractNo(String regionCode, String unitCode) {
        int currentYear = LocalDateTime.now().getYear();

        // 查询或创建序列记录
        LambdaQueryWrapper<ContractNoSequence> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ContractNoSequence::getRegionCode, regionCode)
                .eq(ContractNoSequence::getUnitCode, unitCode)
                .eq(ContractNoSequence::getYear, currentYear);

        ContractNoSequence sequence = sequenceMapper.selectOne(wrapper);

        int nextSeq;
        if (sequence == null) {
            // 首次创建
            sequence = new ContractNoSequence();
            sequence.setRegionCode(regionCode);
            sequence.setUnitCode(unitCode);
            sequence.setYear(currentYear);
            sequence.setLastSeq(1);
            sequenceMapper.insert(sequence);
            nextSeq = 1;
        } else {
            // 更新序列号（使用乐观锁或数据库锁保证并发安全）
            nextSeq = sequence.getLastSeq() + 1;
            sequence.setLastSeq(nextSeq);
            sequenceMapper.updateById(sequence);
        }

        // 生成合同编号：地区-单位-年份-序号（3位补零）
        return String.format("%s-%s-%d-%03d", regionCode, unitCode, currentYear, nextSeq);
    }

    @Override
    public boolean checkContractNoExists(String contractNo) {
        LambdaQueryWrapper<Contract> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Contract::getContractNo, contractNo);
        return contractMapper.selectCount(wrapper) > 0;
    }
}
