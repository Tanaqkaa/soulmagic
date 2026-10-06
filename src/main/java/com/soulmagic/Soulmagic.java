package com.soulmagic;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Soulmagic implements ModInitializer {
	public static final String MOD_ID = "soulmagic";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("[Soulmagic] common init OK");
	}
}
