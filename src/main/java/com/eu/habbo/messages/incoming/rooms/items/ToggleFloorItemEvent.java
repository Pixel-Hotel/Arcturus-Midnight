package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionDice;
import com.eu.habbo.habbohotel.items.interactions.pets.InteractionMonsterPlantSeed;
import com.eu.habbo.habbohotel.pets.MonsterplantPet;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.users.cache.actions.ToggleFloorItemAction;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.items.RemoveFloorItemComposer;
import com.eu.habbo.messages.outgoing.rooms.pets.PetPackageComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserStatusComposer;
import com.eu.habbo.plugin.Event;
import com.eu.habbo.plugin.events.furniture.FurnitureToggleEvent;
import com.eu.habbo.threading.runnables.QueryDeleteHabboItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;

public class ToggleFloorItemEvent extends MessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ToggleFloorItemEvent.class);

    private static final HashSet<String> PET_BOXES = new HashSet<>(Arrays.asList("val11_present", "gnome_box", "leprechaun_box", "velociraptor_egg", "pterosaur_egg", "petbox_epic"));

    @Override
    public void handle() throws Exception {
        try {
            Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

            if (room == null)
                return;

            int itemId = this.packet.readInt();
            int state = this.packet.readInt();

            HabboItem item = room.getHabboItem(itemId);

            if (item == null || item instanceof InteractionDice)
                return;

            Event furnitureToggleEvent = new FurnitureToggleEvent(item, this.client.getHabbo(), state);
            Emulator.getPluginManager().fireEvent(furnitureToggleEvent);

            if (furnitureToggleEvent.isCancelled())
                return;

            // Do not move to onClick(). Wired could trigger it.
            if (item instanceof InteractionMonsterPlantSeed) {
                Emulator.getThreading().run(new QueryDeleteHabboItem(item.getId()));

                boolean isRare = item.getBaseItem().getName().contains("rare");
                int rarity = 0;

                if (item.getExtradata().isEmpty() || Integer.parseInt(item.getExtradata()) - 1 < 0) {
                    rarity = isRare ? InteractionMonsterPlantSeed.randomGoldenRarityLevel() : InteractionMonsterPlantSeed.randomRarityLevel();
                } else {
                    try {
                        rarity = Integer.parseInt(item.getExtradata()) - 1;
                    } catch (Exception ignored) {}
                }
                MonsterplantPet pet = Emulator.getGameEnvironment().getPetManager().createMonsterplant(room, this.client.getHabbo(), isRare, room.getLayout().getTile(item.getX(), item.getY()), rarity);
                room.sendComposer(new RemoveFloorItemComposer(item, true).compose());
                room.removeHabboItem(item);
                room.updateTile(room.getLayout().getTile(item.getX(), item.getY()));
                room.placePet(pet, item.getX(), item.getY(), item.getZ(), item.getRotation());
                pet.cycle();
                room.sendComposer(new RoomUserStatusComposer(pet.getRoomUnit()).compose());
                return;
            }

            if (PET_BOXES.contains(item.getBaseItem().getName()) && room.getCurrentPets().size() < Room.MAXIMUM_PETS) {
                this.client.sendResponse(new PetPackageComposer(item));
                return;
            }
            String oldState = item.getExtradata();
            item.onClick(this.client, room, new Object[]{state});
            this.client.getHabbo().getUndoRedoManager().saveAction(new ToggleFloorItemAction(this.client.getHabbo(), item, oldState, item.getExtradata()));
        } catch (Exception e) {
            LOGGER.error("Caught exception", e);
        }
    }
}
