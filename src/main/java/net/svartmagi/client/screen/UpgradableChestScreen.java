package net.svartmagi.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.svartmagi.menu.UpgradableChestMenu;

/** Kisteskjerm med variabel hoyde (vanilla generic_54-tekstur). */
public class UpgradableChestScreen extends AbstractContainerScreen<UpgradableChestMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");

    private final int rows;

    public UpgradableChestScreen(UpgradableChestMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.rows = menu.getRows();
        this.imageHeight = 114 + rows * 18;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, rows * 18 + 17);
        graphics.blit(TEXTURE, x, y + rows * 18 + 17, 0, 126, imageWidth, 96);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
