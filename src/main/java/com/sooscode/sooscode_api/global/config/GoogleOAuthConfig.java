package com.sooscode.sooscode_api.global.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@ConfigurationProperties(prefix = "oauth.google")
public class GoogleOAuthConfig {
    private String clientId;
    private String clientSecret;
    private String redirectUri;

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }
}
