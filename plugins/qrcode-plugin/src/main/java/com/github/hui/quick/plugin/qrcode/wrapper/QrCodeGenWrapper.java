package com.github.hui.quick.plugin.qrcode.wrapper;

import com.github.hui.quick.plugin.base.*;
import com.github.hui.quick.plugin.base.awt.ColorUtil;
import com.github.hui.quick.plugin.base.awt.ImageLoadUtil;
import com.github.hui.quick.plugin.base.constants.MediaType;
import com.github.hui.quick.plugin.base.file.FileReadUtil;
import com.github.hui.quick.plugin.base.file.FileWriteUtil;
import com.github.hui.quick.plugin.base.gif.GifDecoder;
import com.github.hui.quick.plugin.base.gif.GifHelper;
import com.github.hui.quick.plugin.base.io.IoUtil;
import com.github.hui.quick.plugin.qrcode.constants.QuickQrUtil;
import com.github.hui.quick.plugin.qrcode.entity.RenderImgResources;
import com.github.hui.quick.plugin.qrcode.entity.RenderImgResourcesV2;
import com.github.hui.quick.plugin.qrcode.helper.QrCodeGenerateHelper;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yihui on 2017/7/17.
 */
public class QrCodeGenWrapper {
    public static Builder of(String content) {
        return new Builder().setMsg(content);
    }

    private static ByteArrayOutputStream asGif(QrCodeOptions qrCodeOptions) throws WriterException {
        try {
            BitMatrixEx bitMatrix = QrCodeGenerateHelper.encode(qrCodeOptions);
            List<ImmutablePair<BufferedImage, Integer>> list =
                    QrCodeGenerateHelper.toGifImages(qrCodeOptions, bitMatrix);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            GifHelper.saveGif(list, outputStream);
            return outputStream;
        } finally {
            QuickQrUtil.clear();
        }
    }

    private static BufferedImage asBufferedImage(QrCodeOptions qrCodeOptions) throws WriterException, IOException {
        try {
            BitMatrixEx bitMatrix = QrCodeGenerateHelper.encode(qrCodeOptions);
            return QrCodeGenerateHelper.toBufferedImage(qrCodeOptions, bitMatrix);
        } finally {
            QuickQrUtil.clear();
        }
    }

    private static String asString(QrCodeOptions qrCodeOptions) throws WriterException, IOException {
        if (qrCodeOptions.gifQrCode()) {
            // ?????????????????????
            try (ByteArrayOutputStream outputStream = asGif(qrCodeOptions)) {
                return Base64Util.encode(outputStream);
            }
        }

        // ?????????????????????????????????
        BufferedImage bufferedImage = asBufferedImage(qrCodeOptions);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, qrCodeOptions.getPicType(), outputStream);
            return Base64Util.encode(outputStream);
        }
    }

    private static boolean asFile(QrCodeOptions qrCodeOptions, String absFileName) throws WriterException, IOException {
        File file = new File(absFileName);
        FileWriteUtil.mkDir(file.getParentFile());

        if (qrCodeOptions.gifQrCode()) {
            // ?????????????????????
            try (ByteArrayOutputStream output = asGif(qrCodeOptions)) {
                FileOutputStream out = new FileOutputStream(file);
                out.write(output.toByteArray());
                out.flush();
                out.close();
            }

            return true;
        }

        BufferedImage bufferedImage = asBufferedImage(qrCodeOptions);
        if (!ImageIO.write(bufferedImage, qrCodeOptions.getPicType(), file)) {
            throw new IOException("save QrCode image to: " + absFileName + " error!");
        }

        return true;
    }


    public static class Builder {
        private static Logger log = LoggerFactory.getLogger(QrCodeGenWrapper.Builder.class);
        private static final MatrixToImageConfig DEFAULT_CONFIG = new MatrixToImageConfig();

        /**
         * The message to put into QrCode
         */
        private String msg;

        /**
         * qrcode image width
         */
        private Integer w;


        /**
         * qrcode image height
         */
        private Integer h;


        /**
         * qrcode message's code, default UTF-8
         */
        private String code = "utf-8";


        /**
         * 0 - 4
         */
        private Integer padding;


        /**
         * error level, default H
         */
        private ErrorCorrectionLevel errorCorrection = ErrorCorrectionLevel.H;


        /**
         * output qrcode image type, default png
         */
        private String picType = "png";

        private QrCodeOptions.FrontImgOptions.FtImgOptionsBuilder ftImgOptions;

        private QrCodeOptions.BgImgOptions.BgImgOptionsBuilder bgImgOptions;

        private QrCodeOptions.LogoOptions.LogoOptionsBuilder logoOptions;

        private QrCodeOptions.DrawOptions.DrawOptionsBuilder drawOptions;

        private QrCodeOptions.DetectOptions.DetectOptionsBuilder detectOptions;


        public Builder() {
            // ?????????????????????????????????
            ftImgOptions = QrCodeOptions.FrontImgOptions.builder().imgStyle(QrCodeOptions.ImgStyle.NORMAL);

            // ?????????????????????????????????
            bgImgOptions =
                    QrCodeOptions.BgImgOptions.builder().bgImgStyle(QrCodeOptions.BgImgStyle.OVERRIDE).opacity(0.85f);


            // ???????????????????????????logo??? ?????????
            logoOptions = QrCodeOptions.LogoOptions.builder().logoStyle(QrCodeOptions.LogoStyle.NORMAL).border(false)
                    .rate(12);


            // ?????????????????????????????????
            drawOptions =
                    QrCodeOptions.DrawOptions.builder().drawStyle(QrCodeOptions.DrawStyle.RECT).bgColor(Color.WHITE)
                            .preColor(Color.BLACK).diaphaneityFill(false).enableScale(false);

            // ????????????
            detectOptions = QrCodeOptions.DetectOptions.builder();
        }


        public String getMsg() {
            return msg;
        }

        public Builder setMsg(String msg) {
            this.msg = msg;
            return this;
        }

        public Integer getW() {
            return w == null ? (h == null ? 200 : h) : w;
        }

        public Builder setW(Integer w) {
            if (w != null && w <= 0) {
                throw new IllegalArgumentException("?????????????????????????????????0");
            }
            this.w = w;
            return this;
        }

        public Integer getH() {
            return h == null ? (w == null ? 200 : w) : h;
        }

        public Builder setH(Integer h) {
            if (h != null && h <= 0) {
                throw new IllegalArgumentException("???????????????????????????????????????0");
            }
            this.h = h;
            return this;
        }

        public Builder setCode(String code) {
            this.code = code;
            return this;
        }

        public Integer getPadding() {
            if (padding == null) {
                return 1;
            }

            if (padding < 0) {
                return 0;
            }

            if (padding > 4) {
                return 4;
            }

            return padding;
        }

        public Builder setPadding(Integer padding) {
            this.padding = padding;
            return this;
        }

        public Builder setPicType(String picType) {
            this.picType = picType;
            return this;
        }

        public Builder setErrorCorrection(ErrorCorrectionLevel errorCorrection) {
            this.errorCorrection = errorCorrection;
            return this;
        }

        public Builder setQrStyle(QrCodeOptions.ImgStyle qrStyle) {
            this.drawOptions.qrStyle(qrStyle);
            return this;
        }

        public Builder setQrCornerRadiusRate(float radius) {
            this.drawOptions.cornerRadius(radius);
            return this;
        }


        /////////////// logo ???????????? ///////////////

        public Builder setLogo(String logo) throws IOException {
            try {
                return setLogo(ImageLoadUtil.getImageByPath(logo));
            } catch (IOException e) {
                log.error("load logo error!", e);
                throw new IOException("load logo error!", e);
            }
        }

        public Builder setLogo(InputStream inputStream) throws IOException {
            try {
                return setLogo(ImageIO.read(inputStream));
            } catch (IOException e) {
                log.error("load backgroundImg error!", e);
                throw new IOException("load backgroundImg error!", e);
            }
        }

        public Builder setLogo(BufferedImage img) {
            logoOptions.logo(img);
            return this;
        }

        public Builder setLogoStyle(QrCodeOptions.LogoStyle logoStyle) {
            logoOptions.logoStyle(logoStyle);
            return this;
        }

        public Builder setLogoBgColor(Integer color) {
            if (color == null) {
                return this;
            }

            return setLogoBgColor(ColorUtil.int2color(color));
        }

        /**
         * logo ????????????
         *
         * @param color
         * @return
         */
        public Builder setLogoBgColor(Color color) {
            logoOptions.border(true);
            logoOptions.borderColor(color);
            return this;
        }

        public Builder setLogoBorderBgColor(Integer color) {
            if (color == null) {
                return this;
            }
            return setLogoBorderBgColor(ColorUtil.int2color(color));
        }

        /**
         * logo ??????????????????
         *
         * @param color
         * @return
         */

        public Builder setLogoBorderBgColor(Color color) {
            logoOptions.border(true);
            logoOptions.outerBorderColor(color);
            return this;
        }

        public Builder setLogoBorder(boolean border) {
            logoOptions.border(border);
            return this;
        }

        public Builder setLogoRate(int rate) {
            logoOptions.rate(rate);
            return this;
        }

        /**
         * logo?????????
         *
         * @param opacity
         * @return
         */
        public Builder setLogoOpacity(float opacity) {
            logoOptions.opacity(opacity);
            return this;
        }

        /**
         * ????????????logo??????????????????????????????
         *
         * @param clearLogoArea true ????????????logo??????????????????
         * @return
         */
        public Builder setClearLogoArea(boolean clearLogoArea) {
            logoOptions.clearLogoArea(clearLogoArea);
            return this;
        }

        ///////////////// logo???????????? ///////////////


        // ------------------------------------------


        /////////////// ????????? ???????????? ///////////////

        public Builder setFtImg(String ftImg) throws IOException {
            try {
                return setFtImg(FileReadUtil.getStreamByFileName(ftImg));
            } catch (IOException e) {
                log.error("load backgroundImg error!", e);
                throw new IOException("load backgroundImg error!", e);
            }
        }


        public Builder setFtImg(InputStream inputStream) throws IOException {
            try (ByteArrayInputStream target = IoUtil.toByteArrayInputStream(inputStream)) {
                MediaType media = MediaType.typeOfMagicNum(FileReadUtil.getMagicNum(target));
                if (media == MediaType.ImageGif) {
                    GifDecoder gifDecoder = new GifDecoder();
                    gifDecoder.read(target);
                    ftImgOptions.gifDecoder(gifDecoder);
                    return this;
                } else {
                    return setFtImg(ImageIO.read(target));
                }
            } catch (IOException e) {
                log.error("load backgroundImg error!", e);
                throw new IOException("load backgroundImg error!", e);
            }
        }


        public Builder setFtImg(BufferedImage bufferedImage) {
            ftImgOptions.ftImg(bufferedImage);
            return this;
        }

        /**
         * ???????????????
         *
         * @param imgStyle
         * @return
         */
        public Builder setFtImgStyle(QrCodeOptions.ImgStyle imgStyle) {
            ftImgOptions.imgStyle(imgStyle);
            return this;
        }

        /**
         * ????????????????????????
         *
         * @param radius
         * @return
         */
        public Builder setFtCornerRadiusRate(float radius) {
            ftImgOptions.radius(radius);
            return this;
        }

        public Builder setFtW(int w) {
            ftImgOptions.ftW(w);
            return this;
        }

        public Builder setFtH(int h) {
            ftImgOptions.ftH(h);
            return this;
        }

        /**
         * startX ?????????????????????????????????x????????? ???????????????0,0)
         *
         * @param startX
         * @return
         */
        public Builder setFtStartX(int startX) {
            ftImgOptions.startX(startX);
            return this;
        }

        /**
         * startY ?????????????????????????????????y?????????????????????(0, 0)
         *
         * @param startY
         * @return
         */
        public Builder setFtStartY(int startY) {
            ftImgOptions.startY(startY);
            return this;
        }

        public Builder setFtFillColor(Integer color) {
            if (color == null) {
                return this;
            }

            return setFtFillColor(ColorUtil.int2color(color));
        }

        public Builder setFtFillColor(Color color) {
            ftImgOptions.fillImg(color);
            return this;
        }


        /////////////// ????????? ???????????? ///////////////


        // ------------------------------------------


        /////////////// ?????? ???????????? ///////////////

        public Builder setBgImg(String bgImg) throws IOException {
            try {
                return setBgImg(FileReadUtil.getStreamByFileName(bgImg));
            } catch (IOException e) {
                log.error("load backgroundImg error!", e);
                throw new IOException("load backgroundImg error!", e);
            }
        }


        public Builder setBgImg(InputStream inputStream) throws IOException {
            try (ByteArrayInputStream target = IoUtil.toByteArrayInputStream(inputStream)) {
                MediaType media = MediaType.typeOfMagicNum(FileReadUtil.getMagicNum(target));
                if (media == MediaType.ImageGif) {
                    GifDecoder gifDecoder = new GifDecoder();
                    gifDecoder.read(target);
                    bgImgOptions.gifDecoder(gifDecoder);
                    return this;
                } else {
                    return setBgImg(ImageIO.read(target));
                }
            } catch (IOException e) {
                log.error("load backgroundImg error!", e);
                throw new IOException("load backgroundImg error!", e);
            }
        }


        public Builder setBgImg(BufferedImage bufferedImage) {
            bgImgOptions.bgImg(bufferedImage);
            return this;
        }

        /**
         * ???????????????
         *
         * @param imgStyle
         * @return
         */
        public Builder setBgImgStyle(QrCodeOptions.ImgStyle imgStyle) {
            bgImgOptions.imgStyle(imgStyle);
            return this;
        }

        /**
         * ????????????????????????
         *
         * @param radius
         * @return
         */
        public Builder setBgCornerRadiusRate(float radius) {
            bgImgOptions.cornerRadius(radius);
            return this;
        }

        public Builder setBgStyle(QrCodeOptions.BgImgStyle bgImgStyle) {
            bgImgOptions.bgImgStyle(bgImgStyle);
            return this;
        }


        public Builder setBgW(int w) {
            bgImgOptions.bgW(w);
            return this;
        }

        public Builder setBgH(int h) {
            bgImgOptions.bgH(h);
            return this;
        }


        public Builder setBgOpacity(float opacity) {
            bgImgOptions.opacity(opacity);
            return this;
        }


        public Builder setBgStartX(int startX) {
            bgImgOptions.startX(startX);
            return this;
        }

        public Builder setBgStartY(int startY) {
            bgImgOptions.startY(startY);
            return this;
        }


        /////////////// ?????? ???????????? ///////////////


        // ------------------------------------------


        /////////////// ???????????? ???????????? ///////////////
        public Builder setDetectImg(String detectImg) throws IOException {
            try {
                return setDetectImg(ImageLoadUtil.getImageByPath(detectImg));
            } catch (IOException e) {
                log.error("load detectImage error! img:{}", detectImg, e);
                throw new IOException("load detectImage error!", e);
            }
        }


        public Builder setDetectImg(InputStream detectImg) throws IOException {
            try {
                return setDetectImg(ImageIO.read(detectImg));
            } catch (IOException e) {
                log.error("load detectImage error!", e);
                throw new IOException("load detectImage error!", e);
            }
        }


        public Builder setDetectImg(BufferedImage detectImg) {
            detectOptions.detectImg(detectImg);
            detectOptions.special(true);
            return this;
        }

        /**
         * ?????????????????????
         *
         * @param detectImg
         * @return
         * @throws IOException
         */
        public Builder setLTDetectImg(String detectImg) throws IOException {
            try {
                return setLTDetectImg(ImageLoadUtil.getImageByPath(detectImg));
            } catch (IOException e) {
                log.error("load detectImage error! img:{}", detectImg, e);
                throw new IOException("load detectImage error!", e);
            }
        }

        /**
         * ?????????????????????
         *
         * @param detectImg
         * @return
         * @throws IOException
         */
        public Builder setLTDetectImg(InputStream detectImg) throws IOException {
            try {
                return setLTDetectImg(ImageIO.read(detectImg));
            } catch (IOException e) {
                log.error("load detectImage error! e:{}", e);
                throw new IOException("load detectImage error!", e);
            }
        }

        /**
         * ?????????????????????
         *
         * @param detectImg
         * @return
         * @throws IOException
         */
        public Builder setLTDetectImg(BufferedImage detectImg) {
            detectOptions.detectImgLT(detectImg);
            detectOptions.special(true);
            return this;
        }

        /**
         * ?????????????????????
         *
         * @param detectImg
         * @return
         * @throws IOException
         */
        public Builder setRTDetectImg(String detectImg) throws IOException {
            try {
                return setRTDetectImg(ImageLoadUtil.getImageByPath(detectImg));
            } catch (IOException e) {
                log.error("load detectImage error! img:{}", detectImg, e);
                throw new IOException("load detectImage error!", e);
            }
        }

        /**
         * ?????????????????????
         *
         * @param detectImg
         * @return
         * @throws IOException
         */
        public Builder setRTDetectImg(InputStream detectImg) throws IOException {
            try {
                return setRTDetectImg(ImageIO.read(detectImg));
            } catch (IOException e) {
                log.error("load detectImage error! img:{}", detectImg, e);
                throw new IOException("load detectImage error!", e);
            }
        }

        /**
         * ?????????????????????
         *
         * @param detectImg
         * @return
         * @throws IOException
         */
        public Builder setRTDetectImg(BufferedImage detectImg) {
            detectOptions.detectImgRT(detectImg);
            detectOptions.special(true);
            return this;
        }

        /**
         * ?????????????????????
         *
         * @param detectImg
         * @return
         * @throws IOException
         */
        public Builder setLDDetectImg(String detectImg) throws IOException {
            try {
                return setLDDetectImg(ImageLoadUtil.getImageByPath(detectImg));
            } catch (IOException e) {
                log.error("load detectImage error! img:{}", detectImg, e);
                throw new IOException("load detectImage error!", e);
            }
        }

        /**
         * ?????????????????????
         *
         * @param detectImg
         * @return
         * @throws IOException
         */
        public Builder setLDDetectImg(InputStream detectImg) throws IOException {
            try {
                return setLDDetectImg(ImageIO.read(detectImg));
            } catch (IOException e) {
                log.error("load detectImage error! img:{}", detectImg, e);
                throw new IOException("load detectImage error!", e);
            }
        }

        /**
         * ?????????????????????
         *
         * @param detectImg
         * @return
         * @throws IOException
         */
        public Builder setLDDetectImg(BufferedImage detectImg) {
            detectOptions.detectImgLD(detectImg);
            detectOptions.special(true);
            return this;
        }

        public Builder setDetectOutColor(Integer outColor) {
            if (outColor == null) {
                return this;
            }

            return setDetectOutColor(ColorUtil.int2color(outColor));
        }

        public Builder setDetectOutColor(Color outColor) {
            detectOptions.outColor(outColor);
            return this;
        }

        public Builder setDetectInColor(Integer inColor) {
            if (inColor == null) {
                return this;
            }

            return setDetectInColor(ColorUtil.int2color(inColor));
        }

        public Builder setDetectInColor(Color inColor) {
            detectOptions.inColor(inColor);
            return this;
        }

        /**
         * ??????????????????????????????????????????????????????
         *
         * @return
         */
        public Builder setDetectSpecial() {
            detectOptions.special(true);
            return this;
        }

        /////////////// ???????????? ???????????? ///////////////


        // ------------------------------------------


        /////////////// ??????????????? ???????????? ///////////////

        public Builder setDrawStyle(String style) {
            return setDrawStyle(QrCodeOptions.DrawStyle.getDrawStyle(style));
        }


        public Builder setDrawStyle(QrCodeOptions.DrawStyle drawStyle) {
            drawOptions.drawStyle(drawStyle);
            return this;
        }


        /**
         * ???????????????????????????????????????????????????????????????????????????????????????true????????????bgColor??????????????????????????????false????????????????????????????????????
         */
        public Builder setDiaphaneityFill(boolean fill) {
            drawOptions.diaphaneityFill(fill);
            return this;
        }

        public Builder setDrawPreColor(int color) {
            return setDrawPreColor(ColorUtil.int2color(color));
        }


        public Builder setDrawPreColor(Color color) {
            drawOptions.preColor(color);
            return this;
        }

        public Builder setDrawBgColor(int color) {
            return setDrawBgColor(ColorUtil.int2color(color));
        }

        public Builder setDrawBgColor(Color color) {
            drawOptions.bgColor(color);
            return this;
        }

        public Builder setDrawBgImg(String img) throws IOException {
            try {
                return setDrawBgImg(ImageLoadUtil.getImageByPath(img));
            } catch (IOException e) {
                log.error("load drawBgImg error! img:{}", img, e);
                throw new IOException("load drawBgImg error!", e);
            }
        }

        public Builder setDrawBgImg(InputStream img) throws IOException {
            try {
                return setDrawBgImg(ImageIO.read(img));
            } catch (IOException e) {
                log.error("load drawBgImg error!", e);
                throw new IOException("load drawBgImg error!", e);
            }
        }

        public Builder setDrawBgImg(BufferedImage img) {
            drawOptions.bgImg(img);
            return this;
        }

        public Builder setDrawEnableScale(boolean enable) {
            drawOptions.enableScale(enable);
            return this;
        }


        public Builder setDrawImg(String img) throws IOException {
            try {
                return setDrawImg(ImageLoadUtil.getImageByPath(img));
            } catch (IOException e) {
                log.error("load draw img error! img: {}", img, e);
                throw new IOException("load draw img error!", e);
            }
        }

        public Builder setDrawImg(InputStream input) throws IOException {
            try {
                return setDrawImg(ImageIO.read(input));
            } catch (IOException e) {
                log.error("load draw img error!", e);
                throw new IOException("load draw img error!", e);
            }
        }

        public Builder setDrawImg(BufferedImage img) {
            addImg(1, 1, img);
            return this;
        }


        public Builder addImg(int row, int col, BufferedImage img) {
            return addImg(row, col, img, RenderImgResources.NO_LIMIT_COUNT);
        }


        public Builder addImg(int row, int col, BufferedImage img, int count) {
            if (img == null) {
                return this;
            }
            drawOptions.enableScale(true);
            drawOptions.drawImg(row, col, img, count);
            return this;
        }

        public Builder addImg(int row, int col, String img) throws IOException {
            return addImg(row, col, img, RenderImgResources.NO_LIMIT_COUNT);
        }

        public Builder addImg(int row, int col, String img, int count) throws IOException {
            try {
                return addImg(row, col, ImageLoadUtil.getImageByPath(img), count);
            } catch (IOException e) {
                log.error("load draw size4img error! img: {}", img, e);
                throw new IOException("load draw row:" + row + ", col:" + col + " img error!", e);
            }
        }

        public Builder addImg(int row, int col, InputStream img) throws IOException {
            return addImg(row, col, img, RenderImgResources.NO_LIMIT_COUNT);
        }

        public Builder addImg(int row, int col, InputStream img, int count) throws IOException {
            try {
                return addImg(row, col, ImageIO.read(img), count);
            } catch (IOException e) {
                log.error("load draw size4img error!", e);
                throw new IOException("load draw row:" + row + ", col:" + col + " img error!", e);
            }
        }

        public Builder setImgResourcesForV2(RenderImgResourcesV2 imgResourcesForV2) {
            drawOptions.setImgResourcesV2(imgResourcesForV2);
            return this;
        }

        /**
         * ?????????????????????????????????
         *
         * @param text
         * @return
         */
        public Builder setQrText(String text) {
            drawOptions.text(text);
            return this;
        }

        public Builder setQrTxtMode(QrCodeOptions.TxtMode txtMode) {
            drawOptions.txtMode(txtMode);
            return this;
        }

        /**
         * ?????????
         *
         * @param fontName
         * @return
         */
        public Builder setQrDotFontName(String fontName) {
            drawOptions.fontName(fontName);
            return this;
        }

        /**
         * ????????????
         *
         * @param fontStyle 0 {@link Font#PLAIN} 1 {@link Font#BOLD} 2 {@link Font#ITALIC}
         * @return
         */
        public Builder setQrDotFontStyle(int fontStyle) {
            drawOptions.fontStyle(fontStyle);
            return this;
        }

        /////////////// ??????????????? ???????????? ///////////////


        private void validate() {
            if (msg == null || msg.length() == 0) {
                throw new IllegalArgumentException("????????????????????????????????????!");
            }
        }


        public QrCodeOptions build() {
            this.validate();

            QrCodeOptions qrCodeConfig = new QrCodeOptions();
            qrCodeConfig.setMsg(getMsg());
            qrCodeConfig.setH(getH());
            qrCodeConfig.setW(getW());


            // ???????????????
            QrCodeOptions.FrontImgOptions ftOp = ftImgOptions.build();
            if (ftOp.getFtImg() == null && ftOp.getGifDecoder() == null) {
                qrCodeConfig.setFtImgOptions(null);
            } else {
                qrCodeConfig.setFtImgOptions(ftOp);
            }

            // ??????????????????
            QrCodeOptions.BgImgOptions bgOp = bgImgOptions.build();
            if (bgOp.getBgImg() == null && bgOp.getGifDecoder() == null) {
                qrCodeConfig.setBgImgOptions(null);
            } else {
                qrCodeConfig.setBgImgOptions(bgOp);
            }


            // ??????logo??????
            QrCodeOptions.LogoOptions logoOp = logoOptions.build();
            if (logoOp.getLogo() == null) {
                qrCodeConfig.setLogoOptions(null);
            } else {
                qrCodeConfig.setLogoOptions(logoOp);
            }


            // ????????????
            QrCodeOptions.DrawOptions drawOp = drawOptions.build();
            qrCodeConfig.setDrawOptions(drawOp);


            // ??????detect????????????
            QrCodeOptions.DetectOptions detectOp = detectOptions.build();
            if (detectOp.getOutColor() == null && detectOp.getInColor() == null) {
                detectOp.setInColor(drawOp.getPreColor());
                detectOp.setOutColor(drawOp.getPreColor());
            } else if (detectOp.getOutColor() == null) {
                detectOp.setOutColor(detectOp.getOutColor());
            } else if (detectOp.getInColor() == null) {
                detectOp.setInColor(detectOp.getInColor());
            }
            qrCodeConfig.setDetectOptions(detectOp);


            if (qrCodeConfig.getBgImgOptions() != null &&
                    qrCodeConfig.getBgImgOptions().getBgImgStyle() == QrCodeOptions.BgImgStyle.PENETRATE) {
                // ??????????????????????????????????????????
                drawOp.setPreColor(ColorUtil.OPACITY);
                qrCodeConfig.getBgImgOptions().setOpacity(1);
                qrCodeConfig.getDetectOptions().setInColor(ColorUtil.OPACITY);
                qrCodeConfig.getDetectOptions().setOutColor(ColorUtil.OPACITY);
            }

            // ????????????????????????
            qrCodeConfig.setPicType(picType);

            // ??????????????????
            Map<EncodeHintType, Object> hints = new HashMap<>(3);
            hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrection);
            hints.put(EncodeHintType.CHARACTER_SET, code);
            hints.put(EncodeHintType.MARGIN, this.getPadding());
            qrCodeConfig.setHints(hints);

            return qrCodeConfig;
        }


        public String asString() throws IOException, WriterException {
            return QrCodeGenWrapper.asString(build());
        }


        public BufferedImage asBufferedImage() throws IOException, WriterException {
            return QrCodeGenWrapper.asBufferedImage(build());
        }

        public ByteArrayOutputStream asStream() throws WriterException, IOException {
            QrCodeOptions options = build();
            if (options.gifQrCode()) {
                return QrCodeGenWrapper.asGif(options);
            } else {
                BufferedImage img = QrCodeGenWrapper.asBufferedImage(options);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(img, options.getPicType(), outputStream);
                return outputStream;
            }
        }

        public boolean asFile(String absFileName) throws IOException, WriterException {
            return QrCodeGenWrapper.asFile(build(), absFileName);
        }

        @Override
        public String toString() {
            return "Builder{" + "msg='" + msg + '\'' + ", w=" + w + ", h=" + h + ", code='" + code + '\'' +
                    ", padding=" + padding + ", errorCorrection=" + errorCorrection + ", picType='" + picType + '\'' +
                    ", bgImgOptions=" + bgImgOptions + ", logoOptions=" + logoOptions + ", drawOptions=" + drawOptions +
                    ", detectOptions=" + detectOptions + '}';
        }
    }
}
