package org.lanjianghao.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.jsonwebtoken.Jwt;
import io.swagger.annotations.Api;
import org.lanjianghao.auth.service.SysMenuService;
import org.lanjianghao.auth.service.SysUserService;
import org.lanjianghao.common.config.exception.BusinessException;
import org.lanjianghao.common.jwt.JwtHelper;
import org.lanjianghao.common.result.Result;
import org.lanjianghao.common.utils.MD5;
import org.lanjianghao.model.system.SysUser;
import org.lanjianghao.vo.system.LoginVo;
import org.lanjianghao.vo.system.RouterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.lanjianghao.common.result.ResultCodeEnum.FAIL;

@Api(tags = "后台登录管理")
@RestController
@RequestMapping("/admin/system/index")
public class IndexController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysMenuService sysMenuService;

    @PostMapping("login")
    public Result<?> login(@RequestBody LoginVo loginVo) {
//        Map<String, Object> map = new HashMap<>();
//        map.put("token", "admin-token");
//        return Result.ok(map);

        //查找用户
        String username = loginVo.getUsername();
        SysUser user = sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));

        //判断用户是否存在
        if (user == null) {
            throw new BusinessException(FAIL.getCode(), "用户不存在");
        }

        //判断密码是否正确
        String password = loginVo.getPassword();
        if (!MD5.encrypt(password).equals(user.getPassword())) {
            throw new BusinessException(FAIL.getCode(), "密码错误");
        }

        //判断用户是否被禁用
        if (user.getStatus() == 0) {
            throw new BusinessException(FAIL.getCode(), "用户已经被禁用");
        }

        //生成jwt token
        String token = JwtHelper.createToken(user.getId(), user.getUsername());

        Map<String, String> res = Collections.singletonMap("token", token);
        return Result.ok(res);
    }

    /**
     * 获取用户信息
     * @return
     */
    @GetMapping("info")
    public Result<?> info(HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtHelper.getUserId(token);
//        Long userId = 2L;
        SysUser user = sysUserService.getById(userId);
        if (user == null) {
            throw new BusinessException(FAIL.getCode(), "用户不存在");
        }
        List<RouterVo> routers = sysMenuService.findMenusByUserId(userId);
        List<String> perms = sysMenuService.findPermsByUserId(userId);

        Map<String, Object> map = new HashMap<>();
        map.put("roles","[admin]");
        map.put("name", user.getName());
        map.put("avatar","https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
        map.put("routers", routers);
        map.put("buttons", perms);
        return Result.ok(map);
    }
    /**
     * 退出
     * @return
     */
    @PostMapping("logout")
    public Result<?> logout(){
        return Result.ok();
    }
}
