package org.lanjianghao.process.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.lanjianghao.model.process.ProcessTemplate;
import org.lanjianghao.model.process.ProcessType;
import org.lanjianghao.process.mapper.OaProcessTemplateMapper;
import org.lanjianghao.process.service.OaProcessService;
import org.lanjianghao.process.service.OaProcessTemplateService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lanjianghao.process.service.OaProcessTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 审批模板 服务实现类
 * </p>
 *
 * @author lanjianghao
 * @since 2024-07-16
 */
@Service
public class OaProcessTemplateServiceImpl extends ServiceImpl<OaProcessTemplateMapper, ProcessTemplate> implements OaProcessTemplateService {
    @Autowired
    OaProcessTypeService processTypeService;

    @Autowired
    OaProcessService processService;

    private void getAndFillProcessTypeNames(List<ProcessTemplate> records) {
        if (StringUtils.isEmpty(records)) {
            return;
        }
        List<Long> ids = records.stream().map(ProcessTemplate::getProcessTypeId).collect(Collectors.toList());

        List<ProcessType> types = processTypeService.list(
                new LambdaQueryWrapper<ProcessType>().in(ProcessType::getId, ids));
        Map<Long, String> id2Name = types.stream().collect(Collectors.toMap(ProcessType::getId, ProcessType::getName));
        records.forEach(record -> {
            Long typeId = record.getProcessTypeId();
            if (id2Name.containsKey(typeId)) {
                record.setProcessTypeName(id2Name.get(typeId));
            }
        });
    }

    @Override
    public IPage<ProcessTemplate> selectProcessTemplatePage(Page<ProcessTemplate> pageObj) {
        IPage<ProcessTemplate> resPage = page(pageObj);

        getAndFillProcessTypeNames(resPage.getRecords());

        return resPage;
    }

    @Override
    public void publish(Long id) {
        ProcessTemplate processTemplate = getById(id);
        processService.deployByZip(processTemplate.getProcessDefinitionPath(), processTemplate.getName());

        //更新部署状态
        ProcessTemplate forUpdate = new ProcessTemplate();
        forUpdate.setId(id);
        forUpdate.setStatus(1);
        updateById(forUpdate);
    }
}
