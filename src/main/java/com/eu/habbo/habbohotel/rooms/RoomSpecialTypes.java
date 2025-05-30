package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.items.ICycleable;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.*;
import com.eu.habbo.habbohotel.items.interactions.config.InteractionHanditemBlocker;
import com.eu.habbo.habbohotel.items.interactions.config.InteractionInvisibleItemController;
import com.eu.habbo.habbohotel.items.interactions.config.InteractionRollerSpeedController;
import com.eu.habbo.habbohotel.items.interactions.config.InteractionWiredDisabler;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameGate;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameScoreboard;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameTimer;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.InteractionBattleBanzaiSphere;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.InteractionBattleBanzaiTeleporter;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.gates.InteractionBattleBanzaiGate;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.scoreboards.InteractionBattleBanzaiScoreboard;
import com.eu.habbo.habbohotel.items.interactions.games.football.scoreboards.InteractionFootballScoreboard;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeExitTile;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.gates.InteractionFreezeGate;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.scoreboards.InteractionFreezeScoreboard;
import com.eu.habbo.habbohotel.items.interactions.games.tag.InteractionTagField;
import com.eu.habbo.habbohotel.items.interactions.games.tag.InteractionTagPole;
import com.eu.habbo.habbohotel.items.interactions.invisible.InteractionInvisibleItem;
import com.eu.habbo.habbohotel.items.interactions.pets.*;
import com.eu.habbo.habbohotel.items.interactions.wired.extra.WiredBlob;
import com.eu.habbo.habbohotel.rooms.utils.SpecialItemSet;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredConditionType;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;


public class RoomSpecialTypes {
    private final THashMap<Integer, InteractionBattleBanzaiTeleporter> banzaiTeleporters;
    private final THashMap<Integer, InteractionNest> nests;
    private final THashMap<Integer, InteractionPetDrink> petDrinks;
    private final THashMap<Integer, InteractionPetFood> petFoods;
    private final THashMap<Integer, InteractionPetToy> petToys;
    private final THashMap<Integer, InteractionRoller> rollers;

    private final THashMap<WiredTriggerType, THashSet<InteractionWiredTrigger>> wiredTriggers;
    private final THashMap<WiredEffectType, THashSet<InteractionWiredEffect>> wiredEffects;
    private final THashMap<WiredConditionType, THashSet<InteractionWiredCondition>> wiredConditions;
    private final THashMap<Integer, InteractionWiredExtra> wiredExtras;

    private final THashMap<Integer, InteractionGameScoreboard> gameScoreboards;
    private final THashMap<Integer, InteractionGameGate> gameGates;
    private final THashMap<Integer, InteractionGameTimer> gameTimers;

    private final THashMap<Integer, InteractionFreezeExitTile> freezeExitTile;
    private final THashMap<Integer, HabboItem> undefined;
    private final THashSet<ICycleable> cycleTasks;

    private final SpecialItemSet<InteractionInvisibleItem> invisibleItems;
    private InteractionInvisibleItemController invisibleItemController = null;
    private InteractionWiredDisabler wiredDisabler = null;
    private InteractionHanditemBlocker handitemBlocker = null;
    private InteractionRollerSpeedController rollerSpeedController = null;


    public RoomSpecialTypes() {
        banzaiTeleporters = new THashMap<>(0);
        nests = new THashMap<>(0);
        petDrinks = new THashMap<>(0);
        petFoods = new THashMap<>(0);
        petToys = new THashMap<>(0);
        rollers = new THashMap<>(0);

        wiredTriggers = new THashMap<>(0);
        wiredEffects = new THashMap<>(0);
        wiredConditions = new THashMap<>(0);
        wiredExtras = new THashMap<>(0);

        gameScoreboards = new THashMap<>(0);
        gameGates = new THashMap<>(0);
        gameTimers = new THashMap<>(0);

        freezeExitTile = new THashMap<>(0);
        undefined = new THashMap<>(0);
        cycleTasks = new THashSet<>(0);
        invisibleItems = new SpecialItemSet<>();
    }

    public void handleAddItem(HabboItem item){
        if (item instanceof ICycleable) {
            this.addCycleTask((ICycleable) item);
        }
        if (item instanceof InteractionWiredTrigger) {
            this.addTrigger((InteractionWiredTrigger) item);
        } else if (item instanceof InteractionWiredEffect) {
            this.addEffect((InteractionWiredEffect) item);
        } else if (item instanceof InteractionWiredCondition) {
            this.addCondition((InteractionWiredCondition) item);
        } else if (item instanceof InteractionWiredExtra) {
            this.addExtra((InteractionWiredExtra) item);
        } else if (item instanceof InteractionBattleBanzaiTeleporter) {
            this.addBanzaiTeleporter((InteractionBattleBanzaiTeleporter) item);
        } else if (item instanceof InteractionRoller) {
            this.addRoller((InteractionRoller) item);
        } else if (item instanceof InteractionGameScoreboard) {
            this.addGameScoreboard((InteractionGameScoreboard) item);
        } else if (item instanceof InteractionGameGate) {
            this.addGameGate((InteractionGameGate) item);
        } else if (item instanceof InteractionGameTimer) {
            this.addGameTimer((InteractionGameTimer) item);
        } else if (item instanceof InteractionFreezeExitTile) {
            this.addFreezeExitTile((InteractionFreezeExitTile) item);
        } else if (item instanceof InteractionNest) {
            this.addNest((InteractionNest) item);
        } else if (item instanceof InteractionPetDrink) {
            this.addPetDrink((InteractionPetDrink) item);
        } else if (item instanceof InteractionPetFood) {
            this.addPetFood((InteractionPetFood) item);
        } else if (item instanceof InteractionMoodLight) {
            this.addUndefined(item);
        } else if (item instanceof InteractionPyramid) {
            this.addUndefined(item);
        } else if (item instanceof InteractionMusicDisc) {
            this.addUndefined(item);
        } else if (item instanceof InteractionBattleBanzaiSphere) {
            this.addUndefined(item);
        } else if (item instanceof InteractionTalkingFurniture) {
            this.addUndefined(item);
        } else if (item instanceof InteractionWater) {
            this.addUndefined(item);
        } else if (item instanceof InteractionWaterItem) {
            this.addUndefined(item);
        } else if (item instanceof InteractionMuteArea) {
            this.addUndefined(item);
        } else if (item instanceof InteractionBuildArea) {
            this.addUndefined(item);
        } else if (item instanceof InteractionTagPole) {
            this.addUndefined(item);
        } else if (item instanceof InteractionTagField) {
            this.addUndefined(item);
        } else if (item instanceof InteractionJukeBox) {
            this.addUndefined(item);
        } else if (item instanceof InteractionPetBreedingNest) {
            this.addUndefined(item);
        } else if (item instanceof InteractionBlackHole) {
            this.addUndefined(item);
        } else if (item instanceof InteractionWiredHighscore) {
            this.addUndefined(item);
        } else if (item instanceof InteractionStickyPole) {
            this.addUndefined(item);
        } else if (item instanceof WiredBlob) {
            this.addUndefined(item);
        } else if (item instanceof InteractionTent) {
            this.addUndefined(item);
        } else if (item instanceof InteractionSnowboardSlope) {
            this.addUndefined(item);
        } else if (item instanceof InteractionFireworks) {
            this.addUndefined(item);
        } else if (item instanceof InteractionInvisibleItem specialItem){
            this.getInvisibleItems().add(specialItem);
        } else if (item instanceof InteractionInvisibleItemController specialItem){
            this.setInvisibleItemController(specialItem);
        } else if (item instanceof InteractionWiredDisabler specialItem){
            this.setWiredDisabler(specialItem);
        } else if (item instanceof InteractionHanditemBlocker specialItem){
            this.setHanditemBlocker(specialItem);
        } else if (item instanceof InteractionRollerSpeedController speedController){
            this.setRollerSpeedController(speedController);
        }
    }
    public void handleRemoveItem(HabboItem item){
        if (item instanceof ICycleable) {
            this.removeCycleTask((ICycleable) item);
        }

        if (item instanceof InteractionBattleBanzaiTeleporter) {
            this.removeBanzaiTeleporter((InteractionBattleBanzaiTeleporter) item);
        } else if (item instanceof InteractionWiredTrigger) {
            this.removeTrigger((InteractionWiredTrigger) item);
        } else if (item instanceof InteractionWiredEffect) {
            this.removeEffect((InteractionWiredEffect) item);
        } else if (item instanceof InteractionWiredCondition) {
            this.removeCondition((InteractionWiredCondition) item);
        } else if (item instanceof InteractionWiredExtra) {
            this.removeExtra((InteractionWiredExtra) item);
        } else if (item instanceof InteractionRoller) {
            this.removeRoller((InteractionRoller) item);
        } else if (item instanceof InteractionGameScoreboard) {
            this.removeScoreboard((InteractionGameScoreboard) item);
        } else if (item instanceof InteractionGameGate) {
            this.removeGameGate((InteractionGameGate) item);
        } else if (item instanceof InteractionGameTimer) {
            this.removeGameTimer((InteractionGameTimer) item);
        } else if (item instanceof InteractionFreezeExitTile) {
            this.removeFreezeExitTile((InteractionFreezeExitTile) item);
        } else if (item instanceof InteractionNest) {
            this.removeNest((InteractionNest) item);
        } else if (item instanceof InteractionPetDrink) {
            this.removePetDrink((InteractionPetDrink) item);
        } else if (item instanceof InteractionPetFood) {
            this.removePetFood((InteractionPetFood) item);
        } else if (item instanceof InteractionMoodLight) {
            this.removeUndefined(item);
        } else if (item instanceof InteractionPyramid) {
            this.removeUndefined(item);
        } else if (item instanceof InteractionMusicDisc) {
            this.removeUndefined(item);
        } else if (item instanceof InteractionBattleBanzaiSphere) {
            this.removeUndefined(item);
        } else if (item instanceof InteractionTalkingFurniture) {
            this.removeUndefined(item);
        } else if (item instanceof InteractionWaterItem) {
            this.removeUndefined(item);
        } else if (item instanceof InteractionWater) {
            this.removeUndefined(item);
        } else if (item instanceof InteractionMuteArea) {
            this.removeUndefined(item);
        } else if (item instanceof InteractionTagPole) {
            this.removeUndefined(item);
        } else if (item instanceof InteractionTagField) {
            this.removeUndefined(item);
        } else if (item instanceof InteractionJukeBox) {
            this.removeUndefined(item);
        } else if (item instanceof InteractionPetBreedingNest) {
            this.removeUndefined(item);
        } else if (item instanceof InteractionBlackHole) {
            this.removeUndefined(item);
        } else if (item instanceof InteractionWiredHighscore) {
            this.removeUndefined(item);
        } else if (item instanceof InteractionStickyPole) {
            this.removeUndefined(item);
        } else if (item instanceof WiredBlob) {
            this.removeUndefined(item);
        } else if (item instanceof InteractionTent) {
            this.removeUndefined(item);
        } else if (item instanceof InteractionSnowboardSlope) {
            this.removeUndefined(item);
        } else if (item instanceof InteractionInvisibleItem specialItem) {
            this.getInvisibleItems().remove(specialItem);
        } else if (item instanceof InteractionInvisibleItemController) {
            this.removeInvisibleItemController();
        } else if (item instanceof InteractionWiredDisabler) {
            this.removeWiredDisabler();
        } else if (item instanceof InteractionHanditemBlocker) {
            this.removeHanditemBlocker();
        } else if (item instanceof InteractionRollerSpeedController) {
            this.removeRollerSpeedController();
        }
    }
    public HabboItem getItem(int id){
        List<Supplier<HabboItem>> suppliers = List.of(
                () -> getBanzaiTeleporter(id),
                () -> getTrigger(id),
                () -> getEffect(id),
                () -> getCondition(id),
                () -> getGameGate(id),
                () -> getGameScorebord(id),
                () -> getGameTimer(id),
                () -> getFreezeExitTiles().get(id),
                () -> getRoller(id),
                () -> getNest(id),
                () -> getPetDrink(id),
                () -> getInvisibleItems().get(id),
                this::getInvisibleItemController,
                this::getWiredDisabler,
                this::getHanditemBlocker,
                this::getRollerSpeedController
        );

        for(Supplier<HabboItem> supplier : suppliers){
            HabboItem result = supplier.get();
            if(result != null) return result;
        }
        return null;
    }

    public SpecialItemSet<InteractionInvisibleItem> getInvisibleItems() {
        return invisibleItems;
    }

    public void setInvisibleItemController(InteractionInvisibleItemController item) {
        if(invisibleItemController == null) invisibleItemController = item;
    }
    public void removeInvisibleItemController() {
        invisibleItemController = null;
    }
    public InteractionInvisibleItemController getInvisibleItemController(){
        return invisibleItemController;
    }

    public void setWiredDisabler(InteractionWiredDisabler item) {
        if(wiredDisabler == null) wiredDisabler = item;
    }
    public void removeWiredDisabler() {
        wiredDisabler = null;
    }
    public InteractionWiredDisabler getWiredDisabler(){
        return wiredDisabler;
    }

    public void setHanditemBlocker(InteractionHanditemBlocker item) {
        if(handitemBlocker == null) handitemBlocker = item;
    }
    public void removeHanditemBlocker() {
        handitemBlocker = null;
    }
    public InteractionHanditemBlocker getHanditemBlocker(){
        return handitemBlocker;
    }

    public void setRollerSpeedController(InteractionRollerSpeedController item) {
        if(rollerSpeedController == null) rollerSpeedController = item;
    }
    public void removeRollerSpeedController() {
        rollerSpeedController = null;
    }
    public InteractionRollerSpeedController getRollerSpeedController(){
        return rollerSpeedController;
    }

    public InteractionBattleBanzaiTeleporter getBanzaiTeleporter(int itemId) {
        return this.banzaiTeleporters.get(itemId);
    }

    public void addBanzaiTeleporter(InteractionBattleBanzaiTeleporter item) {
        this.banzaiTeleporters.put(item.getId(), item);
    }

    public void removeBanzaiTeleporter(InteractionBattleBanzaiTeleporter item) {
        this.banzaiTeleporters.remove(item.getId());
    }

    public THashSet<InteractionBattleBanzaiTeleporter> getBanzaiTeleporters() {
        synchronized (this.banzaiTeleporters) {
            THashSet<InteractionBattleBanzaiTeleporter> battleBanzaiTeleporters = new THashSet<>();
            battleBanzaiTeleporters.addAll(this.banzaiTeleporters.values());

            return battleBanzaiTeleporters;
        }
    }

    public InteractionBattleBanzaiTeleporter getRandomTeleporter(Item baseItem, InteractionBattleBanzaiTeleporter exclude) {
        List<InteractionBattleBanzaiTeleporter> teleporterList = new ArrayList<>();
        for (InteractionBattleBanzaiTeleporter teleporter : this.banzaiTeleporters.values()) {
            if (baseItem == null || teleporter.getBaseItem() == baseItem) {
                teleporterList.add(teleporter);
            }
        }

        teleporterList.remove(exclude);

        if (!teleporterList.isEmpty()) {
            Collections.shuffle(teleporterList);
            return teleporterList.get(0);
        }

        return null;
    }


    public InteractionNest getNest(int itemId) {
        return this.nests.get(itemId);
    }

    public void addNest(InteractionNest item) {
        this.nests.put(item.getId(), item);
    }

    public void removeNest(InteractionNest item) {
        this.nests.remove(item.getId());
    }

    public THashSet<InteractionNest> getNests() {
        synchronized (this.nests) {
            THashSet<InteractionNest> nests = new THashSet<>();
            nests.addAll(this.nests.values());

            return nests;
        }
    }


    public InteractionPetDrink getPetDrink(int itemId) {
        return this.petDrinks.get(itemId);
    }

    public void addPetDrink(InteractionPetDrink item) {
        this.petDrinks.put(item.getId(), item);
    }

    public void removePetDrink(InteractionPetDrink item) {
        this.petDrinks.remove(item.getId());
    }

    public THashSet<InteractionPetDrink> getPetDrinks() {
        synchronized (this.petDrinks) {
            THashSet<InteractionPetDrink> petDrinks = new THashSet<>();
            petDrinks.addAll(this.petDrinks.values());

            return petDrinks;
        }
    }


    public InteractionPetFood getPetFood(int itemId) {
        return this.petFoods.get(itemId);
    }

    public void addPetFood(InteractionPetFood item) {
        this.petFoods.put(item.getId(), item);
    }

    public void removePetFood(InteractionPetFood petFood) {
        this.petFoods.remove(petFood.getId());
    }

    public THashSet<InteractionPetFood> getPetFoods() {
        synchronized (this.petFoods) {
            THashSet<InteractionPetFood> petFoods = new THashSet<>();
            petFoods.addAll(this.petFoods.values());

            return petFoods;
        }
    }


    public InteractionPetToy getPetToy(int itemId) {
        return this.petToys.get(itemId);
    }

    public void addPetToy(InteractionPetToy item) {
        this.petToys.put(item.getId(), item);
    }

    public void removePetToy(InteractionPetToy petToy) {
        this.petToys.remove(petToy.getId());
    }

    public THashSet<InteractionPetToy> getPetToys() {
        synchronized (this.petToys) {
            THashSet<InteractionPetToy> petToys = new THashSet<>();
            petToys.addAll(this.petToys.values());

            return petToys;
        }
    }


    public InteractionRoller getRoller(int itemId) {
        synchronized (this.rollers) {
            return this.rollers.get(itemId);
        }
    }

    public void addRoller(InteractionRoller item) {
        synchronized (this.rollers) {
            this.rollers.put(item.getId(), item);
        }
    }

    public void removeRoller(InteractionRoller roller) {
        synchronized (this.rollers) {
            this.rollers.remove(roller.getId());
        }
    }

    public THashMap<Integer, InteractionRoller> getRollers() {
        return this.rollers;
    }


    public InteractionWiredTrigger getTrigger(int itemId) {
        synchronized (this.wiredTriggers) {
            for (Map.Entry<WiredTriggerType, THashSet<InteractionWiredTrigger>> map : this.wiredTriggers.entrySet()) {
                for (InteractionWiredTrigger trigger : map.getValue()) {
                    if (trigger.getId() == itemId)
                        return trigger;
                }
            }

            return null;
        }
    }

    public THashSet<InteractionWiredTrigger> getTriggers() {
        synchronized (this.wiredTriggers) {
            THashSet<InteractionWiredTrigger> triggers = new THashSet<>();

            for (Map.Entry<WiredTriggerType, THashSet<InteractionWiredTrigger>> map : this.wiredTriggers.entrySet()) {
                triggers.addAll(map.getValue());
            }

            return triggers;
        }
    }

    public THashSet<InteractionWiredTrigger> getTriggers(WiredTriggerType type) {
        return this.wiredTriggers.get(type);
    }

    public THashSet<InteractionWiredTrigger> getTriggers(int x, int y) {
        synchronized (this.wiredTriggers) {
            THashSet<InteractionWiredTrigger> triggers = new THashSet<>();

            for (Map.Entry<WiredTriggerType, THashSet<InteractionWiredTrigger>> map : this.wiredTriggers.entrySet()) {
                for (InteractionWiredTrigger trigger : map.getValue()) {
                    if (trigger.getX() == x && trigger.getY() == y)
                        triggers.add(trigger);
                }
            }

            return triggers;
        }
    }

    public void addTrigger(InteractionWiredTrigger trigger) {
        synchronized (this.wiredTriggers) {
            if (!this.wiredTriggers.containsKey(trigger.getType()))
                this.wiredTriggers.put(trigger.getType(), new THashSet<>());

            this.wiredTriggers.get(trigger.getType()).add(trigger);
        }
    }

    public void removeTrigger(InteractionWiredTrigger trigger) {
        synchronized (this.wiredTriggers) {
            this.wiredTriggers.get(trigger.getType()).remove(trigger);

            if (this.wiredTriggers.get(trigger.getType()).isEmpty()) {
                this.wiredTriggers.remove(trigger.getType());
            }
        }
    }


    public InteractionWiredEffect getEffect(int itemId) {
        synchronized (this.wiredEffects) {
            for (Map.Entry<WiredEffectType, THashSet<InteractionWiredEffect>> map : this.wiredEffects.entrySet()) {
                for (InteractionWiredEffect effect : map.getValue()) {
                    if (effect.getId() == itemId)
                        return effect;
                }
            }
        }

        return null;
    }

    public THashSet<InteractionWiredEffect> getEffects() {
        synchronized (this.wiredEffects) {
            THashSet<InteractionWiredEffect> effects = new THashSet<>();

            for (Map.Entry<WiredEffectType, THashSet<InteractionWiredEffect>> map : this.wiredEffects.entrySet()) {
                effects.addAll(map.getValue());
            }

            return effects;
        }
    }

    public THashSet<InteractionWiredEffect> getEffects(WiredEffectType type) {
        return this.wiredEffects.get(type);
    }

    public THashSet<InteractionWiredEffect> getEffects(int x, int y) {
        synchronized (this.wiredEffects) {
            THashSet<InteractionWiredEffect> effects = new THashSet<>();

            for (Map.Entry<WiredEffectType, THashSet<InteractionWiredEffect>> map : this.wiredEffects.entrySet()) {
                for (InteractionWiredEffect effect : map.getValue()) {
                    if (effect.getX() == x && effect.getY() == y)
                        effects.add(effect);
                }
            }

            return effects;
        }
    }

    public void addEffect(InteractionWiredEffect effect) {
        synchronized (this.wiredEffects) {
            if (!this.wiredEffects.containsKey(effect.getType()))
                this.wiredEffects.put(effect.getType(), new THashSet<>());

            this.wiredEffects.get(effect.getType()).add(effect);
        }
    }

    public void removeEffect(InteractionWiredEffect effect) {
        synchronized (this.wiredEffects) {
            this.wiredEffects.get(effect.getType()).remove(effect);

            if (this.wiredEffects.get(effect.getType()).isEmpty()) {
                this.wiredEffects.remove(effect.getType());
            }
        }
    }


    public InteractionWiredCondition getCondition(int itemId) {
        synchronized (this.wiredConditions) {
            for (Map.Entry<WiredConditionType, THashSet<InteractionWiredCondition>> map : this.wiredConditions.entrySet()) {
                for (InteractionWiredCondition condition : map.getValue()) {
                    if (condition.getId() == itemId)
                        return condition;
                }
            }
        }

        return null;
    }

    public THashSet<InteractionWiredCondition> getConditions() {
        synchronized (this.wiredConditions) {
            THashSet<InteractionWiredCondition> conditions = new THashSet<>();

            for (Map.Entry<WiredConditionType, THashSet<InteractionWiredCondition>> map : this.wiredConditions.entrySet()) {
                conditions.addAll(map.getValue());
            }

            return conditions;
        }
    }

    public THashSet<InteractionWiredCondition> getConditions(WiredConditionType type) {
        synchronized (this.wiredConditions) {
            return this.wiredConditions.get(type);
        }
    }

    public THashSet<InteractionWiredCondition> getConditions(int x, int y) {
        synchronized (this.wiredConditions) {
            THashSet<InteractionWiredCondition> conditions = new THashSet<>();

            for (Map.Entry<WiredConditionType, THashSet<InteractionWiredCondition>> map : this.wiredConditions.entrySet()) {
                for (InteractionWiredCondition condition : map.getValue()) {
                    if (condition.getX() == x && condition.getY() == y)
                        conditions.add(condition);
                }
            }

            return conditions;
        }
    }

    public void addCondition(InteractionWiredCondition condition) {
        synchronized (this.wiredConditions) {
            if (!this.wiredConditions.containsKey(condition.getType()))
                this.wiredConditions.put(condition.getType(), new THashSet<>());

            this.wiredConditions.get(condition.getType()).add(condition);
        }
    }

    public void removeCondition(InteractionWiredCondition condition) {
        synchronized (this.wiredConditions) {
            this.wiredConditions.get(condition.getType()).remove(condition);

            if (this.wiredConditions.get(condition.getType()).isEmpty()) {
                this.wiredConditions.remove(condition.getType());
            }
        }
    }


    public THashSet<InteractionWiredExtra> getExtras() {
        synchronized (this.wiredExtras) {
            THashSet<InteractionWiredExtra> conditions = new THashSet<>();

            for (Map.Entry<Integer, InteractionWiredExtra> map : this.wiredExtras.entrySet()) {
                conditions.add(map.getValue());
            }

            return conditions;
        }
    }

    public THashSet<InteractionWiredExtra> getExtras(int x, int y) {
        synchronized (this.wiredExtras) {
            THashSet<InteractionWiredExtra> extras = new THashSet<>();

            for (Map.Entry<Integer, InteractionWiredExtra> map : this.wiredExtras.entrySet()) {
                if (map.getValue().getX() == x && map.getValue().getY() == y) {
                    extras.add(map.getValue());
                }
            }

            return extras;
        }
    }

    public void addExtra(InteractionWiredExtra extra) {
        synchronized (this.wiredExtras) {
            this.wiredExtras.put(extra.getId(), extra);
        }
    }

    public void removeExtra(InteractionWiredExtra extra) {
        synchronized (this.wiredExtras) {
            this.wiredExtras.remove(extra.getId());
        }
    }

    public boolean hasExtraType(short x, short y, Class<? extends InteractionWiredExtra> type) {
        synchronized (this.wiredExtras) {
            for (Map.Entry<Integer, InteractionWiredExtra> map : this.wiredExtras.entrySet()) {
                if (map.getValue().getX() == x && map.getValue().getY() == y && map.getValue().getClass().isAssignableFrom(type)) {
                    return true;
                }
            }
        }

        return false;
    }


    public InteractionGameScoreboard getGameScorebord(int itemId) {
        return this.gameScoreboards.get(itemId);
    }

    public void addGameScoreboard(InteractionGameScoreboard scoreboard) {
        this.gameScoreboards.put(scoreboard.getId(), scoreboard);
    }

    public void removeScoreboard(InteractionGameScoreboard scoreboard) {
        this.gameScoreboards.remove(scoreboard.getId());
    }

    public THashMap<Integer, InteractionFreezeScoreboard> getFreezeScoreboards() {
        synchronized (this.gameScoreboards) {
            THashMap<Integer, InteractionFreezeScoreboard> boards = new THashMap<>();

            for (Map.Entry<Integer, InteractionGameScoreboard> set : this.gameScoreboards.entrySet()) {
                if (set.getValue() instanceof InteractionFreezeScoreboard) {
                    boards.put(set.getValue().getId(), (InteractionFreezeScoreboard) set.getValue());
                }
            }

            return boards;
        }
    }

    public THashMap<Integer, InteractionFreezeScoreboard> getFreezeScoreboards(GameTeamColors teamColor) {
        synchronized (this.gameScoreboards) {
            THashMap<Integer, InteractionFreezeScoreboard> boards = new THashMap<>();

            for (Map.Entry<Integer, InteractionGameScoreboard> set : this.gameScoreboards.entrySet()) {
                if (set.getValue() instanceof InteractionFreezeScoreboard) {
                    if ((set.getValue()).teamColor.equals(teamColor))
                        boards.put(set.getValue().getId(), (InteractionFreezeScoreboard) set.getValue());
                }
            }

            return boards;
        }
    }

    public THashMap<Integer, InteractionBattleBanzaiScoreboard> getBattleBanzaiScoreboards() {
        synchronized (this.gameScoreboards) {
            THashMap<Integer, InteractionBattleBanzaiScoreboard> boards = new THashMap<>();

            for (Map.Entry<Integer, InteractionGameScoreboard> set : this.gameScoreboards.entrySet()) {
                if (set.getValue() instanceof InteractionBattleBanzaiScoreboard) {
                    boards.put(set.getValue().getId(), (InteractionBattleBanzaiScoreboard) set.getValue());
                }
            }

            return boards;
        }
    }

    public THashMap<Integer, InteractionBattleBanzaiScoreboard> getBattleBanzaiScoreboards(GameTeamColors teamColor) {
        synchronized (this.gameScoreboards) {
            THashMap<Integer, InteractionBattleBanzaiScoreboard> boards = new THashMap<>();

            for (Map.Entry<Integer, InteractionGameScoreboard> set : this.gameScoreboards.entrySet()) {
                if (set.getValue() instanceof InteractionBattleBanzaiScoreboard) {
                    if ((set.getValue()).teamColor.equals(teamColor))
                        boards.put(set.getValue().getId(), (InteractionBattleBanzaiScoreboard) set.getValue());
                }
            }

            return boards;
        }
    }

    public THashMap<Integer, InteractionFootballScoreboard> getFootballScoreboards() {
        synchronized (this.gameScoreboards) {
            THashMap<Integer, InteractionFootballScoreboard> boards = new THashMap<>();

            for (Map.Entry<Integer, InteractionGameScoreboard> set : this.gameScoreboards.entrySet()) {
                if (set.getValue() instanceof InteractionFootballScoreboard) {
                    boards.put(set.getValue().getId(), (InteractionFootballScoreboard) set.getValue());
                }
            }

            return boards;
        }
    }

    public THashMap<Integer, InteractionFootballScoreboard> getFootballScoreboards(GameTeamColors teamColor) {
        synchronized (this.gameScoreboards) {
            THashMap<Integer, InteractionFootballScoreboard> boards = new THashMap<>();

            for (Map.Entry<Integer, InteractionGameScoreboard> set : this.gameScoreboards.entrySet()) {
                if (set.getValue() instanceof InteractionFootballScoreboard) {
                    if ((set.getValue()).teamColor.equals(teamColor))
                        boards.put(set.getValue().getId(), (InteractionFootballScoreboard) set.getValue());
                }
            }

            return boards;
        }
    }


    public InteractionGameGate getGameGate(int itemId) {
        return this.gameGates.get(itemId);
    }

    public void addGameGate(InteractionGameGate gameGate) {
        this.gameGates.put(gameGate.getId(), gameGate);
    }

    public void removeGameGate(InteractionGameGate gameGate) {
        this.gameGates.remove(gameGate.getId());
    }

    public THashMap<Integer, InteractionFreezeGate> getFreezeGates() {
        synchronized (this.gameGates) {
            THashMap<Integer, InteractionFreezeGate> gates = new THashMap<>();

            for (Map.Entry<Integer, InteractionGameGate> set : this.gameGates.entrySet()) {
                if (set.getValue() instanceof InteractionFreezeGate) {
                    gates.put(set.getValue().getId(), (InteractionFreezeGate) set.getValue());
                }
            }

            return gates;
        }
    }

    public THashMap<Integer, InteractionBattleBanzaiGate> getBattleBanzaiGates() {
        synchronized (this.gameGates) {
            THashMap<Integer, InteractionBattleBanzaiGate> gates = new THashMap<>();

            for (Map.Entry<Integer, InteractionGameGate> set : this.gameGates.entrySet()) {
                if (set.getValue() instanceof InteractionBattleBanzaiGate) {
                    gates.put(set.getValue().getId(), (InteractionBattleBanzaiGate) set.getValue());
                }
            }

            return gates;
        }
    }


    public InteractionGameTimer getGameTimer(int itemId) {
        return this.gameTimers.get(itemId);
    }

    public void addGameTimer(InteractionGameTimer gameTimer) {
        this.gameTimers.put(gameTimer.getId(), gameTimer);
    }

    public void removeGameTimer(InteractionGameTimer gameTimer) {
        this.gameTimers.remove(gameTimer.getId());
    }

    public THashMap<Integer, InteractionGameTimer> getGameTimers() {
        return this.gameTimers;
    }

    public InteractionFreezeExitTile getFreezeExitTile() {
        return this.freezeExitTile.values().stream().findFirst().orElse(null);
    }

    public InteractionFreezeExitTile getRandomFreezeExitTile() {
        synchronized (this.freezeExitTile) {
            return (InteractionFreezeExitTile) this.freezeExitTile.values().toArray()[Emulator.getRandom().nextInt(this.freezeExitTile.size())];
        }
    }

    public void addFreezeExitTile(InteractionFreezeExitTile freezeExitTile) {
        this.freezeExitTile.put(freezeExitTile.getId(), freezeExitTile);
    }

    public THashMap<Integer, InteractionFreezeExitTile> getFreezeExitTiles() {
        return this.freezeExitTile;
    }

    public void removeFreezeExitTile(InteractionFreezeExitTile freezeExitTile) {
        this.freezeExitTile.remove(freezeExitTile.getId());
    }

    public boolean hasFreezeExitTile() {
        return !this.freezeExitTile.isEmpty();
    }

    public void addUndefined(HabboItem item) {
        synchronized (this.undefined) {
            this.undefined.put(item.getId(), item);
        }
    }

    public void removeUndefined(HabboItem item) {
        synchronized (this.undefined) {
            this.undefined.remove(item.getId());
        }
    }

    public THashSet<HabboItem> getItemsOfType(Class<? extends HabboItem> type) {
        THashSet<HabboItem> items = new THashSet<>();
        synchronized (this.undefined) {
            for (HabboItem item : this.undefined.values()) {
                if (item.getClass() == type)
                    items.add(item);
            }
        }

        return items;
    }

    public HabboItem getLowestItemsOfType(Class<? extends HabboItem> type) {
        HabboItem i = null;
        synchronized (this.undefined) {
            for (HabboItem item : this.undefined.values()) {
                if (i == null || item.getZ() < i.getZ()) {
                    if (item.getClass().isAssignableFrom(type)) {
                        i = item;
                    }
                }
            }
        }

        return i;
    }

    public THashSet<ICycleable> getCycleTasks() {
        return this.cycleTasks;
    }

    public void addCycleTask(ICycleable task) {
        this.cycleTasks.add(task);
    }

    public void removeCycleTask(ICycleable task) {
        this.cycleTasks.remove(task);
    }

    public synchronized void dispose() {
        this.banzaiTeleporters.clear();
        this.nests.clear();
        this.petDrinks.clear();
        this.petFoods.clear();
        this.rollers.clear();

        this.wiredTriggers.clear();
        this.wiredEffects.clear();
        this.wiredConditions.clear();

        this.gameScoreboards.clear();
        this.gameGates.clear();
        this.gameTimers.clear();

        this.freezeExitTile.clear();
        this.undefined.clear();
        this.cycleTasks.clear();
    }

    public Rectangle tentAt(RoomTile location) {
        for (HabboItem item : this.getItemsOfType(InteractionTent.class)) {
            Rectangle rectangle = RoomLayout.getRectangle(item.getX(), item.getY(), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());
            if (RoomLayout.tileInSquare(rectangle, location)) {
                return rectangle;
            }
        }

        return null;
    }


}
