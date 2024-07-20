package org.lanjianghao.auth.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.lanjianghao.auth.service.SysUserService;
import org.lanjianghao.common.result.Result;
import org.lanjianghao.common.utils.MD5;
import org.lanjianghao.model.system.SysUser;
import org.lanjianghao.vo.system.SysUserQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author lanjianghao
 * @since 2024-07-09
 */
@Api(tags = "用户管理接口")
@RestController
@RequestMapping("/admin/system/sysUser")
public class SysUserController {
    @Autowired
    private SysUserService service;

    @ApiOperation("用户条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result<?> index(@PathVariable Long page,
                           @PathVariable Long limit,
                           SysUserQueryVo sysUserQueryVo) {
        Page<SysUser> pageParam = new Page<>(page, limit);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();

        String username = sysUserQueryVo.getKeyword();
        String createTimeBegin = sysUserQueryVo.getCreateTimeBegin();
        String createTimeEnd = sysUserQueryVo.getCreateTimeEnd();
        Long postId = sysUserQueryVo.getPostId();
        Long deptId = sysUserQueryVo.getDeptId();
        if (StringUtils.hasLength(username)) {
            wrapper.like(SysUser::getUsername, username);
        }
        if (StringUtils.hasLength(createTimeBegin)) {
            wrapper.ge(SysUser::getCreateTime, createTimeBegin);
        }
        if (StringUtils.hasLength(createTimeEnd)) {
            wrapper.le(SysUser::getCreateTime, createTimeEnd);
        }
        if (postId != null) {
            wrapper.eq(SysUser::getPostId, postId);
        }
        if (deptId != null) {
            wrapper.eq(SysUser::getDeptId, deptId);
        }

        IPage<SysUser> pageModel = service.page(pageParam, wrapper);

        return Result.ok(pageModel);
    }

    @ApiOperation(value = "获取用户")
    @GetMapping("get/{id}")
    public Result<?> get(@PathVariable Long id) {
        SysUser user = service.getById(id);
        return Result.ok(user);
    }

    @ApiOperation(value = "保存用户")
    @PostMapping("save")
    public Result<?> save(@RequestBody SysUser user) {
        //密码加密
        user.setPassword(MD5.encrypt(user.getPassword()));

        service.save(user);
        return Result.ok();
    }

    @ApiOperation(value = "更新用户")
    @PutMapping("update")
    public Result<?> updateById(@RequestBody SysUser user) {
        service.updateById(user);
        return Result.ok();
    }

    @ApiOperation(value = "删除用户")
    @DeleteMapping("remove/{id}")
    public Result<?> remove(@PathVariable Long id) {
        service.removeById(id);
        return Result.ok();
    }

    @ApiOperation(value = "更新状态")
    @GetMapping("updateStatus/{id}/{status}")
    public Result<?> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        service.updateStatus(id, status);
        return Result.ok();
    }
}

