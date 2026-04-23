package com.gadhub.overseasproduct.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
public class FileUploadUtil {

    private static final String UPLOAD_DIR = "uploads/products/";

    private static final String[] ALLOWED_TYPES = {"image/jpeg", "image/png", "image/gif", "image/webp"};

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    public static String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        String contentType = file.getContentType();
        if (contentType == null || !isAllowedType(contentType)) {
            throw new IllegalArgumentException("只支持 JPG、PNG、GIF、WEBP 格式的图片");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小不能超过5MB");
        }

        try {
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ?
                    originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String fileName = UUID.randomUUID().toString() + extension;

            String uploadPath = UPLOAD_DIR + datePath;
            File dir = new File(uploadPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            Path filePath = Paths.get(uploadPath, fileName);
            Files.write(filePath, file.getBytes());

            String fileUrl = "/uploads/products/" + datePath + "/" + fileName;
            log.info("文件上传成功: {}", fileUrl);

            return fileUrl;

        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    private static boolean isAllowedType(String contentType) {
        for (String allowedType : ALLOWED_TYPES) {
            if (allowedType.equalsIgnoreCase(contentType)) {
                return true;
            }
        }
        return false;
    }
}
