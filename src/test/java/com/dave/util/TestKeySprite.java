package com.dave.util;

import java.awt.*;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class TestKeySprite {

    public static void main(String[] args) throws AWTException, IOException, UnsupportedFlavorException, InterruptedException {
        String url = "http://mp.weixin.qq.com/s?src=11&timestamp=1553765221&ver=1512&signature=7xz6-oewyStPB15m3ZJAO5z9Ol5-82umhQvstJ*zs9EkKBB7h-X7cwYUjvPOrpxjFmi3h7Prh*qyDnK-zJ4Fe25TUFsZnQ3d4qOe5hgd2YvpE-IRo1XGtrbfAO3fA5PT&new=1";
        TimeUnit.SECONDS.sleep(2);
        Point point = java.awt.MouseInfo.getPointerInfo().getLocation();
        System.out.println(point);
        KeySprite.mouseClick(1155,734);
        TimeUnit.SECONDS.sleep(1);
        KeySprite.paste(url);
        KeySprite.mouseClick(1791,778);
        TimeUnit.SECONDS.sleep(1);
        KeySprite.mouseClick(1512,596);
        TimeUnit.SECONDS.sleep(2);
        KeySprite.mouseClick(365,115);
        System.out.println(KeySprite.getSystemClipboard());
        //quickMacro.paste("mouseClick");
        //quickMacro.enter();
    }
}
