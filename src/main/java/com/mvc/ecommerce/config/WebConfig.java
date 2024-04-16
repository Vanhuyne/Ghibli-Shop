package com.mvc.ecommerce.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


import java.util.concurrent.TimeUnit;


@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final String resourcePath = System.getProperty("user.dir") + "/src/main/resources/static/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        addResourceHandler(registry, "/uploads/**", "uploads/");
        addResourceHandler(registry, "/images/**", "images/");
    }

    private void addResourceHandler(ResourceHandlerRegistry registry, String urlPattern, String resourceLocation) {
        registry.addResourceHandler(urlPattern)
                .addResourceLocations("file:" + resourcePath + resourceLocation)
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic());
    }

}
