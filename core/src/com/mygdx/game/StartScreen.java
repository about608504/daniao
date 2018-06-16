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
 * @author : Liu BCY WY STX
 * @date : 2018/5/31 21:52
 */

/**
 * 启动页面类
 */
public class StartScreen implements Screen {

    /**
    舞台对象，在舞台内添加组件
     */
    private Stage stage;
    /**
     * 贴图对象
     */
    private Texture texture;
    /**
     * 游戏逻辑处理批
     */
    private SpriteBatch batch;
    /**
     * 游戏对象
     */
    private MyGdxGame game;
    /**
     * 分数，当分数为-1时，显示欢迎进入游戏，即未进行游戏
     */
    private int score = -1;
    /**
     * 字体贴图器
     */
    private FreeTypeFontGenerator generator;
    /**
     * 生成字体贴图
     */
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    /**
     * 获取字体贴图
     */
    private BitmapFont font;

    /**
     * 构造方法
     * 初始化字体贴图，初始化分数，游戏对象
     * @param game 游戏对象
     * @param score 分数
     */
    public StartScreen(MyGdxGame game, int score) {
        generator = new FreeTypeFontGenerator(Gdx.files.internal("core/assets/font/font.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.characters = "1234567890TotalScore:/Welcome";
        font = generator.generateFont(parameter);
        this.game = game;
        this.score = score;
    }

    /**
     * 构造函数
     * 初始化字体贴图，游戏对象
     * @param game
     */
    public StartScreen(MyGdxGame game) {
        generator = new FreeTypeFontGenerator(Gdx.files.internal("core/assets/font/font.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.characters = "Welcome";
        font = generator.generateFont(parameter);
        this.game = game;
    }

    /**
     * 重写父类show()方法
     * 绘制欢迎界面逻辑
     */
    @Override
    public void show() {
        //新建舞台对象
        stage = new Stage();
        /*
        初始化各种贴图对象
         */
        texture = new Texture(Gdx.files.internal("core/assets/bg1.jpg"));
        Texture upTexture = new Texture(Gdx.files.internal("core/assets/buttonup.jpg"));
        Texture downTexture = new Texture(Gdx.files.internal("core/assets/buttondown.jpg"));
        Button.ButtonStyle style = new Button.ButtonStyle();
        //按钮点击样式
        style.up = new TextureRegionDrawable(new TextureRegion(upTexture));
        style.down = new TextureRegionDrawable(new TextureRegion(downTexture));
        Button button = new Button(style);

        //设置按钮位置
        button.setPosition(640 - 50, 400 - 28);
        //设置点击事件处理器
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y){
                //跳转到游戏Screen
                game.setScreen(new GameScreen(game));
            }
        });

        //舞台对象添加演员（组件）Button
        stage.addActor(button);

        //监听舞台事件
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * 轮询绘制欢迎画面
     * @param delta
     */
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
