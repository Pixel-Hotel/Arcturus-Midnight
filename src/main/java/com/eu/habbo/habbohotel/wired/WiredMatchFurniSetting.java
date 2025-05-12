package com.eu.habbo.habbohotel.wired;

import com.eu.habbo.habbohotel.users.HabboItem;

public class WiredMatchFurniSetting {
    public final int item_id;
    public final String state;
    public final int rotation;
    public final int x;
    public final int y;
    public final double z;

    public WiredMatchFurniSetting(int itemId, String state, int rotation, int x, int y, double z) {
        this.item_id = itemId;
        this.state = state.replace("\t\t\t", " ");
        this.rotation = rotation;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public WiredMatchFurniSetting(HabboItem item) {
        this(item.getId(), item.getExtradata(), item.getRotation(), item.getX(), item.getY(), item.getZ());
    }
    public WiredMatchFurniSetting(String[] stuff) {
        this(Integer.parseInt(stuff[0]), stuff[1], Integer.parseInt(stuff[2]), Integer.parseInt(stuff[3]), Integer.parseInt(stuff[4]), stuff.length > 5 ? Double.parseDouble(stuff[5]) : 0.0);
    }


    @Override
    public String toString() {
        return this.toString(true);
    }

    public String toString(boolean includeState) {
        return this.item_id + "-" +
                (this.state.isEmpty() || !includeState ? " " : this.state) + "-" +
                this.rotation + "-" +
                this.x + "-" + this.y + "-" + this.z;
    }

}
