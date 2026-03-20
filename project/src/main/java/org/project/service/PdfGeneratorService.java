package org.project.service;

import org.project.model.Dto.ContractDto.ContractDetailDto;

/**
 * PDF生成服务接口
 */
public interface PdfGeneratorService {

    /**
     * 生成合同PDF
     *
     * @param contractDetail 合同详情
     * @return PDF文件路径
     */
    String generateContractPdf(ContractDetailDto contractDetail) throws Exception;
}
