package com.campersamu.itemcommander.nbt;

public enum CommanderSource {
    SERVER((byte) 0),
    PLAYER((byte) 1),
    OP((byte) 2);

    public final byte id;

    CommanderSource(byte id) {
        this.id = id;
    }

    public static CommanderSource fromId(byte id) {
        return switch (id) {
            case 1 -> PLAYER;
            case 2 -> OP;
            default -> SERVER;
        };
    }
}
