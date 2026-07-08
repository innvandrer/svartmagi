package net.svartmagi.item;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** Oppgraderingsitem: installeres ved aa hoyreklikke maskiner/kister. */
public class UpgradeItem extends Item {
    public enum Kind {
        SPEED,
        PARALLEL,
        CHEST_JERN,
        CHEST_GULL,
        CHEST_DIAMANT,
        CHEST_STACK
    }

    private final Kind kind;

    public UpgradeItem(Kind kind, Properties properties) {
        super(properties);
        this.kind = kind;
    }

    public Kind kind() {
        return kind;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.svartmagi.upgrade_hint"));
    }
}
