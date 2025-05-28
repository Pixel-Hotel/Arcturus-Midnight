package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.cache.UndoRedoManager;

public class UndoCommand extends Command{
    public UndoCommand() {
        super("cmd_shutdown", new String[]{"undo"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        int steps = 1;

        if(params.length > 0){
            try{
                steps = Integer.parseInt(params[1]);
                if(steps > UndoRedoManager.MAX_STEPS) steps = UndoRedoManager.MAX_STEPS;
                else if(steps < 1) steps = 1;
            }
            catch (NumberFormatException e){
                gameClient.getHabbo().whisper("Parameter muss eine Zahl sein. (1-10)");
                return true;
            }
        }

        UndoRedoManager undoRedoManager = gameClient.getHabbo().getUndoRedoManager();
        if(!undoRedoManager.canRedo(steps)){
            gameClient.getHabbo().whisper("Keine weiteren Undo-Schritte vorhanden, bzw. nicht so viele. Maximal: " + undoRedoManager.getUndoStackSize());
            return true;
        }
        return undoRedoManager.undo(steps);
    }
}
