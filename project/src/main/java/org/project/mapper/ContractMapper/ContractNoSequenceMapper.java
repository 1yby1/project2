package org.project.mapper.ContractMapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.project.model.contract.ContractNoSequence;

@Mapper
public interface ContractNoSequenceMapper extends BaseMapper<ContractNoSequence> {

    /**
     * 获取并更新序列号（使用数据库锁保证并发安全）
     */
    int updateSequence(@Param("regionCode") String regionCode,
                      @Param("unitCode") String unitCode,
                      @Param("year") Integer year);
}
