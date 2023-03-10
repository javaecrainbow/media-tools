package com.github.hui.quick.plugin.image.wrapper.wartermark;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by yihui on 2017/9/28.
 */
public class WaterMarkOptions {


    private BufferedImage source;

    private BufferedImage water;

    private WaterStyle style;

    private int x;

    private int y;

    private float opacity;

    private int basicW;

    public BufferedImage getSource() {
        return source;
    }

    public void setSource(BufferedImage source) {
        this.source = source;
    }

    public BufferedImage getWater() {
        return water;
    }

    public void setWater(BufferedImage water) {
        this.water = water;
    }

    public WaterStyle getStyle() {
        return style;
    }

    public void setStyle(WaterStyle style) {
        this.style = style;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    public int getBasicW() {
        return basicW;
    }

    public void setBasicW(int basicW) {
        this.basicW = basicW;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WaterMarkOptions that = (WaterMarkOptions) o;
        return x == that.x && y == that.y && Float.compare(that.opacity, opacity) == 0 && basicW == that.basicW &&
                Objects.equals(source, that.source) && Objects.equals(water, that.water) && style == that.style;
    }

    @Override
    public String toString() {
        return "WaterMarkOptions{" + "source=" + source + ", water=" + water + ", style=" + style + ", x=" + x +
                ", y=" + y + ", opacity=" + opacity + ", basicW=" + basicW + '}';
    }

    @Override
    public int hashCode() {

        return Objects.hash(source, water, style, x, y, opacity, basicW);
    }

    public enum WaterStyle {
        /**
         * ??????????????????
         */
        FILL_BG,


        /**
         * ??????????????????????????????
         */

        // ?????????
        OVERRIDE_LEFT_TOP, // ?????????
        OVERRIDE_TOP_CENTER, // ?????????
        OVERRIDE_RIGHT_TOP,

        // ??????
        OVERRIDE_LEFT_CENTER, // ??????
        OVERRIDE_CENTER, // ??????
        OVERRIDE_RIGHT_CENTER,

        // ??????
        OVERRIDE_LEFT_BOTTOM, // ??????
        OVERRIDE_BOTTOM_CENTER, // ??????
        OVERRIDE_RIGHT_BOTTOM,;


        private static Map<String, WaterStyle> map = new HashMap<>();

        static {
            for (WaterStyle style : values()) {
                map.put(style.name(), style);
            }
        }


        public static WaterStyle getStyle(String style) {
            if (style == null) {
                return OVERRIDE_RIGHT_BOTTOM;
            }

            WaterStyle ws = map.get(style.toUpperCase());
            return ws == null ? OVERRIDE_RIGHT_BOTTOM : ws;
        }
    }

}
