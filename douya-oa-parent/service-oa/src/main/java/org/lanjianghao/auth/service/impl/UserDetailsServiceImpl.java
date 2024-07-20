package org.lanjianghao.auth.service.impl;

import org.lanjianghao.auth.service.SysMenuService;
import org.lanjianghao.auth.service.SysUserService;
import org.lanjianghao.model.system.SysUser;
import org.lanjianghao.security.custom.CustomUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    SysUserService sysUserService;

    @Autowired
    SysMenuService sysMenuService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUserService.getByUsername(username);
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        if (sysUser.getStatus() == 0) {
            throw new UsernameNotFoundException("账号已停用");
        }

        //查询权限列表
        List<String> perms = sysMenuService.findPermsByUserId(sysUser.getId());
        List<SimpleGrantedAuthority> auths = perms.stream().map(perm -> new SimpleGrantedAuthority(perm.trim())).collect(Collectors.toList());

        return new CustomUser(sysUser, auths);
    }
}
