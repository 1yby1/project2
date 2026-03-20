package org.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import org.project.model.UnitPersonnel;

@Mapper
public interface UnitPersonnelMapper extends BaseMapper<UnitPersonnel> {
    IPage<UnitPersonnel> selectPersonnelWithUnitName(Page<UnitPersonnel> page, @Param("ew") Wrapper<UnitPersonnel> queryWrapper);
}