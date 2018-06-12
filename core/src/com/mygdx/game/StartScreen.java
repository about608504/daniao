package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * @Author: Liu
 * @Date: 2018/5/31 21:52
 */

//启动页面
public class StartScreen implements Screen {

    private Stage stage;
    private Texture texture;
    private SpriteBatch batch;
    private MyGdxGame game;
    private int score = -1;
    private FreeTypeFontGenerator generator;
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    private BitmapFont font;

    public StartScreen(MyGdxGame game, int score) {
        generator = new FreeTypeFontGenerator(Gdx.files.internal("core/assets/font/font.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.characters = "1234567890TotalScore:/欢迎进入游戏";
        font = generator.generateFont(parameter);
        this.game = game;
        this.score = score;
    }

    public StartScreen(MyGdxGame game) {
        generator = new FreeTypeFontGenerator(Gdx.files.internal("core/assets/font/font.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.characters = "欢迎进入游戏";
        font = generator.generateFont(parameter);
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage();
        texture = new Texture(Gdx.files.internal("core/assets/bg1.jpg"));
        Texture upTexture = new Texture(Gdx.files.internal("core/assets/buttonup.jpg"));
        Texture downTexture = new Texture(Gdx.files.internal("core/assets/buttondown.jpg"));
        Button.ButtonStyle style = new Button.ButtonStyle();
        //按钮点击样式
        style.up = new TextureRegionDrawable(new TextureRegion(upTexture));
        style.down = new TextureRegionDrawable(new TextureRegion(downTexture));
        Button button = new Button(style);

        button.setPosition(640 - 50, 400 - 28);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y){
                game.setScreen(new GameScreen(game));
            }
        });

        stage.addActor(button);

        Gdx.input.setInputProcessor(stage);
    }

    //轮询绘制图像
    @Override
    public void render(float delta) {
        batch = new SpriteBatch();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(texture, 0, 0);
        font.draw(batch, score == -1 ? "欢迎进入游戏" : "TotalScore:" + score, 585, 300);
        batch.end();
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
