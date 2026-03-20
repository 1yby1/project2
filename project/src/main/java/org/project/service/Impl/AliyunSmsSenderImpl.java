package org.project.service.Impl;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dypnsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dypnsapi20170525.models.SendSmsVerifyCodeRequest;
import com.aliyun.sdk.service.dypnsapi20170525.models.SendSmsVerifyCodeResponse;
import darabonba.core.client.ClientOverrideConfiguration;
import org.project.config.AliyunSmsProperties;
import org.project.service.AliyunSmsSender;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.CompletableFuture;

@Service
public class AliyunSmsSenderImpl implements AliyunSmsSender {

    private final AliyunSmsProperties props;

    public AliyunSmsSenderImpl(AliyunSmsProperties props) {
        this.props = props;
    }

    @PostConstruct
    public void validateConfig() {
        // fail-fast：启动时就发现配置缺失，而不是等到发短信时报错
        requireNotBlank(props.getAccessKeyId(), "aliyun.pns.access-key-id / env ALIYUN_ACCESS_KEY_ID");
        requireNotBlank(props.getAccessKeySecret(), "aliyun.pns.access-key-secret / env ALIYUN_ACCESS_KEY_SECRET");
        requireNotBlank(props.getSignName(), "aliyun.pns.sign-name / env ALIYUN_SMS_SIGN_NAME");
        requireNotBlank(props.getTemplateCode(), "aliyun.pns.template-code / env ALIYUN_SMS_TEMPLATE_CODE");
        requireNotBlank(props.getRegionId(), "aliyun.pns.region-id / env ALIYUN_REGION_ID");
    }

    private static void requireNotBlank(String value, String hint) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Aliyun SMS config missing: " + hint);
        }
    }

    @Override
    public void sendVerifyCode(String phone, String code) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("phone is blank");
        }
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("code is blank");
        }

        StaticCredentialProvider provider = StaticCredentialProvider.create(
                Credential.builder()
                        .accessKeyId(props.getAccessKeyId())
                        .accessKeySecret(props.getAccessKeySecret())
                        .build()
        );

        try (AsyncClient client = AsyncClient.builder()
                .region(props.getRegionId())
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride("dypnsapi.aliyuncs.com")
                )
                .build()) {

            // 重点：把方法入参 code 塞进模板参数 JSON
            // 你的模板参数里还有 min=1，这里也按原逻辑保留
            String templateParam = String.format("{\"code\":\"%s\",\"min\":\"1\"}", escapeJson(code));

            SendSmsVerifyCodeRequest request = SendSmsVerifyCodeRequest.builder()
                    .phoneNumber(phone)
                    .signName(props.getSignName())
                    .templateCode(props.getTemplateCode())
                    .templateParam(templateParam)
                    .build();

            CompletableFuture<SendSmsVerifyCodeResponse> future = client.sendSmsVerifyCode(request);

            // 等待结果，发送失败时能直接在日志/异常中看到
            SendSmsVerifyCodeResponse resp = future.get();
            if (resp == null || resp.getBody() == null) {
                throw new IllegalStateException("Aliyun SMS response is null");
            }
        } catch (Exception e) {
            throw new RuntimeException("Aliyun SMS sendVerifyCode failed", e);
        }
    }

    private static String escapeJson(String s) {
        // 简单 JSON 转义，避免 code 里意外字符破坏 JSON（一般验证码是数字，不会触发，但写上更稳）
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
