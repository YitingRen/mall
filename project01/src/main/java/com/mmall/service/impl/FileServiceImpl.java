package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private Logger logger= LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file, String path){

        //set new filename
        //create filedir
        //create newfile in filedir
        //file transferto newfile
        String filename = file.getOriginalFilename();
        String fileExtensionName=filename.substring(filename.lastIndexOf(".")+1);
        String newfileName= UUID.randomUUID().toString()+"."+fileExtensionName;

        logger.info("upload filename:{} upload path:{} new filename:{}",filename,fileExtensionName,newfileName);

        File filedir = new File(path);
        if(!filedir.exists()){
            filedir.setWritable(true);
            filedir.mkdirs();
        }
        File targetFile=new File(path,newfileName);

        try {
            file.transferTo(targetFile);

            FTPUtil.uploadFile(Lists.<File>newArrayList(targetFile));

            targetFile.delete();
            boolean isSuccess= true;
        } catch (IOException e) {
            logger.info("error");
            e.printStackTrace();
        }

        return targetFile.getName();
    }
}
