package com.sunzy.reggie.controller;

import com.sunzy.reggie.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 主要用于文件上传和下载
 */

@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;


    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        // file是一个临时文件，后续需要进行转存
        // file需要与前端上传的参数名保持一致，否则无法获取到上传的文件

        // 转存之前需要对文件名进行处理
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 防止文件被覆盖，使用uuid作为文件名
        String filename = UUID.randomUUID().toString();
        filename = filename + suffix;

        File dir = new File(basePath);
        if (!dir.exists()){
            dir.mkdir();
        }

        try {
            file.transferTo(new File(basePath + filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(filename);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws IOException {
        FileInputStream fileInputStream = null;
        ServletOutputStream outputStream = null;

        try {
            fileInputStream = new FileInputStream(new File(basePath + name));

            outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");
            byte[] bytes = new byte[1024];
            int len = 0;
            while( (len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fileInputStream.close();
            outputStream.close();
        }
    }
}
