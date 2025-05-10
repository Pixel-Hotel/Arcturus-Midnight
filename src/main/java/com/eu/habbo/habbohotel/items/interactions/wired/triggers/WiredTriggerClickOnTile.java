package com.eu.habbo.habbohotel.items.interactions.wired.triggers;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.items.interactions.invisible.InteractionInvisibleClickItem;
import com.eu.habbo.habbohotel.items.interactions.invisible.InteractionInvisibleItem;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class WiredTriggerClickOnTile extends InteractionWiredTrigger {

    private static final WiredTriggerType type = WiredTriggerType.CLICK_ON_TILE;
    private final THashSet<HabboItem> items = new THashSet<>();

    public WiredTriggerClickOnTile(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredTriggerClickOnTile(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        this.items.clear();
        String wiredData = set.getString("wired_data");

        if (wiredData.startsWith("{")) {
            WiredTriggerHabboWalkOnFurni.JsonData data = WiredHandler.getGsonBuilder().create().fromJson(wiredData, WiredTriggerHabboWalkOnFurni.JsonData.class);
            for (Integer id: data.itemIds) {
                HabboItem item = room.getHabboItem(id);
                if (item instanceof InteractionInvisibleItem) {
                    this.items.add(item);
                }
            }
        } else {
            if (wiredData.split(":").length >= 3) {
                super.setDelay(Integer.parseInt(wiredData.split(":")[0]));

                if (!wiredData.split(":")[2].equals("\t")) {
                    for (String s : wiredData.split(":")[2].split(";")) {
                        if (s.isEmpty())
                            continue;

                        try {
                            HabboItem item = room.getHabboItem(Integer.parseInt(s));

                            if (item instanceof InteractionInvisibleItem)
                                this.items.add(item);
                        } catch (Exception ignored) {}
                    }
                }
            }
        }
    }

    @Override
    public void onPickUp() {
        items.clear();
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room) {

        clearItems();

        message.appendBoolean(false);
        message.appendInt(WiredHandler.MAXIMUM_ITEM_SELECTION);
        message.appendInt(this.items.size());
        for (HabboItem item : this.items) {
            message.appendInt(item.getId());
        }
        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString("");
        message.appendInt(0);
        message.appendInt(0);
        message.appendInt(this.getType().code);
        message.appendInt(0);
        message.appendInt(0);
    }

    @Override
    public String getWiredData() {
        return WiredHandler.getGsonBuilder().create().toJson(new JsonData(
                this.items.stream().map(HabboItem::getId).collect(Collectors.toList())
        ));
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if (stuff.length >= 1) {
            if (stuff[0] instanceof InteractionInvisibleItem item) {
                return this.items.contains(item);
            }
        }
        return false;
    }

    @Override
    public boolean saveData(WiredSettings settings) {
        items.clear();

        for(int i = 0; i < settings.getItemIds().length; i++){
            HabboItem item = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(settings.getItemIds()[i]);
            if(item instanceof InteractionInvisibleClickItem){
                this.items.add(item);
            }
        }
        return false;
    }

    @Override
    public WiredTriggerType getType() {
        return type;
    }

    @Override
    public boolean isTriggeredByRoomUnit() {
        return true;
    }

    private void clearItems(){
        THashSet<HabboItem> items  = WiredSettings.clearItemByType(this.items, InteractionInvisibleClickItem.class);
        this.items.clear();
        this.items.addAll(items);
    }

    static class JsonData {
        List<Integer> itemIds;

        public JsonData(List<Integer> itemIds) {
            this.itemIds = itemIds;
        }
    }
}
