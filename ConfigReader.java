package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    // 1. Private static instance of the class itself
    private static ConfigReader instance;
    private Properties properties;

    // 2. Private constructor does the heavy lifting (loading the file)
    private ConfigReader() {
        try {
            properties = new Properties();
            FileInputStream fis = new FileInputStream("src/test/resources/config.properties");
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 3. Public static method to provide global access
    public static ConfigReader getInstance() {
        if (instance == null) {
            instance = new ConfigReader();
        }
        return instance;
    }

    // Instance method (not static)
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}