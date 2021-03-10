package com.ztcwork.demo.config;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class dcConfig {
    private static Logger logger = Logger.getLogger(dcConfig.class);

    public static String zookeepers ="";
    static {
        Properties p = readProperty("/dc.properties");
        dcPDOConfig.PDO_TABLE_NAME = p.getProperty("pdo.tableName");
        dcPDOConfig.COL_FAMILY = p.getProperty("pdo.colfamily");
        dcProductConfig.PDP_TABLE_NAME =p.getProperty("pdp.tableName");
        dcProductConfig.PDR_TABLE_NAME =p.getProperty("pdr.tableName");

        String[] pdoColFamilies = p.getProperty("pdp.colfamily").split(",");
        dcProductConfig.INFO_COL_FAMILY =pdoColFamilies[0];
        dcProductConfig.DATA_COL_FAMILY =pdoColFamilies[1];

        zookeepers =p.getProperty("zookeepers");
    }

    private static Properties readProperty(String path) {
        Properties properties = new Properties();
        InputStream inputStream = Object.class.getResourceAsStream(path);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("读取配置文件: "+path+"出错！");
        }
        return properties;
    }
}
