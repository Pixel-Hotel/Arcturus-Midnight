package com.eu.habbo.habbohotel.items.interactions.wired;

import com.eu.habbo.habbohotel.users.HabboItem;
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
}
