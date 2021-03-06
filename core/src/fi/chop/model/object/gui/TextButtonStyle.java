package fi.chop.model.object.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextButtonStyle {

    private NinePatch bgTexture;
    private Color bgColor;
    private Color tint;

    public TextButtonStyle() {
        bgColor = new Color(Color.CLEAR);
        tint = new Color(Color.WHITE);
    }

    public void set(TextButtonStyle style) {
        if (style == null)
            return;
        bgTexture = style.bgTexture;
        bgColor = style.bgColor;
        tint = style.tint;
    }

    public TextButtonStyle tint(Color tint) {
        this.tint = tint == null ? new Color(Color.WHITE) : new Color(tint);
        return this;
    }

    public TextButtonStyle bgColor(Color bgColor) {
        this.bgColor = bgColor == null ? new Color(Color.CLEAR) : new Color(bgColor);
        return this;
    }

    public TextButtonStyle bgTexture(NinePatch bgTexture) {
        this.bgTexture = bgTexture;
        return this;
    }

    public Color getTint() {
        return tint;
    }

    public Color getBgColor() {
        return bgColor;
    }

    public NinePatch getBgTexture() {
        return bgTexture;
    }
}
