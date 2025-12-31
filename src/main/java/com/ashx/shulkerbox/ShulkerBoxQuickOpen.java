package com.ashx.shulkerbox;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShulkerBoxQuickOpen implements ModInitializer {
    public static final String MOD_ID = "shulker-box-quick-open";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Shulker Box Quick Open mod loaded!");
        ShulkerBoxHandler.register();
    }
}