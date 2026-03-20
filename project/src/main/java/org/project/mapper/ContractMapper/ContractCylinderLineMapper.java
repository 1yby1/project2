package org.project.mapper.ContractMapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.project.model.contract.ContractCylinderLine;

import java.util.List;

@Mapper
public interface ContractCylinderLineMapper extends BaseMapper<ContractCylinderLine> {

    /**
     * 查询合同的气瓶明细（带类型名称）
     */
    List<ContractCylinderLine> selectByContractIdWithTypeName(@Param("contractId") Long contractId);

    /**
     * 批量插入气瓶明细
     */
    int insertBatch(@Param("list") List<ContractCylinderLine> list);
}
