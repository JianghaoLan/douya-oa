package org.lanjianghao.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.lanjianghao.auth.service.SysRoleService;
import org.lanjianghao.common.result.Result;
import org.lanjianghao.model.system.SysRole;
import org.lanjianghao.vo.system.AssignRoleVo;
import org.lanjianghao.vo.system.SysRoleQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "角色管理接口")
@RestController
@RequestMapping("/admin/system/sysRole")
public class SysRoleController {
    @Autowired
    private SysRoleService sysRoleService;

    @ApiOperation("查询所有角色")
    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @GetMapping("/findAll")
    public Result<List<SysRole>> findAll() {
        List<SysRole> list = sysRoleService.list();
        return Result.ok(list);
    }

    @ApiOperation("条件分页查询")
    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @GetMapping("{page}/{limit}")
    public Result<IPage<SysRole>> pageQueryRole(@PathVariable("page") Long page,
                                @PathVariable("limit") Long limit,
                                SysRoleQueryVo sysRoleQueryVo) {
        Page<SysRole> pageParam = new Page<>(page, limit);
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        String roleName = sysRoleQueryVo.getRoleName();
        if (StringUtils.hasLength(roleName)) {
            queryWrapper.like(SysRole::getRoleName, sysRoleQueryVo.getRoleName());
        }
        IPage<SysRole> result = sysRoleService.page(pageParam, queryWrapper);
        return Result.ok(result);
    }

    //添加角色
    @ApiOperation("添加角色")
    @PreAuthorize("hasAuthority('bnt.sysRole.add')")
    @PostMapping("save")
    public Result<?> save(@RequestBody SysRole role) {
        boolean isSuccess = sysRoleService.save(role);
        if (isSuccess) {
            return Result.ok();
        }
        return Result.fail();
    }

    @ApiOperation("根据id查询")
    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @GetMapping("get/{id}")
    public Result<SysRole> get(@PathVariable Long id) {
        SysRole sysRole = sysRoleService.getById(id);
        return Result.ok(sysRole);
    }

    //修改角色
    @ApiOperation("修改角色")
    @PreAuthorize("hasAuthority('bnt.sysRole.update')")
    @PutMapping("update")
    public Result<?> update(@RequestBody SysRole role) {
        boolean isSuccess = sysRoleService.updateById(role);
        if (isSuccess) {
            return Result.ok();
        }
        return Result.fail();
    }

    @ApiOperation("删除角色")
    @PreAuthorize("hasAuthority('bnt.sysRole.remove')")
    @DeleteMapping("remove/{id}")
    public Result<?> remove(@PathVariable Long id) {
        boolean isSuccess = sysRoleService.removeById(id);
        if (isSuccess) {
            return Result.ok();
        }
        return Result.fail();
    }

    @ApiOperation("批量删除")
    @PreAuthorize("hasAuthority('bnt.sysRole.remove')")
    @DeleteMapping("batchRemove")
    public Result<?> batchRemove(@RequestBody List<Long> ids) {
        boolean isSuccess = sysRoleService.removeByIds(ids);
        if (isSuccess) {
            return Result.ok();
        }
        return Result.fail();
    }

    @ApiOperation("获取角色")
    @GetMapping("/toAssign/{userId}")
    public Result<?> toAssign(@PathVariable Long userId) {
        Map<String, Object> res = sysRoleService.findRoleDataByUserId(userId);
        return Result.ok(res);
    }

    @ApiOperation("为用户分配角色")
    @PostMapping("/doAssign")
    public Result<?> doAssign(@RequestBody AssignRoleVo assignRoleVo) {
        sysRoleService.doAssign(assignRoleVo);
        return Result.ok();
    }
}
