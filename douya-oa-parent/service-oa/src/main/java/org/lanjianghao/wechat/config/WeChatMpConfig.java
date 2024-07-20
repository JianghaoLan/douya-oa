package org.lanjianghao.wechat.config;

import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class WeChatMpConfig {

    @Autowired
    private WeChatMpConfigurationProperties wechatMpConfigurationProperties;

    @Bean
    public WxMpService wxMpService(WxMpConfigStorage wxMpConfigStorage){
        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(wxMpConfigStorage);
        return wxMpService;
    }

    @Bean
    public WxMpConfigStorage wxMpConfigStorage() {
        WxMpDefaultConfigImpl wxMpConfigStorage = new WxMpDefaultConfigImpl();
        wxMpConfigStorage.setAppId(wechatMpConfigurationProperties.getMpAppId());
        wxMpConfigStorage.setSecret(wechatMpConfigurationProperties.getMpAppSecret());
        return wxMpConfigStorage;
    }

    @Bean
    public WeChatMpConfigurationProperties.Templates templates() {
//        System.out.println("### 初始化WeChatMpConfigurationProperties.Templates ###");
        return wechatMpConfigurationProperties.getTemplates();
    }
}