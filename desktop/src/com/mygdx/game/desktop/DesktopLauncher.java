package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.MyGdxGame;

/**
 * 游戏启动类
 */
public class DesktopLauncher {
    /**
     * 通过 LwjglApplicationConfiguration的对象设置游戏基本信息
     * title为Shoot Bird Game
     * 游戏窗口为 1280 * 800
     * @param arg None
     */
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Shoot Bird Game";
        config.width = 1280;
        config.height = 800;
        new LwjglApplication(new MyGdxGame(), config);
    }
}
