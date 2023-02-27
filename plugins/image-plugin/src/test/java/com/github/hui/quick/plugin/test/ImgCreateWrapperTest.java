package com.github.hui.quick.plugin.test;


import com.github.hui.quick.plugin.base.Base64Util;
import com.github.hui.quick.plugin.base.file.FileReadUtil;
import com.github.hui.quick.plugin.base.awt.ImageLoadUtil;
import com.github.hui.quick.plugin.image.wrapper.create.ImgCreateOptions;
import com.github.hui.quick.plugin.image.wrapper.create.ImgCreateWrapper;
import com.github.hui.quick.plugin.image.util.FontUtil;
import com.sun.imageio.plugins.common.ImageUtil;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yihui on 2017/8/17.
 */
public class ImgCreateWrapperTest {

    @Test
    public void testGenImg() throws IOException {
        int w = 400;
        int h = 600;
        int leftPadding = 5;
        int topPadding = 20;
        int bottomPadding = 20;
        int linePadding = 10;
        Font font = new Font("宋体", Font.PLAIN, 18);

        ImgCreateWrapper.Builder build = ImgCreateWrapper.build()
                .setImgW(w)
                .setImgH(h)
                .setLeftPadding(leftPadding)
                .setTopPadding(topPadding)
                .setBottomPadding(bottomPadding)
                .setLinePadding(linePadding)
                .setFont(font)
                .setAlignStyle(ImgCreateOptions.AlignStyle.CENTER)
                .setDrawStyle(ImgCreateOptions.DrawStyle.HORIZONTAL)
                .setBgColor(Color.WHITE)
                .setBorder(true)
                .setBorderColor(0xFFF7EED6);


        BufferedReader reader = FileReadUtil.createLineRead("text/poem.txt");
        String line;
        int index = 0;
        while ((line = reader.readLine()) != null) {
            build.drawContent(line);

            if (++index == 5) {
                build.drawImage(ImageLoadUtil.getImageByPath("https://static.oschina.net/uploads/img/201708/12175633_sOfz.png"));
            }

            if (index == 7) {
                build.setFontSize(25);
            }

            if (index == 10) {
                build.setFontSize(20);
                build.setFontColor(Color.RED);
            }
        }

        BufferedImage img = build.asImage();
        String out = Base64Util.encode(img, "png");
        System.out.println("<img src=\"data:image/png;base64," + out + "\" />");
    }


    @Test
    public void testLocalGenImg() throws IOException {
        //BufferedImage image = new BufferedImage(1080, 1439, BufferedImage.TYPE_INT_ARGB);
        //ImageIO.write(image, "png", new File("/Users/salk/Documents/小红书/out1.png"));

        int w = 1080;
        int h = 1439;
        int leftPadding = 0;
        int topPadding = 0;
        int bottomPadding = 0;
        int linePadding = 20;
        Font font = new Font("幼圆", Font.PLAIN, 48);

        ImgCreateWrapper.Builder build = ImgCreateWrapper.build()
                .setImgW(w)
                .setImgH(h)
                .setLeftPadding(leftPadding)
                .setRightPadding(leftPadding)
                .setTopPadding(topPadding)
                .setBottomPadding(bottomPadding)
                .setLinePadding(linePadding)
                .setFont(font)
                .setAlignStyle(ImgCreateOptions.AlignStyle.LEFT)
                .setDrawStyle(ImgCreateOptions.DrawStyle.VERTICAL_FIXED)
                .setBgImg(ImageLoadUtil.getImageByPath("createImg/bg.jpeg"))
                .setBgColor(Color.WHITE)
                //.setBgColor(0xA5DED5)
                .setBorder(true)
                .setBorderColor(0xFFF7EED6);
;
        String ss="1. 香港警方披露名媛蔡天凤被碎尸案细节，正全力追缉死者前夫\n" +
                "2.国台办：公安机关对洪政军、孔祥志依法作出稳妥处理\n"+
                "2.港媒称蔡天凤被碎尸案主谋为前夫父亲：反侦察能力强，曾卷入强奸案\n"+
                "2.女子称价值约5万元的包裹被寄丢，德邦物流：正沟通赔偿金额\n"+
                "2.中疾控：去年12月至今年2月23日共发现本土重点关注变异株22例中疾控：去年12月至今年2月23日共发现本土重点关注变异株22例\n"+
                "2.国台办：公安机关对洪政军、孔祥志依法作出稳妥处理\n"+
                "2.国台办：公安机关对洪政军、孔祥志依法作出稳妥处理\n"+
                "2.国台办：公安机关对洪政军、孔祥志依法作出稳妥处理\n"+
                "2.国台办：公安机关对洪政军、孔祥志依法作出稳妥处理\n";
        BufferedReader reader = FileReadUtil.createLineRead("text/poem.txt");
        String line;
//       while ((line = reader.readLine()) != null) {
//            build.drawContent(line);
//        }
        String[] split = ss.split("\n");
       for(String str:split){
           build.drawContent(str);
       }
        //build.drawContent(ss);
        //build.setAlignStyle(ImgCreateOptions.AlignStyle.RIGHT)
        //        .drawImage("https://gitee.com/liuyueyi/Source/raw/master/img/info/blogInfoV2.png");

        BufferedImage img = build.asImage();
        ImageIO.write(img, "png", new File("/Users/salk/Documents/小红书/2out.png"));
    }


    @Test
    public void testLocalGenVerticalImg() throws IOException, FontFormatException {
        int h = 400;
        int leftPadding = 10;
        int topPadding = 10;
        int bottomPadding = 10;
        int linePadding = 10;
        Font font = FontUtil.getFont("font/txlove.ttf", Font.PLAIN, 20);

        ImgCreateWrapper.Builder build = ImgCreateWrapper.build()
                .setImgH(h)
                .setDrawStyle(ImgCreateOptions.DrawStyle.VERTICAL_RIGHT)
                .setAlignStyle(ImgCreateOptions.AlignStyle.CENTER)
                .setLeftPadding(leftPadding)
                .setTopPadding(topPadding)
                .setBottomPadding(bottomPadding)
                .setLinePadding(linePadding)
                .setFont(font)
                .setBgColor(Color.WHITE)
                .setBorder(true)
                .setBorderBottomPadding(8)
                .setBorderLeftPadding(6)
                .setBorderTopPadding(8)
                .setBorderColor(0xFFF7EED6);


        BufferedReader reader = FileReadUtil.createLineRead("text/poem.txt");
        String line;
        while ((line = reader.readLine()) != null) {
            build.drawContent(line);
        }

        build.setFont(FontUtil.getFont("font/txlove.ttf", Font.ITALIC, 18))
                .setAlignStyle(ImgCreateOptions.AlignStyle.BOTTOM);
        build.drawContent(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        build.drawContent(" ");
        build.setAlignStyle(ImgCreateOptions.AlignStyle.CENTER)
                .drawImage("https://gitee.com/liuyueyi/Source/raw/master/img/info/blogInfoV2.png");
//        build.setFontColor(Color.BLUE).drawContent("后缀签名").drawContent("灰灰自动生成");

        BufferedImage img = build.asImage();
        ImageIO.write(img, "png", new File("/tmp/2out.png"));
    }
}
