package org.lanjianghao.wechat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "wechat")
public class WeChatMpConfigurationProperties {
    private String mpAppId;
    private String mpAppSecret;
    private String userInfoPath;
    private Templates templates;

    @Data
    public static class Templates {
        private Template pendingMessageTemplate;
        private Template processedMessageTemplate;
    }

    @Data
    public static class Template {
        private String templateId;
        private String path;
    }
}

