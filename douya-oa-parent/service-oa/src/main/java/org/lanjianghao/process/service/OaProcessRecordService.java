package org.lanjianghao.process.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lanjianghao.model.process.ProcessRecord;

/**
 * <p>
 * 审批记录 服务类
 * </p>
 *
 * @author lanjianghao
 * @since 2024-07-17
 */
public interface OaProcessRecordService extends IService<ProcessRecord> {
    void record(Long processId, Integer status, String description, Long userId, String userName);
}
