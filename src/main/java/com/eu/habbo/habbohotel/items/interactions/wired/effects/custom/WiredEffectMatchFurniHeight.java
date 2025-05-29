package com.eu.habbo.habbohotel.items.interactions.wired.effects.custom;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.items.interactions.wired.interfaces.InteractionWiredMatchFurniSettings;
import com.eu.habbo.habbohotel.rooms.FurnitureMovementError;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredMatchFurniSetting;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.wired.WiredSaveException;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemUpdateComposer;
import gnu.trove.set.hash.THashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WiredEffectMatchFurniHeight extends InteractionWiredEffect implements InteractionWiredMatchFurniSettings {
    private static final Logger LOGGER = LoggerFactory.getLogger(WiredEffectMatchFurniHeight.class);

    private static final WiredEffectType type = WiredEffectType.MATCH_SSHOT;
    public boolean checkForWiredResetPermission = true;
    private final THashSet<WiredMatchFurniSetting> settings;
    private boolean state = false;
    private boolean direction = false;
    private boolean position = false;
    private boolean height = false;
    private final boolean animation = true;

    public WiredEffectMatchFurniHeight(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
        this.settings = new THashSet<>(0);
    }

    public WiredEffectMatchFurniHeight(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
        this.settings = new THashSet<>(0);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {

        for (WiredMatchFurniSetting setting : this.settings) {
            HabboItem item = room.getHabboItem(setting.item_id);
            if (item == null) continue;

            RoomTile oldLocation = room.getLayout().getTile(item.getX(), item.getY());
            boolean needUpdate = false;
            if(this.state) needUpdate = this.handleState(setting, item);
            if(this.direction || this.position) needUpdate = this.handlePositionAndRotation(setting, item, room) || needUpdate;
            if(this.height) needUpdate = this.handleHeight(setting, item) || needUpdate;


            if(!animation){
                updateItem(item, room, needUpdate);
                room.sendComposer(new FloorItemUpdateComposer(item).compose());
            }
            else {
                RoomTile newLocation = room.getLayout().getTile(item.getX(), item.getY());
                if(this.position) {
                    room.sendComposer(new FloorItemOnRollerComposer(item, null, oldLocation, item.getZ(), newLocation, height ? setting.z : item.getZ(), 0, room).compose());
                    Emulator.getThreading().run(item);
                }
                if(this.direction)
                    Emulator.getThreading().run(() -> room.sendComposer(new FloorItemUpdateComposer(item).compose()), 1100);
            }

        }

        return true;
    }

    private void updateItem(HabboItem item, Room room, boolean needUpdate){
        item.needsUpdate(needUpdate);
        room.updateItem(item);
        Emulator.getThreading().run(item);
    }

    private boolean handlePositionAndRotation(WiredMatchFurniSetting setting, HabboItem item, Room room){
        if(item == null) return false;

        RoomTile goalTile = room.getLayout().getTile((short) setting.x, (short) setting.y);
        if(room.furnitureFitsAt(goalTile, item, setting.rotation, true) != FurnitureMovementError.NONE) return false;

        THashSet<RoomTile> needUpdateTiles = room.getLayout().getTilesAt(goalTile, item.getBaseItem().getWidth(), item.getBaseItem().getLength(), setting.rotation);

        RoomTile currentTile = room.getLayout().getTile(item.getX(), item.getY());
        needUpdateTiles.addAll(room.getLayout().getTilesAt(currentTile, item.getX(), item.getY(), item.getRotation()));

        if(this.direction){
            item.setRotation(setting.rotation);
        }
        if(this.position) {
            item.setX((short) setting.x);
            item.setY((short) setting.y);
        }

        room.updateTiles(needUpdateTiles);

        return this.direction || this.position;
    }

    private boolean handleState(WiredMatchFurniSetting setting, HabboItem item){
        if(item == null || !item.allowWiredResetState()) return false;
        item.setExtradata(setting.state);
        return true;
    }

    private boolean handleHeight(WiredMatchFurniSetting setting, HabboItem item){
        if(item == null) return false;
        item.setZ(setting.z);
        return true;
    }

    @Override
    public String getWiredData() {
        this.refresh();
        return WiredHandler.getGsonBuilder().create().toJson(new JsonData(this.state, this.direction, this.position, this.height, new ArrayList<>(this.settings), this.getDelay()));
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        String wiredData = set.getString("wired_data");

        if(wiredData.startsWith("{")) {
            JsonData data = WiredHandler.getGsonBuilder().create().fromJson(wiredData, JsonData.class);
            this.setDelay(data.delay);
            this.state = data.state;
            this.direction = data.direction;
            this.position = data.position;
            this.height = data.height;
            this.settings.clear();
            this.settings.addAll(data.items);
        }
    }

    @Override
    public void onPickUp() {
        this.settings.clear();
        this.state = false;
        this.direction = false;
        this.position = false;
        this.height = false;
        this.setDelay(0);
    }

    @Override
    public WiredEffectType getType() {
        return type;
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room) {
        this.refresh();

        message.appendBoolean(false);
        message.appendInt(WiredHandler.MAXIMUM_FURNI_SELECTION);
        message.appendInt(this.settings.size());

        for (WiredMatchFurniSetting item : this.settings)
            message.appendInt(item.item_id);

        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString("");
        message.appendInt(4);
        message.appendInt(this.state ? 1 : 0);
        message.appendInt(this.direction ? 1 : 0);
        message.appendInt(this.position ? 1 : 0);
        message.appendInt(this.height ? 1 : 0);
        message.appendInt(0);
        message.appendInt(this.getType().code);
        message.appendInt(this.getDelay());
        message.appendInt(0);
    }

    @Override
    public boolean saveData(WiredSettings settings, GameClient gameClient) throws WiredSaveException {
        if(settings.getIntParams().length < 4) throw new WiredSaveException("Invalid data");

        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());

        if (room == null)
            throw new WiredSaveException("Trying to save wired in unloaded room");

        int itemsCount = settings.getFurniIds().length;

        if(itemsCount > Emulator.getConfig().getInt("hotel.wired.furni.selection.count")) {
            throw new WiredSaveException("Too many furni selected");
        }

        List<WiredMatchFurniSetting> newSettings = new ArrayList<>();

        for (int i = 0; i < itemsCount; i++) {
            int itemId = settings.getFurniIds()[i];
            HabboItem it = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(itemId);

            if(it == null)
                throw new WiredSaveException(String.format("Item %s not found", itemId));

            newSettings.add(new WiredMatchFurniSetting(it.getId(), this.checkForWiredResetPermission && it.allowWiredResetState() ? it.getExtradata() : " ", it.getRotation(), it.getX(), it.getY(), it.getZ()));
        }

        int delay = settings.getDelay();

        if(delay > Emulator.getConfig().getInt("hotel.wired.max_delay", 20))
            throw new WiredSaveException("Delay too long");

        this.state = settings.getIntParams()[0] == 1;
        this.direction = settings.getIntParams()[1] == 1;
        this.position = settings.getIntParams()[2] == 1;
        this.height = settings.getIntParams()[3] == 1;
        this.settings.clear();
        this.settings.addAll(newSettings);
        this.setDelay(delay);

        return true;
    }

    private void refresh() {
        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());

        if (room != null && room.isLoaded()) {
            THashSet<WiredMatchFurniSetting> remove = new THashSet<>();

            for (WiredMatchFurniSetting setting : this.settings) {
                HabboItem item = room.getHabboItem(setting.item_id);
                if (item == null) {
                    remove.add(setting);
                }
            }

            for (WiredMatchFurniSetting setting : remove) {
                this.settings.remove(setting);
            }
        }
    }

    @Override
    public THashSet<WiredMatchFurniSetting> getMatchFurniSettings() {
        return this.settings;
    }

    @Override
    public boolean shouldMatchState() {
        return this.state;
    }

    @Override
    public boolean shouldMatchRotation() {
        return this.direction;
    }

    @Override
    public boolean shouldMatchPosition() {
        return this.position;
    }

    static class JsonData {
        boolean state;
        boolean direction;
        boolean position;
        boolean height;
        List<WiredMatchFurniSetting> items;
        int delay;

        public JsonData(boolean state, boolean direction, boolean position, boolean height, List<WiredMatchFurniSetting> items, int delay) {
            this.state = state;
            this.direction = direction;
            this.position = position;
            this.height = height;
            this.items = items;
            this.delay = delay;
        }
    }
}
