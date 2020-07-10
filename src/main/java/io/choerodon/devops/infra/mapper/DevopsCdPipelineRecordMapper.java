package io.choerodon.devops.infra.mapper;

import java.util.List;

import io.choerodon.devops.api.vo.DevopsCdPipelineRecordVO;
import io.choerodon.devops.infra.dto.DevopsCdPipelineRecordDTO;
import io.choerodon.mybatis.common.BaseMapper;

public interface DevopsCdPipelineRecordMapper extends BaseMapper<DevopsCdPipelineRecordDTO> {
    List<DevopsCdPipelineRecordVO> listByCiPipelineId(Long pipelineId);
}
