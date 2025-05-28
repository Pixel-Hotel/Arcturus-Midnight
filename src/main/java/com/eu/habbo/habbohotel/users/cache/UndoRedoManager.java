package com.eu.habbo.habbohotel.users.cache;

import com.eu.habbo.habbohotel.users.cache.actions.ItemAction;
import com.eu.habbo.habbohotel.users.cache.actions.ItemActionKey;

import java.util.*;

public class UndoRedoManager {

    public static final int MAX_STEPS = 10;
    private final Deque<ItemAction> undoStack = new ArrayDeque<>();
    private final Deque<ItemAction> redoStack = new ArrayDeque<>();

    public int getUndoStackSize() { return undoStack.size(); }
    public int getRedoStackSize() { return redoStack.size(); }

    public boolean canUndo(int steps){
        return !undoStack.isEmpty() && undoStack.size() >= steps;
    }

    public boolean canRedo(int steps){
        return !redoStack.isEmpty() && redoStack.size() >= steps;
    }

    public void saveAction(ItemAction command){
        pushLimited(undoStack, command);
        redoStack.clear();
    }

    public boolean undo(int steps){
        if(!canUndo(steps)) return false;
        return handle(steps, undoStack);
    }

    public boolean redo(int steps){
        if(!canRedo(steps)) return false;
        return handle(steps, redoStack);
    }

    private boolean handle(int steps, Deque<ItemAction> stack){
        Map<ItemActionKey, ItemAction> uniqueActions  = new LinkedHashMap<>();

        for(int i = 0; i < steps && !stack.isEmpty(); i++) {
            ItemAction command = stack.pop();

            ItemActionKey key = new ItemActionKey(command.getItem().getId(), command.getClass());
            uniqueActions.put(key, command);
            pushLimited(stack, command);
        }

        boolean success = true;
        for(ItemAction action : uniqueActions.values()){
            success &= action.redo();
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
        stack.push(command);
    }
}
