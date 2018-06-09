package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MyGdxGame extends Game {

    @Override
    public void create() {
        setScreen(new StartScreen(this));
    }

    @Override
    public void render(){
        super.render();
    }

}
