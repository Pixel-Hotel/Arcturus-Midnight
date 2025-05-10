package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserHandItemComposer;

public class RoomUserDropHandItemEvent extends MessageHandler {
    @Override
    public void handle() throws Exception {
        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if(room.isHanditemDisabled()) return;

        this.client.getHabbo().getRoomUnit().setHandItem(0);
        room.unIdle(this.client.getHabbo());
        room.sendComposer(new RoomUserHandItemComposer(this.client.getHabbo().getRoomUnit()).compose());

    }
}
