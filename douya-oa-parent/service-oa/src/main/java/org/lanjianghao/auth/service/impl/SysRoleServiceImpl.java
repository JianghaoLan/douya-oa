package org.lanjianghao.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lanjianghao.auth.mapper.SysRoleMapper;
import org.lanjianghao.auth.service.SysRoleService;
import org.lanjianghao.auth.service.SysUserRoleService;
import org.lanjianghao.model.system.SysRole;
import org.lanjianghao.model.system.SysUserRole;
import org.lanjianghao.vo.system.AssignRoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Override
    public Map<String, Object> findRoleDataByUserId(Long userId) {
        List<SysRole> allRoles = this.list();

        List<SysUserRole> userRoles = sysUserRoleService.list(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));

        List<Long> userRoleIds = userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());

        List<SysRole> assignedRoles = new ArrayList<>();
        for (SysRole role : allRoles) {
            if (userRoleIds.contains(role.getId())) {
                assignedRoles.add(role);
            }
        }

        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("assignRoleList", assignedRoles);
        roleMap.put("allRolesList", allRoles);
        return roleMap;
    }

    @Override
    public void doAssign(AssignRoleVo assignRoleVo) {
        Long userId = assignRoleVo.getUserId();

        //先删除之前分配的角色
        sysUserRoleService.remove(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));

        //重新分配角色
        List<SysUserRole> userRoleList = assignRoleVo.getRoleIdList().stream().map((roleId) -> {
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setRoleId(roleId);
            sysUserRole.setUserId(userId);
            return sysUserRole;
        }).collect(Collectors.toList());
        sysUserRoleService.saveBatch(userRoleList);
    }
}
