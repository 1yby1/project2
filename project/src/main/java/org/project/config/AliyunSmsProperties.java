package org.project.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "aliyun.pns")
public class AliyunSmsProperties {
    private String accessKeyId;
    private String accessKeySecret;

    private String signName;
    private String templateCode;

    private String regionId = "cn-hangzhou";
}

