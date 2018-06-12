package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
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
    private Texture giftImage;
    private Texture bulletImage;
    private List<Texture> birdImages;
    private Sound shotSound;
    private Sound amazingSound;
    private Music birdSound;
    private Sound exchangeSound;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Rectangle shot;
    private Array<Bird> birds;
    private long lastDroptime;
    private int bulletSum = 100;
    private final int reload = 30;
    private int existBullet = reload;
    private int score = 0;
    private long exchangeTime = 1500;
    private Timer timer = new Timer("clock");
    private boolean exchangeFlag = false;
    private boolean exchanging = false;
    private boolean pressDown = false;
    private boolean oneClick = false;
    private int birdSum = 50;
    private double hard = 1.0;
    private FreeTypeFontGenerator generator;
    private BitmapFont font;
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
                    100,
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
        giftImage = new Texture(Gdx.files.internal("core/assets/gift.png"));
        bulletImage = new Texture(Gdx.files.internal("core/assets/bullet.png"));

        amazingSound = Gdx.audio.newSound(Gdx.files.internal("core/assets/amazing.wav"));
        exchangeSound = Gdx.audio.newSound(Gdx.files.internal("core/assets/reload.mp3"));
        shotSound = Gdx.audio.newSound(Gdx.files.internal("core/assets/shot.wav"));
        birdSound = Gdx.audio.newMusic(Gdx.files.internal("core/assets/bird.wav"));
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 700);

        batch = new SpriteBatch();
        generator = new FreeTypeFontGenerator(Gdx.files.internal("core/assets/font/font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.characters = "1234567890score:/BirdSum";
        font = generator.generateFont(parameter);

        shot = new Rectangle();
        shot.x = 60 / 2;
        shot.y = 60 / 2;
        shot.width = 40;
        shot.height = 40;
        Gdx.input.setInputProcessor(new MyInputProcessor());

        birds = new Array<>();
    }



    private void pointedBird(Bird bird, int index){
        bird.setScore(scores.get(index));
        bird.setRangeX(pairs.get(index).x);
        bird.setRangeY(pairs.get(index).y);
        bird.x = 0;
        bird.y = MathUtils.random(0, 700 - pairs.get(index).y);
        bird.setImg(birdImages.get(index));
        bird.width = bird.getRangeX();
        bird.height = bird.getRangeY();
        bird.setRate(rates.get(index));
        double gift = Math.random();
        if (gift >= 0 && gift <= 0.05)
            bird.setGift(1);//增加子弹
        else if (gift > 0.05 && gift <= 0.15)
            bird.setGift(2);
        else
            bird.setGift(0);
        birds.add(bird);//分数宝箱
        birdSum--;
        lastDroptime = TimeUtils.nanoTime();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        if (score > 5000)
            hard = 1.5;
        else
            hard = 1.0;
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
        font.draw(batch, "score:"+score, 100, 100);
        font.draw(batch, existBullet + "/" + bulletSum, 100, 80);
        font.draw(batch, "BirdSum:" + birdSum, 100, 60);
        batch.end();



        Vector3 touchPos = new Vector3();
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);
        shot.x = touchPos.x - 64 / 2;
        shot.y = touchPos.y - 64 / 2;

        if (Gdx.input.isTouched()){
            pressDown = true;
        }
        if (!Gdx.input.isTouched() && pressDown){
            pressDown = false;
            oneClick = true;
        }

        if (oneClick) {
            oneClick = false;
            if (existBullet > 0){
                shotSound.play();
                shot.x += Math.random() * 50;
                shot.y += Math.random() * 50;
                --existBullet;
            } else {
                exchangeSound.play();
                System.out.println("该换弹了");
                if (!exchanging){
                    isExchange();
                    exchanging = true;
                }
                if (exchangeFlag){
                    timer.purge();
                    exchangeFlag = false;
                    exchanging = false;
                    if (bulletSum >= reload) {
                        existBullet = reload;
                        bulletSum -= reload;
                    }
                    else{
                        existBullet = bulletSum;
                        bulletSum = 0;
                    }
                }
            }
        }

        if (shot.x < 0) {
            shot.x = 0;
        }
        if (shot.x > 1280 - 50) {
            shot.x = 1280 - 50;
        }
        if (birdSum >= 0 && TimeUtils.nanoTime() - lastDroptime > 800000000) pointedBird(new Bird(), (int) (Math.random() * 3));
        Iterator<Bird> iterator = birds.iterator();
        while (iterator.hasNext()) {
            try {
                Bird bird = iterator.next();
                //设定不同的鸟有不同的速率
                bird.x += bird.getRate() * hard * Gdx.graphics.getDeltaTime();
                if (bird.x + 50 > 1280) {
                    try {
                        iterator.remove();
                    }catch (ArrayIndexOutOfBoundsException e){
                        continue;
                    }
                }
                if (bird.y < 0) {
                    try {
                        iterator.remove();
                    }catch (ArrayIndexOutOfBoundsException e){
                        continue;
                    }
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

                //宝箱逻辑
                if (bird.getGift() == 1 && bird.isAlive == 0){
                    bird.isAlive = 1;
                    bird.setImg(bulletImage);
                    bird.width = 200;
                    bird.height = 90;
                    if (Math.random() >= 0.5)
                        bird.x += (bird.getRate() - 200) * hard * Gdx.graphics.getDeltaTime();
                    else
                        bird.x -= (bird.getRate() - 200) * hard * Gdx.graphics.getDeltaTime();
                    bird.y -= 400 * Gdx.graphics.getDeltaTime();
                    if ((bird.overlaps(shot)) && (bird.isAlive == 1) &&
                            Gdx.input.isButtonPressed(Input.Buttons.LEFT) &&
                            existBullet > 0){
                        amazingSound.play();
                        bird.isAlive = 0;
                        bird.setGift(0);
                        shot.x += Math.random() * 50;
                        shot.y += Math.random() * 50;
                        bulletSum += 10;
                    }
                }else if (bird.getGift() == 2 && bird.isAlive == 0){
                    bird.isAlive = 1;
                    bird.setImg(giftImage);
                    bird.width = 100;
                    bird.height = 75;
                    if (Math.random() >= 0.5)
                        bird.x += (bird.getRate() - 200) * hard * Gdx.graphics.getDeltaTime();
                    else
                        bird.x -= (bird.getRate() - 200) * hard * Gdx.graphics.getDeltaTime();
                    if (Math.random() >= 0.5)
                        bird.y -= 400 * Gdx.graphics.getDeltaTime();
                    else
                        bird.y += 400 * Gdx.graphics.getDeltaTime();
                    if ((bird.overlaps(shot)) && (bird.isAlive == 1) &&
                            Gdx.input.isButtonPressed(Input.Buttons.LEFT) &&
                            existBullet > 0){
                        amazingSound.play();
                        bird.isAlive = 0;
                        bird.setGift(0);
                        shot.x += Math.random() * 50;
                        shot.y += Math.random() * 50;
                        score += 100;
                        birdSum += 10;
                    }
                }
                if (bird.isAlive == 0) {
                    bird.x += (bird.getRate() - 200) * hard * Gdx.graphics.getDeltaTime();
                    bird.y -= 400 * Gdx.graphics.getDeltaTime();
                }
            }catch (Exception ignored){
            }
        }

        if (bulletSum <= 0 && existBullet <= 0)
            game.setScreen(new StartScreen(game, score));

        if (birdSum <= 0){
            boolean f = true;
            for (Bird bird : birds){
                if (bird.x <= 1280 || bird.y >= 20){
                    f = false;
                    break;
                }
            }
            if (f)
                game.setScreen(new StartScreen(game, score));
        }
    }

    //换弹逻辑
    private void isExchange() {
        if (existBullet <= 0){
            try {
                //利用Timer安排一个延迟exchangeTime之后再执行的任务
                //这里是延迟一段时间后将换弹完成flag置为true
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
        for (Texture texture : birdImages)
            texture.dispose();
        bulletImage.dispose();
        giftImage.dispose();
        backgroundImage.dispose();
        amazingSound.dispose();
        exchangeSound.dispose();
        font.dispose();
        generator.dispose();
        shotImage.dispose();
        shotSound.dispose();
        birdSound.dispose();
        batch.dispose();
    }
}
