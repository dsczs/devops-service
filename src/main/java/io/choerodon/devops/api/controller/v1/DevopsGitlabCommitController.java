package io.choerodon.devops.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.devops.api.vo.CommitFormRecordVO;
import io.choerodon.devops.api.vo.DevopsGitlabCommitVO;
import io.choerodon.devops.app.service.DevopsGitlabCommitService;
import io.choerodon.devops.infra.dto.AppServiceDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Date;
import java.util.Optional;

/**
 * Created by n!Ck
 * Date: 2018/9/19
 * Time: 15:06
 * Description:
 */

@RestController
@RequestMapping(value = "/v1/projects/{project_id}/commits")
public class DevopsGitlabCommitController {

    @Autowired
    private DevopsGitlabCommitService devopsGitlabCommitService;

    /**
     * 服务下commit记录报表
     *
     * @param projectId     项目id
     * @param appServiceIds 服务id
     * @return DevopsGitlabCommitDTO
     */
    @Permission(level = ResourceLevel.ORGANIZATION,
            roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation(value = "获取服务下的代码提交")
    @PostMapping
    public ResponseEntity<DevopsGitlabCommitVO> getCommits(
            @ApiParam(value = "项目id", required = true)
            @PathVariable(value = "project_id") Long projectId,
            @ApiParam(value = "服务ids", required = true)
//            @Encrypt(AppServiceDTO.ENCRYPT_KEY)
            @RequestBody String appServiceIds,
            @ApiParam(value = "开始时间start_date", required = true)
            @RequestParam(value = "start_date") Date startDate,
            @ApiParam(value = "结束时间end_date", required = true)
            @RequestParam(value = "end_date") Date endDate) {
        return Optional.ofNullable(devopsGitlabCommitService.queryCommits(projectId, appServiceIds, startDate, endDate))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.commits.get"));
    }

    // TODO 李浩   这里的appServiceIds需要特殊处理，需要讨论 (主键加密逻辑)
    /**
     * 服务获取下最近的commit记录
     *
     * @param projectId     项目id
     * @param appServiceIds 服务id
     * @param pageable      分页参数
     * @return List
     */
    @Permission(level = ResourceLevel.ORGANIZATION,
            roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @CustomPageRequest
    @ApiOperation(value = "获取服务下的代码提交历史记录")
    @PostMapping("/record")
    public ResponseEntity<Page<CommitFormRecordVO>> getRecordCommits(
            @ApiParam(value = "项目id", required = true)
            @PathVariable(value = "project_id") Long projectId,
            @ApiParam(value = "服务ids", required = true)
            @RequestBody String appServiceIds,
            @ApiParam(value = "分页参数")
            @ApiIgnore PageRequest pageable,
            @ApiParam(value = "开始时间start_date", required = true)
            @RequestParam(value = "start_date") Date startDate,
            @ApiParam(value = "结束时间end_date", required = true)
            @RequestParam(value = "end_date") Date endDate) {
        return Optional.ofNullable(devopsGitlabCommitService.pageRecordCommits(projectId, appServiceIds, pageable,
                startDate, endDate))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.record.commit.get"));
    }
}
