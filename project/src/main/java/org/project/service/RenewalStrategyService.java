package org.project.service;

import org.project.model.renewal.RenewalStrategy;

import java.util.List;

public interface RenewalStrategyService {
    List<RenewalStrategy> getAllStrategies();

    RenewalStrategy getStrategyById(Long strategyId);

    boolean saveStrategy(RenewalStrategy strategy);

    boolean updateStrategy(RenewalStrategy strategy);

    boolean deleteStrategy(Long strategyId);
}
