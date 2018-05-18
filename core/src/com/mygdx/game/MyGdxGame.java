package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MyGdxGame extends ApplicationAdapter {

    private Texture dropImage;
    private Texture shotImage;
    private TextureRegion backRegion;
    private List<Texture> birdImages;
    private Sound shotSound;
    private Music backGroundMusic;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Rectangle bucket;
    private Array<Bird> birds;
    private long lastDroptime;
    private int bulletSum = 150;
    private final int reload = 30;
    private int existBullet = reload;


    @Override
    public void create() {

        birdImages = new ArrayList<Texture>(5);
        for (int i = 0; i < 3; i++)
            birdImages.add(new Texture(Gdx.files.internal("core/assets/birds/" + String.valueOf(i+1) + ".png")));
        dropImage = new Texture(Gdx.files.internal("core/assets/droplet.png"));
        shotImage = new Texture(Gdx.files.internal("core/assets/zhunxin.png"));

        shotSound = Gdx.audio.newSound(Gdx.files.internal("core/assets/shot.wav"));
        backGroundMusic = Gdx.audio.newMusic(Gdx.files.internal("core/assets/bird.wav"));

        backGroundMusic.setLooping(true);
        backGroundMusic.play();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 380);
        //不会设置背景图
        backRegion = new TextureRegion(new Texture(Gdx.files.internal("core/assets/bg.jpg")));

        batch = new SpriteBatch();

        bucket = new Rectangle();
        bucket.x = 1280 / 2 - 64 / 2;
        bucket.y = 20;
        bucket.width = 64;
        bucket.height = 64;

        birds = new Array<Bird>();
        spawnRaindrop();
    }

    private void spawnRaindrop() {
        Bird raindrop = new Bird();
        raindrop.x = 0;
        raindrop.y = MathUtils.random(0,480-32);
        raindrop.width = 32;
        raindrop.height = 32;
        int rand = (int)(Math.random() * 3);
        raindrop.setImg(birdImages.get(rand));
        birds.add(raindrop);
        lastDroptime = TimeUtils.nanoTime();
    }

    @Override
    public void render() {

        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(shotImage, bucket.x, bucket.y);

        for (Bird bird : birds) {
            batch.draw(bird.getImg(), bird.x, bird.y);
        }

        batch.end();

        Vector3 touchPos = new Vector3();
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);
        bucket.x = touchPos.x - 64 / 2;
        bucket.y = touchPos.y - 64 / 2;

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
            shotSound.play();

        if (bucket.x < 0) {
            bucket.x = 0;
        }
        if (bucket.x > 1280 - 50) {
            bucket.x = 1280 - 50;
        }
        if (TimeUtils.nanoTime() - lastDroptime > 800000000) spawnRaindrop();
        Iterator<Bird> iter = birds.iterator();
        while (iter.hasNext()) {
            Bird bird = iter.next();
            bird.x += 200 * Gdx.graphics.getDeltaTime();
            if (bird.x + 64 > 1280) {
                iter.remove();
            }
            if (bird.y < 0){
                iter.remove();
            }
            if ((bird.overlaps(bucket)) && (bird.isalive == 1) &&
                    Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                bird.isalive = 0;
            }
            if (bird.isalive == 0) {
                bird.y -= 1280 * Gdx.graphics.getDeltaTime();
            }
        }
    }

    @Override
    public void dispose() {
        dropImage.dispose();
        shotImage.dispose();
        shotSound.dispose();
        backGroundMusic.dispose();
        batch.dispose();
    }
}
