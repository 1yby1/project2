package org.project.service;

/**
 * 合同编号生成服务
 */
public interface ContractNoService {

    /**
     * 生成合同编号
     * 规则：地区编码-单位编码-年份-序号（如：BJ-HX001-2025-001）
     *
     * @param regionCode 地区编码
     * @param unitCode   单位编码
     * @return 生成的合同编号
     */
    String generateContractNo(String regionCode, String unitCode);

    /**
     * 校验合同编号是否存在
     *
     * @param contractNo 合同编号
     * @return true-存在 false-不存在
     */
    boolean checkContractNoExists(String contractNo);
}
