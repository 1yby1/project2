package org.project.mapper.ContractMapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.project.model.contract.ContractItem;

import java.util.List;

@Mapper
public interface ContractItemMapper extends BaseMapper<ContractItem> {

    /**
     * 批量插入产品明细
     */
    int insertBatch(@Param("list") List<ContractItem> list);
}
