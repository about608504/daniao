package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @Author: Liu
 * @Date: 2018/5/31 22:56
 */
public class GameScreen implements Screen {

    private MyGdxGame game;
    private Texture shotImage;
    private Texture backgroundImage;
    private List<Texture> birdImages;
    private Sound shotSound;
    private Music birdSound;
    private Sound exchangeSound;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Rectangle shot;
    private Array<Bird> birds;
    private long lastDroptime;
    private int bulletSum = 150;
    private final int reload = 30;
    private int existBullet = reload;
    private int score = 0;
    private long exchangeTime = 2000;
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private Future<Boolean> future = executorService.submit(new TimeCounter(exchangeTime));

    public GameScreen(MyGdxGame game) {

        this.game = game;
        backgroundImage = new Texture(Gdx.files.internal("core/assets/bg.jpg"));
        birdImages = new ArrayList<>(5);
        for (int i = 0; i < 3; i++)
            birdImages.add(new Texture(Gdx.files.internal("core/assets/birds/" + String.valueOf(i+1) + ".png")));
        shotImage = new Texture(Gdx.files.internal("core/assets/zhunxin.png"));

        exchangeSound = Gdx.audio.newSound(Gdx.files.internal("core/assets/reload.mp3"));
        shotSound = Gdx.audio.newSound(Gdx.files.internal("core/assets/shot.wav"));
        birdSound = Gdx.audio.newMusic(Gdx.files.internal("core/assets/bird.wav"));
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 700);

        batch = new SpriteBatch();

        shot = new Rectangle();
        shot.x = 60/2;
        shot.y = 60/2;
        shot.width = 60;
        shot.height = 60;

        birds = new Array<>();
        spawnRaindrop();
    }

    private void spawnRaindrop() {
        Bird bird = new Bird();
        bird.x = 0;
        bird.y = MathUtils.random(0,800-32);
        bird.width = 32;
        bird.height = 32;
        int rand = (int)(Math.random() * 3);
        bird.setImg(birdImages.get(rand));
        birds.add(bird);
        lastDroptime = TimeUtils.nanoTime();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(backgroundImage, 0, 0);
        batch.draw(shotImage, shot.x, shot.y);

        for (Bird bird : birds) {
            batch.draw(bird.getImg(), bird.x, bird.y);
        }

        batch.end();

        Vector3 touchPos = new Vector3();
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);
        shot.x = touchPos.x - 64 / 2;
        shot.y = touchPos.y - 64 / 2;

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
            if (existBullet > 0)
                shotSound.play();
            else {

            }
            shot.x += Math.random() * 50;
            shot.y += Math.random() * 50;
            --existBullet;
            isExchange();
        }

        if (shot.x < 0) {
            shot.x = 0;
        }
        if (shot.x > 1280 - 50) {
            shot.x = 1280 - 50;
        }
        if (TimeUtils.nanoTime() - lastDroptime > 800000000) spawnRaindrop();
        Iterator<Bird> iterator = birds.iterator();
        while (iterator.hasNext()) {
            Bird bird = iterator.next();
            bird.x += 200 * Gdx.graphics.getDeltaTime();
            if (bird.x + 64 > 1280) {
                iterator.remove();
            }
            if (bird.y < 0){
                iterator.remove();
            }
            if ((bird.overlaps(shot)) && (bird.isAlive == 1) &&
                    Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                bird.isAlive = 0;
                birdSound.play();
                shot.x += Math.random() * 50;
                shot.y += Math.random() * 50;
                --existBullet;
                isExchange();
            }
            if (bird.isAlive == 0) {
                bird.y -= 1280 * Gdx.graphics.getDeltaTime();
            }
        }
    }

    //换弹逻辑
    private void isExchange() {
        if (existBullet <= 0){
            try {
                if (future.get()){
                    System.out.println("done!");
                    existBullet = reload;
                    exchangeSound.play(15);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        shotImage.dispose();
        shotSound.dispose();
        birdSound.dispose();
        batch.dispose();
    }
}
