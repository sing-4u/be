package com.sing4u.kr.file.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;


import java.nio.file.Path;

import com.sing4u.kr.file.utils.FileUtils;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Client s3Client;
    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;
    @Value("${sing4u.file-url}")
    private String fileUrl;
    @Value("${spring.profiles.active}")
    private String activeProfile;

    public String uploadFile(String path, MultipartFile file) {
        try {
            String uploadPath = this.generateUploadPath(path, file);

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(uploadPath)
                    .acl(ObjectCannedACL.BUCKET_OWNER_FULL_CONTROL)
                    .contentType(file.getContentType())
                    .contentDisposition("inline")
                    .build();

            this.s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return this.getFullPath(uploadPath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    private String generateUploadPath(String path, MultipartFile file) {
        return Path.of(this.activeProfile,
                path,
                FileUtils.generateFileName(file)).toString();
    }

    private String getFullPath(String path) {
        return this.fileUrl + path;
    }
}
