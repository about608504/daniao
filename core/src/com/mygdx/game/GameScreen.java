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

import java.util.*;

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
    private long exchangeTime = 5000;
    private Timer timer = new Timer("clock");
    private boolean exchangeFlag = false;
    private boolean exchanging = false;
    //    private ExecutorService executorService = Executors.newCachedThreadPool();
//    private Future<Boolean> future = executorService.submit(new TimeCounter(exchangeTime));
    private List<Pair> pairs = Collections.unmodifiableList(
            Arrays.asList(
                    new Pair(32, 32),
                    new Pair(60, 60),
                    new Pair(60, 60)
            ));
    private List<Integer> rates = Collections.unmodifiableList(
            Arrays.asList(
                    500,
                    350,
                    300
            )
    );

    private List<Integer> scores = Collections.unmodifiableList(
            Arrays.asList(
                    50,
                    30,
                    10
            )
    );

    private class Pair {
        Pair(int x, int y) {
            this.x = x;
            this.y = y;
        }

        int x;
        int y;
    }

    public GameScreen(MyGdxGame game) {

        this.game = game;
        backgroundImage = new Texture(Gdx.files.internal("core/assets/bg.jpg"));
        birdImages = new ArrayList<>(3);
        for (int i = 0; i < 3; i++)
            birdImages.add(new Texture(Gdx.files.internal("core/assets/birds/" + String.valueOf(i + 1) + ".png")));
        shotImage = new Texture(Gdx.files.internal("core/assets/zhunxin.png"));

        exchangeSound = Gdx.audio.newSound(Gdx.files.internal("core/assets/reload.mp3"));
        shotSound = Gdx.audio.newSound(Gdx.files.internal("core/assets/shot.wav"));
        birdSound = Gdx.audio.newMusic(Gdx.files.internal("core/assets/bird.wav"));
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 700);

        batch = new SpriteBatch();

        shot = new Rectangle();
        shot.x = 60 / 2;
        shot.y = 60 / 2;
        shot.width = 30;
        shot.height = 30;

        birds = new Array<>();
        pointedBird();
    }

    private void pointedBird(Bird bird, int index){
        bird.setScore(scores.get(index));
        bird.setRangeX(pairs.get(index).x);
        bird.setRangeY(pairs.get(index).y);
        bird.x = 0;
        bird.y = MathUtils.random(0, 800 - pairs.get(index).y);
        bird.setImg(birdImages.get(index));
        bird.width = bird.getRangeX();
        bird.height = bird.getRangeY();
        bird.setRate(rates.get(index));
        birds.add(bird);
        lastDroptime = TimeUtils.nanoTime();
    }

    private void pointedBird() {
        Bird bird = new Bird();
        int rand = (int) (Math.random() * 3);
        bird.setScore(scores.get(rand));
        bird.setImg(birdImages.get(rand));
        bird.setRangeX(pairs.get(rand).x);
        bird.setRangeY(pairs.get(rand).y);
        bird.x = 0;
        bird.y = MathUtils.random(0, 800 - bird.getRangeY());
        bird.width = bird.getRangeX();
        bird.height = bird.getRangeY();
        bird.setRate(rand);
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

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            if (existBullet > 0){
                shotSound.play();
                shot.x += Math.random() * 50;
                shot.y += Math.random() * 50;
                --existBullet;
            } else {
                if (!exchanging){
                    isExchange();
                    exchanging = true;
                }
                if (exchangeFlag){
                    System.out.println("该换弹了");
                    timer.purge();
                    exchangeFlag = false;
                    exchanging = false;
                    existBullet = 30;
                }
            }
        }

        if (shot.x < 0) {
            shot.x = 0;
        }
        if (shot.x > 1280 - 50) {
            shot.x = 1280 - 50;
        }
        if (TimeUtils.nanoTime() - lastDroptime > 800000000) pointedBird(new Bird(), (int) (Math.random() * 3));
        Iterator<Bird> iterator = birds.iterator();
        while (iterator.hasNext()) {
            Bird bird = iterator.next();
            //设定不同的鸟有不同的速率
            bird.x += bird.getRate() * Gdx.graphics.getDeltaTime();
            if (bird.x + 64 > 1280) {
                iterator.remove();
            }
            if (bird.y < 0) {
                iterator.remove();
            }
            if ((bird.overlaps(shot)) && (bird.isAlive == 1) &&
                    Gdx.input.isButtonPressed(Input.Buttons.LEFT) &&
                    existBullet > 0) {
                bird.isAlive = 0;
                birdSound.play();
                shot.x += Math.random() * 50;
                shot.y += Math.random() * 50;
                score += bird.getScore();
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
                //利用Timer安排一个延迟exchangeTime之后再执行的任务
                //这里是延迟一段时间后将换弹完成flag置为true
                timer.purge();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        System.out.println("已换弹");
                        exchangeFlag = true;
                    }
                }, exchangeTime);
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
