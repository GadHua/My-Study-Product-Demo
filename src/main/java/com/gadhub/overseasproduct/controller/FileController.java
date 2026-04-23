package com.gadhub.overseasproduct.controller;

import com.gadhub.overseasproduct.common.result.Result;
import com.gadhub.overseasproduct.util.FileUploadUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/backend/file")
@Tag(name = "文件管理")
public class FileController {

    @PostMapping("/upload/image")
    @Operation(summary = "上传图片", description = "上传商品图片到本地服务器")
    public Result uploadImage(@RequestParam("file") MultipartFile file) {
        String url = FileUploadUtil.uploadImage(file);
        return Result.success(url);
    }
}
