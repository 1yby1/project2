package org.project.mapper.unitMapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.project.model.Unit.UnitBankAccount;

@Mapper
public interface UnitBankAccountMapper extends BaseMapper<UnitBankAccount> {

    /**
     * 查询单位的默认银行账户
     */
    @Select("SELECT * FROM unit_bank_account WHERE unit_id = #{unitId} AND is_default = 1 AND status = 1 LIMIT 1")
    UnitBankAccount selectDefaultByUnitId(Long unitId);
}
