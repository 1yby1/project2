package org.project.mapper.CylinderMapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.project.model.contract.ContractCylinderRegister;

import java.util.List;

@Mapper
public interface ContractCylinderRegisterMapper extends BaseMapper<ContractCylinderRegister> {

    /**
     * 分页查询气瓶登记列表（带关联信息）
     */
    IPage<ContractCylinderRegister> selectRegisterPage(
            Page<ContractCylinderRegister> page,
            @Param("contractId") Long contractId,
            @Param("cylinderTypeId") Long cylinderTypeId,
            @Param("cylinderNo") String cylinderNo,
            @Param("rfidTag") String rfidTag,
            @Param("cylinderStatus") Integer cylinderStatus,
            @Param("unitId") Long unitId,
            @Param("inspectionStatus") String inspectionStatus
    );

    /**
     * 根据气瓶编号查询详情（带关联信息）
     */
    ContractCylinderRegister selectByCylinderNo(@Param("cylinderNo") String cylinderNo);

    /**
     * 查询即将到期的气瓶（下次检验日期在指定天数内）
     */
    List<ContractCylinderRegister> selectInspectDueSoon(@Param("days") Integer days);

    /**
     * 查询已过期的气瓶（下次检验日期已过）
     */
    List<ContractCylinderRegister> selectInspectOverdue();

    /**
     * 批量查询指定合同下的所有气瓶
     */
    List<ContractCylinderRegister> selectByContractId(@Param("contractId") Long contractId);
}
