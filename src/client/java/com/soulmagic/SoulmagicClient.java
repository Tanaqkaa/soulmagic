package com.soulmagic;

import net.fabricmc.api.ClientModInitializer;

public class SoulmagicClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		Soulmagic.LOGGER.info("[Soulmagic] client init OK");
	}
}
