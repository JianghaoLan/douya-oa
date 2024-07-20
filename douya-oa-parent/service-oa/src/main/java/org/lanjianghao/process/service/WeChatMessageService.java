package org.lanjianghao.process.service;

import org.activiti.engine.task.Task;

import java.util.List;

public interface WeChatMessageService {

    /**
     * 推送待审批人员
     *
     * @param processId
     * @param tasks
     */
    void pushPendingMessage(Long processId, List<Task> tasks);
    /**
     * 审批后推送提交审批人员
     * @param processId
     * @param userId
     * @param status
     */
    void pushProcessedMessage(Long processId, Long userId, Integer status);
}
