package com.soulmagic;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Soulmagic implements ModInitializer {
	public static final String MOD_ID = "soulmagic";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ManaData.init();
		ModItems.init();

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			int tick = server.getTickCount();
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				ManaData.tickBuff(player);

				// реген: +1 каждые 10 тиков (2 в секунду)
				if (tick % 10 == 0) {
					ManaData.regen(player, 1);
				}

				// ВРЕМЕННАЯ ОТЛАДКА: Shift тратит 20 маны раз в секунду
				if (player.isShiftKeyDown() && tick % 20 == 0) {
					ManaData.tryConsume(player, 20);
				}

				// ВРЕМЕННАЯ ОТЛАДКА: показать ману и бафф над хотбаром
				if (tick % 10 == 0) {
					int buffSec = ManaData.getBuff(player) / 20;
					String text = "Мана: " + ManaData.getMana(player) + "/" + ManaData.getMaxMana(player);
					if (buffSec > 0) {
						text += "  [зелье: " + buffSec + " с]";
					}
					player.displayClientMessage(Component.literal(text), true);
				}
			}
		});

		LOGGER.info("[Soulmagic] common init OK");
	}
}package com.soulmagic;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Soulmagic implements ModInitializer {
	public static final String MOD_ID = "soulmagic";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ManaData.init();

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			int tick = server.getTickCount();
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				// реген: +1 каждые 10 тиков (2 в секунду)
				if (tick % 10 == 0) {
					ManaData.regen(player, 1);
				}

				// ВРЕМЕННАЯ ОТЛАДКА: Shift тратит 20 маны раз в секунду
				if (player.isShiftKeyDown() && tick % 20 == 0) {
					ManaData.tryConsume(player, 20);
				}

				// ВРЕМЕННАЯ ОТЛАДКА: показать ману над хотбаром
				if (tick % 10 == 0) {
					player.displayClientMessage(
							net.minecraft.network.chat.Component.literal(
									"Мана: " + ManaData.getMana(player) + "/" + ManaData.getMaxMana(player)),
							true);
				}
			}
		});

		LOGGER.info("[Soulmagic] common init OK");
	}
}
