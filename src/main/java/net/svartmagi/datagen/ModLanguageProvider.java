package net.svartmagi.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.svartmagi.Svartmagi;
import net.svartmagi.registry.ModBlocks;
import net.svartmagi.registry.ModItems;

public class ModLanguageProvider extends LanguageProvider {
    private final boolean norwegian;

    public ModLanguageProvider(PackOutput output, String locale) {
        super(output, Svartmagi.MODID, locale);
        this.norwegian = locale.startsWith("nb") || locale.startsWith("no");
    }

    private void both(String key, String english, String norsk) {
        add(key, norwegian ? norsk : english);
    }

    @Override
    protected void addTranslations() {
        both("itemGroup.svartmagi", "Svartmagi", "Svartmagi");

        both(ModBlocks.KULLGENERATOR.get().getDescriptionId(), "Coal Generator", "Kullgenerator");
        both(ModBlocks.ELEKTRISK_OVN.get().getDescriptionId(), "Electric Furnace", "Elektrisk ovn");
        both(ModBlocks.KNUSER.get().getDescriptionId(), "Crusher", "Knuser");
        both(ModBlocks.SKYGGEINFUSER.get().getDescriptionId(), "Shadow Infuser", "Skyggeinfuser");
        both(ModBlocks.INNHOSTER.get().getDescriptionId(), "Harvester", "Innhøster");
        both(ModBlocks.UTTREKKER.get().getDescriptionId(), "Item Mover", "Uttrekker");
        both(ModBlocks.KRAFTKABEL.get().getDescriptionId(), "Power Cable", "Kraftkabel");
        both(ModBlocks.KOBBEROVN.get().getDescriptionId(), "Copper Furnace", "Kobberovn");
        both(ModBlocks.JERNOVN.get().getDescriptionId(), "Iron Furnace", "Jernovn");
        both(ModBlocks.DIAMANTOVN.get().getDescriptionId(), "Diamond Furnace", "Diamantovn");
        both(ModBlocks.OPPGRADERBAR_KISTE.get().getDescriptionId(), "Upgradable Chest", "Oppgraderbar kiste");
        both(ModBlocks.RITUALALTER.get().getDescriptionId(), "Ritual Altar", "Ritualalter");
        both(ModBlocks.PIDESTALL.get().getDescriptionId(), "Pedestal", "Pidestall");
        both(ModBlocks.SKYGGEPORTAL.get().getDescriptionId(), "Shadow Portal", "Skyggeportal");
        both(ModBlocks.SKYGGEMALM.get().getDescriptionId(), "Shadow Ore", "Skyggemalm");
        both(ModBlocks.SKYGGESTEIN.get().getDescriptionId(), "Shadowstone", "Skyggestein");

        both(ModItems.SKYGGESTOV.get().getDescriptionId(), "Shadow Dust", "Skyggestøv");
        both(ModItems.SKYGGESKAAR.get().getDescriptionId(), "Shadow Shard", "Skyggeskår");
        both(ModItems.LADET_SKYGGESKAAR.get().getDescriptionId(), "Charged Shadow Shard", "Ladet skyggeskår");
        both(ModItems.SKYGGEKJERNE.get().getDescriptionId(), "Shadow Core", "Skyggekjerne");
        both(ModItems.SKYGGESTAAL_BARRE.get().getDescriptionId(), "Shadow Steel Ingot", "Skyggestålbarre");
        both(ModItems.MASKINKJERNE.get().getDescriptionId(), "Machine Core", "Maskinkjerne");
        both(ModItems.KNUST_JERN.get().getDescriptionId(), "Crushed Iron", "Knust jern");
        both(ModItems.KNUST_GULL.get().getDescriptionId(), "Crushed Gold", "Knust gull");
        both(ModItems.KNUST_KOBBER.get().getDescriptionId(), "Crushed Copper", "Knust kobber");
        both(ModItems.FARTSOPPGRADERING.get().getDescriptionId(), "Speed Upgrade", "Fartsoppgradering");
        both(ModItems.PARALLELLOPPGRADERING.get().getDescriptionId(), "Parallel Upgrade", "Parallelloppgradering");
        both(ModItems.KISTEOPPGRADERING_JERN.get().getDescriptionId(), "Iron Chest Upgrade", "Kisteoppgradering (jern)");
        both(ModItems.KISTEOPPGRADERING_GULL.get().getDescriptionId(), "Gold Chest Upgrade", "Kisteoppgradering (gull)");
        both(ModItems.KISTEOPPGRADERING_DIAMANT.get().getDescriptionId(), "Diamond Chest Upgrade", "Kisteoppgradering (diamant)");
        both(ModItems.STABELOPPGRADERING.get().getDescriptionId(), "Stack Upgrade", "Stabeloppgradering");
        both(ModItems.GJENKALLINGSAMULETT.get().getDescriptionId(), "Recall Amulet", "Gjenkallingsamulett");
        both(ModItems.SKYGGESTAAL_SVERD.get().getDescriptionId(), "Shadow Steel Sword", "Skyggestålsverd");
        both(ModItems.SKYGGESTAAL_HAKKE.get().getDescriptionId(), "Shadow Steel Pickaxe", "Skyggestålhakke");
        both(ModItems.SKYGGESTAAL_OKS.get().getDescriptionId(), "Shadow Steel Axe", "Skyggeståløks");
        both(ModItems.SKYGGESTAAL_SPADE.get().getDescriptionId(), "Shadow Steel Shovel", "Skyggestålspade");
        both(ModItems.SKYGGESTAAL_GREIP.get().getDescriptionId(), "Shadow Steel Hoe", "Skyggestålgreip");
        both(ModItems.SKYGGESTAAL_HJELM.get().getDescriptionId(), "Shadow Steel Helmet", "Skyggestålhjelm");
        both(ModItems.SKYGGESTAAL_BRYNJE.get().getDescriptionId(), "Shadow Steel Chestplate", "Skyggestålbrynje");
        both(ModItems.SKYGGESTAAL_BUKSER.get().getDescriptionId(), "Shadow Steel Leggings", "Skyggestålbukser");
        both(ModItems.SKYGGESTAAL_STOVLER.get().getDescriptionId(), "Shadow Steel Boots", "Skyggestålstøvler");
        both(ModItems.SKYGGEVOKTER_SPAWN_EGG.get().getDescriptionId(), "Shadow Warden Spawn Egg", "Skyggevokter-spawnegg");

        both("entity.svartmagi.skyggevokter", "Shadow Warden", "Skyggevokteren");

        both("key.categories.svartmagi", "Svartmagi", "Svartmagi");
        both("key.svartmagi.veinmine", "Vein Mine (hold)", "Åregraving (hold)");

        both("message.svartmagi.upgrade_not_supported", "This upgrade does not fit here", "Denne oppgraderingen passer ikke her");
        both("message.svartmagi.upgrade_full", "No more upgrades of this type fit", "Det er ikke plass til flere slike oppgraderinger");
        both("message.svartmagi.upgrade_installed", "Upgrade installed", "Oppgradering installert");
        both("message.svartmagi.chest_wrong_tier", "Install the previous tier first", "Installer forrige nivå først");
        both("message.svartmagi.magic_disabled", "The magic pillar is disabled on this server", "Magi-pilaren er skrudd av på denne serveren");
        both("message.svartmagi.altar_empty", "Place a center item on the altar first", "Legg et senter-item på alteret først");
        both("message.svartmagi.ritual_no_match", "The ritual fizzles... wrong ingredients?", "Ritualet svikter... feil ingredienser?");
        both("message.svartmagi.portal_opened", "A shadow portal tears open above the altar!", "En skyggeportal rives opp over alteret!");
        both("message.svartmagi.portal_blocked", "The portal needs open space two blocks above the altar", "Portalen trenger åpen plass to blokker over alteret");
        both("message.svartmagi.recalled", "You are pulled home through the shadows", "Du trekkes hjem gjennom skyggene");

        both("tooltip.svartmagi.upgrade_hint", "Right-click a machine or chest to install", "Høyreklikk en maskin eller kiste for å installere");
        both("tooltip.svartmagi.gjenkallingsamulett", "Teleports you home (or to spawn)", "Teleporterer deg hjem (eller til spawn)");

        both("command.svartmagi.disabled", "Svartmagi commands are disabled on this server", "Svartmagi-kommandoer er skrudd av på denne serveren");
        both("command.svartmagi.tpa_self", "You cannot teleport to yourself", "Du kan ikke teleportere til deg selv");
        both("command.svartmagi.tpa_sent", "Teleport request sent to %s", "Teleportforespørsel sendt til %s");
        both("command.svartmagi.tpa_received", "%s wants to teleport to you. /tpaccept or /tpdeny", "%s vil teleportere til deg. /tpaccept eller /tpdeny");
        both("command.svartmagi.tpahere_received", "%s wants you to teleport to them. /tpaccept or /tpdeny", "%s vil at du skal teleportere til dem. /tpaccept eller /tpdeny");
        both("command.svartmagi.tpa_none", "No pending teleport request", "Ingen ventende teleportforespørsel");
        both("command.svartmagi.tpa_offline", "That player is no longer online", "Spilleren er ikke lenger pålogget");
        both("command.svartmagi.tpa_accepted", "Teleport request accepted", "Teleportforespørsel akseptert");
        both("command.svartmagi.tpa_denied", "Teleport request denied", "Teleportforespørsel avslått");
        both("command.svartmagi.tpa_denied_sender", "%s denied your teleport request", "%s avslo teleportforespørselen din");
        both("command.svartmagi.rtp_cooldown", "You must wait %s more seconds before /rtp", "Du må vente %s sekunder til før /rtp");
        both("command.svartmagi.rtp_success", "Teleported to %s, %s, %s", "Teleportert til %s, %s, %s");
        both("command.svartmagi.rtp_failed", "Could not find a safe spot, try again", "Fant ikke et trygt sted, prøv igjen");
        both("command.svartmagi.spawn_teleported", "Teleported to spawn", "Teleportert til spawn");
        both("command.svartmagi.setspawn_done", "Server spawn set to your position", "Server-spawn satt til posisjonen din");
        both("command.svartmagi.home_limit", "You can have at most %s homes", "Du kan ha maks %s homes");
        both("command.svartmagi.sethome_done", "Home '%s' set", "Home '%s' satt");
        both("command.svartmagi.home_unknown", "Unknown home '%s'", "Ukjent home '%s'");
        both("command.svartmagi.home_teleported", "Teleported to home '%s'", "Teleportert til home '%s'");
        both("command.svartmagi.delhome_done", "Home '%s' deleted", "Home '%s' slettet");
        both("command.svartmagi.homes_none", "You have no homes. Use /sethome", "Du har ingen homes. Bruk /sethome");
        both("command.svartmagi.homes_list", "Your homes: %s", "Dine homes: %s");
        both("command.svartmagi.back_none", "No position to go back to", "Ingen posisjon å gå tilbake til");
        both("command.svartmagi.back_teleported", "Teleported back", "Teleportert tilbake");
        both("command.svartmagi.invsee_title", "Inventory: %s", "Inventar: %s");

        both("jei.svartmagi.crushing", "Crushing", "Knusing");
        both("jei.svartmagi.infusing", "Shadow Infusing", "Skyggeinfusering");
        both("jei.svartmagi.ritual", "Ritual", "Ritual");
        both("jei.svartmagi.ritual.outcome.item", "Result appears above the altar", "Resultatet dukker opp over alteret");
        both("jei.svartmagi.ritual.outcome.portal", "Opens a shadow portal", "Åpner en skyggeportal");
        both("jei.svartmagi.ritual.outcome.summon_boss", "Summons the Shadow Warden", "Påkaller Skyggevokteren");
    }
}
