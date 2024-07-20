package org.lanjianghao.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lanjianghao.auth.mapper.SysUserMapper;
import org.lanjianghao.auth.service.SysUserService;
import org.lanjianghao.model.system.SysUser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author lanjianghao
 * @since 2024-07-09
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public void updateStatus(Long id, Integer status) {
        baseMapper.updateStatusById(id, status);
    }

    @Override
    public SysUser getByUsername(String username) {
        return this.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
    }

    @Override
    public List<SysUser> listByUsernames(List<String> usernames) {
        return this.list(new LambdaQueryWrapper<SysUser>().in(SysUser::getUsername, usernames));
    }

    @Override
    public SysUser getByOpenId(String openId) {
        return this.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getOpenId, openId));
    }

    @Override
    public SysUser getByPhone(String phone) {
        return this.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getPhone, phone));
    }
}
