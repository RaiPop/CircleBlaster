package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
// shows your stats when you lose or win
public class GameOverScreen implements Screen {

    final CircleBlaster game;
    int frames;

    OrthographicCamera camera;

    public GameOverScreen(final CircleBlaster game, int frames) {
        this.game = game;
        this.frames = frames;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0,0,0.2f,1);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        String congratsMessage;
        if (frames > 20000) {
            game.font.draw(game.batch, "Congratulations on winning", 100, 80);
        } else {
            game.font.draw(game.batch, "Too bad on the loss, maybe you'll win next time.", 100, 80);
        }
        game.font.draw(game.batch, "Click anywhere to begin.", 100, 60);
        game.font.draw(game.batch, "Frames:" + frames + "/" + "20000", 20, Gdx.graphics.getHeight()-20);
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