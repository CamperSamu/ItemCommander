package com.campersamu.itemcommander;

import com.campersamu.itemcommander.command.CreateCommanderCommand;
import com.campersamu.itemcommander.nbt.Commander;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemCommanderInit implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("itemcommander");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		CreateCommanderCommand.init();
		LOGGER.info(">when a stick is more powerful than you");
	}
}
