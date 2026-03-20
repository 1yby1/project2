package org.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.project.model.UnitPersonnel;

import java.util.List;

public interface UnitPersonnelService {
    /**
     * 获取单位人员列表（分页）
     */
    IPage<UnitPersonnel> getPersonnelList(int page, int size, String keyword, Long unitId, String department, String role);

    /**
     * 获取所有单位人员
     */
    List<UnitPersonnel> getAllPersonnel();

    /**
     * 根据ID获取单位人员
     */
    UnitPersonnel getPersonnelById(Long personnelId);

    /**
     * 根据手机号获取单位人员
     */
    UnitPersonnel getPersonnelByPhone(String phone);

    /**
     * 新增单位人员
     */
    Boolean savePersonnel(UnitPersonnel personnel);

    /**
     * 更新单位人员信息
     */
    Boolean updatePersonnel(UnitPersonnel personnel);

    /**
     * 删除单位人员
     */
    Boolean deletePersonnel(Long personnelId);

    /**
     * 更新单位人员状态
     */
    Boolean updatePersonnelStatus(Long personnelId, Integer status);
}