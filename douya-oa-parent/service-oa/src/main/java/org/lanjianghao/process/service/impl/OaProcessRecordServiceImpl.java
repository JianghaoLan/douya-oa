package org.lanjianghao.process.service.impl;

import org.lanjianghao.model.process.ProcessRecord;
import org.lanjianghao.process.mapper.OaProcessRecordMapper;
import org.lanjianghao.process.service.OaProcessRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 审批记录 服务实现类
 * </p>
 *
 * @author lanjianghao
 * @since 2024-07-17
 */
@Service
public class OaProcessRecordServiceImpl extends ServiceImpl<OaProcessRecordMapper, ProcessRecord> implements OaProcessRecordService {

    @Override
    public void record(Long processId, Integer status, String description, Long userId, String userName) {
        ProcessRecord record = new ProcessRecord();
        record.setProcessId(processId);
        record.setStatus(status);
        record.setDescription(description);
        record.setOperateUser(userName);
        record.setOperateUserId(userId);
        this.save(record);
    }
}
