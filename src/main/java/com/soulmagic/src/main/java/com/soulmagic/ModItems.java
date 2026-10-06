package com.soulmagic;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Consumables;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public final class ModItems {
	private static final ResourceKey<Item> MANA_POTION_KEY = ResourceKey.create(
			Registries.ITEM, Identifier.fromNamespaceAndPath(Soulmagic.MOD_ID, "mana_potion"));

	public static final Item MANA_POTION = Registry.register(
			BuiltInRegistries.ITEM,
			MANA_POTION_KEY,
			new ManaPotionItem(new Item.Properties()
					.setId(MANA_POTION_KEY)
					.stacksTo(16)
					.rarity(Rarity.RARE)
					.component(DataComponents.CONSUMABLE, Consumables.DEFAULT_DRINK)
					.component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)));

	private ModItems() {
	}

	// Вызывается из Soulmagic.onInitialize
	public static void init() {
	}

	// Зелье: выпил, получил бафф
	public static class ManaPotionItem extends Item {
		public ManaPotionItem(Properties properties) {
			super(properties);
		}

		@Override
		public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
			if (!level.isClientSide() && entity instanceof ServerPlayer player) {
				ManaData.setBuff(player, ManaData.POTION_BUFF_TICKS);
				player.displayClientMessage(Component.literal(
						"Зелье маны: +20% к силе и -10% к цене заклинаний на 2 минуты"), true);
			}
			return super.finishUsingItem(stack, level, entity);
		}
	}
}
