package com.campersamu.itemcommander.exception;

import static com.campersamu.itemcommander.ItemCommanderInit.LOGGER;

public class CommanderNoCommandException extends CommanderException {
    public static final String error = "No command was specified for Commander item!";

    public CommanderNoCommandException() {
        super();
        LOGGER.error(error);
    }
}
