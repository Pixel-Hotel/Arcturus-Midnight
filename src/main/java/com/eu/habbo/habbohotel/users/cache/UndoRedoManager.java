package com.eu.habbo.habbohotel.users.cache;

import com.eu.habbo.habbohotel.users.cache.actions.ItemAction;
import com.eu.habbo.habbohotel.users.cache.actions.ItemActionKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class UndoRedoManager {

    public static final Logger LOGGER = LoggerFactory.getLogger(UndoRedoManager.class);
    public static final int MAX_STEPS = 10;
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
        LOGGER.debug("{}: fromStack: {} -> toStack: {}", undo ? "UNDO" : "REDO", fromStack, toStack);
        for(int i = 0; i < steps; i++) {
            ItemAction command = fromStack.pollLast();
            if(command == null) break;
            ItemActionKey key = new ItemActionKey(command.getItem().getId(), command.getClass());
            LOGGER.debug("{} step {}: {} - {}",undo ? "Undoing" : "Redoing", i, command.getItem().getId(), command.getClass().getSimpleName());
            uniqueActions.put(key, command);
            LOGGER.debug("Unique actions: {}", uniqueActions.size());
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
