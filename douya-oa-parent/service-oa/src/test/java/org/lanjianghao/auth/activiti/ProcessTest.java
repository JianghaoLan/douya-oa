package org.lanjianghao.auth.activiti;

import io.github.classgraph.BaseTypeSignature;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ProcessTest {
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Test
    public void deployProcess() {
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("process/leave-request.bpmn20.xml")
                .addClasspathResource("process/leave-request.svg")
                .name("请假申请流程")
                .deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }

    @Test
    public void startProcess() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("leave-request");
//        ProcessInstance processInstance = runtimeService.startProcessInstanceByMessage("leave-request");
        System.out.println("流程定义Id" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例Id" + processInstance.getId());
        System.out.println("流程活动Id" + processInstance.getActivityId());
    }

    @Test
    public void findTasks() {
        String assign = "zhangsan";
        List<Task> tasks = taskService.createTaskQuery()
                .taskAssignee(assign).list();
        for (Task task : tasks) {
            System.out.println("流程实例Id：" + task.getProcessInstanceId());
            System.out.println("任务Id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        }
    }

    @Test
    public void completeTask() {
        String assign = "zhangsan";
        Task task = taskService.createTaskQuery()
                .taskAssignee(assign).singleResult();
        taskService.complete(task.getId());
    }

    @Test
    public void findCompleteTasks() {
        List<HistoricTaskInstance> instances = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee("lisi")
                .finished().list();
        for (HistoricTaskInstance inst : instances) {
            System.out.println("实例Id：" + inst.getProcessInstanceId());
            System.out.println("任务Id：" + inst.getId());
            System.out.println("任务负责人：" + inst.getAssignee());
            System.out.println("任务名称：" + inst.getName());
        }
    }

    @Test
    public void suspendAllProcessInstances() {
        ProcessDefinition procDef = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("leave-request").singleResult();
        boolean suspended = procDef.isSuspended();
        if (suspended) {
            repositoryService.activateProcessDefinitionById(procDef.getId(),
                    true, null);
            System.out.println(procDef.getId() + "已激活");
        } else {
            repositoryService.suspendProcessDefinitionById(procDef.getId(),
                    true, null);
            System.out.println(procDef.getId() + "已挂起");
        }
    }

    @Test
    public void suspend() {

    }
}
