package fi.chop.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import fi.chop.Chop;
import fi.chop.effect.ColorFade;
import fi.chop.engine.Layer;
import fi.chop.event.EventData;
import fi.chop.event.EventListener;
import fi.chop.event.Events;
import fi.chop.input.ExecutionScreenInput;
import fi.chop.model.fsm.states.guillotine.GuillotineStates;
import fi.chop.model.fsm.states.powermeter.PowerMeterStates;
import fi.chop.model.object.*;
import fi.chop.model.object.gui.GUIObject;
import fi.chop.model.object.gui.GameGUIObject;
import fi.chop.model.object.util.TextureObject;
import fi.chop.model.world.PopularityPerk;
import fi.chop.sound.MusicType;
import fi.chop.timer.GameTimer;
import fi.chop.util.FontRenderer;
import fi.chop.util.MathUtil;

public class ExecutionScreen extends ChopScreen implements EventListener {

    private static final float FADEOUT_START_DELAY_SEC = 1.5f;
    private static final float POPULARITY_DELTA = 0.05f;
    private static final float REPUTATION_DELTA = 0.05f;

    private FontRenderer timeText;
    private ColorFade fadeOut;
    private GameTimer.DelayedAction timer;

    private float powerUsed;

    private boolean isNotExiting;
    private boolean isPowerMeterIdle;
    private boolean isGuillotineIdle;

    public ExecutionScreen(Chop game) {
        super(game);
    }

    @Override
    public void show() {
        loadAssets();
        initializeScreen();

        initializeScene();
        initializeEventListener();
    }

    private void initializeScreen() {
        Gdx.input.setInputProcessor(new ExecutionScreenInput(this, getInputMap()));
        timer = Chop.timer.addAction(60, this::endDay);

        isNotExiting = true;
        isGuillotineIdle = true;
        isPowerMeterIdle = true;

        getSounds().setBackgroundMusic(MusicType.MAIN_MUSIC);
    }

    private void loadAssets() {
        BitmapFont font = getAssets().get("ZCOOL-40.ttf", BitmapFont.class);
        timeText = new FontRenderer(font);
    }

    private void initializeScene() {
        getScene().addLayer("Background", new Layer());
        getScene().addLayer("Guillotine", new Layer());
        getScene().addLayer("Heads", new Layer());
        getScene().addLayer("GUI", new Layer());

        TextureObject background = new TextureObject(getAssets(), getCamera(), getWorld(), getPlayer());
        background.setTexture("textures/execution_screen/Background.png");
        background.load();
        background.getTransform().setSize(getCamera().viewportWidth, getCamera().viewportHeight);

        GameObject powerBar = new PowerBarObject(getAssets(), getCamera(), getWorld(), getPlayer());
        powerBar.getTransform().setOrigin(0.5f, 0.5f);
        powerBar.getTransform().setPosition(getCamera().viewportWidth / 2, getCamera().viewportHeight / 2);
        powerBar.load();

        PowerMeterObject powerMeter = new PowerMeterObject(getAssets(), getCamera(), getWorld(), getPlayer());
        powerMeter.getTransform().setOrigin(0, 0.5f);
        powerMeter.getTransform().setPosition(
                powerBar.getTransform().getX() + powerBar.getTransform().getWidth() / 2 + 10,
                powerBar.getTransform().getY());
        powerMeter.load();
        powerMeter.pack();

        GameObject guillotine = new GuillotineObject(getAssets(), getCamera(), getWorld(), getPlayer());
        guillotine.getTransform().setOrigin(0.5f, 0);
        guillotine.getTransform().setPosition(480, 0);
        guillotine.load();

        GameObject person = new PersonObject(getAssets(), getCamera(), getWorld(), getPlayer());
        person.load();
        person.getTransform().setOrigin(0.5f, 0.5f);
        person.getTransform().setScale(0.25f, 0.25f);
        person.resizeToFitChildren();
        person.getTransform().setPosition(
                guillotine.getTransform().getX() - 15,
                guillotine.getTransform().getY() + 260);

        ScrollObject scroll = new ScrollObject(getAssets(), getCamera(), getWorld(), getPlayer());
        scroll.getTransform().setPosition(getCamera().viewportWidth - 600, 75);
        scroll.load();
        scroll.setExecution(getWorld().getExecution());

        GUIObject gui = new GameGUIObject(getAssets(), getCamera(), getWorld(), getPlayer());
        gui.load();
        gui.pack();

        Chop.events.addListener(powerMeter, Events.EVT_GUILLOTINE_RAISE, Events.EVT_GUILLOTINE_PREPARED);
        Chop.events.addListener(guillotine, Events.EVT_GUILLOTINE_RAISE);
        Chop.events.addListener(person, Events.ACTION_MERCY, Events.EVT_PERSON_KILLED);

        getScene().addObjects("Background", background);
        getScene().addObjects("Heads", person);
        getScene().addObjects("Guillotine", guillotine);
        getScene().addObjects("GUI", gui, powerBar, powerMeter, scroll);
        getScene().addQueued();
    }

    private void initializeEventListener() {
        Chop.events.addListener(this,
                Events.ACTION_BACK, Events.ACTION_INTERACT,
                Events.EVT_GUILLOTINE_PREPARED, Events.EVT_GUILLOTINE_RESTORED,
                Events.EVT_PERSON_SAVED, Events.EVT_PERSON_KILLED,
                Events.EVT_GUILLOTINE_STATE_CHANGED, Events.EVT_POWER_METER_STATE_CHANGED
                );
    }

    @Override
    public void hide() {
        super.hide();
        getStats().resetDailyKills();
    }

    @Override
    protected void update(float delta) {
        getScene().update(delta);

        if (fadeOut != null)
            fadeOut.update(delta);
    }

    @Override
    protected void render(SpriteBatch batch) {
        if (fadeOut != null)
            batch.setColor(fadeOut.getColor());
        getScene().render(batch);
        drawTime(batch);
        batch.setColor(Color.WHITE);
    }

    private void drawTime(SpriteBatch batch) {
        timeText
                .text("Time left: " + String.format("%.0f", Chop.timer.getTimeLeft(timer)))
                .center(getCamera(), true, false)
                .y(getCamera().viewportHeight - 30)
                .draw(batch);
    }

    private void endDay() {
        if (isNotExiting) {
            isNotExiting = false;
            fadeOut = new ColorFade(Color.WHITE, Color.BLACK, 2.0f, (t) -> MathUtil.smoothStartN(t, 2))
                    .onFinish(() -> Chop.timer.addAction(1.0f, () -> {
                        getPlayer().randomizePopularityPerks();
                        getWorld().nextDay();
                        setScreen(Screens.TOWN);
                    }));
        }
    }

    private void exitGame() {
        if (isNotExiting) {
            isNotExiting = false;
            fadeOut = new ColorFade(Color.WHITE, Color.BLACK, 1.0f, (t) -> MathUtil.smoothStartN(t, 2))
                    .onFinish(() -> Chop.timer.addAction(0.5f, () -> setScreen(Screens.MAIN_MENU)));
        }
    }

    private void updatePlayerStats(boolean wasCorrect, boolean wasKill) {
        boolean ignoreWrong = getPlayer().hasPerk(PopularityPerk.TURNING_A_BLIND_EYE);
        getPlayer().addPopularity(wasCorrect ? POPULARITY_DELTA : ignoreWrong ? 0 : -POPULARITY_DELTA);
        getPlayer().addReputation(wasKill ? REPUTATION_DELTA : -REPUTATION_DELTA);

        int payMultiplier = 1;
        if (wasCorrect && getPlayer().hasPerk(PopularityPerk.GIFT_OF_THE_PEOPLE))
            payMultiplier = 2;

        if (wasKill)
            paySalary(payMultiplier);
        else
            payBribe(payMultiplier);
    }

    private void paySalary(int multiplier) {
        int baseIncome = getWorld().getExecution().getSalary();
        int skillIncome = (int) (MathUtil.smoothStartN(powerUsed, 3) * baseIncome);
        int totalIncome = multiplier * (baseIncome + skillIncome);
        getPlayer().addMoney(totalIncome);
    }

    private void payBribe(int multiplier) {
        int totalIncome = multiplier * getWorld().getExecution().getBribe();
        getPlayer().addMoney(totalIncome);
    }

    @Override
    public void handle(Events event, EventData data) {
        switch (event) {
            case ACTION_INTERACT:
                if (isPowerMeterIdle && isGuillotineIdle && isNotExiting) {
                    float value = getScene().findOne(PowerBarObject.class).getValue();
                    Chop.events.notify(Events.EVT_GUILLOTINE_RAISE, new EventData<>(value));
                }
                break;
            case ACTION_BACK:
                exitGame();
                break;
            case EVT_GUILLOTINE_STATE_CHANGED:
                isGuillotineIdle = data.get() == GuillotineStates.IDLE;
                break;
            case EVT_POWER_METER_STATE_CHANGED:
                isPowerMeterIdle = data.get() == PowerMeterStates.IDLE;
                break;
            case EVT_GUILLOTINE_PREPARED:
                PowerMeterObject meter = getScene().findOne(PowerMeterObject.class);
                float power = meter.getMeterFillPercentage();
                powerUsed = power;
                getStats().registerPower(power);
                break;
            case EVT_GUILLOTINE_RESTORED:
                endDay();
                break;
            case EVT_PERSON_SAVED:
                Chop.timer.addAction(FADEOUT_START_DELAY_SEC, this::endDay);
                updatePlayerStats(!getWorld().getExecution().isFairPunishment(), false);
                break;
            case EVT_PERSON_KILLED:
                getStats().addDailyKill();
                updatePlayerStats(getWorld().getExecution().isFairPunishment(), true);
                break;
            default:
                break;
        }
    }
}
