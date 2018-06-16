package com.mygdx.game;

import com.badlogic.gdx.Game;

/**
 * @author : Liu BCY WY STX
 * @date : 2018/6/9 19:43
 * 游戏对象类
 * 具有设置场景的作用
 */
public class MyGdxGame extends Game {

    /**
     * 初始化场景为欢迎界面
     */
    @Override
    public void create() {
        setScreen(new StartScreen(this));
    }

    @Override
    public void render(){
        super.render();
    }

}
