package org.project.model.Dto.ContractDto;

import lombok.Data;

import java.io.Serializable;

/**
 * 新建合同初始化数据 DTO
 */
@Data
public class ContractInitDto implements Serializable {

    /**
     * 预生成的合同编号（用户可修改）
     */
    private String preGeneratedContractNo;

    /**
     * 甲方（申请单位）信息
     */
    private PartyAInfo partyAInfo;

    /**
     * 乙方（服务公司）信息
     */
    private PartyBInfo partyBInfo;

    @Data
    public static class PartyAInfo {
        /**
         * 单位名称
         */
        private String unitName;

        /**
         * 社会信用代码
         */
        private String socialCreditCode;

        /**
         * 单位地址
         */
        private String unitAddress;

        /**
         * 经营气瓶类型
         */
        private String businessScope;

        /**
         * 负责人
         */
        private String principalName;

        /**
         * 负责人电话
         */
        private String principalPhone;

        /**
         * 开户银行
         */
        private String bankName;

        /**
         * 银行账号
         */
        private String bankAccount;
    }

    @Data
    public static class PartyBInfo {
        /**
         * 公司名称
         */
        private String companyName;

        /**
         * 统一信用代码
         */
        private String socialCreditCode;

        /**
         * 公司地址
         */
        private String address;

        /**
         * 联系人
         */
        private String contactName;

        /**
         * 联系电话
         */
        private String contactPhone;

        /**
         * 开户银行
         */
        private String bankName;

        /**
         * 银行账号
         */
        private String bankAccount;
    }
}
