package com.soulmagic;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public final class ManaData {
	public static final int BASE_MAX_MANA = 100;

	// Текущая мана: сохраняется между сессиями, переживает смерть, отправляется самому игроку
	public static final AttachmentType<Integer> MANA = AttachmentRegistry.create(
			Identifier.fromNamespaceAndPath(Soulmagic.MOD_ID, "mana"),
			builder -> builder
					.persistent(Codec.INT)
					.copyOnDeath()
					.syncWith(ByteBufCodecs.INT, AttachmentSyncPredicate.targetOnly())
	);

	private ManaData() {
	}

	// Вызывается из Soulmagic.onInitialize, чтобы класс точно загрузился
	public static void init() {
	}

	public static int getMaxMana(ServerPlayer player) {
		return BASE_MAX_MANA; // позже сюда добавится броня
	}

	public static int getMana(ServerPlayer player) {
		return player.getAttachedOrElse(MANA, BASE_MAX_MANA);
	}

	public static void setMana(ServerPlayer player, int value) {
		int clamped = Math.max(0, Math.min(value, getMaxMana(player)));
		player.setAttached(MANA, clamped);
	}

	// Регенерация: прибавить amount, но не выше максимума
	public static void regen(ServerPlayer player, int amount) {
		int mana = getMana(player);
		if (mana < getMaxMana(player)) {
			setMana(player, mana + amount);
		}
	}

	// Аналог pay.mcfunction: true, если мана списана
	public static boolean tryConsume(ServerPlayer player, int cost) {
		int mana = getMana(player);
		if (mana < cost) {
			player.displayClientMessage(
					Component.literal("Недостаточно маны! Нужно: " + cost), true);
			return false;
		}
		setMana(player, mana - cost);
		return true;
	}
}
