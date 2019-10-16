package com.dcxiaolou.tinyJD.manage.util;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class PmsUploadUtil {

    //上传图片到服务器
    public static String uploadImage(MultipartFile multipartFile) {
        String imgUrl = "http://192.168.159.3";

        String file = PmsUploadUtil.class.getResource("/tracker.conf").getFile();

        try {
            ClientGlobal.init(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = null;
        try {
            trackerServer = trackerClient.getConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        StorageClient storageClient = new StorageClient(trackerServer, null);

        try {
            byte[] bytes = multipartFile.getBytes(); //获取上传的二进制对象
            String originalFilename = multipartFile.getOriginalFilename();
            int index = originalFilename.lastIndexOf(".");
            String extName = originalFilename.substring(index + 1);
            String[] uploadFile = storageClient.upload_file(bytes, extName, null);
            for (String s : uploadFile) {
                imgUrl += "/" + s;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imgUrl;
    }

}
