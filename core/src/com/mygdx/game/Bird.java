package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

/**
 * @author : Liu BCY WY STX
 * @date : 2018/6/9 18:43
 *鸟类
 */
public class Bird extends Rectangle {

    /**
     * 鸟是否存活的标志
     * 1为存活
     * 0为死亡
     */
    public int isAlive;
    /**
     * 鸟的贴图对象
     */
    private Texture img;

    /**
     * 鸟的运动速率
     */
    private int rate;

    /**
     * 鸟的击中识别范围x
     */
    private int rangeX;
    /**
     * 鸟的击中识别范围y
     */
    private int rangeY;
    /**
     * 该鸟被击中时获得的分数
     */
    private int score;
    /**
     * 该鸟携带的宝箱类型
     * 0-》无宝箱
     * 1-》子弹宝箱
     * 2-》分数宝箱
     */
    private int gift;

    /**
     * gift get方法
     * @return 返回gift
     */
    public int getGift() {
        return gift;
    }

    public void setGift(int gift) {
        this.gift = gift;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getRangeX() {
        return rangeX;
    }

    public void setRangeX(int rangeX) {
        this.rangeX = rangeX;
    }

    public int getRangeY() {
        return rangeY;
    }

    public void setRangeY(int rangeY) {
        this.rangeY = rangeY;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public Texture getImg() {
        return img;
    }

    public void setImg(Texture img) {
        this.img = img;
    }

    public Bird() {
        this.isAlive = 1;
    }
}
