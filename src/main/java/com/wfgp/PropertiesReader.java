package com.wfgp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class PropertiesReader {

    private Properties prop = new Properties();

    public Map<String, String> getCusomters() {
        Map<String, String> configuration = new HashMap<>();
        prop.forEach((k, v) -> configuration.put((String) k, (String)v));
        return configuration;
    }

    public static String getCustomersFileName(final String dir) {
        return dir + "\\conf\\customers.txt";
    }

    public PropertiesReader(final String dir) throws IOException {
        InputStream input = new FileInputStream(getCustomersFileName(dir));
        InputStreamReader isr = new InputStreamReader(input, "UTF-8");
        prop.load(isr);
        input.close();
        isr.close();
    }

    public static void main(String[] args) throws IOException {
        PropertiesReader reader = new PropertiesReader(Constants.appDir);
        Map<String, String> configuration = reader.getCusomters();
        for (Map.Entry<String, String> entry : configuration.entrySet()) {
            System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
        }
    }
}
