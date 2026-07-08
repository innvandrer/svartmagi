package net.svartmagi.tech;

import net.svartmagi.registry.ModBlockEntities;

/**
 * Innhoester: holder aaker- og trefarmer i gang automatisk med strom.
 * Hoester modne avlinger (poteter/hvete/gulrot m.m.), replanter, og
 * feller trær (eik/gran/moerk eik m.m.) og replanter oppsamlede saplings.
 */
public class HarvesterBlock extends MachineBlock {
    public HarvesterBlock(Properties properties) {
        super(properties, () -> ModBlockEntities.INNHOSTER.get());
    }
}
