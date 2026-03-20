package org.project.model.contract;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("contract_file")
public class ContractFile {
    @TableId(type = IdType.AUTO)
    private Long fileId;

    private Long contractId;

    /**
     * 类型：1-扫描件 2-草稿PDF 3-签章PDF 4-证据链
     */
    private Integer fileType;

    private String fileName;

    private String fileUrl;

    private String fileHash;

    private Long uploadedBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime uploadedAt;
}
