package io.choerodon.devops.infra.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;

import io.choerodon.mybatis.entity.BaseDTO;

/**
 * Created by Sheep on 2019/3/14.
 */

@Table(name = "devops_registry_secret")
public class DevopsRegistrySecretDTO extends BaseDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ApiModelProperty("环境id，在0.21版本修复数据之后，0.22版本可以删除此字段")
    private Long envId;
    @ApiModelProperty("docker仓库配置id")
    private Long configId;
    @ApiModelProperty("拉取镜像的secret的code")
    private String secretCode;
    @ApiModelProperty("环境code，也是集群的namespace")
    private String namespace;
    @ApiModelProperty("集群id")
    private Long clusterId;
    @ApiModelProperty("目前Secret中的配置，用于对比更新")
    private String secretDetail;
    private Boolean status;
    private Long objectVersionNumber;



    public DevopsRegistrySecretDTO() {
    }

    public DevopsRegistrySecretDTO(Long envId, Long configId, String envCode, Long clusterId, String secretCode, String secretDetail) {
        this.clusterId = clusterId;
        this.envId = envId;
        this.namespace = envCode;
        this.configId = configId;
        this.secretCode = secretCode;
        this.secretDetail = secretDetail;
        this.status = false;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEnvId() {
        return envId;
    }

    public void setEnvId(Long envId) {
        this.envId = envId;
    }

    public Long getConfigId() {
        return configId;
    }

    public void setConfigId(Long configId) {
        this.configId = configId;
    }

    public String getSecretCode() {
        return secretCode;
    }

    public void setSecretCode(String secretCode) {
        this.secretCode = secretCode;
    }

    public String getSecretDetail() {
        return secretDetail;
    }

    public void setSecretDetail(String secretDetail) {
        this.secretDetail = secretDetail;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    @Override
    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }
}
