package com.eu.habbo.habbohotel.rooms;

public enum RoomUserRotation {
    NORTH(0),
    NORTH_EAST(1),
    EAST(2),
    SOUTH_EAST(3),
    SOUTH(4),
    SOUTH_WEST(5),
    WEST(6),
    NORTH_WEST(7);

    private final int direction;

    RoomUserRotation(int direction) {
        this.direction = direction;
    }

    public static RoomUserRotation fromValue(int rotation) {
        rotation %= 8;
        for (RoomUserRotation rot : values()) {
            if (rot.getValue() == rotation) {
                return rot;
            }
        }

        return NORTH;
    }

    public static RoomUserRotation counterClockwise(RoomUserRotation rotation) {
        return fromValue(rotation.getValue() + 7);
    }

    public static RoomUserRotation clockwise(RoomUserRotation rotation) {
        return fromValue(rotation.getValue() + 9);
    }

    public int getValue() {
        return this.direction;
    }

    public static RoomUserRotation getRotation(int rotation){
        return switch(rotation){
            case 1 -> NORTH_EAST;
            case 2 -> EAST;
            case 3 -> SOUTH_EAST;
            case 4 -> SOUTH;
            case 5 -> SOUTH_WEST;
            case 6 -> WEST;
            case 7 -> NORTH_WEST;
            default -> NORTH;
        };
    }

    public RoomUserRotation getOpposite() {
        return switch (this) {
            case NORTH -> RoomUserRotation.SOUTH;
            case NORTH_EAST -> RoomUserRotation.SOUTH_WEST;
            case EAST -> RoomUserRotation.WEST;
            case SOUTH_EAST -> RoomUserRotation.NORTH_WEST;
            case SOUTH -> RoomUserRotation.NORTH;
            case SOUTH_WEST -> RoomUserRotation.NORTH_EAST;
            case WEST -> RoomUserRotation.EAST;
            case NORTH_WEST -> RoomUserRotation.SOUTH_EAST;
        };
    }
}
