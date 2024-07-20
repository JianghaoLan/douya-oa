package org.lanjianghao.process.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.lanjianghao.model.process.ProcessTemplate;
import org.lanjianghao.model.process.ProcessType;
import org.lanjianghao.process.mapper.OaProcessTypeMapper;
import org.lanjianghao.process.service.OaProcessTemplateService;
import org.lanjianghao.process.service.OaProcessTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author lanjianghao
 * @since 2024-07-16
 */
@Service
public class OaProcessTypeServiceImpl extends ServiceImpl<OaProcessTypeMapper, ProcessType> implements OaProcessTypeService {

    @Autowired
    private OaProcessTemplateService processTemplateService;

    @Override
    public List<ProcessType> findProcessTypes() {
        List<ProcessType> types = this.list();
        Map<Long, ProcessType> typeMap = types.stream()
                .peek(t -> t.setProcessTemplateList(new ArrayList<>()))
                .collect(Collectors.toMap(ProcessType::getId, t -> t));
        List<ProcessTemplate> templates = processTemplateService.list();
        templates.forEach(t -> {
            ProcessType type = typeMap.get(t.getProcessTypeId());
            if (type != null) {
                type.getProcessTemplateList().add(t);
            }
        });
        return types;
    }
}
