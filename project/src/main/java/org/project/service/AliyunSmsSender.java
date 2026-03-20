package org.project.service;

public interface AliyunSmsSender {
    /**
     * 发送短信验证码
     */
    void sendVerifyCode(String phone, String code);
}

