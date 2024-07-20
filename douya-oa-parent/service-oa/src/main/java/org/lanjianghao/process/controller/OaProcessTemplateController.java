package org.lanjianghao.process.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.lanjianghao.common.result.Result;
import org.lanjianghao.model.process.ProcessTemplate;
import org.lanjianghao.process.service.OaProcessTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * <p>
 * 审批模板 前端控制器
 * </p>
 *
 * @author lanjianghao
 * @since 2024-07-16
 */
@RestController
@RequestMapping("/admin/process/processTemplate")
public class OaProcessTemplateController {
    @Autowired
    private OaProcessTemplateService processTemplateService;

    @ApiOperation("获取分页审批模板数据")
    @GetMapping("{page}/{limit}")
    public Result<IPage<ProcessTemplate>> index(@PathVariable Long page, @PathVariable Long limit) {
        Page<ProcessTemplate> pageObj = new Page<>(page, limit);
        IPage<ProcessTemplate> resPageObj = processTemplateService.selectProcessTemplatePage(pageObj);
        return Result.ok(resPageObj);
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.list')")
    @ApiOperation(value = "获取")
    @GetMapping("get/{id}")
    public Result<ProcessTemplate> get(@PathVariable Long id) {
        ProcessTemplate processTemplate = processTemplateService.getById(id);
        return Result.ok(processTemplate);
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.templateSet')")
    @ApiOperation(value = "新增")
    @PostMapping("save")
    public Result<?> save(@RequestBody ProcessTemplate processTemplate) {
        processTemplateService.save(processTemplate);
        return Result.ok();
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.templateSet')")
    @ApiOperation(value = "修改")
    @PutMapping("update")
    public Result<?> updateById(@RequestBody ProcessTemplate processTemplate) {
        processTemplateService.updateById(processTemplate);
        return Result.ok();
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.remove')")
    @ApiOperation(value = "删除")
    @DeleteMapping("remove/{id}")
    public Result<?> remove(@PathVariable Long id) {
        processTemplateService.removeById(id);
        return Result.ok();
    }

    @ApiOperation(value = "上传流程定义")
    @PostMapping("/uploadProcessDefinition")
    public Result<Map<String, String>> uploadProcessDefinition(MultipartFile file) throws IOException {
        //获取classes目录位置
        String path = new File(ResourceUtils.getURL("classpath:").getPath()).getAbsolutePath();
        File uploadDir = new File(path + "/processes/");
        if (!uploadDir.exists()) {
            boolean ignored = uploadDir.mkdirs();
        }
        String filename = file.getOriginalFilename();
        if (filename == null) {
            filename = UUID.randomUUID() + ".zip";
        }
        File zipFile = new File(path + "/processes/" + filename);
        file.transferTo(zipFile);

        Map<String, String> map = new HashMap<>();
        //根据上传地址后续部署流程定义，文件名称为流程定义的默认key
        map.put("processDefinitionPath", "processes/" + filename);
        map.put("processDefinitionKey", filename.substring(0, filename.lastIndexOf(".")));
        return Result.ok(map);
    }

    @ApiOperation("发布")
    @GetMapping("/publish/{id}")
    public Result<?> publish(@PathVariable Long id) {
        processTemplateService.publish(id);
        return Result.ok();
    }
}
