package com.example.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class RandomdropClient implements ClientModInitializer {
	private static KeyMapping openConfigKey;

	@Override
	public void onInitializeClient() {
		openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.random-drop.open_config",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_R,
				KeyMapping.Category.MISC
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (openConfigKey.consumeClick()) {
				Minecraft.getInstance().setScreen(new RandomdropConfigScreen(client.screen));
			}
		});
	}
}
