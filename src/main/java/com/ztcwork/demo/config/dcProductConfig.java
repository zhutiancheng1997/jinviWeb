package com.ztcwork.demo.config;

import com.ztcwork.demo.entity.dcPDO;
import com.ztcwork.demo.entity.dcProduct;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.HashSet;
import java.util.Set;

public class dcProductConfig {
    //表名
    public static String PDP_TABLE_NAME = "PDP";
    public static String PDR_TABLE_NAME = "PDR";

    //列族1
    public static String INFO_COL_FAMILY = "info";

    //列族2
    public static String DATA_COL_FAMILY ="data";

    //固定字段
    public static byte[] START_TIME = Bytes.toBytes(dcProduct._Fields.START_TIME.getFieldName());

    public static byte[] END_TIME = Bytes.toBytes(dcProduct._Fields.END_TIME.getFieldName());

    public static byte[] DEVICE_NAME = Bytes.toBytes(dcProduct._Fields.DEVICE_NAME.getFieldName());

    public static byte[] MATERIAL_NAME = Bytes.toBytes(dcProduct._Fields.MATERIAL_NAME.getFieldName());

    public static Set<String> PERMANENT_FILED_SET = new HashSet<>();

    static {
        PERMANENT_FILED_SET.add(dcProduct._Fields.START_TIME.getFieldName());
        PERMANENT_FILED_SET.add(dcProduct._Fields.END_TIME.getFieldName());
        PERMANENT_FILED_SET.add(dcProduct._Fields.DEVICE_NAME.getFieldName());
        PERMANENT_FILED_SET.add(dcProduct._Fields.MATERIAL_NAME.getFieldName());
    }

}
