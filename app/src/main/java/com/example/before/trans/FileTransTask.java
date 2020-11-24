package com.example.before.trans;

import com.example.before.rmi.PictureRmi;
import com.example.before.rmi.RmiClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.Callable;

import lipermi.net.Client;

public class FileTransTask implements Callable<String> {
    private String fileName;
    private File picutre;
    private Integer id;

    public FileTransTask(String fileName, File picutre, Integer id) {
        this.fileName = fileName;
        this.picutre = picutre;
        this.id = id;
    }

    @Override
    public String  call() throws Exception {
        Client client = RmiClient.getClient();
        PictureRmi pictureRmi = (PictureRmi) client.getGlobal(PictureRmi.class);
        byte[] bytes = null;
        FileInputStream fis = null;
        try{

            fis = new FileInputStream(picutre);
            bytes = new byte[(int) picutre.length()];
            fis.read(bytes);
        }catch(IOException e){
            e.printStackTrace();
            throw e;
        }finally{
            fis.close();
        }

        String msg = pictureRmi.uploadPicutre(bytes,  fileName, id);

        return msg;
    }
}
