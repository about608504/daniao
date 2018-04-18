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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class MyGdxGame extends ApplicationAdapter {

    private Texture dropImage;
    private Texture bucketImage;
    private Sound dropSound;
    private Music rainMusic;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Rectangle bucket;
    private Array<Bird> raindrops;
    private long lastDroptime;


    @Override
    public void create() {
        dropImage = new Texture(Gdx.files.internal("core/assets/bird.png"));
        bucketImage = new Texture(Gdx.files.internal("core/assets/bucket.png"));

        dropSound = Gdx.audio.newSound(Gdx.files.internal("core/assets/drop.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("core/assets/rain.mp3"));

        rainMusic.setLooping(true);
        rainMusic.play();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 400);
        batch = new SpriteBatch();

        bucket = new Rectangle();
        bucket.x = 800 / 2 - 64 / 2;
        bucket.y = 20;
        bucket.width = 64;
        bucket.height = 64;

        raindrops = new Array<Bird>();
        spawnRaindrop();
    }

    private void spawnRaindrop() {
        Bird raindrop = new Bird();
        raindrop.x = 0;
        raindrop.y = MathUtils.random(0,480-32);
        raindrop.width = 32;
        raindrop.height = 32;
        raindrops.add(raindrop);
        lastDroptime = TimeUtils.nanoTime();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(bucketImage, bucket.x, bucket.y);
        for (Rectangle raindrop : raindrops) {
            batch.draw(dropImage,raindrop.x,raindrop.y);
        }
        batch.end();

        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            bucket.x = touchPos.x - 64 / 2;
            bucket.y = touchPos.y - 64 / 2;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            bucket.x -= 200 * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            bucket.x += 200 * Gdx.graphics.getDeltaTime();
        }
        if (bucket.x < 0) {
            bucket.x = 0;
        }
        if (bucket.x > 800 - 64) {
            bucket.x = 800 - 64;
        }
        if (TimeUtils.nanoTime() - lastDroptime > 800000000) spawnRaindrop();
        Iterator<Bird> iter = raindrops.iterator();
        while (iter.hasNext()) {
            Bird raindrop = iter.next();
            raindrop.x += 200 * Gdx.graphics.getDeltaTime();
            if (raindrop.x + 64 > 800) {
                iter.remove();
            }
            if (raindrop.y < 0){
                iter.remove();
            }
            if ((raindrop.overlaps(bucket)) && (raindrop.isalive == 1)) {
                raindrop.isalive = 0;
                dropSound.play();
            }
            if (raindrop.isalive == 0) {
                raindrop.y -= 800 * Gdx.graphics.getDeltaTime();
            }
        }
    }

    @Override
    public void dispose() {
        dropImage.dispose();
        bucketImage.dispose();
        dropSound.dispose();
        rainMusic.dispose();
        batch.dispose();
    }
}
