package io.choerodon.devops.api.vo;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class GitEnvConfigVO {
    private String namespace;
    private Long envId;
    private String gitRsaKey;
    private String gitUrl;
    @ApiModelProperty("环境的实例的code")
    private List<String> instances;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public Long getEnvId() {
        return envId;
    }

    public void setEnvId(Long envId) {
        this.envId = envId;
    }

    public String getGitRsaKey() {
        return gitRsaKey;
    }

    public void setGitRsaKey(String gitRsaKey) {
        this.gitRsaKey = gitRsaKey;
    }

    public String getGitUrl() {
        return gitUrl;
    }

    public void setGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
    }

    public List<String> getInstances() {
        return instances;
    }

    public void setInstances(List<String> instances) {
        this.instances = instances;
    }
}
