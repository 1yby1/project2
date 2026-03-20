package org.project.service.Impl;

import org.project.service.AliyunSmsSender;
import org.project.service.SmsCodeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
public class SmsCodeServiceImpl implements SmsCodeService {

    private static final String KEY_CODE_PREFIX = "sms:code:";
    private static final String KEY_SEND_INTERVAL_PREFIX = "sms:send_interval:";
    private static final String KEY_VERIFY_ATTEMPT_PREFIX = "sms:verify_attempt:";

    private final StringRedisTemplate redisTemplate;
    private final SecureRandom random = new SecureRandom();
    private final AliyunSmsSender aliyunSmsSender;

    @Value("${sms.code.ttl-seconds:300}")
    private long ttlSeconds;

    @Value("${sms.code.send-interval-seconds:60}")
    private long sendIntervalSeconds;

    @Value("${sms.code.verify-max-attempts:5}")
    private long verifyMaxAttempts;

    public SmsCodeServiceImpl(StringRedisTemplate redisTemplate, AliyunSmsSender aliyunSmsSender) {
        this.redisTemplate = redisTemplate;
        this.aliyunSmsSender = aliyunSmsSender;
    }

    @Override
    public String sendLoginOrRegisterCode(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("phone is blank");
        }

        // 发送频率限制：同一手机号 sendIntervalSeconds 内只能发送一次
        String intervalKey = KEY_SEND_INTERVAL_PREFIX + phone;
        Boolean canSend = redisTemplate.opsForValue().setIfAbsent(intervalKey, "1", Duration.ofSeconds(sendIntervalSeconds));
        if (canSend == null || !canSend) {
            throw new IllegalStateException("发送过于频繁，请稍后再试");
        }

        String code = String.format("%06d", random.nextInt(1_000_000));

        // 存验证码
        String codeKey = KEY_CODE_PREFIX + phone;
        redisTemplate.opsForValue().set(codeKey, code, Duration.ofSeconds(ttlSeconds));

        // 重置校验次数
        String attemptKey = KEY_VERIFY_ATTEMPT_PREFIX + phone;
        redisTemplate.delete(attemptKey);

        // 接入阿里云短信发送
        aliyunSmsSender.sendVerifyCode(phone, code);

        // 开发联调返回验证码；生产环境建议不返回或返回固定文案
        return code;
    }

    @Override
    public boolean verifyCode(String phone, String code) {
        if (phone == null || phone.isBlank() || code == null || code.isBlank()) {
            return false;
        }

        String attemptKey = KEY_VERIFY_ATTEMPT_PREFIX + phone;
        Long attempts = redisTemplate.opsForValue().increment(attemptKey);
        if (attempts != null && attempts == 1) {
            // attempts 第一次创建时给一个与验证码相同的 TTL
            redisTemplate.expire(attemptKey, Duration.ofSeconds(ttlSeconds));
        }
        if (attempts != null && attempts > verifyMaxAttempts) {
            // 超过次数，直接作废验证码
            invalidate(phone);
            return false;
        }

        String codeKey = KEY_CODE_PREFIX + phone;
        String cached = redisTemplate.opsForValue().get(codeKey);
        boolean ok = cached != null && cached.equals(code);
        if (ok) {
            invalidate(phone);
        }
        return ok;
    }

    private void invalidate(String phone) {
        redisTemplate.delete(KEY_CODE_PREFIX + phone);
        redisTemplate.delete(KEY_VERIFY_ATTEMPT_PREFIX + phone);
    }
}
