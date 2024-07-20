package org.lanjianghao.process.service;

import org.lanjianghao.model.process.ProcessType;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 审批类型 服务类
 * </p>
 *
 * @author lanjianghao
 * @since 2024-07-16
 */
public interface OaProcessTypeService extends IService<ProcessType> {

    List<ProcessType> findProcessTypes();
}
