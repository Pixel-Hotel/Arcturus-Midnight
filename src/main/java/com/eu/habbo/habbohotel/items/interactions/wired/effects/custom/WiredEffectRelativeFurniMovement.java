package com.eu.habbo.habbohotel.items.interactions.wired.effects.custom;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.wired.WiredSaveException;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class WiredEffectRelativeFurniMovement extends InteractionWiredEffect {

    private static final WiredEffectType type = WiredEffectType.Relative_FURNI_MOVE;

    private final THashSet<HabboItem> items = new THashSet<>();
    private boolean directionX = false;
    private short distanceX = 0;
    private boolean directionY = false;
    private short distanceY = 0;

    public WiredEffectRelativeFurniMovement(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectRelativeFurniMovement(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean saveData(WiredSettings settings, GameClient gameClient) throws WiredSaveException {
        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());

        if (room == null)
            return false;

        if(settings.getIntParams().length < 4) throw new WiredSaveException("invalid data");

        this.directionX = settings.getIntParams()[0] == 1;
        this.distanceX = (short) settings.getIntParams()[1];
        this.directionY = settings.getIntParams()[2] == 1;
        this.distanceY = (short) settings.getIntParams()[3];

        int count = settings.getFurniIds().length;
        if (count > Emulator.getConfig().getInt("hotel.wired.furni.selection.count", WiredHandler.MAXIMUM_FURNI_SELECTION)) return false;

        this.items.clear();
        for (int i = 0; i < count; i++) {
            this.items.add(room.getHabboItem(settings.getFurniIds()[i]));
        }

        this.setDelay(settings.getDelay());

        return true;
    }

    @Override
    public WiredEffectType getType() {
        return type;
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {

        for(HabboItem item : this.items){
            short positionX = (short) (item.getX() + (directionX ? -distanceX : distanceX));
            short positionY = (short) (item.getY() + (directionY ? -distanceY : distanceY));

            RoomTile tile = room.getLayout().getTile(positionX, positionY);
            room.moveFurniTo(item, tile, item.getRotation(), null, true, true);
        }
        return false;
    }

    @Override
    public String getWiredData() {
        THashSet<HabboItem> itemsToRemove = new THashSet<>(this.items.size());

        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());

        for (HabboItem item : this.items) {
            if (item.getRoomId() != this.getRoomId() || (room != null && room.getHabboItem(item.getId()) == null))
                itemsToRemove.add(item);
        }

        for (HabboItem item : itemsToRemove) {
            this.items.remove(item);
        }

        return WiredHandler.getGsonBuilder().create().toJson(new JsonData(
                this.directionX,
                this.distanceX,
                this.directionY,
                this.distanceY,
                this.getDelay(),
                this.items.stream().map(HabboItem::getId).collect(Collectors.toList())
        ));
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room) {
        THashSet<HabboItem> items = new THashSet<>();

        for (HabboItem item : this.items) {
            if (item.getRoomId() != this.getRoomId() || Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(item.getId()) == null)
                items.add(item);
        }

        for (HabboItem item : items) {
            this.items.remove(item);
        }

        message.appendBoolean(false);
        message.appendInt(WiredHandler.MAXIMUM_FURNI_SELECTION);
        message.appendInt(this.items.size());
        for (HabboItem item : this.items)
            message.appendInt(item.getId());
        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString("");
        message.appendInt(4);
        message.appendInt(this.directionX ? 1 : 0);
        message.appendInt(this.distanceX);
        message.appendInt(this.directionY ? 1: 0);
        message.appendInt(this.distanceY);
        message.appendInt(0);
        message.appendInt(type.code);
        message.appendInt(this.getDelay());
        message.appendInt(0);
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        this.items.clear();
        String wiredData = set.getString("wired_data");

        if (wiredData.startsWith("{")) {
            JsonData data = WiredHandler.getGsonBuilder().create().fromJson(wiredData, JsonData.class);
            this.setDelay(data.delay);
            this.directionX = data.directionX;
            this.distanceX = data.distanceX;
            this.directionY = data.directionY;
            this.distanceY = data.distanceY;
            for (Integer id: data.itemIds) {
                HabboItem item = room.getHabboItem(id);
                if (item != null) {
                    this.items.add(item);
                }
            }
        }
    }

    @Override
    public void onPickUp() {
        items.clear();
        boolean directionX = false;
        short distanceX = 0;
        boolean directionY = false;
        short distanceY = 0;
    }

    static class JsonData {
        boolean directionX;
        short distanceX;
        boolean directionY;
        short distanceY;
        int delay;
        List<Integer> itemIds;

        public JsonData(boolean directionX, short distanceX, boolean directionY, short distanceY, int delay, List<Integer> itemIds ) {
            this.directionX = directionX;
            this.distanceX = distanceX;
            this.directionY = directionY;
            this.distanceY = distanceY;
            this.delay = delay;
            this.itemIds = itemIds;
        }
    }
}
