package com.campersamu.itemcommander;

import com.campersamu.itemcommander.command.AppendCommanderCommand;
import com.campersamu.itemcommander.command.CreateCommanderCommand;
import com.campersamu.itemcommander.command.GiveCommanderCommand;
import com.campersamu.itemcommander.command.SaveCommanderCommand;
import com.campersamu.itemcommander.config.CommanderIO;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemCommanderInit implements ModInitializer {
    public static final String MODID = "itemcommander";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static final boolean PLACEHOLDERS_LOADED = FabricLoader.getInstance().isModLoaded("placeholder-api");

    @Override
    public void onInitialize() {
        //region Init: Configs
        CommanderIO.init();
        //endregion
        //region Init: Commands
        CreateCommanderCommand.init();
        AppendCommanderCommand.init();
        SaveCommanderCommand.init();
        GiveCommanderCommand.init();
        //endregion
        LOGGER.info(">item commander loading");
        LOGGER.info(">placeholder-api support: {}", PLACEHOLDERS_LOADED ? "enabled" : "disabled");
        LOGGER.info(">when a stick is more powerful than you");
    }
}
