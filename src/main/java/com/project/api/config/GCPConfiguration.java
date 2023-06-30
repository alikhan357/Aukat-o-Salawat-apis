package com.project.api.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
public class GCPConfiguration {


    @Value("${gcp.gcs.bucket_name}")
    private String bucketName;

    @Value("${gcp.config}")
    private String configFile;

    @Value("${gcp.project-id}")
    private String projectId;

    @Bean
    public Storage storageOptions() throws IOException {
        return StorageOptions.newBuilder().setProjectId(projectId).setCredentials(
                GoogleCredentials.fromStream(
                        new ClassPathResource(configFile).getInputStream()
                )).build().getService();
    }
}
