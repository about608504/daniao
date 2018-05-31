package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Bird extends Rectangle {

    private Texture img;

    public Texture getImg() {
        return img;
    }

    public void setImg(Texture img) {
        this.img = img;
    }

    public int isAlive;

    /**
     * Constructs a new rectangle with all values set to zero
     */
    public Bird() {
        this.isAlive = 1;
    }
}
