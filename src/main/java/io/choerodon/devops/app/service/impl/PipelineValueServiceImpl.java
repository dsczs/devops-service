package io.choerodon.devops.app.service.impl;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.convertor.ConvertPageHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.devops.api.dto.PipelineValueDTO;
import io.choerodon.devops.app.service.PipelineValueService;
import io.choerodon.devops.domain.application.entity.DevopsEnvironmentE;
import io.choerodon.devops.domain.application.entity.PipelineAppDeployE;
import io.choerodon.devops.domain.application.entity.PipelineValueE;
import io.choerodon.devops.domain.application.entity.iam.UserE;
import io.choerodon.devops.domain.application.repository.DevopsEnvironmentRepository;
import io.choerodon.devops.domain.application.repository.IamRepository;
import io.choerodon.devops.domain.application.repository.PipelineAppDeployRepository;
import io.choerodon.devops.domain.application.repository.PipelineValueRepository;
import io.choerodon.devops.infra.common.util.EnvUtil;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Creator: ChangpingShi0213@gmail.com
 * Date:  10:01 2019/4/10
 * Description:
 */
@Service
public class PipelineValueServiceImpl implements PipelineValueService {
    @Autowired
    private PipelineValueRepository valueRepository;
    @Autowired
    private IamRepository iamRepository;
    @Autowired
    private EnvUtil envUtil;
    @Autowired
    private DevopsEnvironmentRepository devopsEnviromentRepository;
    @Autowired
    private PipelineAppDeployRepository appDeployRepository;

    @Override
    public PipelineValueDTO createOrUpdate(Long projectId, PipelineValueDTO pipelineValueDTO) {
        PipelineValueE pipelineValueE = ConvertHelper.convert(pipelineValueDTO, PipelineValueE.class);
        pipelineValueE.setProjectId(projectId);
        if (pipelineValueE.getId() == null) {
            valueRepository.checkName(projectId, pipelineValueE.getName());
        }
        pipelineValueE = valueRepository.createOrUpdate(pipelineValueE);
        return ConvertHelper.convert(pipelineValueE, PipelineValueDTO.class);
    }

    @Override
    public Boolean delete(Long projectId, Long valueId) {
        List<PipelineAppDeployE> list = appDeployRepository.queryByValueId(valueId);
        if (list == null) {
            return false;
        }
        valueRepository.delete(valueId);
        return true;
    }

    @Override
    public Page<PipelineValueDTO> listByOptions(Long projectId, Long appId, Long envId, PageRequest pageRequest, String params) {
        List<Long> connectedEnvList = envUtil.getConnectedEnvList();
        List<Long> updatedEnvList = envUtil.getUpdatedEnvList();

        Page<PipelineValueDTO> valueDTOS = ConvertPageHelper.convertPage(valueRepository.listByOptions(projectId, appId, envId, pageRequest, params), PipelineValueDTO.class);
        Page<PipelineValueDTO> page = new Page<>();
        BeanUtils.copyProperties(valueDTOS, page);
        page.setContent(valueDTOS.getContent().stream().peek(t -> {
            UserE userE = iamRepository.queryUserByUserId(t.getCreateBy());
            t.setCreateUserName(userE.getLoginName());
            t.setCreateUserUrl(userE.getImageUrl());
            t.setCreateUserRealName(userE.getRealName());
            DevopsEnvironmentE devopsEnvironmentE = devopsEnviromentRepository.queryById(t.getEnvId());
            if (connectedEnvList.contains(devopsEnvironmentE.getClusterE().getId())
                    && updatedEnvList.contains(devopsEnvironmentE.getClusterE().getId())) {
                t.setEnvStatus(true);
            }
        }).collect(Collectors.toList()));
        return page;
    }

    @Override
    public PipelineValueDTO queryById(Long valueId) {
        PipelineValueDTO valueDTO = ConvertHelper.convert(valueRepository.queryById(valueId), PipelineValueDTO.class);
        valueDTO.setIndex(appDeployRepository.queryByValueId(valueId) == null);
        return valueDTO;
    }

    @Override
    public void checkName(Long projectId, String name) {
        valueRepository.checkName(projectId, name);
    }

    @Override
    public List<PipelineValueDTO> queryByAppIdAndEnvId(Long projectId, Long appId, Long envId) {
        return ConvertHelper.convertList(valueRepository.queryByAppIdAndEnvId(projectId, appId, envId), PipelineValueDTO.class);
    }
}
