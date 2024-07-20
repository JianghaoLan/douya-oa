package org.lanjianghao.security.filter;

import org.lanjianghao.common.jwt.JwtHelper;
import org.lanjianghao.common.result.Result;
import org.lanjianghao.common.result.ResultCodeEnum;
import org.lanjianghao.common.utils.ResponseUtils;
import org.lanjianghao.security.custom.LoginUserInfoHelper;
import org.lanjianghao.security.utils.UserAuthorityCache;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final UserAuthorityCache authCache;

    public TokenAuthenticationFilter(UserAuthorityCache authCache) {
        this.authCache = authCache;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        logger.info("uri:" + request.getRequestURI());
        //如果是登录接口，直接放行
        if("/admin/system/index/login".equals(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        String token = getToken(request);
        logger.info("token:" + token);
        if (StringUtils.isEmpty(token)) {
            ResponseUtils.out(response, Result.build(null, ResultCodeEnum.PERMISSION));
            return;
        }
        Long userId = JwtHelper.getUserId(token);
        if (userId == null) {
            ResponseUtils.out(response, Result.build(null, ResultCodeEnum.PERMISSION));
            return;
        }
        String username = JwtHelper.getUsername(token);
        logger.info("username:" + username);
        LoginUserInfoHelper.setUserId(userId);
        LoginUserInfoHelper.setUsername(username);

        Collection<SimpleGrantedAuthority> auths = authCache.getByUsername(username);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                username, null, auths);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        return request.getHeader("token");
    }
}
