package org.lanjianghao.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.lanjianghao.auth.mapper.SysMenuMapper;
import org.lanjianghao.auth.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lanjianghao.auth.service.SysRoleMenuService;
import org.lanjianghao.auth.utils.MenuHelper;
import org.lanjianghao.common.config.exception.BusinessException;
import org.lanjianghao.model.system.SysMenu;
import org.lanjianghao.model.system.SysRoleMenu;
import org.lanjianghao.vo.system.AssignMenuVo;
import org.lanjianghao.vo.system.MetaVo;
import org.lanjianghao.vo.system.RouterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.lanjianghao.common.result.ResultCodeEnum.FAIL;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author lanjianghao
 * @since 2024-07-09
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    SysRoleMenuService sysRoleMenuService;

    @Override
    public List<SysMenu> findNodes() {
        List<SysMenu> list = this.list();
        return MenuHelper.buildTree(list);
    }

    @Override
    public void removeMenuById(Long id) {
        //判断是否有子菜单
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId, id);
        int count = count(wrapper);
        if (count > 0) {
            throw new BusinessException(FAIL.getCode(), "含有子菜单，无法删除");
        }
        removeById(id);
    }

    @Override
    public List<SysMenu> findMenuByRoleId(Long roleId) {
        //查询角色的菜单ID
        List<SysRoleMenu> roleMenus = sysRoleMenuService.list(
                new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        Set<Long> roleMenuIds = roleMenus.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toSet());

        //查询可用的所有菜单
        List<SysMenu> allMenus = this.list(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getStatus, 1));

        //封装菜单列表
        for (SysMenu menu : allMenus) {
            menu.setSelect(roleMenuIds.contains(menu.getId()));
        }

        return MenuHelper.buildTree(allMenus);
    }

    @Override
    public void doAssign(AssignMenuVo assignMenuVo) {
        Long roleId = assignMenuVo.getRoleId();
        List<Long> menuIds = assignMenuVo.getMenuIdList();

        //删除角色的所有菜单
        sysRoleMenuService.remove(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));

        //保存角色-菜单关系
        List<SysRoleMenu> roleMenus = menuIds.stream().map(menuId -> {
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setRoleId(roleId);
            sysRoleMenu.setMenuId(menuId);
            return sysRoleMenu;
        }).collect(Collectors.toList());
        sysRoleMenuService.saveBatch(roleMenus);
    }

    private List<SysMenu> findActiveMenus(boolean sort) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getStatus, 1);
        if (sort) {
            wrapper.orderByAsc(SysMenu::getSortValue);
        }
        return list(wrapper);
    }

    @Override
    public List<RouterVo> findMenusByUserId(Long userId) {
        List<SysMenu> menus;
        //id为1为管理员，找到所有statue为1的菜单
        if (userId == 1) {
            menus = findActiveMenus(true);
        } else {
            menus = this.baseMapper.selectActiveMenusByUserId(userId, true);
        }

        menus = MenuHelper.buildTree(menus);
        return buildRouterVos(menus);
    }

    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    private String getRouterPath(SysMenu menu) {
        String routerPath = "/" + menu.getPath();
        if(menu.getParentId().intValue() != 0) {
            routerPath = menu.getPath();
        }
        return routerPath;
    }

    private RouterVo getBaseRouterVo(SysMenu menu) {
        RouterVo router = new RouterVo();
        router.setPath(getRouterPath(menu));
        router.setComponent(menu.getComponent());
        MetaVo meta = new MetaVo();
        meta.setIcon(menu.getIcon());
        meta.setTitle(menu.getName());
        router.setMeta(meta);
        router.setAlwaysShow(false);
        router.setHidden(false);
        return router;
    }

    private List<RouterVo> buildRouterVos(List<SysMenu> menus) {
        List<RouterVo> hiddenRouters = new ArrayList<>();
        List<RouterVo> routers = menus.stream().map(menu -> {
            RouterVo router = getBaseRouterVo(menu);
            if (menu.getType() < 1) {  //因为1为二级菜单，孩子为2，是按钮或隐藏路由，无需递归
                if (!CollectionUtils.isEmpty(menu.getChildren())) {
                    router.setChildren(buildRouterVos(menu.getChildren()));  //递归
                    router.setAlwaysShow(true);
                }
            } else {    //找到所有隐藏2级路由
                List<SysMenu> hiddenMenus = menu.getChildren().stream()
                        .filter(child -> StringUtils.hasLength(child.getComponent()))
                        .collect(Collectors.toList());
                hiddenRouters.addAll(hiddenMenus.stream().map(hiddenMenu -> {
                    RouterVo r = getBaseRouterVo(hiddenMenu);
                    r.setHidden(true);
                    return r;
                }).collect(Collectors.toList()));
            }
            return router;
        }).collect(Collectors.toList());
        routers.addAll(hiddenRouters);
        return routers;
    }

    @Override
    public List<String> findPermsByUserId(Long userId) {
        List<SysMenu> menus;
        //id为1为管理员，找到所有statue为1的菜单
        if (userId == 1) {
            menus = findActiveMenus(false);
        } else {
            menus = this.baseMapper.selectActiveMenusByUserId(userId, false);
        }

        return menus.stream()
                .filter(menu -> menu.getType() == 2)
                .map(SysMenu::getPerms)
                .filter(StringUtils::hasLength)
                .collect(Collectors.toList());
    }
}
