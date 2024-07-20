package org.lanjianghao.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lanjianghao.model.system.SysMenu;
import org.lanjianghao.vo.system.AssignMenuVo;
import org.lanjianghao.vo.system.RouterVo;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author lanjianghao
 * @since 2024-07-09
 */
public interface SysMenuService extends IService<SysMenu> {

    List<SysMenu> findNodes();

    void removeMenuById(Long id);

    List<SysMenu> findMenuByRoleId(Long roleId);

    void doAssign(AssignMenuVo assignMenuVo);

    List<RouterVo> findMenusByUserId(Long userId);

    List<String> findPermsByUserId(Long userId);
}
