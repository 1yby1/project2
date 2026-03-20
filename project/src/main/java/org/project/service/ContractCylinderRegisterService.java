package org.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.project.model.contract.ContractCylinderRegister;
import org.project.model.Dto.CylinderRegisterDto.*;

import java.util.List;

/**
 * 气瓶登记追踪服务接口
 */
public interface ContractCylinderRegisterService {

    /**
     * 登记气瓶
     *
     * @param dto        登记DTO
     * @param operatorId 操作人ID
     * @return 登记ID
     */
    Long registerCylinder(CylinderRegisterDto dto, Long operatorId);

    /**
     * 批量登记气瓶
     *
     * @param dtoList    登记DTO列表
     * @param operatorId 操作人ID
     * @return 成功登记数量
     */
    Integer batchRegister(List<CylinderRegisterDto> dtoList, Long operatorId);

    /**
     * 分页查询气瓶登记列表
     *
     * @param queryDto 查询条件
     * @return 分页结果
     */
    IPage<CylinderRegisterListDto> queryRegisterPage(CylinderRegisterQueryDto queryDto,Long userId);

    /**
     * 获取气瓶登记详情
     *
     * @param registerId 登记ID
     * @return 详情DTO
     */
    CylinderRegisterDetailDto getRegisterDetail(Long registerId);

    /**
     * 根据气瓶编号查询详情
     *
     * @param cylinderNo 气瓶编号
     * @return 详情DTO
     */
    CylinderRegisterDetailDto getDetailByCylinderNo(String cylinderNo);

    /**
     * 更新气瓶状态
     *
     * @param dto        状态更新DTO
     * @param operatorId 操作人ID
     */
    void updateCylinderStatus(CylinderStatusUpdateDto dto, Long operatorId);

    /**
     * 记录气瓶检验
     *
     * @param dto        检验DTO
     * @param operatorId 操作人ID
     */
    void recordInspection(CylinderInspectDto dto, Long operatorId);

    /**
     * 查询即将到期的气瓶
     *
     * @param days 提前天数
     * @return 气瓶列表
     */
    List<ContractCylinderRegister> getInspectDueSoon(Integer days);

    /**
     * 查询已过期的气瓶
     *
     * @return 气瓶列表
     */
    List<ContractCylinderRegister> getInspectOverdue();

    /**
     * 查询指定合同下的所有气瓶
     *
     * @param contractId 合同ID
     * @return 气瓶列表
     */
    List<ContractCylinderRegister> getByContractId(Long contractId);
}
