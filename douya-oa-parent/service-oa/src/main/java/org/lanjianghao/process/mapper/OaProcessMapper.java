package org.lanjianghao.process.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.lanjianghao.model.process.Process;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.lanjianghao.vo.process.ProcessQueryVo;
import org.lanjianghao.vo.process.ProcessVo;

/**
 * <p>
 * 审批类型 Mapper 接口
 * </p>
 *
 * @author lanjianghao
 * @since 2024-07-17
 */
public interface OaProcessMapper extends BaseMapper<Process> {
    IPage<ProcessVo> selectPage(IPage<ProcessVo> pageParam, @Param("query") ProcessQueryVo query);
}
