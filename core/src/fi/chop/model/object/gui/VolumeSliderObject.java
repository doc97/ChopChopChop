package fi.chop.model.object.gui;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import fi.chop.model.auxillary.Align;
import fi.chop.model.object.GameObject;
import fi.chop.model.object.util.ValueSliderObject;
import fi.chop.model.world.Player;
import fi.chop.model.world.WorldState;

public class VolumeSliderObject extends GUIObject {

    private String sliderBackground;
    private String sliderKnob;
    private String atlasName;
    private String fontName;
    private float sliderWidth;
    private float sliderHeight;

    private String label;
    private TextObject text;
    private ValueSliderObject slider;

    public VolumeSliderObject(AssetManager assets, OrthographicCamera camera, WorldState world, Player player) {
        super(assets, camera, world, player);
    }

    @Override
    public void pack() {
        text.pack();
        slider.pack();

        // Y spacing between text and slider
        float spacingY = 5;
        text.getTransform().setPosition(0, spacingY);
        // Slider origin y is 0.5f
        slider.getTransform().setPosition(0, slider.getTransform().getHeight() / 2);

        float totalHeight = text.getTransform().getHeight() + slider.getTransform().getHeight() + spacingY;
        float totalWidth = slider.getTransform().getWidth();
        getTransform().setSize(totalWidth, totalHeight);
    }

    @Override
    public void load() {
        text = new TextObject(getAssets(), getCamera(), getWorld(), getPlayer());
        text.create(fontName, () -> label);
        text.load();
        text.getTransform().setParent(getTransform());
        text.getTransform().setAlign(Align.TOP_LEFT);
        text.getTransform().setOrigin(0, 1);

        slider = new ValueSliderObject(getAssets(), getCamera(), getWorld(), getPlayer());
        slider.init(atlasName, sliderBackground, sliderKnob, fontName,
                () -> String.format("%.0f", slider.getValue() * 100) + "%", () -> "100%");
        slider.load();
        slider.getTransform().setSize(sliderWidth, sliderHeight);
        slider.getTransform().setParent(getTransform());
        slider.getTransform().setAlign(Align.BOTTOM_LEFT);
        slider.getTransform().setOrigin(0, 1);
    }

    @Override
    public void render(SpriteBatch batch) { }

    @Override
    public void dispose() { }

    @Override
    public void die() {
        super.die();
        text.die();
        slider.die();
    }

    @Override
    public GameObject[] getChildren() {
        return new GameObject[] { text, slider };
    }

    public void init(String atlasName, String sliderBackground, String sliderKnob, String fontName, String label,
                     float sliderWidth, float sliderHeight) {
        this.atlasName = atlasName;
        this.sliderBackground = sliderBackground;
        this.sliderKnob = sliderKnob;
        this.fontName = fontName;
        this.label = label;
        this.sliderWidth = sliderWidth;
        this.sliderHeight = sliderHeight;
    }
}
