package com.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class Randomdrop implements ModInitializer {
	public static final String MOD_ID = "random-drop";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final Random RANDOM = new Random();
	private static final List<Item> REGISTERED_ITEMS = new ArrayList<>();
	private static RandomdropConfig config;

	@Override
	public void onInitialize() {
		config = RandomdropConfig.load();
		BuiltInRegistries.ITEM.forEach(REGISTERED_ITEMS::add);

		PlayerBlockBreakEvents.BEFORE.register(Randomdrop::replaceBlockDropWithRandomItem);
		LOGGER.info("Random Drop loaded with {} registered items.", REGISTERED_ITEMS.size());
	}

	private static boolean replaceBlockDropWithRandomItem(Level world, net.minecraft.world.entity.player.Player player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
		if (!getConfig().randomBlockDropsEnabled || !(world instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer) || player.isCreative() || state.isAir()) {
			return true;
		}

		ItemStack dropStack = createRandomBlockDropItemStack();
		if (dropStack.isEmpty()) {
			return true;
		}

		ItemStack toolStack = player.getMainHandItem();
		serverLevel.levelEvent(player, 2001, pos, Block.getId(state));
		serverLevel.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
		toolStack.mineBlock(serverLevel, state, pos, serverPlayer);
		dropItemStack(serverLevel, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, dropStack);
		return false;
	}

	public static boolean isRandomCraftingResultsEnabled() {
		return getConfig().randomCraftingResultsEnabled;
	}

	public static ItemStack createRandomCraftingResultItemStack() {
		RandomdropConfig currentConfig = getConfig();
		return createRandomItemStack(item -> !currentConfig.isBlacklistedForCraftingResult(item));
	}

	public static ItemStack createRandomMobDropItemStack() {
		RandomdropConfig currentConfig = getConfig();
		return createRandomItemStack(item -> !currentConfig.isBlacklistedForMobDrop(item));
	}

	public static boolean dropRandomItemAtEntity(Entity entity) {
		if (!getConfig().randomMobDropsEnabled || !(entity.level() instanceof ServerLevel world)) {
			return false;
		}

		ItemStack dropStack = createRandomMobDropItemStack();
		if (dropStack.isEmpty()) {
			return false;
		}

		dropItemStack(world, entity.getX(), entity.getY(), entity.getZ(), dropStack);
		return true;
	}

	private static ItemStack createRandomBlockDropItemStack() {
		RandomdropConfig currentConfig = getConfig();
		return createRandomItemStack(item -> !currentConfig.isBlacklistedForBlockDrop(item));
	}

	private static RandomdropConfig getConfig() {
		if (config == null) {
			config = RandomdropConfig.load();
		}
		return config;
	}

	private static ItemStack createRandomItemStack(Predicate<Item> itemFilter) {
		List<Item> allowedItems = REGISTERED_ITEMS.stream()
				.filter(itemFilter)
				.toList();
		if (allowedItems.isEmpty()) {
			return ItemStack.EMPTY;
		}

		Item randomItem = allowedItems.get(RANDOM.nextInt(allowedItems.size()));
		return new ItemStack(randomItem);
	}

	private static void dropItemStack(ServerLevel world, double x, double y, double z, ItemStack dropStack) {
		ItemEntity itemEntity = new ItemEntity(world, x, y, z, dropStack);
		world.addFreshEntity(itemEntity);
	}
}
