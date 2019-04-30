package com.dave.util;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 实现由BIG5编码到GB2312编码转换的工具类
 */
public class Big2Gb{
    private static final String tabFile ="bg-gb.tab";
    private static byte[] data;

    static {
        try {
            File file = new File(tabFile);
            if (file.canRead()) {
                data = Files.readAllBytes(Paths.get(file.toURI()));
            } else {
                try(InputStream stream = Big2Gb.class.getResourceAsStream("/" + tabFile)){
                    int len = stream.available();
                    data = new byte[len];
                    stream.read(data);
                }

                if (data == null) throw new RuntimeException("Cannot find resource: " + tabFile);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    
    /**
    *取得BIG5汉字big在data中的偏移
    */
    private static int indexOf(int big) {

        int high = (big >>> 8) & 0xff;
        int low = big & 0xff;

        high -= 0xa1;
        if (low <= 0x7e) low -= 0x40;
        else low -= (0xa1 - 0x7e - 1) + 0x40;

        return 2 * (high * 157 + low);
    }

    /**
     *将保存在bs数字中的big5编码的字符串数据转换成gb2312编码的数据
     *注意:此方法将更改原先存储的数据
     *@param bs 需要转换的以big5编码的字符串数据
     *@return bs 经过转换的数据,保存在参数中的byte数组中
     */
    public static byte[] translateBig5ToGb(byte[] bs) {
        int index = 0;
        while (index < bs.length) {
            int high = bs[index] & 0xff;
            if (high >= 0xa1 && high <= 0xfe) {
                index++;
                if (index >= bs.length) break;
                int low = bs[index] & 0xff;
                if (low < 0x40 || low > 0xfe) continue;
                if (low > 0x7e && low < 0xa1) continue;
                int offset = indexOf((high << 8) | low);
                bs[index - 1] = data[offset];
                bs[index] = data[offset + 1];
                index++;
            } else index++;
        }
        return bs;
    }

    public static String translateBig5ToGb(String big) {
        String result = null;
        try {
            byte[] bs = big.getBytes("big5");
            bs = translateBig5ToGb(bs);
            result = new String(bs, "gb2312");
        } catch (Exception e) {
        }
        return result;
    }
    
}