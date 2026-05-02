package com.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class RandomdropConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("random-drop.json");

	public boolean randomBlockDropsEnabled = true;
	public boolean randomCraftingResultsEnabled = true;
	public boolean randomMobDropsEnabled = true;

	public List<String> blockDropBlacklist = new ArrayList<>();
	public List<String> craftingResultBlacklist = new ArrayList<>();
	public List<String> mobDropBlacklist = new ArrayList<>();

	public static RandomdropConfig load() {
		try {
			if (Files.notExists(CONFIG_PATH)) {
				RandomdropConfig config = createDefault();
				config.save();
				return config;
			}

			try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
				RandomdropConfig config = GSON.fromJson(reader, RandomdropConfig.class);
				if (config == null) {
					config = createDefault();
				}
				config.sanitize();
				config.save();
				return config;
			}
		} catch (IOException exception) {
			Randomdrop.LOGGER.error("Failed to load Random Drop config, using default config.", exception);
			return createDefault();
		}
	}

	public boolean isBlacklistedForBlockDrop(Item item) {
		return isBlacklisted(item, blockDropBlacklist);
	}

	public boolean isBlacklistedForCraftingResult(Item item) {
		return isBlacklisted(item, craftingResultBlacklist);
	}

	public boolean isBlacklistedForMobDrop(Item item) {
		return isBlacklisted(item, mobDropBlacklist);
	}

	private static RandomdropConfig createDefault() {
		RandomdropConfig config = new RandomdropConfig();
		config.blockDropBlacklist.addAll(defaultSpecialItemBlacklist());
		config.craftingResultBlacklist.addAll(defaultSpecialItemBlacklist());
		config.mobDropBlacklist.addAll(defaultSpecialItemBlacklist());
		return config;
	}

	private static List<String> defaultSpecialItemBlacklist() {
		return List.of(
				"minecraft:air",
				"minecraft:barrier",
				"minecraft:debug_stick",
				"minecraft:command_block",
				"minecraft:chain_command_block",
				"minecraft:repeating_command_block",
				"minecraft:structure_block",
				"minecraft:structure_void",
				"minecraft:jigsaw",
				"minecraft:light",
				"minecraft:knowledge_book"
		);
	}

	private void sanitize() {
		if (blockDropBlacklist == null) {
			blockDropBlacklist = new ArrayList<>();
		}
		if (craftingResultBlacklist == null) {
			craftingResultBlacklist = new ArrayList<>();
		}
		if (mobDropBlacklist == null) {
			mobDropBlacklist = new ArrayList<>();
		}
	}

	private void save() throws IOException {
		Files.createDirectories(CONFIG_PATH.getParent());
		try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
			GSON.toJson(this, writer);
		}
	}

	private static boolean isBlacklisted(Item item, List<String> blacklist) {
		String itemId = BuiltInRegistries.ITEM.getKey(item).toString();
		return blacklist.contains(itemId);
	}
}
