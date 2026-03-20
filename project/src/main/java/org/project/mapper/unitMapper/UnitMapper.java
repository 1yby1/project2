package org.project.mapper.unitMapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.project.model.Unit.Unit;

import java.util.Map;

@Mapper
public interface UnitMapper extends BaseMapper<Unit> {
	IPage<Unit> selectUnitPageWithContractCount(Page<Unit> page, @Param("params") Map<String, Object> params);

	Integer countContractsByUnitId(@Param("unitId") Long unitId);
}
