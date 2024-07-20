package org.lanjianghao.process.controller.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.lanjianghao.common.result.Result;
import org.lanjianghao.model.process.Process;
import org.lanjianghao.model.process.ProcessTemplate;
import org.lanjianghao.model.process.ProcessType;
import org.lanjianghao.process.service.OaProcessRecordService;
import org.lanjianghao.process.service.OaProcessService;
import org.lanjianghao.process.service.OaProcessTemplateService;
import org.lanjianghao.process.service.OaProcessTypeService;
import org.lanjianghao.security.custom.LoginUserInfoHelper;
import org.lanjianghao.vo.process.ApprovalVo;
import org.lanjianghao.vo.process.ProcessFormVo;
import org.lanjianghao.vo.process.ProcessVo;
import org.lanjianghao.vo.system.LoginUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "审批流管理")
@RestController
@RequestMapping(value="/admin/process")
@CrossOrigin
public class ProcessController {
    @Autowired
    private OaProcessTypeService processTypeService;

    @Autowired
    private OaProcessTemplateService processTemplateService;

    @Autowired
    private OaProcessService processService;

    @GetMapping("findProcessType")
    public Result<?> findProcessTypes() {
        List<ProcessType> types = processTypeService.findProcessTypes();
        return Result.ok(types);
    }

    @ApiOperation("获取审批模板数据")
    @GetMapping("getProcessTemplate/{processTemplateId}")
    public Result<ProcessTemplate> getProcessTemplate(@PathVariable Long processTemplateId) {
        ProcessTemplate temp = processTemplateService.getById(processTemplateId);
        return Result.ok(temp);
    }

    @ApiOperation("启动流程示例")
    @PostMapping("startUp")
    public Result<?> startUp(@RequestBody ProcessFormVo processFormVo) {
        processService.startUp(processFormVo);
        return Result.ok();
    }

    @ApiOperation(value = "待处理")
    @GetMapping("/findPending/{page}/{limit}")
    public Result<IPage<ProcessVo>> findPending(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,

            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit) {
        Page<ProcessVo> pageParam = new Page<>(page, limit);
        IPage<ProcessVo> pageModel = processService.findPending(pageParam);
        return Result.ok(pageModel);
    }

    @ApiOperation(value = "查看审批详情")
    @GetMapping("show/{id}")
    public Result<Map<String, Object>> show(@PathVariable Long id) {
        Map<String, Object> map = processService.show(id, LoginUserInfoHelper.getUsername());
        return Result.ok(map);
    }

    @ApiOperation(value = "审批")
    @PostMapping("approve")
    public Result<?> approve(@RequestBody ApprovalVo approvalVo) {
        LoginUserVo loginUser = new LoginUserVo();
        loginUser.setUserId(LoginUserInfoHelper.getUserId());
        loginUser.setUsername(LoginUserInfoHelper.getUsername());

        processService.approve(approvalVo, loginUser);
        return Result.ok();
    }

    @ApiOperation(value = "已处理")
    @GetMapping("/findProcessed/{page}/{limit}")
    public Result<IPage<ProcessVo>> findProcessed(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,
            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit) {
        Page<ProcessVo> pageParam = new Page<>(page, limit);
        Long userId = LoginUserInfoHelper.getUserId();
        String username = LoginUserInfoHelper.getUsername();
        IPage<ProcessVo> resPage = processService.pageCompleted(pageParam, userId, username);
        return Result.ok(resPage);
    }

    @ApiOperation(value = "已发起")
    @GetMapping("/findStarted/{page}/{limit}")
    public Result<IPage<ProcessVo>> findStarted(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,
            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit) {
        Page<ProcessVo> pageParam = new Page<>(page, limit);
        Long userId = LoginUserInfoHelper.getUserId();
        String username = LoginUserInfoHelper.getUsername();
        IPage<ProcessVo> resPage = processService.findOpened(pageParam, userId, username);
        return Result.ok(resPage);
    }
}
