package net.svartmagi.client.screen;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * Liten sidepanel-visning av installerte oppgraderinger, festet til hoyre
 * kant av GUI-et. Rent klientside - dataene kommer fra menyens
 * ContainerData (maskiner) eller synket blockentity (kiste).
 */
public final class UpgradeDisplay {
    public record Entry(ItemStack stack, int count) {}

    private static final int SLOT = 20;
    private static final int WIDTH = 26;

    private UpgradeDisplay() {}

    /**
     * Tegner panelet med topp-venstre paa (x, y) og haandterer hover-tooltip.
     * Viser alltid minst en tom slot saa spilleren ser at maskinen kan
     * oppgraderes.
     */
    public static void render(GuiGraphics graphics, Font font, int x, int y,
                              List<Entry> entries, int mouseX, int mouseY) {
        int slots = Math.max(1, entries.size());
        int height = slots * SLOT + 6;

        // Panel i vanilla-graatt med lys/moerk kant
        graphics.fill(x, y, x + WIDTH, y + height, 0xFF000000);
        graphics.fill(x + 1, y + 1, x + WIDTH - 1, y + height - 1, 0xFFC6C6C6);
        graphics.fill(x + 1, y + 1, x + WIDTH - 1, y + 2, 0xFFFFFFFF);
        graphics.fill(x + 1, y + 1, x + 2, y + height - 1, 0xFFFFFFFF);
        graphics.fill(x + 1, y + height - 2, x + WIDTH - 1, y + height - 1, 0xFF555555);
        graphics.fill(x + WIDTH - 2, y + 1, x + WIDTH - 1, y + height - 1, 0xFF555555);

        for (int i = 0; i < slots; i++) {
            int sx = x + 4;
            int sy = y + 4 + i * SLOT;
            // Innsunket slot-bakgrunn
            graphics.fill(sx - 1, sy - 1, sx + 17, sy + 17, 0xFF8B8B8B);
            graphics.fill(sx - 1, sy - 1, sx + 17, sy, 0xFF373737);
            graphics.fill(sx - 1, sy - 1, sx, sy + 17, 0xFF373737);
            graphics.fill(sx - 1, sy + 16, sx + 17, sy + 17, 0xFFFFFFFF);
            graphics.fill(sx + 16, sy - 1, sx + 17, sy + 17, 0xFFFFFFFF);

            if (i < entries.size()) {
                Entry entry = entries.get(i);
                graphics.renderItem(entry.stack(), sx, sy);
                if (entry.count() > 1) {
                    graphics.renderItemDecorations(font, entry.stack(), sx, sy, String.valueOf(entry.count()));
                }
            }
        }

        if (mouseX >= x && mouseX < x + WIDTH && mouseY >= y && mouseY < y + height) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("gui.svartmagi.upgrades"));
            if (entries.isEmpty()) {
                tooltip.add(Component.translatable("gui.svartmagi.no_upgrades"));
            } else {
                for (Entry entry : entries) {
                    tooltip.add(entry.stack().getHoverName().copy()
                            .append(entry.count() > 1 ? " x" + entry.count() : ""));
                }
            }
            graphics.renderComponentTooltip(font, tooltip, mouseX, mouseY);
        }
    }
}
