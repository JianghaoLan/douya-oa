package org.lanjianghao.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.lanjianghao.common.jwt.JwtHelper;
import org.lanjianghao.common.result.Result;
import org.lanjianghao.common.utils.ResponseUtils;
import org.lanjianghao.security.custom.CustomUser;
import org.lanjianghao.security.utils.UserAuthorityCache;
import org.lanjianghao.vo.system.LoginVo;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.lanjianghao.common.result.ResultCodeEnum.LOGIN_ERROR;

//@Component
public class TokenLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;

    private final UserAuthorityCache authCache;

    public TokenLoginFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper,
                            UserAuthorityCache authCache) {
        setPostOnly(false);
        setRequiresAuthenticationRequestMatcher(
                new AntPathRequestMatcher("/admin/system/index/login", "POST"));
        setAuthenticationManager(authenticationManager);
        this.objectMapper = objectMapper;
        this.authCache = authCache;
    }

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            LoginVo loginVo = objectMapper.readValue(request.getInputStream(), LoginVo.class);
            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(loginVo.getUsername(), loginVo.getPassword());
            return this.getAuthenticationManager().authenticate(authRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response, FilterChain chain, Authentication authResult) {
        CustomUser user = (CustomUser)authResult.getPrincipal();

        //保存用户权限数据到redis
        authCache.set(user);

        String token = JwtHelper.createToken(user.getSysUser().getId(), user.getSysUser().getUsername());
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        ResponseUtils.out(response, Result.ok(map), objectMapper);
    }

    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response, AuthenticationException failed) {
        ResponseUtils.out(response, Result.build(null, LOGIN_ERROR).message(failed.getMessage()), objectMapper);
    }

//    private void readUserAuthorities(CustomUser user)
}
