package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
// actually does a thing
public class MainMenuScreen implements Screen {

    final CircleBlaster game;

    OrthographicCamera camera;
    // makes basic camera
    public MainMenuScreen(final CircleBlaster game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
    // draws the screen
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0,0,0.2f,1);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.font.draw(game.batch, "Welcome to Circle Blaster", 100, 100);
        game.font.draw(game.batch, "Click anywhere to begin.", 100, 80);
        game.font.draw(game.batch, "WASD to move   Mouse to turn   Space to shoot (fire at the spheres)", Gdx.graphics.getWidth()-450, 100);
        game.font.draw(game.batch, "Magenta cubes make you stronger and the spheres kill you.", Gdx.graphics.getWidth()-450, 60);
        game.font.draw(game.batch, "Esc to pause and resume", Gdx.graphics.getWidth()-450, 80);
        game.batch.end();

        if (Gdx.input.isTouched()) {
            game.setScreen(new CircleBlasterScreen(game));
            dispose();
        }
    }

    @Override
    public void dispose() {}

    @Override
    public void show() {}

    @Override
    public void resize(int x, int y) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}
}
