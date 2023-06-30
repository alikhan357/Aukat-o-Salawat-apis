package com.project.api.service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.google.cloud.storage.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class CloudUploadService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloudUploadService.class);


    @Autowired
    private AmazonS3 amazonS3;

    @Autowired
    private Storage gcpGcs;

    @Value("${server.storage}")
    private String storage;


    @Value("${gcp.gcs.bucket_name}")
    private String gcsBucketName;

    @Value("${aws.s3.bucket_name}")
    private String s3BucketName;

    @Value("${aws.s3.url}")
    private String s3Url;


    public String uploadFile(String fileName, InputStream stream, ObjectMetadata metadata){
        if(storage.equalsIgnoreCase("GCP"))
            return uploadFileGCS(gcsBucketName,fileName,stream,metadata);
        else
            return uploadFileS3(s3BucketName,fileName,stream,metadata);
    }

    private String uploadFileS3(String bucketName, String fileName, InputStream stream, ObjectMetadata metadata){
        LOGGER.info("UPLOADING FILE TO S3");
        amazonS3.putObject(bucketName, fileName, stream, metadata);
        LOGGER.info("UPLOADING COMPLETE");
        return s3Url + fileName;
    }

    private String uploadFileGCS(String bucketName, String fileName, InputStream stream, ObjectMetadata metadata){
        LOGGER.info("UPLOADING FILE TO GCS");
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("audio/mpeg").build();
        Blob blob = gcpGcs.create(blobInfo, stream);
        LOGGER.info("UPLOADING COMPLETE");
        return blob.getMediaLink();
    }


}
