package com.example.before.rmi;

import java.io.File;

public interface PictureRmi {
    public  String uploadPicutre(byte[] bytes, String fileName, Integer id);
}
