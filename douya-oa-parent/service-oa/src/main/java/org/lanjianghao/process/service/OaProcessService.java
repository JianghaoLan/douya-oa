package org.lanjianghao.process.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.activiti.engine.task.Task;
import org.lanjianghao.model.process.Process;
import org.lanjianghao.vo.process.ApprovalVo;
import org.lanjianghao.vo.process.ProcessFormVo;
import org.lanjianghao.vo.process.ProcessQueryVo;
import org.lanjianghao.vo.process.ProcessVo;
import org.lanjianghao.vo.system.LoginUserVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 审批类型 服务类
 * </p>
 *
 * @author lanjianghao
 * @since 2024-07-17
 */
public interface OaProcessService extends IService<Process> {

    IPage<ProcessVo> pageByConditions(Page<ProcessVo> pageParam, ProcessQueryVo processQueryVo);

    //部署流程定义
    void deployByZip(String zipPath, String name);

    void startUp(ProcessFormVo processFormVo);

    List<Task> getCurrentTasks(String id);

    IPage<ProcessVo> findPending(Page<ProcessVo> pageParam);

    Map<String, Object> show(Long id, String username);

    void approve(ApprovalVo approvalVo, LoginUserVo loginUser);

    IPage<ProcessVo> pageCompleted(Page<ProcessVo> pageParam, Long userId, String username);

    IPage<ProcessVo> findOpened(Page<ProcessVo> pageParam, Long userId, String username);
}
