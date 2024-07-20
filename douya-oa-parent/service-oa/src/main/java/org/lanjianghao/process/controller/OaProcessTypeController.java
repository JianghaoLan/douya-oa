package org.lanjianghao.process.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.lanjianghao.common.result.Result;
import org.lanjianghao.model.process.ProcessType;
import org.lanjianghao.process.service.OaProcessTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 审批类型 前端控制器
 * </p>
 *
 * @author lanjianghao
 * @since 2024-07-16
 */
@RestController
@RequestMapping("/admin/process/processType")
public class OaProcessTypeController {
    @Autowired
    private OaProcessTypeService processTypeService;

    @ApiOperation(value = "获取分页插件")
    @GetMapping("{page}/{limit}")
    public Result<IPage<ProcessType>> index(@PathVariable Long page, @PathVariable Long limit) {
        Page<ProcessType> pageObj = new Page<>(page, limit);
        IPage<ProcessType> resPageObj = processTypeService.page(pageObj);
        return Result.ok(resPageObj);
    }

    @ApiOperation(value = "获取类型列表")
    @GetMapping("findAll")
    public Result<List<ProcessType>> findAll() {
        return Result.ok(processTypeService.list());
    }

//    @PreAuthorize("hasAuthority('bnt.processType.list')")
    @ApiOperation(value = "获取")
    @GetMapping("get/{id}")
    public Result<ProcessType> get(@PathVariable Long id) {
        ProcessType processType = processTypeService.getById(id);
        return Result.ok(processType);
    }

//    @PreAuthorize("hasAuthority('bnt.processType.add')")
    @ApiOperation(value = "新增")
    @PostMapping("save")
    public Result<?> save(@RequestBody ProcessType processType) {
        processTypeService.save(processType);
        return Result.ok();
    }

//    @PreAuthorize("hasAuthority('bnt.processType.update')")
    @ApiOperation(value = "修改")
    @PutMapping("update")
    public Result<?> updateById(@RequestBody ProcessType processType) {
        processTypeService.updateById(processType);
        return Result.ok();
    }

//    @PreAuthorize("hasAuthority('bnt.processType.remove')")
    @ApiOperation(value = "删除")
    @DeleteMapping("remove/{id}")
    public Result<?> remove(@PathVariable Long id) {
        processTypeService.removeById(id);
        return Result.ok();
    }
}

