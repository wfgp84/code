package com.wfgp;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class Constants {
    public static String appDir = "d:\\wfgp_util\\DataExtra\\";
    //public static String appDir = "c:\\wfgp\\wfgp_util\\DataExtra\\";
    public static String output_data = appDir + "output_data\\";
    public static String input_data = appDir + "input_data\\";
    public static String templateDir = appDir + "template\\";

    public static void writeLogProperties() throws Exception {
        String path = appDir.replaceAll("\\\\", "/");
        String content="handlers= java.util.logging.ConsoleHandler, java.util.logging.FileHandler\n" +
                ".level= INFO\n" +
                "java.util.logging.FileHandler.pattern = "+ path + "log/java%u.log\n" +
                "java.util.logging.FileHandler.limit = 50000000\n" +
                "java.util.logging.FileHandler.count = 5\n" +
                "java.util.logging.FileHandler.formatter = java.util.logging.SimpleFormatter\n" +
                "java.util.logging.ConsoleHandler.level = INFO\n" +
                "java.util.logging.ConsoleHandler.formatter = java.util.logging.SimpleFormatter";
        FileOutputStream fous = new FileOutputStream(appDir + "log\\logging.properties");
        fous.write(content.getBytes());
    }
}
