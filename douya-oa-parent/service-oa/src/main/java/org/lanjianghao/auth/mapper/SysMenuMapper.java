package org.lanjianghao.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.lanjianghao.model.system.SysMenu;

import java.util.List;

/**
 * <p>
 * 菜单表 Mapper 接口
 * </p>
 *
 * @author lanjianghao
 * @since 2024-07-09
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<SysMenu> selectActiveMenusByUserId(@Param("userId") Long userId, @Param("sort") boolean sort);
}
