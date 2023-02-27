package com.github.hui.quick.plugin.test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import com.github.hui.quick.plugin.base.awt.ColorUtil;
import com.github.hui.quick.plugin.base.awt.GraphicUtil;
import com.github.hui.quick.plugin.base.awt.ImageLoadUtil;
import com.github.hui.quick.plugin.image.wrapper.create.ImgCreateOptions;
import com.github.hui.quick.plugin.image.wrapper.create.ImgCreateWrapper;
import com.github.hui.quick.plugin.image.wrapper.merge.cell.*;

/**
 * @author salkli
 * @since 2023/2/26
 **/
public class CreateDysNewsImages {
    public static void main(String[] args) throws Exception{
        //BufferedImage bg = createBg();
        //testCover(bg,"2023年2月24日","20230224");
        //"星期二 (农历二月初二） 晴"
        String ss = "1、近日，疑似泽连斯基的“替身”出现在拜登访问基辅期间的镜头里。对此，乌方呼吁不要相信这些“假消息”，这些消息“旨在破坏乌克兰领导层的名声”。\n" +
                /*
                "2、在外界对鹤岗固有印象中，这座城市似乎很难“供养”一家独立咖啡馆，但现实情况是，“咖啡热潮”同样蔓延到了鹤岗，数家咖啡馆正在这座小城落地生长。\n"+
                "3、结对认亲心连心，民族团结情更浓。石榴籽抱心贴心，新疆各族群众手足相亲，守望相助，在交往交流交融中建设美好家园，人民过上了更好的生活。\n"+
                "4、近日，福建福州。22岁的小林患上再生障碍性贫血，母亲欲变卖房产给小林治病，遭到丈夫反对：儿子活不了半年，不需要治疗了，卖房最少要分他一半。\n"+
                "5、2月24日，湖南桑植县某中学举行高考冲刺百日誓师大会，一名高三学生代表激情澎湃的发言燃爆全场，但有网友的关注点却走偏，说她表情难看。\n"+
                "6、中国乒乓球队26日确认，马龙、王楚钦、孙颖莎、陈梦、王曼昱、何卓佳、王艺迪等人因身体原因，将不参加世界乒乓球职业大联盟球星挑战赛果阿站比赛。\n"+
                "7、2月26日，网传广州某学校一男生因进女生宿舍睡觉被发现，被锁在女寝楼下后砸东西，被抓时大喊“我爸是干部”。目前，该男生已被送到派出所。\n"+
                "8、近日，在张大大直播间里，一位男网友连麦后说自己正在洗澡，但是突然停水了，许愿张大大帮自己打开水龙头。\n"+
                "9、央视曝光磁性文具安全隐患\n"+
                */
                "10、赵继伟晃倒对手三分\n"+
                "11、近日，香港大埔龙尾村发生碎尸案，死者系香港模特、谭仔米线创办人的儿媳蔡天凤。据港媒26日报道，警方搜索3日后已寻回死者头颅。\n"+

                "12、韦德儿子获女性身份网民炸锅\n"+
                "13、女演员练空中瑜伽被网暴造黄谣\n"+

                "14、网友呼喊天王嫂方媛“多请保镖\n"+

                "15、全职妈妈花三千租自习室假装上班\n";
        String[] split = ss.split("\n");
        List<String> strings = Arrays.asList(split);
        List<BufferedImage> news = createNews(strings);
        int i=0;
        for(BufferedImage image:news){
            BufferedImage bg = createBg("60秒知天下");
            BufferedImage titleImage = createTitleImage("星期一 (农历二月初八）");
            combineImage(bg,titleImage,image, "2023年2月27日"  ,"20230227_"+i);
            i++;
        }

    }



    public static  List<BufferedImage> createNews(List<String> newStrs) {
        ImgCreateWrapper.Builder build = createNewsImgBuilder();
        List<BufferedImage> result = new ArrayList<>();
        // title

       // String[] split = ss.split("\n");
        for (String str : newStrs) {
            build.drawContent(str);
            // 创建新的imag
            if (build.getContentH() > build.getOptions().getImgH()) {
                result.add(build.asImage());
                build = createNewsImgBuilder();
            }
        }
        // build.drawContent(ss);

        // .drawImage("https://gitee.com/liuyueyi/Source/raw/master/img/info/blogInfoV2.png");

        result.add(build.asImage());
        return result;
    }

    public static BufferedImage createTitleImage(String message){
        int witdh = 1000;
        int height = 140;
        int leftPadding = 0;
        int topPadding = 0;
        int bottomPadding = 0;
        //int linePadding = 20;
        ImgCreateWrapper.Builder build = ImgCreateWrapper.build()
                .setImgW(witdh)
                .setImgH(height)
                .setLeftPadding(leftPadding)
                .setRightPadding(leftPadding)
                .setTopPadding(topPadding)
                .setBottomPadding(bottomPadding)
                //.setLinePadding(linePadding)
                .setDrawStyle(ImgCreateOptions.DrawStyle.VERTICAL_FIXED)
                .setBgColor(Color.white)
                .setBorder(true)
                .setBorderColor(ColorUtil.int2color(0xFFCD3E4F))
                .setBorderLeftPadding(4)
                .setBorderTopPadding(4)
                .setBorderBottomPadding(4);

        // title
        build.setFont(new Font("宋体", Font.BOLD, 64));
        build.setFontColor(Color.black);
        build.setAlignStyle(ImgCreateOptions.AlignStyle.CENTER);
        build.setLinePadding(20);
        ImgCreateWrapper.Builder builder = build.drawContent(message);
        return builder.asImage();
    }



    private static ImgCreateWrapper.Builder createNewsImgBuilder() {
        int witdh = 1000;
        int height = 1020;
        int leftPadding = 20;
        int rightPadding = 5;
        int topPadding = 5;
        int bottomPadding = 5;
        int linePadding = 20;
        ImgCreateWrapper.Builder build =
            ImgCreateWrapper.build().setImgW(witdh).setImgH(height).setLeftPadding(leftPadding)
                .setRightPadding(rightPadding).setTopPadding(topPadding).setBottomPadding(bottomPadding)
                .setLinePadding(linePadding).setDrawStyle(ImgCreateOptions.DrawStyle.VERTICAL_FIXED)
                .setBgColor(Color.white).setBorder(true).setBorderColor(ColorUtil.int2color(0xFFCD3E4F))
                .setBorderLeftPadding(4).setBorderTopPadding(4).setBorderBottomPadding(4);
        build.setFont(new Font("宋体", Font.PLAIN, 40));
        build.setFontColor(Color.black);
        build.setAlignStyle(ImgCreateOptions.AlignStyle.LEFT);
        return build;
    }


    private static BufferedImage createBg(String topic)throws Exception{
        int witdh = 1080;
        int height = 1439;
        int leftPadding = 0;
        int topPadding = 0;
        int bottomPadding = 0;
        int linePadding = 0;
        ImgCreateWrapper.Builder build = ImgCreateWrapper.build()
                .setImgW(witdh)
                .setImgH(height)
                .setLeftPadding(leftPadding)
                .setRightPadding(leftPadding)
                .setTopPadding(topPadding)
                .setBottomPadding(bottomPadding)
                .setLinePadding(linePadding)
                //.setAlignStyle(ImgCreateOptions.AlignStyle.LEFT)
                .setDrawStyle(ImgCreateOptions.DrawStyle.VERTICAL_FIXED)
                //.setBgColor(ColorUtil.int2color(0xFFD6BEA9));
                //.setBgColor(Color.cyan)
                .setBgImg(ImageLoadUtil.getImageByPath("bk1.png"));

        //.setBorder(true)
                //.setBorderColor(Color.pink);
        // title
        build.setFont(new Font("宋体", Font.BOLD, 80));
        build.setFontColor(Color.black);
        build.setAlignStyle(ImgCreateOptions.AlignStyle.CENTER);
        //"60秒知天下"
        ImgCreateWrapper.Builder builder = build.drawContent(topic);
        return builder.asImage();
    }



    public static void combineImage( BufferedImage bg,BufferedImage title, BufferedImage news,String text, String out) throws Exception {
            int w = bg.getWidth();
            int h = bg.getHeight();
            /* */
            int rectWidth = 716;
            int rectHigh = 106;
            int beginX = 180;
            int beginY = 126;

            RectFillCell fillCell = new RectFillCell();
            fillCell.setX(beginX);
            fillCell.setY(beginY);
            fillCell.setW(rectWidth);
            fillCell.setH(rectHigh);
            fillCell.setRadius(120);
            //87CEFA
            //90EE90
            //FFFFE0
            //DDA0DD
            //FFC0CB
            //FFA500
            //87CEFA
            //ColorUtil.int2color(0xFFCD3E4F)
            //0xFFBE8225
            fillCell.setColor(ColorUtil.int2color(0xFFCD3E4F));

            OvalFillCell leftOvalFillCell = new OvalFillCell();
            leftOvalFillCell.setColor(ColorUtil.int2color(0xFFD6BEA9));
            leftOvalFillCell.setX(beginX + 40);
            leftOvalFillCell.setY(beginY + rectHigh / 2 - 10);
            leftOvalFillCell.setH(20);
            leftOvalFillCell.setW(20);

            OvalFillCell rightFillCell = new OvalFillCell();
            rightFillCell.setColor(ColorUtil.int2color(0xFFD6BEA9));
            rightFillCell.setX(beginX + rectWidth - 40 - 20);
            rightFillCell.setY(beginY + rectHigh / 2 - 10);
            rightFillCell.setH(20);
            rightFillCell.setW(20);

            TextCell textCell = new TextCell();
            textCell.setColor(Color.white);
            textCell.addText(text);
            textCell.setFont(new Font("幼圆", Font.PLAIN, 60));
            textCell.setStartX(leftOvalFillCell.getX() + 60);
            textCell.setStartY(beginY + 70);
            textCell.setEndX(rightFillCell.getX() - 60);
            textCell.setEndY(beginY + 30);
            textCell.setDrawStyle(ImgCreateOptions.DrawStyle.HORIZONTAL);
            textCell.setAlignStyle(ImgCreateOptions.AlignStyle.CENTER);

            RectCell rect = new RectCell();
            rect.setX(40);
            rect.setY(fillCell.getY() + fillCell.getH() + 1);
            rect.setW(w - 40 * 2);
            rect.setH(160);
            rect.setStroke(new BasicStroke(2));
            //fillCell.setRadius(120);
            rect.setColor(Color.red);


            Graphics2D g2d = GraphicUtil.getG2d(bg);
            List<IMergeCell> list = new ArrayList<>();
            list.add(fillCell);
            //list.add(textCell);
            list.add(leftOvalFillCell);
            list.add(rightFillCell);
            list.add(textCell);
            //list.add(rect);
            list.stream().forEach(s -> s.draw(g2d));
            g2d.drawImage(img_alpha(title, 150), null, 40, fillCell.getY() + fillCell.getH() + 1);

            g2d.drawImage(img_alpha(news, 150), null, 40, fillCell.getY() + fillCell.getH() + 1 + 141);
            System.out.println("---绘制完成---");
            try {
                ImageIO.write(bg, "png", new File("/Users/salk/Documents/小红书/" + out + ".png"));
            } catch (Exception e) {
                e.printStackTrace();
            }

    }



    public static BufferedImage img_alpha(BufferedImage imgsrc,int alpha) {
        try {
            //创建一个包含透明度的图片,半透明效果必须要存储为png合适才行，存储为jpg，底色为黑色
            BufferedImage back = new BufferedImage(imgsrc.getWidth(), imgsrc.getHeight(), BufferedImage.TYPE_INT_ARGB);
            int width = imgsrc.getWidth();
            int height = imgsrc.getHeight();
            for (int j = 0; j < height; j++) {
                for (int i = 0; i < width; i++) {
                    int rgb = imgsrc.getRGB(i, j);
                    Color color = new Color(rgb);
                    Color newcolor = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
                    back.setRGB(i, j, newcolor.getRGB());
                }
            }
            return back;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
