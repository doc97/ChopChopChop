package fi.chop.model.world;

import fi.chop.event.EventData;
import fi.chop.event.EventSystem;
import fi.chop.event.Events;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

public class Player {

    public static final int MIN_REPUTATION_LEVEL = 1;
    public static final int MAX_REPUTATION_LEVEL = 5;

    private final EventSystem eventSystem;
    private final Set<PopularityPerk> perks;
    private float popularity;
    private float reputation;
    private int reputationLevel;
    private int money;

    public Player(EventSystem eventSystem) {
        this.eventSystem = eventSystem;
        perks = EnumSet.noneOf(PopularityPerk.class);
        reputationLevel = MIN_REPUTATION_LEVEL;
    }

    public void addPerks(PopularityPerk... perks) {
        this.perks.addAll(Arrays.asList(perks));
    }

    public void removePerks(PopularityPerk... perks) {
        this.perks.removeAll(Arrays.asList(perks));
    }

    public boolean hasPerk(PopularityPerk perk) {
        return perks.contains(perk);
    }

    public boolean hasAnyPerks() {
        return !perks.isEmpty();
    }

    public void addPopularity(float delta) {
        popularity = Math.min(Math.max(popularity + delta, 0), 1);
        if (eventSystem != null)
            eventSystem.notify(Events.EVT_POPULARITY_CHANGED, new EventData<>(popularity));
    }

    public float getPopularity() {
        return popularity;
    }

    public void addReputation(float delta) {
        reputation += delta;
        while (reputation >= 1) {
            increaseReputationLevel();
            reputation -= 1.0f;
            if (reputationLevel == MAX_REPUTATION_LEVEL) {
                reputation = 1;
                break;
            }
        }
        while (reputation < 0) {
            decreaseReputationLevel();
            reputation += 1.0f;
            if (reputationLevel == MIN_REPUTATION_LEVEL)
                reputation = 0;
        }
        if (eventSystem != null)
            eventSystem.notify(Events.EVT_REPUTATION_CHANGED, new EventData<>(reputation));
    }

    public float getReputation() {
        return reputation;
    }

    private void increaseReputationLevel() {
        if (++reputationLevel > MAX_REPUTATION_LEVEL)
            reputationLevel = MAX_REPUTATION_LEVEL;
        if (eventSystem != null)
            eventSystem.notify(Events.EVT_REPUTATION_LVL_CHANGED, new EventData<>(reputationLevel));
    }

    private void decreaseReputationLevel() {
        if (--reputationLevel < MIN_REPUTATION_LEVEL)
            reputationLevel = MIN_REPUTATION_LEVEL;
        if (eventSystem != null)
            eventSystem.notify(Events.EVT_REPUTATION_LVL_CHANGED, new EventData<>(reputationLevel));
    }

    public int getReputationLevel() {
        return reputationLevel;
    }

    public void addMoney(int delta) {
        money = Math.max(money + delta, 0);
    }

    public int getMoney() {
        return money;
    }

    public boolean hasEnoughMoney(int limit) {
        return money >= limit;
    }
}
