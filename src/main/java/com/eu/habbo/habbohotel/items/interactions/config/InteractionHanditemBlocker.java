package com.eu.habbo.habbohotel.items.interactions.config;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import com.eu.habbo.habbohotel.rooms.Room;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionHanditemBlocker extends InteractionDefault {
    /*
    Handitem-Blocker:
    Durch das Aktivieren dieses Items können keine Handitems mehr zwischen Personen herumgereicht bzw. abgestellt werden.
    Jetzt erhält man Handitems nur noch im Kühlschrank oder durch Wireds.
    */

    public InteractionHanditemBlocker(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionHanditemBlocker(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    public boolean isDisabled(){
        return this.getExtradata().equals("1");
    }

    @Override
    public void onPickUp(Room room) {
        setExtradata("0");
    }
}
