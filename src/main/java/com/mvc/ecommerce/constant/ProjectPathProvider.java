package com.mvc.ecommerce.constant;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectPathProvider {

    private final ResourceLoader resourceLoader;

    public String getStaticResourcesPath() {
        try {
            return "file:" + System.getProperty("user.dir") + "/src/main/resources/static/";
        } catch (Exception e) {
            // Handle exception as per your application's requirements
            e.printStackTrace();
            return null;
        }
    }
}