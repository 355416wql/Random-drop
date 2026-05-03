package com.example.client;

import com.example.Randomdrop;
import com.example.RandomdropConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RandomdropConfigScreen extends Screen {
	private static final int BACKGROUND = 0xF0101118;
	private static final int HEADER = 0xFF171A26;
	private static final int SECTION = 0xAA1C2130;
	private static final int CARD = 0xCC252B3A;
	private static final int CARD_HOVER = 0xEE30394C;
	private static final int ACCENT = 0xFF8B6DFF;
	private static final int GREEN = 0xFF51D16A;
	private static final int RED = 0xFFFF6767;
	private static final int TEXT = 0xFFEFEFFF;
	private static final int MUTED = 0xFFAEB4C6;
	private static final int DIM = 0xFF7E8496;

	private final Screen parent;
	private final RandomdropConfig config;
	private Layout layout;
	private int scrollOffset;
	private Button blockDropsButton;
	private Button naturalBlockDropsButton;
	private Button craftingResultsButton;
	private Button mobDropsButton;
	private EditBox blockDropBlacklistBox;
	private EditBox naturalBlockDropBlacklistBox;
	private EditBox craftingResultBlacklistBox;
	private EditBox mobDropBlacklistBox;

	public RandomdropConfigScreen(Screen parent) {
		super(Component.literal("Random Drop 配置"));
		this.parent = parent;
		this.config = Randomdrop.getConfig();
	}

	@Override
	protected void init() {
		layout = Layout.of(width, height);
		scrollOffset = Math.min(scrollOffset, layout.maxScroll);
		int[] cardX = layout.cardXs();
		int[] cardY = layout.cardYs();

		blockDropsButton = addRenderableWidget(toggle(cardX[0] + 10, cardY[0] + 21 - scrollOffset, "玩家破坏", config.randomBlockDropsEnabled, b -> {
			config.randomBlockDropsEnabled = !config.randomBlockDropsEnabled;
			b.setMessage(toggleText("玩家破坏", config.randomBlockDropsEnabled));
		}));
		naturalBlockDropsButton = addRenderableWidget(toggle(cardX[1] + 10, cardY[1] + 21 - scrollOffset, "自然掉落", config.randomNaturalBlockDropsEnabled, b -> {
			config.randomNaturalBlockDropsEnabled = !config.randomNaturalBlockDropsEnabled;
			b.setMessage(toggleText("自然掉落", config.randomNaturalBlockDropsEnabled));
		}));
		craftingResultsButton = addRenderableWidget(toggle(cardX[2] + 10, cardY[2] + 21 - scrollOffset, "合成结果", config.randomCraftingResultsEnabled, b -> {
			config.randomCraftingResultsEnabled = !config.randomCraftingResultsEnabled;
			b.setMessage(toggleText("合成结果", config.randomCraftingResultsEnabled));
		}));
		mobDropsButton = addRenderableWidget(toggle(cardX[3] + 10, cardY[3] + 21 - scrollOffset, "生物掉落", config.randomMobDropsEnabled, b -> {
			config.randomMobDropsEnabled = !config.randomMobDropsEnabled;
			b.setMessage(toggleText("生物掉落", config.randomMobDropsEnabled));
		}));

		blockDropBlacklistBox = addRenderableWidget(box(layout.fieldX, layout.fieldY(0) - scrollOffset, config.blockDropBlacklist));
		naturalBlockDropBlacklistBox = addRenderableWidget(box(layout.fieldX, layout.fieldY(1) - scrollOffset, config.naturalBlockDropBlacklist));
		craftingResultBlacklistBox = addRenderableWidget(box(layout.fieldX, layout.fieldY(2) - scrollOffset, config.craftingResultBlacklist));
		mobDropBlacklistBox = addRenderableWidget(box(layout.fieldX, layout.fieldY(3) - scrollOffset, config.mobDropBlacklist));

		int buttonWidth = Math.min(128, Math.max(96, layout.panelWidth / 5));
		int buttonX = layout.panelX + (layout.panelWidth - buttonWidth * 2 - 10) / 2;
		addRenderableWidget(Button.builder(Component.literal("保存配置"), b -> saveAndClose()).bounds(buttonX, layout.buttonY, buttonWidth, 22).build());
		addRenderableWidget(Button.builder(Component.literal("取消"), b -> onClose()).bounds(buttonX + buttonWidth + 10, layout.buttonY, buttonWidth, 22).build());
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		if (layout == null || layout.screenWidth != width || layout.screenHeight != height) {
			rebuildWidgets();
			return;
		}
		renderPanel(graphics);
		renderHeader(graphics);
		renderFeatureSection(graphics, mouseX, mouseY);
		renderBlacklistSection(graphics);
		renderFooter(graphics);
		super.render(graphics, mouseX, mouseY, delta);
	}

	@Override
	public void onClose() {
		Minecraft.getInstance().setScreen(parent);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		if (layout == null || layout.maxScroll <= 0) {
			return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
		}
		syncInputsToConfig();
		scrollOffset = Math.max(0, Math.min(layout.maxScroll, scrollOffset - (int) (scrollY * 18)));
		rebuildWidgets();
		return true;
	}

	private void syncInputsToConfig() {
		if (blockDropBlacklistBox == null) {
			return;
		}
		config.blockDropBlacklist = parseBlacklist(blockDropBlacklistBox.getValue());
		config.naturalBlockDropBlacklist = parseBlacklist(naturalBlockDropBlacklistBox.getValue());
		config.craftingResultBlacklist = parseBlacklist(craftingResultBlacklistBox.getValue());
		config.mobDropBlacklist = parseBlacklist(mobDropBlacklistBox.getValue());
	}

	private Button toggle(int x, int y, String label, boolean enabled, Button.OnPress press) {
		return Button.builder(toggleText(label, enabled), press).bounds(x, y, layout.buttonWidth, 20).build();
	}

	private EditBox box(int x, int y, List<String> value) {
		EditBox editBox = new EditBox(font, x, y + 17, layout.fieldWidth, 20, Component.empty());
		editBox.setMaxLength(4096);
		editBox.setValue(String.join(",", value));
		return editBox;
	}

	private void renderPanel(GuiGraphics g) {
		g.fill(layout.panelX, layout.panelY, layout.panelX + layout.panelWidth, layout.panelY + layout.panelHeight, BACKGROUND);
		g.fill(layout.panelX, layout.panelY, layout.panelX + layout.panelWidth, layout.panelY + 52, HEADER);
		g.fill(layout.panelX, layout.panelY, layout.panelX + layout.panelWidth, layout.panelY + 2, ACCENT);
		g.fill(layout.featureX, layout.featureY - scrollOffset, layout.featureX + layout.featureWidth, layout.featureY - scrollOffset + layout.featureHeight, SECTION);
		g.fill(layout.blacklistX, layout.blacklistY - scrollOffset, layout.blacklistX + layout.blacklistWidth, layout.blacklistY - scrollOffset + layout.blacklistHeight, SECTION);
		if (!layout.compact) {
			g.fill(layout.blacklistX - 12, layout.featureY - scrollOffset, layout.blacklistX - 11, layout.featureY - scrollOffset + layout.featureHeight, 0x55383F55);
		}
	}

	private void renderHeader(GuiGraphics g) {
		g.drawString(font, "Random Drop 配置", layout.panelX + 18, layout.panelY + 13, TEXT);
		g.drawString(font, layout.compact ? "黑名单用英文逗号分隔" : "黑名单用英文逗号分隔，例如 minecraft:diamond", layout.panelX + 18, layout.panelY + 31, MUTED);
		g.drawString(font, "快捷键 R", layout.panelX + layout.panelWidth - 62, layout.panelY + 22, ACCENT);
	}

	private void renderFeatureSection(GuiGraphics g, int mouseX, int mouseY) {
		g.drawString(font, "功能开关", layout.featureX + 8, layout.featureY + 8 - scrollOffset, TEXT);
		int[] xs = layout.cardXs();
		int[] ys = layout.cardYs();
		drawCard(g, xs[0], ys[0] - scrollOffset, "玩家破坏方块", "主动挖掘时随机", config.randomBlockDropsEnabled, mouseX, mouseY);
		drawCard(g, xs[1], ys[1] - scrollOffset, "自然/非玩家掉落", "树叶腐败、水流等", config.randomNaturalBlockDropsEnabled, mouseX, mouseY);
		drawCard(g, xs[2], ys[2] - scrollOffset, "合成结果", "含 Shift 快速合成", config.randomCraftingResultsEnabled, mouseX, mouseY);
		drawCard(g, xs[3], ys[3] - scrollOffset, "生物死亡掉落", "非玩家生物死亡", config.randomMobDropsEnabled, mouseX, mouseY);
	}

	private void drawCard(GuiGraphics g, int x, int y, String title, String desc, boolean enabled, int mouseX, int mouseY) {
		boolean hover = mouseX >= x && mouseX <= x + layout.cardWidth && mouseY >= y && mouseY <= y + 42;
		g.fill(x, y, x + layout.cardWidth, y + 42, hover ? CARD_HOVER : CARD);
		g.fill(x, y, x + 4, y + 42, enabled ? GREEN : RED);
		g.drawString(font, title, x + 10, y + 7, TEXT);
		g.drawString(font, desc, x + 10, y + 20, MUTED);
		g.drawString(font, enabled ? "ON" : "OFF", x + layout.cardWidth - 30, y + 7, enabled ? GREEN : RED);
	}

	private void renderBlacklistSection(GuiGraphics g) {
		g.drawString(font, "掉落黑名单", layout.blacklistX + 8, layout.blacklistY + 8 - scrollOffset, TEXT);
		drawLabel(g, 0, "玩家破坏黑名单");
		drawLabel(g, 1, "自然掉落黑名单");
		drawLabel(g, 2, "合成结果黑名单");
		drawLabel(g, 3, "生物掉落黑名单");
		g.drawString(font, "留空表示不额外禁用；保存后立即生效", layout.fieldX, layout.hintY - scrollOffset, DIM);
	}

	private void drawLabel(GuiGraphics g, int index, String text) {
		g.drawString(font, text, layout.fieldX, layout.fieldY(index) + 4 - scrollOffset, TEXT);
	}

	private void renderFooter(GuiGraphics g) {
		g.fill(layout.panelX, layout.footerY, layout.panelX + layout.panelWidth, layout.footerY + 1, 0x55383F55);
		g.drawCenteredString(font, "配置文件：config/random-drop.json", layout.panelX + layout.panelWidth / 2, layout.panelY + layout.panelHeight - 16, MUTED);
	}

	private void saveAndClose() {
		config.blockDropBlacklist = parseBlacklist(blockDropBlacklistBox.getValue());
		config.naturalBlockDropBlacklist = parseBlacklist(naturalBlockDropBlacklistBox.getValue());
		config.craftingResultBlacklist = parseBlacklist(craftingResultBlacklistBox.getValue());
		config.mobDropBlacklist = parseBlacklist(mobDropBlacklistBox.getValue());
		if (config.save()) Randomdrop.setConfig(config);
		onClose();
	}

	private static List<String> parseBlacklist(String value) {
		if (value == null || value.isBlank()) return new ArrayList<>();
		return Arrays.stream(value.split(",")).map(String::trim).filter(s -> !s.isEmpty()).distinct().collect(java.util.stream.Collectors.toCollection(ArrayList::new));
	}

	private static Component toggleText(String label, boolean enabled) {
		return Component.literal(label + "：" + (enabled ? "开启" : "关闭"));
	}

	private static class Layout {
		final int screenWidth, screenHeight, panelX, panelY, panelWidth, panelHeight, featureX, featureY, featureWidth, featureHeight, blacklistX, blacklistY, blacklistWidth, blacklistHeight, cardWidth, fieldX, fieldWidth, buttonWidth, hintY, footerY, buttonY, maxScroll;
		final boolean compact;

		private Layout(int sw, int sh, boolean compact, int px, int py, int pw, int ph, int fx, int fy, int fw, int fh, int bx, int by, int bw, int bh, int cw, int fieldX, int fieldWidth, int buttonWidth, int hintY, int footerY, int buttonY, int maxScroll) {
			this.screenWidth = sw; this.screenHeight = sh; this.compact = compact; this.panelX = px; this.panelY = py; this.panelWidth = pw; this.panelHeight = ph; this.featureX = fx; this.featureY = fy; this.featureWidth = fw; this.featureHeight = fh; this.blacklistX = bx; this.blacklistY = by; this.blacklistWidth = bw; this.blacklistHeight = bh; this.cardWidth = cw; this.fieldX = fieldX; this.fieldWidth = fieldWidth; this.buttonWidth = buttonWidth; this.hintY = hintY; this.footerY = footerY; this.buttonY = buttonY; this.maxScroll = maxScroll;
		}

		static Layout of(int width, int height) {
			boolean compact = width < 720;
			int margin = compact ? 8 : 24;
			int pw = Math.max(300, Math.min(width - margin * 2, compact ? 520 : 760));
			int ph = Math.max(compact ? 410 : 330, Math.min(height - margin * 2, compact ? 450 : 360));
			int px = (width - pw) / 2;
			int py = Math.max(margin, (height - ph) / 2);
			int contentY = py + 66;
			int footerY = py + ph - 52;
			int buttonY = py + ph - 38;
			if (compact) {
				int sectionW = pw - 28;
				int fx = px + 14;
				int fy = contentY;
				int fh = 126;
				int bx = px + 14;
				int by = fy + fh + 10;
				int bh = footerY - by - 8;
				int cw = (sectionW - 26) / 2;
				int contentBottom = by + 220;
				int maxScroll = Math.max(0, contentBottom - (footerY - 8));
				return new Layout(width, height, true, px, py, pw, ph, fx, fy, sectionW, fh, bx, by, sectionW, bh, cw, bx + 10, sectionW - 20, Math.max(72, cw - 20), by + 188, footerY, buttonY, maxScroll);
			}
			int fw = Math.max(202, Math.min(230, pw / 3));
			int bw = pw - fw - 58;
			int fx = px + 16;
			int bx = fx + fw + 26;
			int sectionH = footerY - contentY - 8;
			int contentBottom = contentY + 230;
			int maxScroll = Math.max(0, contentBottom - (footerY - 8));
			return new Layout(width, height, false, px, py, pw, ph, fx, contentY, fw, sectionH, bx, contentY, bw, sectionH, fw - 16, bx + 10, bw - 20, fw - 36, contentY + 202, footerY, buttonY, maxScroll);
		}

		int[] cardXs() {
			if (!compact) return new int[]{featureX + 8, featureX + 8, featureX + 8, featureX + 8};
			return new int[]{featureX + 8, featureX + 18 + cardWidth, featureX + 8, featureX + 18 + cardWidth};
		}

		int[] cardYs() {
			if (!compact) return new int[]{featureY + 26, featureY + 74, featureY + 122, featureY + 170};
			return new int[]{featureY + 26, featureY + 26, featureY + 76, featureY + 76};
		}

		int fieldY(int index) {
			return blacklistY + 28 + index * 42;
		}
	}
}
