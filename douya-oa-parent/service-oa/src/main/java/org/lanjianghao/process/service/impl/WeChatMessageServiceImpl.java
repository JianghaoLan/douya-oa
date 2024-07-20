package org.lanjianghao.process.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.lanjianghao.auth.service.SysUserService;
import org.lanjianghao.model.process.Process;
import org.lanjianghao.model.process.ProcessTemplate;
import org.lanjianghao.model.system.SysUser;
import org.lanjianghao.process.service.OaProcessService;
import org.lanjianghao.process.service.OaProcessTemplateService;
import org.lanjianghao.process.service.WeChatMessageService;
import org.lanjianghao.wechat.config.WeChatMpConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WeChatMessageServiceImpl implements WeChatMessageService {

    @Autowired
    private OaProcessService processService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private OaProcessTemplateService processTemplateService;

    @Autowired
    private WxMpService wxMpService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WeChatMpConfigurationProperties.Templates templates;

    @Value("${network.front-end-public-address}")
    private String publicAddress;

    @Override
    public void pushPendingMessage(Long processId, List<Task> tasks) {
        List<String> usernames = tasks.stream().map(Task::getAssignee).collect(Collectors.toList());
        List<SysUser> assignees = sysUserService.listByUsernames(usernames);

        //如果推送人没有绑定微信，则不推送
        assignees = assignees.stream().filter(u -> StringUtils.hasLength(u.getOpenId())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(assignees)) {
            return;
        }
        Map<String, String> username2TaskId = tasks.stream().collect(
                Collectors.toMap(TaskInfo::getAssignee, Task::getId));

        //流程信息
        Process process = processService.getById(processId);

        //获取流程创建人信息
        SysUser initiator = sysUserService.getById(process.getUserId());

        //流程模板信息
        ProcessTemplate processTemplate = processTemplateService.getById(process.getProcessTemplateId());

        WeChatMpConfigurationProperties.Template pendingMessageTemplate = templates.getPendingMessageTemplate();

        for (SysUser assignee : assignees) {
            String taskId = username2TaskId.get(assignee.getUsername());
            Map<String, Object> uriVars = new HashMap<>();
            uriVars.put("processId", processId);
            uriVars.put("taskId", taskId);
            String url = UriComponentsBuilder
                    .fromHttpUrl(publicAddress)
                    .path(pendingMessageTemplate.getPath())
                    .uriVariables(uriVars)
                    .build().toString();

            WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                    .toUser(assignee.getOpenId())
                    .templateId(pendingMessageTemplate.getTemplateId())
                    .url(url)
                    .build();
            log.info("给" + assignee.getName() + "推送待审批消息，URL: " + url);

            DateFormat dateFormat = objectMapper.getDateFormat();
            String content = parseContentFromFormValues(process.getFormValues());
            //设置模板参数值
            templateMessage.addData(new WxMpTemplateData("first",
                    initiator.getName() + "提交" + processTemplate.getName() + "，需要您审批", "#272727"));
            templateMessage.addData(new WxMpTemplateData("keyword1", process.getProcessCode()));
            templateMessage.addData(new WxMpTemplateData("keyword2", dateFormat.format(process.getCreateTime()), "#272727"));
            templateMessage.addData(new WxMpTemplateData("content", content, "#272727"));
            String msg;
            try {
                msg = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
            } catch (WxErrorException e) {
                throw new RuntimeException(e);
            }
            log.info("推送消息返回：{}", msg);
        }
    }

    private String parseContentFromFormValues(String formValues) {
        JSONObject jsonObject = JSON.parseObject(formValues);
        JSONObject jsonShowData = jsonObject.getJSONObject("formShowData");
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : jsonShowData.entrySet()) {
            sb.append(entry.getKey()).append("：").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public void pushProcessedMessage(Long processId, Long userId, Integer status) {
        //推送人信息
        SysUser assignee = sysUserService.getById(userId);
        //如果推送人没有绑定微信，则不推送
        if (StringUtils.isEmpty(assignee.getOpenId())) {
            return;
        }

        //流程信息
        Process process = processService.getById(processId);

        //获取流程创建人信息
        SysUser initiator = sysUserService.getById(process.getUserId());

        //流程模板信息
        ProcessTemplate processTemplate = processTemplateService.getById(process.getProcessTemplateId());

        WeChatMpConfigurationProperties.Template processedMessageTemplate = templates.getProcessedMessageTemplate();

        Map<String, Object> uriVars = new HashMap<>();
        uriVars.put("processId", processId);
        uriVars.put("taskId", 0);
        String url = UriComponentsBuilder
                .fromHttpUrl(publicAddress)
                .path(processedMessageTemplate.getPath())
                .uriVariables(uriVars)
                .build().toString();
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser(assignee.getOpenId())
                .templateId(processedMessageTemplate.getTemplateId())
                .url(url)
                .build();
        log.info("给" + assignee.getName() + "推送已审批消息，URL: " + url);

        DateFormat dateFormat = objectMapper.getDateFormat();
        String content = parseContentFromFormValues(process.getFormValues());
        String statusData = status == 1 ? "审批通过" : "审批拒绝";
        String statusColor = status == 1 ? "#009966" : "#FF0033";
        //设置模板参数值
        templateMessage.addData(new WxMpTemplateData("first",
                "您" + (status == 1 ? "通过" : "拒绝") + "了" +
                        initiator.getName() + "提交的" + processTemplate.getName(), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword1", process.getProcessCode()));
        templateMessage.addData(new WxMpTemplateData("keyword2", dateFormat.format(process.getCreateTime()), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword3", assignee.getName(), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword4", statusData, statusColor));
        templateMessage.addData(new WxMpTemplateData("content", content, "#272727"));
        String msg;
        try {
            msg = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
        log.info("推送消息返回：{}", msg);
    }
}
