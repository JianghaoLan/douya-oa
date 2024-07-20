package org.lanjianghao.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.lanjianghao.model.system.SysUser;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author lanjianghao
 * @since 2024-07-09
 */
@Repository
public interface SysUserMapper extends BaseMapper<SysUser> {

    @Update("update sys_user set status = #{status} where id = #{id}")
    int updateStatusById(@Param("id") Long id, @Param("status") Integer status);
}
