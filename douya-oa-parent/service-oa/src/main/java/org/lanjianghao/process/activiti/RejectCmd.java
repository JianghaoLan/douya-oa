package org.lanjianghao.process.activiti;

import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.ActivitiEngineAgenda;
import org.activiti.engine.impl.history.HistoryManager;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityManager;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityManager;
import org.activiti.engine.impl.util.ProcessDefinitionUtil;

import java.util.List;

public class RejectCmd implements Command<Void> {

    private final String taskId;

    public RejectCmd(String taskId) {
        this.taskId = taskId;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        ActivitiEngineAgenda agenda = commandContext.getAgenda();
        TaskEntityManager taskEntityManager = commandContext.getTaskEntityManager();
        TaskEntity taskEntity = taskEntityManager.findById(taskId);
        // 执行实例 id
        String executionId = taskEntity.getExecutionId();
        String processDefinitionId = taskEntity.getProcessDefinitionId();
        ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();
        HistoryManager historyManager = commandContext.getHistoryManager();
        // 执行实例对象
        ExecutionEntity executionEntity = executionEntityManager.findById(executionId);
        Process process = ProcessDefinitionUtil.getProcess(processDefinitionId);
        List<? extends FlowElement> endEvents = process.findFlowElementsOfType(EndEvent.class);
//        FlowElement targetFlowElement = process.getFlowElement(targetNodeId);
        if (endEvents.isEmpty()) {
            throw new RuntimeException("结束节点不存在");
        }
        FlowElement endEvent = endEvents.get(0);
        // 将历史活动表更新（当前活动结束）
        historyManager.recordActivityEnd(executionEntity, "reject");
        // 将目标节点设置为当前流程
        executionEntity.setCurrentFlowElement(endEvent);
        // 跳转, 触发执行实例运转
        agenda.planContinueProcessInCompensation(executionEntity);
        //删除任务涉及的变量（否则会报错）
        commandContext.getVariableInstanceEntityManager().deleteVariableInstanceByTask(taskEntity);
        // 从runtime 表中删除当前任务
        taskEntityManager.delete(taskId);
        // 将历史任务表更新, 历史任务标记为完成
        historyManager.recordTaskEnd(taskId, "reject");
        return null;
    }
}