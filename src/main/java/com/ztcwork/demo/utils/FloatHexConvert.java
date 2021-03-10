package com.ztcwork.demo.utils;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

public class FloatHexConvert {
    private static String host = "10.88.50.58";
    private static int port = 5000;

    public static float getFloat(byte[] b) {
        int accum = 0;
        accum = accum | (b[0] & 0xff) << 24;
        accum = accum | (b[1] & 0xff) << 16;
        accum = accum | (b[2] & 0xff) << 8;
        accum = accum | (b[3] & 0xff) << 0;
        return Float.intBitsToFloat(accum);
    }

    /**
     * 高位在前，低位在后
     *
     * @param bytes
     * @return
     */

    public static int Bytes2Int(byte[] bytes) {
        int number = 0;
        for (int i = 0; i < 4; i++) {
            number = number | ((0xff & bytes[i]) << (3 - i) * 8);
        }
        return number;
    }

    /**
     * Little Endian
     * @param bytes
     * @return
     */
    public static float Bytes2FloatLittle(byte[] bytes) {
        int number = 0;
        for (int i = 0; i < 4; i++) {
            number = number | ((0xff & bytes[i]) << (3 - i) * 8);
        }
        return Float.intBitsToFloat(number);
    }

    /**
     * Big Endian
     * @param bytes
     * @return
     */
    public static float Bytes2FloatBig(byte[] bytes) {
        int number = 0;
        for (int i = 0; i < 4; i++) {
            number = number | ((0xff & bytes[i]) <<  i * 8);
        }
        return Float.intBitsToFloat(number);
    }

    public static double Bytes2Double(byte[] bytes) {
        long number = 0;
        for (int i = 0; i < 8; i++) {
            number = number | ((long) (0xff & bytes[i]) << (7 - i) * 8);
        }
        return Double.longBitsToDouble(number);
    }

    public static byte[] Int2Bytes(int val) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) ((val >> (3-i) * 8) & 0xff);
        }
        b[0] = (byte) (val & 0xff);
        b[1] = (byte) ((val >> 8) & 0xff);
        b[2] = (byte) ((val >> 16) & 0xff);
        b[3] = (byte) ((val >> 24) & 0xff);
        return b;
    }

    public static void testIBA() throws IOException {
        ServerSocket servSock = new ServerSocket();
        servSock.bind(new InetSocketAddress(15010));
        System.out.println("socket服务端:" + servSock.getInetAddress() + ":" + servSock.getLocalPort() + "");
        // 2.调用accept方法，建立和客户端的连接
        Socket client = servSock.accept();
        SocketAddress clientAddress = client.getRemoteSocketAddress();
        System.out.println("socket链接客户端:" + clientAddress);
        DataInputStream in = new DataInputStream(client.getInputStream());
        int cnt = 132;
        int count = 0;
        int interval =0;//间隔几个字节
        int fsize =32;//几个float
        int bsize =1;//几个boolean
        while (true) {
            if (in.available() >= cnt) {
                byte[] dt = new byte[cnt];
                in.read(dt);
                boolean[] bArray =new boolean[bsize];
                float[] fArray =new float[fsize];
                for(int i =0;i<fsize;i++){
                    byte[] bs = new byte[4];
                    System.arraycopy(dt, i*4, bs, 0, 4);
                    fArray[i]=Bytes2FloatLittle(bs);
                }

                byte bbyte;
                bbyte = dt[4*fsize+interval];
                for(int i=0;i<bsize;i++){
                    byte t = (byte)(8>>i);
                    bArray[i]=(bbyte&t)==t;
                }
                StringBuilder sb =new StringBuilder();
                sb.append("float: ");
                for(int i=0;i<fsize;i++){
                    sb.append(fArray[i]+" ");
                }
                sb.append("  bool: ");
                for(int i=0;i<bsize;i++){
                    sb.append(bArray[i]);
                }
                System.out.println(sb.toString());
                count++;
                System.out.println("-----" + count + "------");
            }
        }
    }


    public static void main(String[] args) throws IOException {
//        byte[] bb =new byte[]{(byte)0x40, (byte)0x4E, (byte) 0x14, (byte) 0x7A};
//        float v1 = Bytes2Float(bb);
//        System.out.println(v1);

        testIBA();


    }





}
