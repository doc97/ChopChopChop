package fi.chop.model.object;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import fi.chop.engine.DrawParameters;
import fi.chop.util.FontRenderer;

public class TextObject extends GameObject {

    public interface TextConstructor {
        String construct();
    }

    private String fontName;
    private TextConstructor textConstructor;
    private FontRenderer renderer;
    private Color background;

    private OrthographicCamera cam;
    private SpriteBatch batch;
    private FrameBuffer fbo;
    private TextureRegion fboRegion;
    private DrawParameters params;

    private float paddingX;
    private float paddingY;
    private boolean dirty;

    public TextObject(AssetManager assets, OrthographicCamera camera) {
        super(assets, camera);
        background = new Color(Color.CLEAR);
        batch = new SpriteBatch();
        cam = new OrthographicCamera(0, 0);
    }

    public void create(String fontName, TextConstructor textConstructor) {
        this.fontName = fontName;
        this.textConstructor = textConstructor;
    }

    public void pad(float paddingX, float paddingY) {
        this.paddingX = Math.max(paddingX, 0);
        this.paddingY = Math.max(paddingY, 0);
        dirty = true;
    }

    private void generateTexture() {
        if (fbo != null)
            fbo.dispose();
        createTexture(renderer);
        setSize(fboRegion.getRegionWidth(), fboRegion.getRegionHeight());
        params = new DrawParameters(fboRegion);
        dirty = false;
    }

    private void createTexture(FontRenderer text) {
        int width = Math.round(text.width());
        int height = Math.round(text.height());
        int totalWidth = Math.round(width + paddingX);
        int totalHeight = Math.round(height + paddingY);

        cam.setToOrtho(false, totalWidth, totalHeight);
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, totalWidth, totalHeight, false);
        fboRegion = new TextureRegion(fbo.getColorBufferTexture());
        fboRegion.flip(false, true);

        fbo.begin();
        Gdx.gl.glClearColor(background.r, background.g, background.b, background.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(cam.combined);

        batch.begin();
        batch.setBlendFunction(-1, -1);
        Gdx.gl20.glBlendFuncSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE);

        text.center(cam, true, true).translate(0, height).draw(batch);
        batch.end();

        fbo.end();

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClearColor(0, 0, 0, 1);
    }

    @Override
    public void load() {
        BitmapFont font = getAssets().get(fontName, BitmapFont.class);
        renderer = new FontRenderer(font);
    }

    @Override
    public void update(float delta) {
        String newText = textConstructor.construct();
        if (!newText.equals(renderer.str())) {
            dirty = true;
            renderer.edit(newText);
        }

        if (dirty)
            generateTexture();
    }

    @Override
    public void render(SpriteBatch batch) {
        draw(batch, fboRegion, params);
    }

    @Override
    public void dispose() {
        batch.dispose();
        fbo.dispose();
    }

    public void tint(Color color) {
        renderer.tint(color);
        dirty = true;
    }

    public void background(Color background) {
        this.background = background == null ? new Color(Color.CLEAR) : new Color(background);
    }
}