package com.soulmagic;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public final class ManaData {
	public static final int BASE_MAX_MANA = 100;
	public static final int POTION_BUFF_TICKS = 2400; // 2 минуты

	// Текущая мана: сохраняется между сессиями, переживает смерть, отправляется самому игроку
	public static final AttachmentType<Integer> MANA = AttachmentRegistry.create(
			Identifier.fromNamespaceAndPath(Soulmagic.MOD_ID, "mana"),
			builder -> builder
					.persistent(Codec.INT)
					.copyOnDeath()
					.syncWith(ByteBufCodecs.INT, AttachmentSyncPredicate.targetOnly())
	);

	// Оставшееся время баффа зелья маны (в тиках)
	public static final AttachmentType<Integer> BUFF = AttachmentRegistry.create(
			Identifier.fromNamespaceAndPath(Soulmagic.MOD_ID, "mana_buff"),
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

	// ===== бафф зелья маны =====

	public static int getBuff(ServerPlayer player) {
		return player.getAttachedOrElse(BUFF, 0);
	}

	public static boolean hasBuff(ServerPlayer player) {
		return getBuff(player) > 0;
	}

	public static void setBuff(ServerPlayer player, int ticks) {
		player.setAttached(BUFF, Math.max(0, ticks));
	}

	// Вызывать каждый тик: отсчёт баффа и частицы
	public static void tickBuff(ServerPlayer player) {
		int buff = getBuff(player);
		if (buff <= 0) {
			return;
		}
		setBuff(player, buff - 1);
		if (buff % 5 == 0 && player.level() instanceof ServerLevel level) {
			level.sendParticles(ParticleTypes.ENCHANT,
					player.getX(), player.getY() + 1.0, player.getZ(),
					2, 0.4, 0.6, 0.4, 0.3);
		}
	}

	// Аналог pay.mcfunction: с баффом цена 90% (cost * 9 / 10), true если мана списана
	public static boolean tryConsume(ServerPlayer player, int cost) {
		int finalCost = hasBuff(player) ? cost * 9 / 10 : cost;
		int mana = getMana(player);
		if (mana < finalCost) {
			player.displayClientMessage(
					Component.literal("Недостаточно маны! Нужно: " + finalCost), true);
			return false;
		}
		setMana(player, mana - finalCost);
		return true;
	}
}
