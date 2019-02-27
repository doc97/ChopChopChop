package fi.chop.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import fi.chop.Chop;
import fi.chop.engine.Layer;
import fi.chop.event.EventData;
import fi.chop.event.EventListener;
import fi.chop.event.Events;
import fi.chop.input.TextButtonHandler;
import fi.chop.input.TownScreenInput;
import fi.chop.model.object.gui.*;
import fi.chop.sound.MusicType;

public class TownScreen extends ChopScreen implements EventListener {

    public TownScreen(Chop game) {
        super(game);
    }

    @Override
    public void show() {
        registerEventListener();
        initializeScreen();
        initializeScene();
        checkTaxes();
        checkBribe();
        
        getSounds().setBackgroundMusic(MusicType.MUSIC);
    }

    private void registerEventListener() {
        Chop.events.addListener(this, Events.ACTION_BACK, Events.EVT_NEW_GAME);
    }

    private void initializeScreen() {
        Gdx.input.setInputProcessor(new TownScreenInput(this, getInputMap()));
    }

    private void checkTaxes() {
        if (getWorld().getDay() % 7 == 0) {
            getPlayer().addMoney(-getWorld().getTaxes());
            getWorld().increaseTaxes();
            if (getPlayer().getMoney() < 0)
                Gdx.app.exit();
        }
    }

    private void checkBribe() {
        if (getWorld().getExecution().getBribe() > 0 || true) {
            DialogBoxObject dialog = new DialogBoxObject(getAssets(), getCamera(), getWorld(), getPlayer());
            dialog.background("textures/packed/Chop.atlas", "ui-box")
                    .avatar("textures/packed/Chop.atlas", "avatar-empty")
                    .text("ZCOOL-40.ttf",
                    () -> "Please! Don't kill my child... I beg of you. I will see that you are well compensated. " +
                            "I know you will make the right choice.",
                    Color.BLACK)
                    .speed(0.5f, 0.025f)
                    .textWidth(getCamera().viewportWidth * 3 / 4f)
                    .onFinish(() -> Gdx.app.log("Dialog", "Dismissed."));
            dialog.load();
            dialog.pack();
            dialog.getTransform().setOrigin(0.5f, 0.0f);
            dialog.getTransform().setPosition(getCamera().viewportWidth / 2, 50);
            Chop.events.addListener(dialog, Events.ACTION_INTERACT);

            getScene().addObjects("GUI", dialog);
        }
    }

    private void initializeScene() {
        TextureAtlas atlas = getAssets().get("textures/packed/Chop.atlas", TextureAtlas.class);

        getScene().addLayer("Background", new Layer());
        getScene().addLayer("Buttons", new Layer());
        getScene().addLayer("Text", new Layer());
        getScene().addLayer("GUI", new Layer());
        getScene().addLayer("PopUp", new Layer());

        TextObject dayText = new TextObject(getAssets(), getCamera(), getWorld(), getPlayer());
        dayText.getTransform().setOrigin(0.5f, 1);
        dayText.getTransform().setPosition(getCamera().viewportWidth / 2, getCamera().viewportHeight - 30);
        dayText.create("ZCOOL-60.ttf", () -> "DAY " + getWorld().getDay());
        dayText.load();

        TextButtonObject castleBtn = new TextButtonObject(getAssets(), getCamera(), getWorld(), getPlayer());
        castleBtn.create("ZCOOL-40.ttf", () -> "Castle");
        castleBtn.load();
        castleBtn.pack();
        castleBtn.getTransform().setPosition(getCamera().viewportWidth / 2 - 150, getCamera().viewportHeight - 250);
        castleBtn.setTouchHandler(new TextButtonHandler(castleBtn, (btn) -> Gdx.app.log("Castle", "Go!")));
        castleBtn.disable();

        TextButtonObject tavernBtn = new TextButtonObject(getAssets(), getCamera(), getWorld(), getPlayer());
        tavernBtn.getTransform().setPosition(300, 250);
        tavernBtn.setHoverScale(1.1f, 1.1f);
        tavernBtn.create("ZCOOL-40.ttf", () -> "Tavern");
        tavernBtn.load();
        tavernBtn.setStyle(TextButtonObject.StyleType.NORMAL,
                new TextButtonStyle().bgTexture(atlas.createPatch("ui-box")).tint(Color.BROWN));
        tavernBtn.setTouchHandler(new TextButtonHandler(tavernBtn, (btn) -> setScreen(Screens.TAVERN)));

        TextButtonObject guillotineBtn = new TextButtonObject(getAssets(), getCamera(), getWorld(), getPlayer());
        guillotineBtn.getTransform().setPosition(getCamera().viewportWidth - 350, 450);
        guillotineBtn.setHoverScale(1.1f, 1.1f);
        guillotineBtn.create("ZCOOL-40.ttf", () -> "Guillotine");
        guillotineBtn.load();
        guillotineBtn.setTouchHandler(new TextButtonHandler(guillotineBtn, (btn) -> setScreen(Screens.EXECUTION)));

        GUIObject gui = new GameGUIObject(getAssets(), getCamera(), getWorld(), getPlayer());
        gui.load();
        gui.pack();

        TooltipObject tooltip = new TooltipObject(getAssets(), getCamera(), getWorld(), getPlayer());
        tooltip.pad(5, 5, 5, 5);
        tooltip.load();
        tooltip.pack();
        Chop.events.addListener(tooltip, Events.MSG_ADD_TOOLTIP, Events.MSG_REMOVE_TOOLTIP);

        getScene().addObjects("Buttons", castleBtn, tavernBtn, guillotineBtn);
        getScene().addObjects("Text", dayText);
        getScene().addObjects("GUI", gui);
        getScene().addObjects("PopUp", tooltip);
        getScene().addQueued();
    }

    @Override
    protected void update(float delta) {
        getScene().update(delta);
    }

    @Override
    protected void render(SpriteBatch batch) {
        getScene().render(batch);
    }

    @Override
    public void handle(Events event, EventData data) {
        if (event == Events.ACTION_BACK) {
            getWorld().reset();
            setScreen(Screens.MAIN_MENU);
        } else if (event == Events.EVT_NEW_GAME) {
            showWelcomePopUp();
        }
    }

    private void showWelcomePopUp() {
        PopUpBoxObject popUp = new PopUpBoxObject(getAssets(), getCamera(), getWorld(), getPlayer());
        popUp.background("textures/packed/Chop.atlas", "ui-box")
                .text("ZCOOL-40.ttf",
                    () -> "Welcome to ChopChop!\n\n" +
                        "Guillotine: Execute people and earn your pay\n" +
                        "Tavern: Drink and socialize to raise your popularity\n" +
                        "Castle: (Coming soon)",
                    Color.BLACK)
                .btn("ZCOOL-40.ttf", () -> "OK!", (btn) -> popUp.die())
                .pad(50, 50, 50, 50)
                .size(getCamera().viewportWidth / 4, getCamera().viewportHeight / 4);
        popUp.load();
        popUp.pack();
        popUp.getTransform().setOrigin(0.5f, 0.5f);
        popUp.getTransform().setPosition(getCamera().viewportWidth / 2, getCamera().viewportHeight / 2);
        getScene().addObjects("PopUp", popUp);
    }
}
