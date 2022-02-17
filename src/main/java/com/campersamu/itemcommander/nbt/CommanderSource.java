package com.campersamu.itemcommander.nbt;

public enum CommanderSource {
    SERVER((byte)0),
    PLAYER((byte)1);

    public final byte id;

    CommanderSource(byte id) {
        this.id = id;
    }

    public static CommanderSource fromId(byte id){
        //todo: more sources
//        return switch (id){
//            case 1 -> KEEP;
//            default -> CONSUME;
//        };

        if(id == 1)
            return PLAYER;

        return SERVER;
    }
}
