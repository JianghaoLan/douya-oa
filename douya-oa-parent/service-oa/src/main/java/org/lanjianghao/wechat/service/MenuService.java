package org.lanjianghao.wechat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lanjianghao.model.wechat.Menu;
import org.lanjianghao.vo.wechat.MenuVo;

import java.util.List;

/**
 * <p>
 * 菜单 服务类
 * </p>
 *
 * @author lanjianghao
 * @since 2024-07-18
 */
public interface MenuService extends IService<Menu> {

    List<MenuVo> findMenuInfo();

    void syncMenu();

    void removeMenu();
}
