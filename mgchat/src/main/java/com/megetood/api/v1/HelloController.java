package com.megetood.api.v1;

import com.megetood.utils.JSONResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Iterator;

/**
 * @author Megetood
 * @date 2019/11/19 15:38
 */
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello(){

        return "hello";
    }

    @PostMapping("/upload")
    public JSONResult upload( String user, String bgmId, HttpServletRequest request) throws Exception {
        System.out.println("[upload]接收值："+bgmId);
        if (user != null){
            System.out.println("[upload]接收值user："+user);

        }else {
            System.out.println("[upload]接收值user："+null);
        }



        // 文件保存的命名空间
        String fileSpace = "C:\\Users\\Administrator\\Desktop";

        System.out.println("upload method start");

        // 获取文件
        MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
        Iterator<String> files = mRequest.getFileNames();
        while (files.hasNext()) {
            MultipartFile mFile = mRequest.getFile(files.next());
            System.out.println(mFile.toString());
            System.out.println(mFile.getOriginalFilename());
            byte[] bytes = null;

//        FileOutputStream fileOutputStream = null;
//        InputStream inputStream = null;
            BufferedOutputStream stream = null;
            try {
                bytes = mFile.getBytes();
                String fileName = mFile.getOriginalFilename();
                if (StringUtils.isNotBlank(fileName)) {
                    // 文件上传的最终保存路径
                    String finalFacePath = fileSpace + "/" + fileName;

                    File outFile = new File(finalFacePath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        // 创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }

//                    fileOutputStream = new FileOutputStream(outFile);
//                    inputStream = files[0].getInputStream();
//                    IOUtils.copy(inputStream, fileOutputStream);
                    stream= new BufferedOutputStream(new FileOutputStream(outFile));
                    stream.write(bytes);

                }else{
                    return JSONResult.errorMsg("文件不存在...");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return JSONResult.errorMsg("异常，上传出错...");
            } finally {
                if (stream != null) {
//                    fileOutputStream.flush();
//                    fileOutputStream.close();
                    stream.flush();
                    stream.close();
                }
            }
        }


        return JSONResult.errorMsg("成功");
    }
}
