package org.lanjianghao.process.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.activiti.bpmn.model.*;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.bpmn.parser.BpmnParser;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.lanjianghao.auth.service.SysUserService;
import org.lanjianghao.model.process.Process;
import org.lanjianghao.model.process.ProcessRecord;
import org.lanjianghao.model.process.ProcessTemplate;
import org.lanjianghao.model.system.SysUser;
import org.lanjianghao.process.activiti.RejectCmd;
import org.lanjianghao.process.mapper.OaProcessMapper;
import org.lanjianghao.process.service.OaProcessRecordService;
import org.lanjianghao.process.service.OaProcessService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lanjianghao.process.service.OaProcessTemplateService;
import org.lanjianghao.process.service.WeChatMessageService;
import org.lanjianghao.security.custom.LoginUserInfoHelper;
import org.lanjianghao.vo.process.ApprovalVo;
import org.lanjianghao.vo.process.ProcessFormVo;
import org.lanjianghao.vo.process.ProcessQueryVo;
import org.lanjianghao.vo.process.ProcessVo;
import org.lanjianghao.vo.system.LoginUserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author lanjianghao
 * @since 2024-07-17
 */
@Service
public class OaProcessServiceImpl extends ServiceImpl<OaProcessMapper, Process> implements OaProcessService {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private OaProcessTemplateService processTemplateService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskService taskService;

    @Autowired
    private OaProcessRecordService processRecordService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private ManagementService managementService;

    @Autowired
    private WeChatMessageService weChatMessageService;

    @Override
    public IPage<ProcessVo> pageByConditions(Page<ProcessVo> pageParam, ProcessQueryVo processQueryVo) {
        return baseMapper.selectPage(pageParam, processQueryVo);
    }

    @Override
    public void deployByZip(String zipPath, String name) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(zipPath);

        assert inputStream != null;
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        Deployment deployment = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .name(name)
                .deploy();
        System.out.println(deployment.getId());
        System.out.println(deployment.getName());
    }

    /**
     * 启动流程实例
     * @param processFormVo
     */
    @Override
    public void  startUp(ProcessFormVo processFormVo) {
        Long userId = LoginUserInfoHelper.getUserId();
        SysUser sysUser = sysUserService.getById(userId);

        ProcessTemplate template = processTemplateService.getById(processFormVo.getProcessTemplateId());

        Process process = new Process();
        BeanUtils.copyProperties(processFormVo, process);
        String workNo = System.currentTimeMillis() + "";
        process.setProcessCode(workNo);
        process.setUserId(LoginUserInfoHelper.getUserId());
        process.setTitle(sysUser.getName() + "发起" + template.getName() + "申请");
        process.setStatus(1);
        this.save(process);

        String defKey = template.getProcessDefinitionKey();
        String busKey = String.valueOf(process.getId());
        Map<String, Object> formValues;
        try {
            formValues = objectMapper.readValue(processFormVo.getFormValues(),
                    new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        ProcessInstance procInst = runtimeService.startProcessInstanceByKey(
                defKey, busKey, Collections.singletonMap("data", formValues));

        List<Task> tasks = this.getCurrentTasks(procInst.getId());

        List<String> assignees = tasks.stream().map(Task::getAssignee).collect(Collectors.toList());
        List<SysUser> sysUsers = sysUserService.listByUsernames(assignees);
        List<String> assigneeNames = sysUsers.stream().map(SysUser::getName).collect(Collectors.toList());

        weChatMessageService.pushPendingMessage(process.getId(), tasks);

        //更新process表中的process_instance_id
        Process forUpdate = new Process();
        forUpdate.setId(process.getId());
        forUpdate.setProcessInstanceId(procInst.getId());
        forUpdate.setDescription("等待" + String.join("，", assigneeNames) + "审批");
        updateById(forUpdate);

        //记录信息
        processRecordService.record(process.getId(), 1, "发起申请",
                userId, LoginUserInfoHelper.getUsername());
    }

    @Override
    public List<Task> getCurrentTasks(String id) {
        return taskService.createTaskQuery().processInstanceId(id).list();
    }

    @Override
    public IPage<ProcessVo> findPending(Page<ProcessVo> pageParam) {
        String username = LoginUserInfoHelper.getUsername();
        int pageSize = (int)pageParam.getSize();
        int offset = (int)(pageParam.getCurrent() - 1) * pageSize;
        List<Task> tasks = taskService.createTaskQuery()
                .taskAssignee(username)
                .listPage(offset, pageSize);

        List<Process> processes = this.findProcessesByTaskInfos(tasks);

        List<ProcessVo> processVos = this.buildProcessVos(processes, tasks);

        pageParam.setRecords(processVos);
        pageParam.setTotal(processVos.size());
        return pageParam;
    }

    @Override
    public Map<String, Object> show(Long id, String curUsername) {
        Process proc = this.getById(id);

        List<ProcessRecord> records = processRecordService.list(new LambdaQueryWrapper<ProcessRecord>()
                .eq(ProcessRecord::getProcessId, proc.getId()));

        ProcessTemplate template = processTemplateService.getById(proc.getProcessTemplateId());

        boolean isApprove = false;

        List<Task> curTasks = this.getCurrentTasks(proc.getProcessInstanceId());
        for (Task task : curTasks) {
            if (task.getAssignee().equals(curUsername)) {
                isApprove = true;
            }
        }

        Map<String, Object> res = new HashMap<>();
        res.put("process", proc);
        res.put("processRecordList", records);
        res.put("processTemplate", template);
        res.put("isApprove", isApprove);

        return res;
    }

    @Override
    public void approve(ApprovalVo approvalVo, LoginUserVo loginUser) {
        String taskId = approvalVo.getTaskId();
        Map<String, Object> vars = taskService.getVariables(taskId);
        for (Map.Entry<String, Object> entry : vars.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        }

        Process process = this.getById(approvalVo.getProcessId());

        if (approvalVo.getStatus() == 1) {
            Map<String, Object> variables = new HashMap<>();
            taskService.complete(taskId, variables);
            weChatMessageService.pushProcessedMessage(approvalVo.getProcessId(),
                    process.getUserId(), approvalVo.getStatus());
        } else {
            this.endTask(taskId);       //拒绝：直接结束流程
            weChatMessageService.pushProcessedMessage(approvalVo.getProcessId(),
                    process.getUserId(), approvalVo.getStatus());
        }

        String desc = approvalVo.getStatus() == 1 ? "已通过" : "已拒绝";
        processRecordService.record(approvalVo.getProcessId(), approvalVo.getStatus(), desc,
                loginUser.getUserId(), loginUser.getUsername());

        List<Task> curTasks = this.getCurrentTasks(process.getProcessInstanceId());
        Process forUpdate = new Process();
        forUpdate.setId(process.getId());
        if (!CollectionUtils.isEmpty(curTasks)) {       //流程还有剩余任务
            List<String> assignees = curTasks.stream().map(Task::getAssignee).collect(Collectors.toList());
            List<SysUser> sysUsers = sysUserService.listByUsernames(assignees);
            List<String> userNames = sysUsers.stream().map(SysUser::getName).collect(Collectors.toList());

            List<Long> userIds = sysUsers.stream().map(SysUser::getId).collect(Collectors.toList());
            weChatMessageService.pushPendingMessage(process.getId(), curTasks);

            //更新process流程信息
            forUpdate.setStatus(1);
            forUpdate.setDescription("等待" + String.join(",", userNames) + "审批");
        } else {        //流程完成
            if (approvalVo.getStatus() == 1) {
                //更新process流程信息
                forUpdate.setStatus(2);
                forUpdate.setDescription("审批完成（通过）");
            } else {
                //更新process流程信息
                forUpdate.setStatus(-1);
                forUpdate.setDescription("审批完成（驳回）");
            }
        }
        this.updateById(forUpdate);
    }

    @Override
    public IPage<ProcessVo> pageCompleted(Page<ProcessVo> pageParam, Long userId, String username) {
        int pageSize = (int)pageParam.getSize();
        int pageOffset = (int)(pageParam.getCurrent() - 1) * pageSize;
        List<? extends TaskInfo> tasks = historyService.createHistoricTaskInstanceQuery()
                .finished()
                .taskAssignee(username)
                .listPage(pageOffset, pageSize);

        List<Process> processes = findProcessesByTaskInfos(tasks);

        List<ProcessVo> processVos = buildProcessVos(processes, tasks);

        pageParam.setRecords(processVos);
        pageParam.setTotal(processVos.size());
        return pageParam;
    }

    @Override
    public IPage<ProcessVo> findOpened(Page<ProcessVo> pageParam, Long userId, String username) {
        ProcessQueryVo query = new ProcessQueryVo();
        query.setUserId(userId);
        return this.pageByConditions(pageParam, query);
    }

    private List<ProcessVo> buildProcessVos(List<Process> processes, List<? extends TaskInfo> tasks) {
        Map<String, ? extends TaskInfo> instId2Task = tasks.stream().collect(
                Collectors.toMap(TaskInfo::getProcessInstanceId, t -> t));
        return processes.stream().map(proc -> {
            ProcessVo vo = new ProcessVo();
            BeanUtils.copyProperties(proc, vo);
            TaskInfo curTask = instId2Task.get(proc.getProcessInstanceId());
            vo.setTaskId(curTask.getId());
            return vo;
        }).collect(Collectors.toList());
    }

    private List<Process> findProcessesByTaskInfos(List<? extends TaskInfo> tasks) {
        if (CollectionUtils.isEmpty(tasks)) {
            return Collections.emptyList();
        }

        Set<String> procInstIds = tasks.stream()
                .map(TaskInfo::getProcessInstanceId)
                .collect(Collectors.toSet());
//        List<ProcessInstance> procInstances = runtimeService.createProcessInstanceQuery()
//                .processInstanceIds(procInstIds).list();
//
//        List<Long> procIds = procInstances.stream()
//                .map(ProcessInstance::getBusinessKey)
//                .filter(StringUtils::hasLength)
//                .map(Long::valueOf)
//                .collect(Collectors.toList());
//        return this.list(new LambdaQueryWrapper<Process>()
//                .in(Process::getId, procIds)
//                .orderByDesc(Process::getCreateTime));

        LambdaQueryWrapper<Process> procQuery = new LambdaQueryWrapper<>();
        procQuery.in(Process::getProcessInstanceId, procInstIds)
                .orderByDesc(Process::getCreateTime);
        return this.list(procQuery);
    }

    private void endTask(String taskId) {
        managementService.executeCommand(new RejectCmd(taskId));

//        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
//
//        //得到BPMN模型
//        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
//
//        //得到结束结点
//        List<EndEvent> endEvents = bpmnModel.getMainProcess().findFlowElementsOfType(EndEvent.class);
//        if (CollectionUtils.isEmpty(endEvents)) {
//            return;
//        }
//        FlowNode endNode = endEvents.get(0);
//
//        //得到当前结点
//        FlowNode curNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(task.getTaskDefinitionKey());
//
//        //清除当前结点的出流
//        List<SequenceFlow> oriOutgoingFlows = new ArrayList<>(curNode.getOutgoingFlows());
//        curNode.getOutgoingFlows().clear();
//
//        //将当前结点指向结束结点
//        SequenceFlow seqFlow = new SequenceFlow();
//        seqFlow.setId("refuse");
//        seqFlow.setSourceFlowElement(curNode);
//        seqFlow.setTargetFlowElement(endNode);
//        curNode.setOutgoingFlows(Collections.singletonList(seqFlow));
//
//        //完成当前任务
//        taskService.complete(taskId);
////        historyService.deleteHistoricTaskInstance(taskId);
//
//        //恢复原始方向
//        //得到当前结点
//        curNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(task.getTaskDefinitionKey());
//        curNode.getOutgoingFlows().clear();
//        curNode.setOutgoingFlows(oriOutgoingFlows);
    }

}
