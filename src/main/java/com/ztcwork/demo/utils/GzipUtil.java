package com.ztcwork.demo.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipUtil {

//    public static void main(String[] args) {
//
//        byte[] bytes = Convert.hexToBytes("1F8B0800000000000400cF8F0D595010706000Bk1B131C000000");
////        byte[] bs = new byte[100];
////        uncompress()
//        System.out.println();
//    }

    /**
     * GZIP解压字节数据
     * @param bytes
     * @return
     */
    public static byte[] uncompress(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        try {
            GZIPInputStream ungzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = ungzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    /**
     * GZIP压缩字节数据
     * @param str
     * @param encoding
     * @return
     */
    public static byte[] compress(String str, String encoding) {
        if (str == null || str.length() == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes(encoding));
            gzip.close();
        } catch ( Exception e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }


    private static float Byte2Float(byte[] bytes) {
        int number = 0;
        for (int i = 0; i < 4; i++) {
            number = number | ((0xff & bytes[i]) <<  i * 8);
        }
        return Float.intBitsToFloat(number);
    }

    public static int bytes2Int(byte[] bytes )
    {
        int int1=bytes[0]&0xff;
        int int2=(bytes[1]&0xff)<<8;
        int int3=(bytes[2]&0xff)<<16;
        int int4=(bytes[3]&0xff)<<24;

        return int1|int2|int3|int4;
    }
    /**
     * 字节数组转换int数组
     * @param bytes
     * @return
     */
    public static Integer[] Bytes2Ints(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        int len =bytes.length/4;
        Integer[] rs =new Integer[len];
        for(int k=0;k<len;k++){
            int number = 0;
            for (int i = 0; i < 4; i++) {
                number = number | ((0xff & bytes[4*k+i]) <<  i * 8);
            }
            rs[k] = number;
        }
        return rs;
    }
    /**
     * 字节数组转换float浮点数数组
     * @param bytes
     * @return
     */
    public static Float[] Bytes2Floats(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        int len =bytes.length/4;
        Float[] rs =new Float[len];
        for(int k=0;k<len;k++){
            int number = 0;
            for (int i = 0; i <4; i++) {
                int dd = bytes[k*4+i];
                number = number | ((0xff & dd) <<  i * 8);
            }
            rs[k] = Float.intBitsToFloat(number);
        }
        return rs;
    }


    /**
     * 字节数组转换Double浮点数数组
     * @param bytes
     * @return
     */
    public static Double[] Bytes2Doubles(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        int len =bytes.length/8;
        Double[] rs =new Double[len];
        for(int k=0;k<len;k++){
            long number = 0;
            for (int i = 0; i < 8; i++) {
                number = number | ((long)(0xff & bytes[8*k+i]) <<  (i * 8));
            }
            rs[k] = Double.longBitsToDouble(number);
        }

        return rs;
    }

    private static double Bytes2Double(byte[] arr) {

        long value = 0;
        for(int i = 0; i < 8; i++){

            value |= ((long)(arr[i] & 0xff)) << (8 * i);
        }
        return Double.longBitsToDouble(value);

    }
    public static byte[] Doubles2Bytes(double[] dArray) {
        if (dArray == null || dArray.length == 0) {
            return null;
        }
        int len =dArray.length*8;
        byte[] rs =new byte[len];
        for(int i=0;i<dArray.length;i++){
            long l = Double.doubleToRawLongBits(dArray[i]);
            for(int j=0;j<8;j++){
                rs[j+i*8] = (byte)((l>>(j*8))&0xff);
            }
        }
        return rs;
    }

    public static void main(String[] args) {
        double[] dArray =new double[]{1.23d,2.76d,7.88d};
//        double[] dArray =new double[]{503.2135013d};
        byte[] bs = Doubles2Bytes(dArray);
        byte[] tmp =new byte[]{(byte)0x75,(byte)0xd2,(byte)0x56,(byte)0x80,(byte)0x6a,(byte)0x73,(byte)0x7f,(byte)0x40};
        double v = Bytes2Double(tmp);

//        double[] rsdArray = Bytes2Doubles(bs);
        System.out.println();

    }

    public static Integer[] Bytes2Boolean(byte[] arr) {
        Integer[] numbers = new Integer[arr.length];
        for(int i=0;i<arr.length;i++){
            numbers[i]=(int)arr[i];
        }
        return numbers;
    }
}
