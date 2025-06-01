package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.ICycleable;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUserRotation;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.wired.WiredSaveException;
import gnu.trove.set.hash.THashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

public class WiredEffectMoveRotateUser extends InteractionWiredEffect implements ICycleable {

    private static final Logger LOGGER = LoggerFactory.getLogger(WiredEffectMoveRotateUser.class);

    public static final WiredEffectType type = WiredEffectType.MOVE_ROTATE_USER;
    private int direction;
    private int rotation;

    public WiredEffectMoveRotateUser(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectMoveRotateUser(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {

        RoomTile tile = room.getLayout().getTileInFront(roomUnit.getCurrentLocation(),
                                                        roomUnit.getBodyRotation().getValue());

        roomUnit.setGoalLocation(tile);
        roomUnit.setBodyRotation(this.getMovementDirection());
        return true;
    }

    @Override
    public String getWiredData() {
        return WiredHandler.getGsonBuilder().create().toJson(new JsonData(
                this.direction,
                this.rotation,
                this.getDelay())
        );
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        String wiredData = set.getString("wired_data");

        if (wiredData.startsWith("{")) {
            JsonData data = WiredHandler.getGsonBuilder().create().fromJson(wiredData, JsonData.class);
            this.setDelay(data.delay);
            this.direction = data.direction;
            this.rotation = data.rotation;
        }
    }

    @Override
    public void onPickUp() {
        this.direction = 0;
        this.rotation = 0;
        this.setDelay(0);
    }

    @Override
    public WiredEffectType getType() {
        return type;
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room) {
        THashSet<HabboItem> items = new THashSet<>();

        message.appendBoolean(false);
        message.appendInt(WiredHandler.MAXIMUM_FURNI_SELECTION);
        message.appendInt(0);
        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString("");
        message.appendInt(2);
        message.appendInt(this.direction);
        message.appendInt(this.rotation);
        message.appendInt(0);
        message.appendInt(this.getType().code);
        message.appendInt(this.getDelay());
        message.appendInt(0);
    }

    @Override
    public boolean saveData(WiredSettings settings, GameClient gameClient) throws WiredSaveException {
        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());

        if (room == null)
            return false;

        if(settings.getIntParams().length < 2) throw new WiredSaveException("invalid data");

        this.direction = settings.getIntParams()[0];
        this.rotation = settings.getIntParams()[1];
        this.setDelay(settings.getDelay());

        return true;
    }


    /**
     * Returns a new rotation for an item based on the wired options
     *
     * @param item HabboItem
     * @return new rotation
     */
    private int getNewRotation(HabboItem item) {
        int rotationToAdd = 0;

        if(item.getMaximumRotations() == 2) {
            return item.getRotation() == 0 ? 4 : 0;
        }
        else if(item.getMaximumRotations() == 1) {
            return item.getRotation();
        }
        else if(item.getMaximumRotations() > 4) {
            if (this.rotation == 1) {
                return item.getRotation() == item.getMaximumRotations() - 1 ? 0 : item.getRotation() + 1;
            } else if (this.rotation == 2) {
                return item.getRotation() > 0 ? item.getRotation() - 1 : item.getMaximumRotations() - 1;
            } else if (this.rotation == 3) { //Random rotation
                THashSet<Integer> possibleRotations = new THashSet<>();
                for (int i = 0; i < item.getMaximumRotations(); i++)
                {
                    possibleRotations.add(i);
                }

                possibleRotations.remove(item.getRotation());

                if(!possibleRotations.isEmpty()) {
                    int index = Emulator.getRandom().nextInt(possibleRotations.size());
                    Iterator<Integer> iter = possibleRotations.iterator();
                    for (int i = 0; i < index; i++) {
                        iter.next();
                    }
                    return iter.next();
                }
            }
        }
        else {
            if (this.rotation == 1) {
                return (item.getRotation() + 2) % 8;
            } else if (this.rotation == 2) {
                int rot = (item.getRotation() - 2) % 8;
                if(rot < 0) {
                    rot += 8;
                }
                return rot;
            } else if (this.rotation == 3) { //Random rotation
                THashSet<Integer> possibleRotations = new THashSet<>();
                for (int i = 0; i < item.getMaximumRotations(); i++)
                {
                    possibleRotations.add(i * 2);
                }

                possibleRotations.remove(item.getRotation());

                if(!possibleRotations.isEmpty()) {
                    int index = Emulator.getRandom().nextInt(possibleRotations.size());
                    Iterator<Integer> iter = possibleRotations.iterator();
                    for (int i = 0; i < index; i++) {
                        iter.next();
                    }
                    return iter.next();
                }
            }
        }

        return item.getRotation();
    }

    /**
     * Returns the direction of movement based on the wired settings
     *
     * @return direction
     */
    private RoomUserRotation getMovementDirection() {
        RoomUserRotation movemementDirection = RoomUserRotation.NORTH;
        if (this.direction == 1) {
            movemementDirection = RoomUserRotation.values()[Emulator.getRandom().nextInt(RoomUserRotation.values().length / 2) * 2];
        } else if (this.direction == 2) {
            if (Emulator.getRandom().nextInt(2) == 1) {
                movemementDirection = RoomUserRotation.EAST;
            } else {
                movemementDirection = RoomUserRotation.WEST;
            }
        } else if (this.direction == 3) {
            if (Emulator.getRandom().nextInt(2) != 1) {
                movemementDirection = RoomUserRotation.SOUTH;
            }
        } else if (this.direction == 4) {
            movemementDirection = RoomUserRotation.SOUTH;
        } else if (this.direction == 5) {
            movemementDirection = RoomUserRotation.EAST;
        } else if (this.direction == 7) {
            movemementDirection = RoomUserRotation.WEST;
        }
        return movemementDirection;
    }

    @Override
    public void cycle(Room room) {}

    static class JsonData {
        int direction;
        int rotation;
        int delay;

        public JsonData(int direction, int rotation, int delay) {
            this.direction = direction;
            this.rotation = rotation;
            this.delay = delay;
        }
    }
}
