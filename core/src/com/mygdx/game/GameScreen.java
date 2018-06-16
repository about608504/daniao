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
 * @author : Liu BCY WY STX
 * @date : 2018/5/31 22:56
 */
//游戏界面
public class GameScreen implements Screen {
    /**
     * 游戏对象
     */
    private MyGdxGame game;
    /**
     * 准心图像
     */
    private Texture shotImage;
    /**
     * 背景图片贴图
     */
    private Texture backgroundImage;
    /**
     * 宝箱图片贴图
     */
    private Texture giftImage;
    /**
     * 子弹图片贴图
     */
    private Texture bulletImage;
    /**
     * 3种鸟的图片贴图
     */
    private List<Texture> birdImages;
    /**
     * 射击枪声
     */
    private Sound shotSound;
    /**
     * 惊喜声音
     */
    private Sound amazingSound;
    /**
     * 鸟被射击之后的惨叫声
     */
    private Music birdSound;
    /**
     * 换弹声
     */
    private Sound exchangeSound;
    /**
     * 游戏镜头，在本游戏中镜头不会移动，固定
     */
    private OrthographicCamera camera;
    /**
     * 游戏批处理对象
     */
    private SpriteBatch batch;
    /**
     * 射击准心范围（矩形）
     */
    private Rectangle shot;
    /**
     * 储存鸟对象
     */
    private Array<Bird> birds;
    /**
     * 出现一次鸟的时间点
     */
    private long lastBirdTime;
    /**
     * 剩余子弹总数
     */
    private int bulletSum = 100;
    /**
     * 弹夹容量
     */
    private final int reload = 30;
    /**
     * 初始化子弹数量为30
     */
    private int existBullet = reload;
    /**
     * 分数
     */
    private int score = 0;
    /**
     * 换弹时间 1.5秒
     */
    private long exchangeTime = 1500;
    /**
     * 计时任务执行者
     */
    private Timer timer = new Timer("clock");
    /**
     * 换弹标志
     */
    private boolean exchangeFlag = false;
    /**
     * 正在换弹的标志
     */
    private boolean exchanging = false;
    /**
     * 按下鼠标左键的标志
     */
    private boolean pressDown = false;
    /**
     * 点击动作完成一次的的标志
     */
    private boolean oneClick = false;
    /**
     * 初始化鸟的总量
     */
    private int birdSum = 50;
    /**
     * 鸟速度调节参数，当分数大于5000时，值为1.5
     */
    private double hard = 1.0;
    /**
     * 文字图片贴图生成器
     */
    private FreeTypeFontGenerator generator;
    /**
     * 文字贴图对象
     */
    private BitmapFont font;
    /**
     * 每种鸟的命中范围
     */
    private List<Pair> pairs = Collections.unmodifiableList(
            Arrays.asList(
                    new Pair(32, 32),
                    new Pair(60, 60),
                    new Pair(60, 60)
            ));
    /**
     * 每种鸟的速度
     */
    private List<Integer> rates = Collections.unmodifiableList(
            Arrays.asList(
                    500,
                    350,
                    300
            )
    );

    /**
     * 每种鸟的分数
     */
    private List<Integer> scores = Collections.unmodifiableList(
            Arrays.asList(
                    100,
                    30,
                    10
            )
    );

    /**
     * 私有内部类
     * 存储一对int类型的数据
     */
    private class Pair {
        Pair(int x, int y) {
            this.x = x;
            this.y = y;
        }

        int x;
        int y;
    }

    /**
     * 初始化各种参数
     * @param game 游戏对象
     */
    public GameScreen(MyGdxGame game) {

        /*
        初始化各种参数
         */
        this.game = game;
        /**
         * 初始化各种贴图对象，声音对象
         */
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

        //游戏逻辑处理批对象
        batch = new SpriteBatch();
        generator = new FreeTypeFontGenerator(Gdx.files.internal("core/assets/font/font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.characters = "1234567890score:/BirdSum";
        font = generator.generateFont(parameter);

        //射击判定方框
        shot = new Rectangle();
        shot.x = 60 / 2;
        shot.y = 60 / 2;
        shot.width = 40;
        shot.height = 40;
        //设置输入时间处理器，只处理鼠标左键的动作
        Gdx.input.setInputProcessor(new MyInputProcessor());

        birds = new Array<>();
    }

    /**
     * 根据鸟的种类初始化鸟的对象
     * 共有3种鸟
     * 0-》100分
     * 1-》30分
     * 2-》10分
     * 不同的鸟运动的速率也不同
     */

    private void pointedBird(Bird bird, int index){
        bird.setScore(scores.get(index));
        bird.setRangeX(pairs.get(index).x);
        bird.setRangeY(pairs.get(index).y);
        bird.x = 0;//鸟在屏幕上的初始位置
        bird.y = MathUtils.random(0, 700 - pairs.get(index).y);
        bird.setImg(birdImages.get(index));
        bird.width = bird.getRangeX();
        bird.height = bird.getRangeY();
        bird.setRate(rates.get(index));
        //指定宝箱类型
        double gift = Math.random();
        if (gift >= 0 && gift <= 0.05)
            bird.setGift(1);//增加子弹
        else if (gift > 0.05 && gift <= 0.15)
            bird.setGift(2);//增加分数+鸟数量
        else
            bird.setGift(0);
        birds.add(bird);//分数宝箱
        birdSum--;//每增加一只鸟，鸟的总数-1
        lastBirdTime = TimeUtils.nanoTime();
    }

    @Override
    public void show() {

    }

    /**
     * 游戏逻辑处理函数
     * 通过轮询调用此函数来更新图像，处理输入事件
     */
    @Override
    public void render(float delta) {
        if (score > 5000)
            hard = 1.5;
        else
            hard = 1.0;
        //画面初始化
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        //设置画面batch处理的镜头
        batch.setProjectionMatrix(camera.combined);
        //画面处理开始
        batch.begin();
        //绘制图像
        batch.draw(backgroundImage, 0, 0);
        batch.draw(shotImage, shot.x, shot.y);

        for (Bird bird : birds) {
            batch.draw(bird.getImg(), bird.x, bird.y);
        }
        font.draw(batch, "score:"+score, 100, 100);
        font.draw(batch, existBullet + "/" + bulletSum, 100, 80);
        font.draw(batch, "BirdSum:" + birdSum, 100, 60);
        batch.end();
        //结束绘制图像

        //更新鼠标坐标
        Vector3 touchPos = new Vector3();
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);
        shot.x = touchPos.x - 64 / 2;
        shot.y = touchPos.y - 64 / 2;

        //如果鼠标左键点击，pressDown标志为true
        if (Gdx.input.isTouched()){
            pressDown = true;
        }
        //如果鼠标左键点击后抬起，置pressDown为false
        //一次点击标志为true
        if (!Gdx.input.isTouched() && pressDown){
            pressDown = false;
            oneClick = true;
        }

        //如果进行了一次点击，射出一颗子弹
        if (oneClick) {
            oneClick = false;
            if (existBullet > 0){
                //枪声
                shotSound.play();
                //射击抖动逻辑，后坐力
                shot.x += Math.random() * 50;
                shot.y += Math.random() * 50;
                --existBullet;
            } else {
                //如果没有子弹
                //换弹声
                exchangeSound.play();
                System.out.println("该换弹了");
                //如果不是正在换弹中
                if (!exchanging){
                    //且没有子弹，进行换弹操作
                    isExchange();
                    //正在换弹标志置为true
                    exchanging = true;
                }
                //如果换弹完成
                if (exchangeFlag){
                    //清除计时任务列表
                    timer.purge();
                    //初始化
                    exchangeFlag = false;
                    exchanging = false;
                    //当子弹总数小于30时，直接令弹夹中子弹数为剩余的子弹数
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

        //防止鼠标越界（1280，800）
        if (shot.x < 0) {
            shot.x = 0;
        }
        if (shot.x > 1280 - 50) {
            shot.x = 1280 - 50;
        }
        //在一段时间后生成一只鸟
        if (birdSum >= 0 && TimeUtils.nanoTime() - lastBirdTime > 800000000) pointedBird(new Bird(), (int) (Math.random() * 3));
        Iterator<Bird> iterator = birds.iterator();
        //遍历鸟的数组
        //处理鸟的运动，被击落等事件
        while (iterator.hasNext()) {
            try {
                Bird bird = iterator.next();
                //设定不同的鸟有不同的速率
                bird.x += bird.getRate() * hard * Gdx.graphics.getDeltaTime();
                //当鸟超出屏幕后移除鸟
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
                //当鸟被击中
                if ((bird.overlaps(shot)) && (bird.isAlive == 1) &&
                        Gdx.input.isButtonPressed(Input.Buttons.LEFT) &&
                        existBullet > 0) {
                    bird.isAlive = 0;//置为0
                    birdSound.play();//鸟惨叫
                    //后座力
                    shot.x += Math.random() * 50;
                    shot.y += Math.random() * 50;
                    //增加分数
                    score += bird.getScore();
                }

                //宝箱逻辑
                //把鸟的图片换成宝箱或子弹的图片，并将其存活状态置为1，特殊处理
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

        //当子弹数量为0
        //设置为开始画面
        if (bulletSum <= 0 && existBullet <= 0)
            game.setScreen(new StartScreen(game, score));

        //当鸟的数量为0
        //设置当前画面为开始画面
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

    /**
     * 换弹逻辑函数
     * 利用Timer安排一个延迟exchangeTime之后再执行的任务
     * 这里是延迟一段时间后将换弹完成flag置为true
     */
    private void isExchange() {
        if (existBullet <= 0){
            try {
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

    /**
     * 内存回收函数
     */
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
