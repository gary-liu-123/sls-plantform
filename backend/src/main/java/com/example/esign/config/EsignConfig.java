package com.example.esign.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "esign")
public class EsignConfig {

    private String host;
    private String projectId;
    private String projectSecret;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectSecret() {
        return projectSecret;
    }

    public void setProjectSecret(String projectSecret) {
        this.projectSecret = projectSecret;
    }
}
