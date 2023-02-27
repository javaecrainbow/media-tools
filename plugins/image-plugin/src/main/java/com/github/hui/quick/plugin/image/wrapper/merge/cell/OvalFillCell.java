package com.github.hui.quick.plugin.image.wrapper.merge.cell;


import java.awt.*;
import java.util.Objects;

/**
 * 填充圆
 * Created by yihui on 2017/10/16.
 */
public class OvalFillCell implements IMergeCell {


    private Color color;


    private int x, y, w, h;


    @Override
    public void draw(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.fillOval(x,y,w,h);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OvalFillCell that = (OvalFillCell) o;

        if (x != that.x) return false;
        if (y != that.y) return false;
        if (w != that.w) return false;
        if (h != that.h) return false;
        return color.equals(that.color);
    }

    @Override
    public int hashCode() {
        int result = color.hashCode();
        result = 31 * result + x;
        result = 31 * result + y;
        result = 31 * result + w;
        result = 31 * result + h;
        return result;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
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

}
