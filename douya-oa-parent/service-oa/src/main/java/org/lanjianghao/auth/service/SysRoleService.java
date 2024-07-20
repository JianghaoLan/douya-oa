package org.lanjianghao.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lanjianghao.model.system.SysRole;
import org.lanjianghao.vo.system.AssignRoleVo;

import java.util.Map;

public interface SysRoleService extends IService<SysRole> {
    Map<String, Object> findRoleDataByUserId(Long userId);

    void doAssign(AssignRoleVo assignRoleVo);
}
