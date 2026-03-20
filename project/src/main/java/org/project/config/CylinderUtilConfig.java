package org.project.config;

import org.project.util.CylinderExcelImporter;
import org.project.util.CylinderTemplateGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 气瓶工具类配置
 */
@Configuration
public class CylinderUtilConfig {

    /**
     * Excel导入工具
     */
    @Bean
    public CylinderExcelImporter cylinderExcelImporter() {
        return new CylinderExcelImporter();
    }

    /**
     * Excel模板生成工具
     */
    @Bean
    public CylinderTemplateGenerator cylinderTemplateGenerator() {
        return new CylinderTemplateGenerator();
    }
}
