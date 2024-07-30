package com.atguigu.srb.oss.controller.api;

import com.atguigu.common.exception.BusinessException;
import com.atguigu.common.result.R;
import com.atguigu.common.result.ResponseEnum;
import com.atguigu.srb.oss.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import javafx.print.PageOrientation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Author:zxp
 * @Description:
 * @Date:13:51 2024/2/26
 */
@Api(tags = "阿里云文件管理")
//@CrossOrigin //跨域
@RestController
@RequestMapping("/api/oss/file")
public class FileController {
    @Resource
    private FileService fileService;
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public R upload(
            @ApiParam(value = "上传文件",required = true)
            @RequestParam("file")MultipartFile file,
            @ApiParam(value = "模块",required = true)
             @RequestParam("module") String module){
        try {
            InputStream inputStream = file.getInputStream();
            String originalFilename = file.getOriginalFilename();
            String upload = fileService.upload(inputStream, module, originalFilename);
            return R.ok().message("文件上传成功").data("url",upload);
        } catch (IOException e) {
            throw new BusinessException(ResponseEnum.UPLOAD_ERROR,e);
        }
    }
    @ApiOperation("删除文件")
    @DeleteMapping("/remove")
    public R remove(@ApiParam(value = "删除的url",required = true)
                    @RequestParam("url") String url){
        fileService.removeFile(url);
        return R.ok().message("删除成功");
    }
}
