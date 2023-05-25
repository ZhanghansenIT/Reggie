package com.it.reggie.controller;

import com.it.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
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
 * 文件上传和下载
 */
@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath ;
    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        log.info(file.getName());
        String orginName = file.getOriginalFilename() ;
        // 将临时文件转存到指定位置

        // 使用UUID重新生成文件名,防止覆盖

        String suffix = file.getOriginalFilename().substring(orginName.lastIndexOf(".")) ;
        String fileName = UUID.randomUUID().toString() +suffix;
        // 判断当前目录是否存在
        File dir = new File(basePath) ;
        if(!dir.exists()){
            //如果目录不存在,就创建
            dir.mkdir() ;
        }

        try {
            file.transferTo(new File(basePath +fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName) ;
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name , HttpServletResponse response) {
        // 输入流 ， 通过输入流读取文件内容
        File file = new File(basePath+name ) ;
        try {
            FileInputStream fileInputStream = new FileInputStream(file) ;
            // 输出流，通过输出流将文件写回浏览器，在浏览器展示图片

            ServletOutputStream outputStream =  response.getOutputStream() ;
            // 设置响应的格式
            response.setContentType("image/jpeg");
            int len = 0 ;
            byte [] bytes = new byte[1024] ;
            while ((len = fileInputStream.read(bytes) )!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            // 关闭资源
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
