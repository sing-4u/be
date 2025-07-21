package com.sing4u.kr.file.service;

import com.sing4u.kr.common.exception.ApiException;
import com.sing4u.kr.common.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;


import java.nio.file.Path;
import java.util.regex.Pattern;

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
        return String.join("/", this.activeProfile, path, FileUtils.generateFileName(file));
    }

    private String getFullPath(String path) {
        return this.fileUrl + path;
    }

    public void deleteFile(String fullUrl) {
        if (fullUrl == null || fullUrl.isBlank()) {
            throw new IllegalArgumentException("Provided S3 URL is null or blank.");
        }

        if (!fullUrl.startsWith(fileUrl)) {
            throw new IllegalArgumentException("URL does not match fileUrl: " + fullUrl);
        }

        String key = fullUrl.replaceFirst("^" + Pattern.quote(fileUrl), "");

        try {
            boolean exists = s3Client.headObject(builder -> builder
                    .bucket(bucketName)
                    .key(key)
                    .build()).sdkHttpResponse().isSuccessful();

            if (!exists) {
                throw new ApiException(ExceptionCode.S3_FILE_DELETE_FAIL, "S3 object does not exist: " + key);
            }

            s3Client.deleteObject(builder -> builder
                    .bucket(bucketName)
                    .key(key)
                    .build());
        } catch (Exception e) {
            throw new ApiException(ExceptionCode.S3_FILE_DELETE_FAIL);
        }
    }
}
