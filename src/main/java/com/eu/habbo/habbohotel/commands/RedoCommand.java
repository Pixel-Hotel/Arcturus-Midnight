package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.cache.UndoRedoManager;

public class RedoCommand extends Command{
    public RedoCommand() {
        super("cmd_undo_redo", new String[]{"redo"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        int steps = 1;

        if(params.length > 1){
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
        if(undoRedoManager.canNotRedo(steps)){
            gameClient.getHabbo().whisper("Keine weiteren Redo-Schritte vorhanden, bzw. nicht so viele. Maximal: " + undoRedoManager.getRedoStackSize());
            return true;
        }
        return undoRedoManager.redo(steps);
    }
}
