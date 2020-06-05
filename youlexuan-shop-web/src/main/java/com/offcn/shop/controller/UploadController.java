package com.offcn.shop.controller;

import com.offcn.entity.Result;
import com.offcn.util.FastDFSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
//@RequestMapping("")
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    String FILE_SERVER_URL;

    @RequestMapping("/upload")
    public Result upload(MultipartFile file){
        //解析后缀
        String originalFilename = file.getOriginalFilename();
        String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);// jpg
        try {
            FastDFSClient client = new FastDFSClient("classpath:config/fdfs_client.conf");
            String s = client.uploadFile(file.getBytes(), extName);
            String url = FILE_SERVER_URL + s; //http://192.168.25.133/group1/M00/00/01/wKgZhVmHINyAQAXHAAgawLS1G5Y136.jpg
            Result result = new Result(true,url);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            Result result = new Result(false,"上传失败");
            return result;
        }
    }

}
