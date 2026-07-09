package net.svartmagi.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.svartmagi.menu.TieredFurnaceMenu;

/** Bruker vanilla ovnstekstur; fremdrift tegnes med sprites. */
public class TieredFurnaceScreen extends AbstractContainerScreen<TieredFurnaceMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.withDefaultNamespace("textures/gui/container/furnace.png");
    private static final ResourceLocation LIT_PROGRESS =
            ResourceLocation.withDefaultNamespace("container/furnace/lit_progress");
    private static final ResourceLocation BURN_PROGRESS =
            ResourceLocation.withDefaultNamespace("container/furnace/burn_progress");

    public TieredFurnaceScreen(TieredFurnaceMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        int duration = Math.max(1, menu.getBurnDuration());
        if (menu.getBurnTime() > 0) {
            int lit = Math.min(13, menu.getBurnTime() * 13 / duration);
            graphics.blitSprite(LIT_PROGRESS, 14, 14, 0, 14 - lit - 1, x + 56, y + 36 + 14 - lit - 1, 14, lit + 1);
        }

        int total = Math.max(1, menu.getTotalTime());
        int arrow = Math.min(24, menu.getProgress() * 24 / total);
        if (arrow > 0) {
            graphics.blitSprite(BURN_PROGRESS, 24, 16, 0, 0, x + 79, y + 34, arrow + 1, 16);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);

        java.util.List<UpgradeDisplay.Entry> upgrades = new java.util.ArrayList<>();
        if (menu.getSpeedUpgrades() > 0) {
            upgrades.add(new UpgradeDisplay.Entry(
                    new net.minecraft.world.item.ItemStack(net.svartmagi.registry.ModItems.FARTSOPPGRADERING.get()),
                    menu.getSpeedUpgrades()));
        }
        if (menu.getParallelUpgrades() > 0) {
            upgrades.add(new UpgradeDisplay.Entry(
                    new net.minecraft.world.item.ItemStack(net.svartmagi.registry.ModItems.PARALLELLOPPGRADERING.get()),
                    menu.getParallelUpgrades()));
        }
        UpgradeDisplay.render(graphics, font, leftPos + imageWidth + 2, topPos + 6, upgrades, mouseX, mouseY);
    }
}
