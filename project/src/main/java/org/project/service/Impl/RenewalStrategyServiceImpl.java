package org.project.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.project.mapper.RenewalMapper.RenewalStrategyMapper;
import org.project.model.renewal.RenewalStrategy;
import org.project.service.RenewalStrategyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RenewalStrategyServiceImpl implements RenewalStrategyService {

    @Autowired
    private RenewalStrategyMapper renewalStrategyMapper;

    @Override
    public List<RenewalStrategy> getAllStrategies() {
        QueryWrapper<RenewalStrategy> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1);
        queryWrapper.orderByAsc("sort_order");
        return renewalStrategyMapper.selectList(queryWrapper);
    }

    @Override
    public RenewalStrategy getStrategyById(Long strategyId) {
        return renewalStrategyMapper.selectById(strategyId);
    }

    @Override
    public boolean saveStrategy(RenewalStrategy strategy) {
        return renewalStrategyMapper.insert(strategy) > 0;
    }

    @Override
    public boolean updateStrategy(RenewalStrategy strategy) {
        return renewalStrategyMapper.updateById(strategy) > 0;
    }

    @Override
    public boolean deleteStrategy(Long strategyId) {
        return renewalStrategyMapper.deleteById(strategyId) > 0;
    }
}
