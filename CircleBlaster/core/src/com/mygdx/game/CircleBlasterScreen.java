package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
// actual game
public class CircleBlasterScreen implements Screen {
    final CircleBlaster game;

    private final PerspectiveCamera camera;
    public ArrayList<ModelInstance> enemies;
    public ArrayList<Bullet> bullets;
    public ArrayList<ModelInstance> box;
    public ArrayList<ModelInstance> allies;
    public ModelBatch modelBatch;
    public Model model;
    public Model bullet;
    public Model enemy;
    public Model ally;
    public Bullet bulletInstance;
    public ModelInstance instance;
    public Environment environment;
    private double lastShot = 0;
    private double fireRateFrames = 5;
    private final double speed = 4;
    private final double bulletSpeed = 8;
    private int shots = 30;
    private final int maxShots = shots;
    private int reloadTimeFrames = 30;
    private double nextReload = System.nanoTime();
    final float enemySize = 15f;
    float bulletSize = 0.5f;
    final float areaSize = 200f;
    final float playerHeight = 10f;
    float enemySpeed = 1f;
    int framesPerEnemy = 11;

    private double framesToFPSInc = 0;
    private int fps = 20;
    private int frames = 0;
    private boolean PAUSED = false;
    private double nanoPause = 0;
    private final double nanosBetweenPause = 1e9;
    private final ShapeRenderer sr;

    // initializes all things and creates a box to stand in
    public CircleBlasterScreen(final CircleBlaster game) {
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.None);
        this.game = game;
        modelBatch = new ModelBatch();
        bullets = new ArrayList<>();
        allies = new ArrayList<>();
        box = new ArrayList<>();
        enemies = new ArrayList<>();
        camera = new PerspectiveCamera(120, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(areaSize/2, playerHeight, areaSize/2);
        //camera.lookAt(0f, 10f, 100f);
        camera.near = 1f;
        camera.far = (float) (areaSize*Math.sqrt(3));
        camera.update();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
        sr = new ShapeRenderer();
        sr.setAutoShapeType(true);

        ModelBuilder modelBuilder = new ModelBuilder();

        ally = modelBuilder.createBox(playerHeight, playerHeight, playerHeight,
                new Material(ColorAttribute.createDiffuse(Color.MAGENTA)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        model = modelBuilder.createBox(1f, areaSize, 1f, // Y
                new Material(ColorAttribute.createDiffuse(Color.GREEN)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        instance = new ModelInstance(model, 0, areaSize/2, 0);
        box.add(0, instance);
        instance = new ModelInstance(model, areaSize, areaSize/2, 0);
        box.add(0, instance);
        instance = new ModelInstance(model, 0, areaSize/2, areaSize);
        box.add(0, instance);
        instance = new ModelInstance(model, areaSize, areaSize/2, areaSize);
        box.add(0, instance);

        model = modelBuilder.createBox(1f, 1f, areaSize, // Z
                new Material(ColorAttribute.createDiffuse(Color.BLUE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        instance = new ModelInstance(model, 0, 0, areaSize/2);
        box.add(0, instance);
        instance = new ModelInstance(model, 0, areaSize, areaSize/2);
        box.add(0, instance);
        instance = new ModelInstance(model, areaSize, 0, areaSize/2);
        box.add(0, instance);
        instance = new ModelInstance(model, areaSize, areaSize, areaSize/2);
        box.add(0, instance);

        model = modelBuilder.createBox(areaSize, 1f, 1f, // X
                new Material(ColorAttribute.createDiffuse(Color.RED)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        instance = new ModelInstance(model, areaSize/2, 0, 0);
        box.add(0, instance);
        instance = new ModelInstance(model, areaSize/2, areaSize, 0);
        box.add(0, instance);
        instance = new ModelInstance(model, areaSize/2, 0, areaSize);
        box.add(0, instance);
        instance = new ModelInstance(model, areaSize/2, areaSize, areaSize);
        box.add(0, instance);

        bullet = modelBuilder.createSphere(bulletSize, bulletSize, bulletSize, 10, 10,
                new Material(ColorAttribute.createDiffuse(Color.YELLOW)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        enemy = modelBuilder.createSphere(enemySize, enemySize, enemySize, 50, 50,
                new Material(ColorAttribute.createDiffuse(Color.RED)),
                        VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
    }
    // changes the model of a bullet and increases the hitbox, also checks to see if holding space makes you immortal.
    public void bulletSizeInc() {
        bulletSize += 1.5f;
        ModelBuilder modelBuilder = new ModelBuilder();
        bullet = modelBuilder.createSphere(bulletSize, bulletSize, bulletSize, 10, 10,
                new Material(ColorAttribute.createDiffuse(Color.YELLOW)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
    }
    // Checks to see if holding space makes you immortal and increases reload speed and fire rate
    public void fireSpeedInc() {
        if (reloadTimeFrames <= 0 && fireRateFrames <= 0) {
            randomUpgrade();
        } else {
            reloadTimeFrames -= 8;
            fireRateFrames -= 0.75;
        }
    }
    // slows enemies
    public void slowEnemies() {
        enemySpeed -= 0.1f;
    }
    // randomly chose an upgrade
    public void randomUpgrade() {
        int rand = (int) (Math.random() * 4) + 1;
        if (rand == 1) {
            bulletSizeInc();
            System.out.println(1);
        } else if (rand == 2) {
            fireSpeedInc();
            System.out.println(2);
        } else if (rand == 3) {
            slowEnemies();
            System.out.println(3);
        } else if (rand == 4) {
            framesPerEnemy += 2;
            System.out.println(4);
        }
    }
    // renders and updates
    @Override
    public void render (float delta) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth() * 2, 2 * Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        camera.update();
        if (!PAUSED) {
            // render, translate, and collision

            modelBatch.begin(camera);
            for (ModelInstance instance : box) {
                modelBatch.render(instance, environment);
            }
            float hitSize = enemySize + bulletSize;
            float dist2 = hitSize * hitSize;
            for (int j = bullets.size() - 1; (j >= 0) && (bullets.size() > 0); j--) {
                modelBatch.render(bullets.get(j), environment);
                bullets.get(j).transform.translate(bullets.get(j).direction);

                for (int i = enemies.size() - 1; (i >= 0) && (enemies.size() > 0); i--) {
                    Vector3 posEnemy = enemies.get(i).transform.getTranslation(new Vector3(0, 0, 0));
                    Vector3 posBullet = bullets.get(j).transform.getTranslation(new Vector3(0, 0, 0));
                    if (posEnemy.dst2(posBullet) < dist2) {
                        enemies.remove(i);
                        bullets.remove(j);
                        break;
                    }
                }
            }
            for (ModelInstance instance : enemies) {
                modelBatch.render(instance, environment);
                Vector3 pos = instance.transform.getTranslation(new Vector3(0, 0, 0));
                pos.x -= camera.position.x;
                pos.y -= camera.position.y;
                pos.z -= camera.position.z;
                float tot = (Math.abs(pos.x) + Math.abs(pos.y) + Math.abs(pos.z)) / enemySpeed;
                instance.transform.translate(-pos.x / tot, -pos.y / tot, -pos.z / tot);
            }
            hitSize = playerHeight;
            dist2 = hitSize * hitSize;
            for (int i = allies.size() - 1; (i >= 0) && (allies.size() > 0); i--) {
                modelBatch.render(allies.get(i), environment);
                Vector3 posAlly = allies.get(i).transform.getTranslation(new Vector3(0, 0, 0));
                if (camera.position.dst2(posAlly) < dist2) {
                    allies.remove(i);
                    randomUpgrade();
                }
            }
            modelBatch.end();


            // Spawning phase
            hitSize = (float) (2 * (enemySize + speed + enemySpeed));
            dist2 = hitSize * hitSize;
            if (frames % 1500 == 0 && framesPerEnemy >= 1) {
                framesPerEnemy--;
            }
            if (frames % framesPerEnemy == 0) {
                Vector3 position = new Vector3();
                position.x = (float) (Math.random() * areaSize);
                position.y = (float) (Math.random() * areaSize);
                position.z = (float) (Math.random() * areaSize);
                while (position.dst2(camera.position) < dist2) {
                    position.x = (float) (Math.random() * areaSize);
                    position.y = (float) (Math.random() * areaSize);
                    position.z = (float) (Math.random() * areaSize);
                }
                instance = new ModelInstance(enemy, position);
                enemies.add(0, instance);
            }

            if (frames % 1000 == 0) {
                Vector3 position = new Vector3();
                position.x = (float) (Math.random() * areaSize);
                position.y = playerHeight;
                position.z = (float) (Math.random() * areaSize);

                instance = new ModelInstance(ally, position);
                allies.add(0, instance);
            }

            // Keyboard and mouse events

            if (Gdx.input.getX() != Gdx.graphics.getWidth() / 2 && Gdx.input.getY() != Gdx.graphics.getHeight() / 2) {
                //camera.rotate(new Vector3(0, 1, 0), (Gdx.graphics.getWidth()/2-Gdx.input.getX())/2);
                //camera.rotate(new Vector3(camera.view.val[0], camera.view.val[1], camera.view.val[2]), (Gdx.graphics.getHeight() / 2 - Gdx.input.getY()) / 2);
                camera.lookAt(camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0)));
                camera.up.x = 0;
                camera.up.y = 1;
                camera.up.z = 0;
                Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && frames - lastShot - fireRateFrames > 0 && shots > 0) {
                bulletInstance = new Bullet(bullet, camera.position.x, camera.position.y - 3, camera.position.z);
                bulletInstance.direction = new Vector3((float) (camera.direction.x * bulletSpeed), (float) (camera.direction.y * bulletSpeed), (float) (camera.direction.z * bulletSpeed));
                bullets.add(0, bulletInstance);
                lastShot = frames;
                shots--;
                if (shots == 0) {
                    nextReload = frames + reloadTimeFrames;
                }
            } else if (nextReload < frames && shots == 0) {
                shots = maxShots;
                nextReload = frames + reloadTimeFrames;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                float x = camera.direction.x;
                float z = camera.direction.z;
                float mult = (float) (speed / (Math.sqrt(Math.pow(camera.direction.x, 2) + Math.pow(camera.direction.z, 2))));
                camera.translate(z * mult / (float) (Math.sqrt(2)), 0, -x * mult / (float) (Math.sqrt(2)));
            }

            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                float x = camera.direction.x;
                float z = camera.direction.z;
                float mult = (float) (speed / (Math.sqrt(Math.pow(camera.direction.x, 2) + Math.pow(camera.direction.z, 2))));
                camera.translate(-z * mult / (float) (Math.sqrt(2)), 0, x * mult / (float) (Math.sqrt(2)));
            }

            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                float x = camera.direction.x;
                float z = camera.direction.z;
                float mult = (float) (speed / (Math.sqrt(Math.pow(camera.direction.x, 2) + Math.pow(camera.direction.z, 2))));
                camera.translate(-x * mult / (float) (Math.sqrt(2)), 0, -z * mult / (float) (Math.sqrt(2)));
            }
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                float x = camera.direction.x;
                float z = camera.direction.z;
                float mult = (float) (speed / (Math.sqrt(Math.pow(camera.direction.x, 2) + Math.pow(camera.direction.z, 2))));
                camera.translate(x * mult / (float) (Math.sqrt(2)), 0, z * mult / (float) (Math.sqrt(2)));
            }

            if (camera.position.x < 0) {
                camera.position.x = 0;
            }
            if (camera.position.z < 0) {
                camera.position.z = 0;
            }

            if (camera.position.x > 200) {
                camera.position.x = 200;
            }
            if (camera.position.z > 200) {
                camera.position.z = 200;
            }

            for (int i = 0; i < bullets.size(); i++) {
                Vector3 pos = bullets.get(i).transform.getTranslation(new Vector3(0, 0, 0));
                if ((pos.x < 0) || (pos.y < 0) || (pos.z < 0) ||
                        (pos.x > areaSize) || (pos.y > areaSize) || (pos.z > areaSize)) {
                    bullets.remove(i);
                }
            }

            // Change FPS
            framesToFPSInc += 0.005;
            frames++;
            while (framesToFPSInc > 1) {
                fps++;
                Gdx.graphics.setForegroundFPS(fps);
                framesToFPSInc--;
            }

            // renders cursor
            sr.begin();
            sr.setColor(new Color(125f, 125f, 125f, 0.5f));
            sr.rect(Gdx.graphics.getWidth()/2f-10, Gdx.graphics.getHeight()/2f-5, 5, 1);
            sr.rect(Gdx.graphics.getWidth()/2f+5, Gdx.graphics.getHeight()/2f-5, 5, 1);
            sr.rect(Gdx.graphics.getWidth()/2f, Gdx.graphics.getHeight()/2f-15, 1, 5);
            sr.rect(Gdx.graphics.getWidth()/2f, Gdx.graphics.getHeight()/2f, 1, 5);
            sr.end();

            // End game checks
            hitSize = enemySize;
            dist2 = hitSize * hitSize;
            for (ModelInstance instance : enemies) {
                Vector3 pos = instance.transform.getTranslation(new Vector3(0, 0, 0));
                if (pos.dst2(camera.position) < dist2 && model != null) {
                    modelBatch.end();
                    game.setScreen(new GameOverScreen(game, frames));
                    dispose();

                }
            }

            if (enemySpeed <= 0 && model != null) {
                modelBatch.end();
                game.setScreen(new GameOverScreen(game, Integer.MAX_VALUE));
                dispose();
            }
        } else {
            game.batch.begin();
            game.font.draw(game.batch, "Press L to give up.", 100, 100);
            game.batch.end();
            if (Gdx.input.isKeyPressed(Input.Keys.L))  {
                modelBatch.end();
                dispose();
                System.exit(0);
            }
        }

        // Pause Check

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE) && !PAUSED && System.nanoTime() - nanoPause > nanosBetweenPause) {
            pause();
            nanoPause = System.nanoTime();
        } else if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE) && PAUSED && System.nanoTime() - nanoPause > nanosBetweenPause) {
            resume();
            nanoPause = System.nanoTime();
        }
    }
    // disposes of resources
    @Override
    public void dispose () {
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        fps = 20;
        Gdx.graphics.setForegroundFPS(fps);
        model.dispose();
        bullet.dispose();
        enemy.dispose();
        ally.dispose();
        modelBatch.dispose();
    }

    @Override
    public void hide() {

    }

    @Override
    public void show() {}

    @Override
    public void resize(int x, int y) {

    }
    // pauses
    @Override
    public void pause() {
        PAUSED = true;
    }
    // resumes
    @Override
    public void resume() {
        PAUSED = false;
    }
}
