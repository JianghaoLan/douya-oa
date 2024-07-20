package org.lanjianghao.wechat.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.lanjianghao.auth.service.SysUserService;
import org.lanjianghao.common.config.network.NetworkConfigurationProperties;
import org.lanjianghao.common.jwt.JwtHelper;
import org.lanjianghao.common.result.Result;
import org.lanjianghao.model.system.SysUser;
import org.lanjianghao.vo.wechat.BindPhoneVo;
import org.lanjianghao.wechat.config.WeChatMpConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Api(tags = "微信接口")
@Controller
@RequestMapping("/admin/wechat")
@CrossOrigin
@Slf4j
public class WeChatController {
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private WxMpService wxMpService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String userInfoUrl;

    WeChatController(NetworkConfigurationProperties netConfig, WeChatMpConfigurationProperties wxConfig) {
        this.userInfoUrl = UriComponentsBuilder
                .fromHttpUrl(netConfig.getApiPublicAddress())
                .path(wxConfig.getUserInfoPath())
                .build().toUriString();
    }

    @GetMapping("/authorize")
    public String authorize(@RequestParam("returnUrl") String returnUrl) {
        returnUrl = returnUrl.replace("guiguoa", "#");
        try {
            String redirectUrl = wxMpService.getOAuth2Service().buildAuthorizationUrl(userInfoUrl,
                    WxConsts.OAuth2Scope.SNSAPI_USERINFO, URLEncoder.encode(returnUrl, "UTF-8"));
            log.info("redirectUrl:" + redirectUrl);
            return "redirect:" + redirectUrl;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/userInfo")
    public String userInfo(@RequestParam("code") String code, @RequestParam("state") String returnUrl)
            throws WxErrorException, JsonProcessingException {
        //获取 accessToken
        WxOAuth2AccessToken accessToken = wxMpService.getOAuth2Service().getAccessToken(code);
        //获取 openid
        String openId = accessToken.getOpenId();
        log.info("openId:" + openId);

        WxOAuth2UserInfo wxUserInfo = wxMpService.getOAuth2Service().getUserInfo(accessToken, null);
        log.info("微信用户信息：" + objectMapper.writeValueAsString(wxUserInfo));

        SysUser sysUser = sysUserService.getByOpenId(openId);
        String token = "";
        if (sysUser != null) {
            token = JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());
        }
        if(!returnUrl.contains("?")) {
            return "redirect:" + returnUrl + "?token=" + token + "&openId=" + openId;
        } else {
            return "redirect:" + returnUrl + "&token=" + token + "&openId=" + openId;
        }
    }

    @ApiOperation("绑定微信（openId）到系统账号")
    @PostMapping("bindPhone")
    @ResponseBody
    public Result<String> bindPhone(@RequestBody BindPhoneVo bindPhoneVo) {
        SysUser sysUser = sysUserService.getByPhone(bindPhoneVo.getPhone());
        if (sysUser != null) {
            SysUser forUpdate = new SysUser();
            forUpdate.setId(sysUser.getId());
            forUpdate.setOpenId(bindPhoneVo.getOpenId());
            sysUserService.updateById(forUpdate);

            //生成token
            String token = JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());
            return Result.ok(token);
        } else {
            return Result.fail("手机号不存在，请联系管理员修改");
        }
    }
}
