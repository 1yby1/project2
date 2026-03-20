package org.project.mapper.CylinderMapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.project.model.CylinderInspectLog;

import java.util.List;

@Mapper
public interface CylinderInspectLogMapper extends BaseMapper<CylinderInspectLog> {

    /**
     * 查询指定气瓶的检验历史记录
     */
    @Select("""
            SELECT * FROM cylinder_inspect_log
            WHERE register_id = #{registerId}
            ORDER BY inspect_date DESC
            """)
    List<CylinderInspectLog> selectByRegisterId(@Param("registerId") Long registerId);

    /**
     * 查询指定气瓶编号的检验历史记录
     */
    @Select("""
            SELECT * FROM cylinder_inspect_log
            WHERE cylinder_no = #{cylinderNo}
            ORDER BY inspect_date DESC
            """)
    List<CylinderInspectLog> selectByCylinderNo(@Param("cylinderNo") String cylinderNo);
}
