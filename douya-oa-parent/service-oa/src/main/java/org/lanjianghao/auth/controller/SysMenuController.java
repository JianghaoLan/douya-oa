package org.lanjianghao.auth.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.lanjianghao.auth.service.SysMenuService;
import org.lanjianghao.common.result.Result;
import org.lanjianghao.model.system.SysMenu;
import org.lanjianghao.vo.system.AssignMenuVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 菜单表 前端控制器
 * </p>
 *
 * @author lanjianghao
 * @since 2024-07-09
 */
@Api(tags = "菜单管理接口")
@RestController
@RequestMapping("/admin/system/sysMenu")
public class SysMenuController {
    @Autowired
    private SysMenuService sysMenuService;

    @ApiOperation("查询所有菜单和角色分配的菜单")
    @GetMapping("toAssign/{roleId}")
    public Result<?> toAssign(@PathVariable Long roleId) {
        List<SysMenu> menus = sysMenuService.findMenuByRoleId(roleId);
        return Result.ok(menus);
    }

    @ApiOperation("角色分配菜单")
    @PostMapping("/doAssign")
    public Result<?> doAssign(@RequestBody AssignMenuVo assignMenuVo) {
        sysMenuService.doAssign(assignMenuVo);
        return Result.ok();
    }

    @ApiOperation("菜单列表")
    @GetMapping("findNodes")
    public Result<List<SysMenu>> findNodes() {
        List<SysMenu> res = sysMenuService.findNodes();
        return Result.ok(res);
    }

    @ApiOperation(value = "新增菜单")
    @PostMapping("save")
    public Result<?> save(@RequestBody SysMenu permission) {
        sysMenuService.save(permission);
        return Result.ok();
    }

    @ApiOperation(value = "修改菜单")
    @PutMapping("update")
    public Result<?> updateById(@RequestBody SysMenu permission) {
        sysMenuService.updateById(permission);
        return Result.ok();
    }

    @ApiOperation(value = "删除菜单")
    @DeleteMapping("remove/{id}")
    public Result<?> remove(@PathVariable Long id) {
        sysMenuService.removeMenuById(id);
        return Result.ok();
    }
}
