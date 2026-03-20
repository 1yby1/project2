package org.project.service.Impl;

import lombok.extern.slf4j.Slf4j;
import org.project.service.FileUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

/**
 * 本地文件上传服务实现（简单版）
 * 生产环境建议使用OSS（阿里云OSS、MinIO等）
 */
@Slf4j
@Service
public class LocalFileUploadServiceImpl implements FileUploadService {

    @Value("${file.upload.path:uploads}")
    private String uploadBasePath;

    @Value("${file.upload.url-prefix:http://localhost:8080/files}")
    private String urlPrefix;

    @Override
    public String uploadFile(MultipartFile file, String fileType) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件为空");
        }

        // 生成文件存储路径：uploads/fileType/2025/01/06/uuid_filename.ext
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String relativePath = fileType + "/" + datePath;
        Path dirPath = Paths.get(uploadBasePath, relativePath);

        // 创建目录
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String filename = UUID.randomUUID().toString() + extension;

        // 保存文件
        Path filePath = dirPath.resolve(filename);
        file.transferTo(filePath.toFile());

        // 返回访问URL
        String fileUrl = urlPrefix + "/" + relativePath + "/" + filename;
        log.info("文件上传成功：{}", fileUrl);
        return fileUrl;
    }

    @Override
    public void deleteFile(String fileUrl) throws Exception {
        if (fileUrl == null || !fileUrl.startsWith(urlPrefix)) {
            return;
        }

        // 从URL提取相对路径
        String relativePath = fileUrl.substring(urlPrefix.length() + 1);
        Path filePath = Paths.get(uploadBasePath, relativePath);

        if (Files.exists(filePath)) {
            Files.delete(filePath);
            log.info("文件删除成功：{}", fileUrl);
        }
    }

    @Override
    public boolean validateFileType(MultipartFile file, String... allowedTypes) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return false;
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        return Arrays.asList(allowedTypes).contains(extension);
    }

    @Override
    public boolean validateFileSize(MultipartFile file, long maxSize) {
        return file != null && file.getSize() <= maxSize;
    }
}
