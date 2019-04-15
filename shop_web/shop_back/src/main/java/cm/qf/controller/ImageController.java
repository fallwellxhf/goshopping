package cm.qf.controller;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/imgs")
public class ImageController {
    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    /**
     * 图片上传
     * @return
     */
    @RequestMapping("/uploader")
    @ResponseBody
    public String uploadImg(MultipartFile file){
        System.out.println("图片名："+file.getOriginalFilename()+"&&&&&&&&&&&&&&&&&&&");
       //获得最后一个点的下标
        int indexOf = file.getOriginalFilename().lastIndexOf(".");
        String houszhui=file.getOriginalFilename().substring(indexOf+1);
        //上传到FastDFS
        try {
            StorePath storePath = fastFileStorageClient.uploadImageAndCrtThumbImage(
                    file.getInputStream(), file.getSize(),
                    houszhui,
                    null
            );
            //获取上传到FastDFS中的图片访问路径
            String storageUrl=storePath.getFullPath();
            System.out.println("上传到fastDFS的路径："+storageUrl);
            // \" 转义（thymeleaf （传过去的数据应该加\"xxxx\"  ））
            return "{\"uploadPath\":\""+storageUrl+"\"}";//传json数据过去
                  //{name:xiaoming}http://ssd/uploadPath/
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }


}
