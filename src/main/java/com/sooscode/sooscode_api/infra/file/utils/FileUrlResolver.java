package com.sooscode.sooscode_api.infra.file.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileUrlResolver {

    @Value("${FILE_BASE_URL}")
    private String fileBaseUrl;

    public String resolve(String path) {
        if (path == null || path.isBlank()) {
            return null;
        }

        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        return fileBaseUrl + "/" + path;
    }
}
