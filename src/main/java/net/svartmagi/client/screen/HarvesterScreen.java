package net.svartmagi.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.svartmagi.menu.HarvesterMenu;

/** Bruker vanilla dispenser-tekstur (3x3) + energibar. */
public class HarvesterScreen extends AbstractContainerScreen<HarvesterMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.withDefaultNamespace("textures/gui/container/dispenser.png");

    public HarvesterScreen(HarvesterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

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

        java.util.List<UpgradeDisplay.Entry> upgrades = new java.util.ArrayList<>();
        if (menu.getSpeedUpgrades() > 0) {
            upgrades.add(new UpgradeDisplay.Entry(
                    new net.minecraft.world.item.ItemStack(net.svartmagi.registry.ModItems.FARTSOPPGRADERING.get()),
                    menu.getSpeedUpgrades()));
        }
        UpgradeDisplay.render(graphics, font, leftPos + imageWidth + 2, topPos + 6, upgrades, mouseX, mouseY);
    }
}
