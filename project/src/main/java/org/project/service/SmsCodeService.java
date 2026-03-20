package org.project.service;

public interface SmsCodeService {

    /**
     * 发送登录/注册验证码（验证码会写入 Redis，并在有效期内可校验）
     *
     * @param phone 手机号
     * @return 本次生成的验证码（仅用于开发联调；生产环境建议返回固定文案或不返回）
     */
    String sendLoginOrRegisterCode(String phone);

    /**
     * 校验验证码（一次性使用：校验成功会删除 Redis 中的验证码）
     */
    boolean verifyCode(String phone, String code);
}

