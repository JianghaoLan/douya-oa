package org.lanjianghao.process.controller.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.lanjianghao.auth.service.SysUserService;
import org.lanjianghao.common.result.Result;
import org.lanjianghao.model.system.SysUser;
import org.lanjianghao.security.custom.LoginUserInfoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(tags = "用户接口")
@RestController
@CrossOrigin
@RequestMapping("/admin/system/sysUser")
public class UserController {

    @Autowired
    SysUserService sysUserService;

    @ApiOperation("获取当前用户信息")
    @GetMapping("getCurrentUser")
    public Result<Map<String, Object>> getCurrentUser() {
        SysUser curUser = sysUserService.getById(LoginUserInfoHelper.getUserId());
        Map<String, Object> resp = new HashMap<>();
        resp.put("name", curUser.getName());
        resp.put("phone", curUser.getPhone());
        return Result.ok(resp);
    }
}
