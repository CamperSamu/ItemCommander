package com.campersamu.itemcommander.nbt;

public enum CommanderSource {
    DANGEROUSLY_OP((byte) -2),
    SERVER_AS_PLAYER((byte) -1),
    SERVER((byte) 0),
    PLAYER((byte) 1),
    OP((byte) 2);

    public final byte id;

    CommanderSource(byte id) {
        this.id = id;
    }

    public static CommanderSource fromId(byte id) {
        return switch (id) {
            case -2 -> DANGEROUSLY_OP;
            case -1 -> SERVER_AS_PLAYER;
            case 1 -> PLAYER;
            case 2 -> OP;
            default -> SERVER;
        };
    }
}
