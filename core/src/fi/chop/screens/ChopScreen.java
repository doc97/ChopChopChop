package fi.chop.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import fi.chop.Chop;
import fi.chop.model.GameStats;
import fi.chop.engine.InputMap;

public abstract class ChopScreen extends ScreenAdapter {

    private final Chop game;
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final AssetManager manager;
    private final InputMap input;
    private final GameStats stats;

    public ChopScreen(Chop game) {
        this.game = game;
        this.manager = game.getAssetManager();
        this.batch = game.getSpriteBatch();
        this.camera = game.getCamera();
        this.input = game.getInputMap();
        this.stats = game.getStats();
    }

    protected abstract void update(float delta);
    protected abstract void render(SpriteBatch batch);

    protected void beginRender() {
        getCamera().update();
        batch.setProjectionMatrix(getCamera().combined);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
    @Override
    public void render(float delta) {
        update(delta);
        render(batch);
    }

    public void setScreen(Screens screen) {
        switch (screen) {
            case GUILLOTINE:
                game.setScreen(new GuillotineScreen(game));
                break;
            case MAIN_MENU:
                game.setScreen(new MainMenuScreen(game));
        }
    }

    protected OrthographicCamera getCamera() {
        return camera;
    }

    protected AssetManager getAssets() {
        return manager;
    }

    protected InputMap getInputMap() {
        return input;
    }

    protected GameStats getStats() {
        return stats;
    }
}
