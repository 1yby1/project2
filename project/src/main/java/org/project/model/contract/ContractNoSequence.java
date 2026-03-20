package org.project.model.contract;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("contract_no_sequence")
public class ContractNoSequence {
    @TableId(type = IdType.AUTO)
    private Long seqId;

    private String regionCode;

    private String unitCode;

    private Integer year;

    private Integer lastSeq;

    /**
     * 数据库列定义为 NOT NULL DEFAULT CURRENT_TIMESTAMP。
     * 由于当前项目未配置 MyBatis-Plus 的自动填充处理器，这里提供默认值，避免 insert 时传入 null。
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
