package com.eu.habbo.habbohotel.users.cache;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

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
        boolean success = true;
        for(int i = 0; i < steps && !undoStack.isEmpty(); i++) {
            ItemAction command = undoStack.pop();
            pushLimited(redoStack, command);
            success = command.undo();
        }
        return success;
    }

    public boolean redo(int steps){
        if(!canRedo(steps)) return false;
        boolean success = true;
        for(int i = 0; i < steps && !redoStack.isEmpty(); i++) {
            ItemAction command = redoStack.pop();
            pushLimited(undoStack, command);
            success = command.redo();
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
