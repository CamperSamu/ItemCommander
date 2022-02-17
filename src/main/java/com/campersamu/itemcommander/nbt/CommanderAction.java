package com.campersamu.itemcommander.nbt;

public enum CommanderAction {
    CONSUME((byte)0),
    KEEP((byte)1);

    public final byte id;

    CommanderAction(byte id) {
        this.id = id;
    }

    public static CommanderAction fromId(byte id){
        //todo: more actions
//        return switch (id){
//            case 1 -> KEEP;
//            default -> CONSUME;
//        };

        if(id == 1)
            return KEEP;

        return CONSUME;
    }
}
