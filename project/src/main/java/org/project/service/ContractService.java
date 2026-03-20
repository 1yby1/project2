package org.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.project.model.contract.Contract;
import org.project.model.Dto.ContractDto.*;

import java.util.List;

/**
 * 合同服务接口
 */
public interface ContractService {


     /**
     * 获取即将到期的合同（状态为6或7）
     * @param unitId 单位ID，如果为null则查询所有单位
     * @return 即将到期的合同列表
     */
    List<Contract> getExpiringContracts(Long unitId);
    
    /**
     * 获取新建合同初始化数据
     *
     * @param userId 当前登录用户ID
     * @return 初始化数据（包含甲乙方信息和预生成合同编号）
     */
    ContractInitDto getContractInitData(Long userId);

    /**
     * 创建合同
     *
     * @param dto       创建DTO
     * @param creatorId 创建人ID
     * @return 合同ID
     */
    Long createContract(ContractCreateDto dto, Long creatorId);

    /**
     * 更新合同
     *
     * @param dto       更新DTO
     * @param updaterId 更新人ID
     */
    void updateContract(ContractUpdateDto dto, Long updaterId);

    /**
     * 分页查询合同列表
     *
     * @param queryDto 查询条件
     * @return 分页结果
     */
    IPage<ContractListItemDto> queryContractPage(Long userId,ContractQueryDto queryDto);

    /**
     * 获取合同详情
     *
     * @param contractId 合同ID
     * @return 详情DTO
     */
    ContractDetailDto getContractDetail(Long contractId);

    /**
     * 终止合同
     *
     * @param dto      终止DTO
     * @param operatorId 操作人ID
     */
    void terminateContract(ContractTerminateDto dto, Long operatorId);

    /**
     * 下载合同PDF
     *
     * @param contractId 合同ID
     * @return PDF文件路径
     */
    String downloadContractPdf(Long contractId);

    /**
     * 查询即将到期的合同
     *
     * @param days 天数
     * @return 合同列表
     */
    List<Contract> getExpiringContracts(Integer days);

    /**
     * 更新合同状态
     *
     * @param contractId 合同ID
     * @param status     新状态
     */
    void updateContractStatus(Long contractId, Integer status);

    // ==================== 合同审核流程 ====================

    /**
     * 管理员退回合同
     *
     * @param contractId   合同ID
     * @param rejectReason 退回原因
     * @param operatorId   操作人ID
     */
    void rejectContract(Long contractId, String rejectReason, Long operatorId);

    /**
     * 管理员确认合同（审核通过，进入待缴费状态）
     *
     * @param contractId 合同ID
     * @param remark     确认备注
     * @param operatorId 操作人ID
     */
    void confirmContract(Long contractId, String remark, Long operatorId);

    /**
     * 单位用户重新提交合同（被退回后修改重新提交）
     *
     * @param contractId 合同ID
     * @param userId     用户ID
     */
    void resubmitContract(Long contractId, Long userId);

    /**
     * 用户提交缴费信息
     *
     * @param contractId        合同ID
     * @param paymentMethod     缴费方式
     * @param paymentAmount     缴费金额
     * @param paymentVoucherUrl 缴费凭证URL
     * @param remark            备注
     * @param userId            用户ID
     */
    void submitPayment(Long contractId, Integer paymentMethod, java.math.BigDecimal paymentAmount,
                       String paymentVoucherUrl, String remark, Long userId);

    /**
     * 管理员审核缴费
     *
     * @param contractId  合同ID
     * @param approved    是否通过
     * @param auditRemark 审核备注
     * @param operatorId  操作人ID
     */
    void auditPayment(Long contractId, Boolean approved, String auditRemark, Long operatorId);

    /**
     * 管理员签署合同
     *
     * @param contractId 合同ID
     * @param userId     用户ID
     */
    void signContract(Long contractId, Long userId);

    /**
     * 获取首页统计数据
     *
     * @param userId 当前用户ID
     * @return 统计数据对象
     */
    DashboardStatsDto getDashboardStats(Long userId);

    /**
     * 首页统计数据DTO
     */
    class DashboardStatsDto {
        private long validContracts;
        private long pendingRenewals;
        private long newContracts;
        private long expiringDays;

        // 构造函数
        public DashboardStatsDto(long validContracts, long pendingRenewals, long newContracts, long expiringDays) {
            this.validContracts = validContracts;
            this.pendingRenewals = pendingRenewals;
            this.newContracts = newContracts;
            this.expiringDays = expiringDays;
        }

        // getter和setter方法
        public long getValidContracts() {
            return validContracts;
        }

        public void setValidContracts(long validContracts) {
            this.validContracts = validContracts;
        }

        public long getPendingRenewals() {
            return pendingRenewals;
        }

        public void setPendingRenewals(long pendingRenewals) {
            this.pendingRenewals = pendingRenewals;
        }

        public long getNewContracts() {
            return newContracts;
        }

        public void setNewContracts(long newContracts) {
            this.newContracts = newContracts;
        }

        public long getExpiringDays() {
            return expiringDays;
        }

        public void setExpiringDays(long expiringDays) {
            this.expiringDays = expiringDays;
        }
    }

}
