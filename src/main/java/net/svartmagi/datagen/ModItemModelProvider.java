package net.svartmagi.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.svartmagi.Svartmagi;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Svartmagi.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(Svartmagi.id("skyggestov"));
        basicItem(Svartmagi.id("skyggeskaar"));
        basicItem(Svartmagi.id("ladet_skyggeskaar"));
        basicItem(Svartmagi.id("skyggekjerne"));
        basicItem(Svartmagi.id("skyggestaal_barre"));
        basicItem(Svartmagi.id("maskinkjerne"));
        basicItem(Svartmagi.id("knust_jern"));
        basicItem(Svartmagi.id("knust_gull"));
        basicItem(Svartmagi.id("knust_kobber"));
        basicItem(Svartmagi.id("fartsoppgradering"));
        basicItem(Svartmagi.id("parallelloppgradering"));
        basicItem(Svartmagi.id("kisteoppgradering_jern"));
        basicItem(Svartmagi.id("kisteoppgradering_gull"));
        basicItem(Svartmagi.id("kisteoppgradering_diamant"));
        basicItem(Svartmagi.id("stabeloppgradering"));
        basicItem(Svartmagi.id("gjenkallingsamulett"));

        handheldItem("skyggestaal_sverd");
        handheldItem("skyggestaal_hakke");
        handheldItem("skyggestaal_oks");
        handheldItem("skyggestaal_spade");
        handheldItem("skyggestaal_greip");

        basicItem(Svartmagi.id("skyggestaal_hjelm"));
        basicItem(Svartmagi.id("skyggestaal_brynje"));
        basicItem(Svartmagi.id("skyggestaal_bukser"));
        basicItem(Svartmagi.id("skyggestaal_stovler"));

        withExistingParent("skyggevokter_spawn_egg", mcLoc("item/template_spawn_egg"));
    }

    private void handheldItem(String name) {
        withExistingParent(name, mcLoc("item/handheld"))
                .texture("layer0", modLoc("item/" + name));
    }
}
