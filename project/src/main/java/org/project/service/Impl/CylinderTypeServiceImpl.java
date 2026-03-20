package org.project.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.project.mapper.CylinderMapper.CylinderTypeDictMapper;
import org.project.model.CylinderTypeDict;
import org.project.model.Dto.CylinderTypeOptionDto;
import org.project.service.CylinderTypeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CylinderTypeServiceImpl implements CylinderTypeService {

    private final CylinderTypeDictMapper typeMapper;

    @Override
    public List<CylinderTypeOptionDto> getEnabledTypeOptions() {
        LambdaQueryWrapper<CylinderTypeDict> qw = new LambdaQueryWrapper<>();
        qw.eq(CylinderTypeDict::getStatus, 1)
          .orderByAsc(CylinderTypeDict::getCylinderTypeId);
        List<CylinderTypeDict> list = typeMapper.selectList(qw);
        return list.stream()
                .map(t -> new CylinderTypeOptionDto(t.getCylinderTypeId(), t.getName()))
                .collect(Collectors.toList());
    }
}
