package com.eu.habbo.habbohotel.items.interactions.wired;

import com.eu.habbo.habbohotel.items.interactions.InteractionWired;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.set.hash.THashSet;


public class WiredSettings {
    private int[] intParams;
    private String stringParam;
    private int[] itemIds;
    private int stuffTypeSelectionCode;
    private int delay;

    public WiredSettings(int[] intParams, String stringParam, int[] itemIds, int stuffTypeSelectionCode, int delay)
    {
        this.itemIds = itemIds;
        this.intParams = intParams;
        this.stringParam = stringParam;
        this.stuffTypeSelectionCode = stuffTypeSelectionCode;
        this.delay = delay;
    }

    public WiredSettings(int[] intParams, String stringParam, int[] itemIds, int stuffTypeSelectionCode)
    {
        this(intParams, stringParam, itemIds, stuffTypeSelectionCode, 0);
    }

    public int getStuffTypeSelectionCode() {
        return stuffTypeSelectionCode;
    }

    public void setStuffTypeSelectionCode(int stuffTypeSelectionCode) {
        this.stuffTypeSelectionCode = stuffTypeSelectionCode;
    }

    public int[] getFurniIds() {
        return itemIds;
    }

    public void setItemIds(int[] itemIds) {
        this.itemIds = itemIds;
    }

    public String getStringParam() {
        return stringParam;
    }

    public void setStringParam(String stringParam) {
        this.stringParam = stringParam;
    }

    public int[] getIntParams() {
        return intParams;
    }

    public void setIntParams(int[] intParams) {
        this.intParams = intParams;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    @SafeVarargs
    public static void clearItemByType(THashSet<HabboItem> items, Class<? extends HabboItem>... allowedTypes) {
        if(items == null) {
            new THashSet<>();
            return;
        } else if(allowedTypes == null || allowedTypes.length == 0) return;
        items.removeIf(item -> {
            if (item == null) return true; // Entfernen, wenn Item nicht existiert
            for (Class<? extends HabboItem> allowed : allowedTypes) {
                if (allowed.isInstance(item)) {
                    return false; // Behalten
                }
            }
            return true; // Entfernen, wenn kein Typ gepasst hat
        });
    }

    public static void serializeWiredData(ServerMessage message, InteractionWired wired, int[] intParams, int[] itemIds, String stringParam){
        if(stringParam == null) stringParam = "";

        message.appendBoolean(false); // I think this is for: has advanced settings

        message.appendInt(WiredHandler.MAXIMUM_FURNI_SELECTION);

        serializeWiredDataIntParams(message, itemIds); // handle item selection

        message.appendInt(wired.getBaseItem().getSpriteId());
        message.appendInt(wired.getId());

        message.appendString(stringParam);

        serializeWiredDataIntParams(message, intParams);

        message.appendInt(0); // I don't know what this is for.
        message.appendInt(getWiredType(wired));

        if (wired instanceof InteractionWiredEffect effect) {
            message.appendInt(effect.getDelay());
        } else {
            message.appendInt(0);
        }

        message.appendInt(0); // I don't know what this is for.
    }

    private static void serializeWiredDataIntParams(ServerMessage message, int[] params){
        if(params == null) params = new int[0];

        message.appendInt(params.length);
        for(int i : params){
            message.appendInt(i);
        }
    }

    private static int getWiredType(InteractionWired wired){
        if(wired instanceof InteractionWiredEffect effect){
            return effect.getType().code;
        }else if (wired instanceof InteractionWiredTrigger trigger){
            return trigger.getType().code;
        } else if(wired instanceof InteractionWiredCondition condition){
            return condition.getType().code;
        } else return -1;
    }
}
