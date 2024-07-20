package org.lanjianghao.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lanjianghao.model.system.SysUser;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author lanjianghao
 * @since 2024-07-09
 */
public interface SysUserService extends IService<SysUser> {

    void updateStatus(Long id, Integer status);

    SysUser getByUsername(String username);

    List<SysUser> listByUsernames(List<String> usernames);

    SysUser getByOpenId(String openId);

    SysUser getByPhone(String phone);
}
