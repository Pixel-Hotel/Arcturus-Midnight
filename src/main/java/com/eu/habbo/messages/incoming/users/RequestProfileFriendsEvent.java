package com.eu.habbo.messages.incoming.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.messenger.Messenger;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.users.ProfileFriendsComposer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestProfileFriendsEvent extends MessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestProfileFriendsEvent.class);

    @Override
    public void handle() throws Exception {
        int userId = this.packet.readInt();
        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(userId);
        LOGGER.info("Requested friends for user: {}", userId);
        if (habbo != null) {
            handleWired(habbo.getRoomUnit());
            this.client.sendResponse(new ProfileFriendsComposer(habbo));
        } else{
            handleWired(this.getBot(userId));
            this.client.sendResponse(new ProfileFriendsComposer(Messenger.getFriends(userId), userId));
        }
    }

    private RoomUnit getBot(int id){
        for(Room room : Emulator.getGameEnvironment().getRoomManager().getActiveRooms()){
            Bot bot = room.getBot(id);
            if(bot != null) return bot.getRoomUnit();
        }
        return null;
    }

    private void handleWired(RoomUnit roomUnit){
        LOGGER.debug(roomUnit == null ? "RoomUnit is null." : "RoomUnit is not null.");
        if(roomUnit == null) return;
        Emulator.getThreading().run(() -> WiredHandler.handle(WiredTriggerType.CLICK_ON_AVATAR, roomUnit, roomUnit.getRoom(), new Object[]{}));
    }
}
