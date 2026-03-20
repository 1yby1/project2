package org.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.project.model.renewal.RenewalCase;

public interface RenewalCaseService {
    IPage<RenewalCase> getPageList(int page, int size, Long unitId, Integer status);

    RenewalCase getCaseById(Long caseId);

    boolean saveCase(RenewalCase renewalCase);

    boolean saveCaseWithContract(RenewalCase renewalCase, Long originalContractId);

    boolean updateCase(RenewalCase renewalCase);

    boolean updateStatus(Long caseId, Integer status);

    boolean deleteCase(Long caseId);
}
