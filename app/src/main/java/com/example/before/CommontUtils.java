package com.example.before;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommontUtils {
    public static String MAC_ADDR = "";
    public static String WEB_FILE_PATH = "";
    public static List<Integer> idList = Collections.synchronizedList(new ArrayList());
    public static ExecutorService executor = Executors.newFixedThreadPool(8);
}
