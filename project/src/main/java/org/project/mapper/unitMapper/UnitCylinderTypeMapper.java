package org.project.mapper.unitMapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.project.model.Unit.UnitCylinderType;

import java.util.List;

@Mapper
public interface UnitCylinderTypeMapper extends BaseMapper<UnitCylinderType> {

    /**
     * 查询单位经营的所有气瓶类型（带类型名称）
     */
    @Select("""
            SELECT uct.*, ct.name AS cylinderTypeName
            FROM unit_cylinder_type uct
            LEFT JOIN cylinder_type_dict ct ON uct.cylinder_type_id = ct.cylinder_type_id
            WHERE uct.unit_id = #{unitId}
            ORDER BY uct.created_at ASC
            """)
    List<UnitCylinderType> selectByUnitIdWithTypeName(Long unitId);
}
