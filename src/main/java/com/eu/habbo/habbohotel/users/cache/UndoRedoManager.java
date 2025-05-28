package com.eu.habbo.habbohotel.users.cache;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.cache.actions.ItemAction;
import com.eu.habbo.habbohotel.users.cache.actions.ItemActionKey;

import java.util.*;

public class UndoRedoManager {

    public static final int MAX_STEPS = Emulator.getConfig().getInt("hotel.undo.redo.max_steps", 10);
    private final Deque<ItemAction> undoStack = new ArrayDeque<>();
    private final Deque<ItemAction> redoStack = new ArrayDeque<>();

    public int getUndoStackSize() { return undoStack.size(); }
    public int getRedoStackSize() { return redoStack.size(); }

    public boolean canNotUndo(int steps){
        return undoStack.isEmpty() || undoStack.size() < steps;
    }

    public boolean canNotRedo(int steps){
        return redoStack.isEmpty() || redoStack.size() < steps;
    }

    public void saveAction(ItemAction command){
        pushLimited(undoStack, command);
        redoStack.clear();
    }

    public boolean undo(int steps){
        if(canNotUndo(steps)) return false;
        return handle(steps,true);
    }

    public boolean redo(int steps){
        if(canNotRedo(steps)) return false;
        return handle(steps,false);
    }

    private boolean handle(int steps, boolean undo){
        Map<ItemActionKey, ItemAction> uniqueActions  = new LinkedHashMap<>();

        Deque<ItemAction> fromStack = undo ? undoStack : redoStack;
        Deque<ItemAction> toStack = undo ? redoStack: undoStack;

        for(int i = 0; i < steps; i++) {
            ItemAction command = fromStack.pollLast();
            if(command == null) break;
            ItemActionKey key = new ItemActionKey(command.getItem().getId(), command.getClass());
            uniqueActions.put(key, command);
            pushLimited(toStack, command);
        }

        boolean success = true;
        for(ItemAction action : uniqueActions.values()){
            success &= undo ? action.undo() : action.redo();
        }
        return success;
    }

    private void pushLimited(Deque<ItemAction> stack, ItemAction command){
        if(stack.size() >= MAX_STEPS){
            List<ItemAction> temp = new ArrayList<>(stack);
            temp.remove(0);
            stack.clear();
            stack.addAll(temp);
        }
        stack.addLast(command);
    }

    public void clear(){
        undoStack.clear();
        redoStack.clear();
    }
}
