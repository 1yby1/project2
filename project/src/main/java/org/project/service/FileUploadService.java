package org.project.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传服务接口
 */
public interface FileUploadService {

    /**
     * 上传文件
     *
     * @param file     上传的文件
     * @param fileType 文件类型（contract/voucher等）
     * @return 文件访问URL
     */
    String uploadFile(MultipartFile file, String fileType) throws Exception;

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     */
    void deleteFile(String fileUrl) throws Exception;

    /**
     * 校验文件类型
     *
     * @param file          上传的文件
     * @param allowedTypes  允许的文件类型（如：pdf,jpg,png）
     * @return true-通过 false-不通过
     */
    boolean validateFileType(MultipartFile file, String... allowedTypes);

    /**
     * 校验文件大小
     *
     * @param file    上传的文件
     * @param maxSize 最大大小（字节）
     * @return true-通过 false-不通过
     */
    boolean validateFileSize(MultipartFile file, long maxSize);
}
