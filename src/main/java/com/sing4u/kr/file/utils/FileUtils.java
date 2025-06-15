package com.sing4u.kr.file.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

@UtilityClass
public class FileUtils {
    public String generateFileName(MultipartFile file) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String uuId = java.util.UUID.randomUUID().toString();

        return uuId.replace("-", "") + "." + extension;
    }
}
