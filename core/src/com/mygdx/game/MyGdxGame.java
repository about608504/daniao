package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class MyGdxGame extends Game {

    private SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();

        setScreen(new StartScreen(this));
    }

    @Override
    public void render(){
        super.render();
    }

}
