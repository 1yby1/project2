package org.project.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Paths;

/**
 * Web MVC 配置
 * 用于配置静态资源访问
 */
@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置文件上传访问路径
        // 访问URL: http://localhost:8081/files/**
        // 映射到本地路径: uploads/**
        
        // 获取绝对路径
        File uploadDir = new File(uploadPath);
        String absolutePath = uploadDir.getAbsolutePath();
        
        // 确保路径以文件分隔符结尾
        if (!absolutePath.endsWith(File.separator)) {
            absolutePath += File.separator;
        }
        
        // 转换为file URL格式
        String fileUrl = "file:///" + absolutePath.replace("\\", "/");
        
        log.info("配置静态资源映射: /files/** -> {}", fileUrl);
        
        registry.addResourceHandler("/files/**")
                .addResourceLocations(fileUrl);
    }
}
