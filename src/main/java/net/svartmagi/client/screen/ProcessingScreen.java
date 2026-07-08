package net.svartmagi.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.svartmagi.Svartmagi;
import net.svartmagi.menu.ProcessingMenu;

/** Skjerm for elektrisk ovn, knuser og infuser. */
public class ProcessingScreen extends AbstractContainerScreen<ProcessingMenu> {
    private static final ResourceLocation TEXTURE = Svartmagi.id("textures/gui/processing.png");

    public ProcessingScreen(ProcessingMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // Fremdriftspil
        int total = Math.max(1, menu.getTotalTime());
        int arrow = menu.getProgress() * 24 / total;
        graphics.fill(x + 79, y + 34, x + 79 + 24, y + 42, 0xFF3B3B3B);
        if (arrow > 0) {
            graphics.fill(x + 79, y + 34, x + 79 + arrow, y + 42, 0xFF80D080);
        }

        // Energibar
        int capacity = Math.max(1, menu.getCapacity());
        int energyHeight = menu.getEnergy() * 52 / capacity;
        graphics.fill(x + 10, y + 17, x + 18, y + 69, 0xFF3B3B3B);
        if (energyHeight > 0) {
            graphics.fill(x + 10, y + 69 - energyHeight, x + 18, y + 69, 0xFFD04040);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
        if (mouseX >= leftPos + 10 && mouseX <= leftPos + 18 && mouseY >= topPos + 17 && mouseY <= topPos + 69) {
            graphics.renderTooltip(font, Component.literal(menu.getEnergy() + " / " + menu.getCapacity() + " FE"),
                    mouseX, mouseY);
        }
    }
}
