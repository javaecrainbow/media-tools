package com.github.hui.quick.plugin.image.wrapper.merge.cell;

import java.awt.*;

/**
 * @author salkli
 * @since 2023/2/26
 **/
public class RoundCell implements IMergeCell {
    /**
     * 起始坐标
     */
    private int x, y;

    /**
     * 矩形宽高
     */
    private int w, h;
    /**
     * 虚线样式，指定线宽等，如 {@link CellConstants#RECT_DEFAULT_DASH}
     */
    private Stroke stroke;

    /**
     * 颜色
     */
    private Color color;
    @Override
    public void draw(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.drawOval(x,y,w,h);
        if(this.stroke!=null){
            g2d.setStroke(this.stroke);

        }

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

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Stroke getStroke() {
        return stroke;
    }

    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }
}
