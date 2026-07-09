# Svartmagi

En NeoForge-mod for Minecraft **1.21.1** (Java 21, offisielle Mojang-mappings) bygget
**multiplayer-first** for en dedikert server med en liten vennegjeng.

Én sammenhengende progresjon gjennom fire pilarer – ikke fire separate systemer:

> **Tech** gir kraft til **magi**-ritualer → ritualet åpner portalen til **Skyggeverden**
> → bossmaterialet derfra gir **toppnivå verktøy og rustning** forbi netherite.

## Progresjonen (Fase 1 – kjerneloopen)

1. **Tech:** Lag en *maskinkjerne* (jern + redstone) → bygg *kullgenerator*, *knuser*
   (malmdobling: rå malm → 2x knust malm), *elektrisk ovn* og *skyggeinfuser*.
2. **Broen tech → magi:** Lag *skyggeskår* (ametyst + obsidian + kull) og lad det i
   skyggeinfuseren (krever FE) → *ladet skyggeskår*.
3. **Magi:** Bygg *ritualalter* + *pidestaller*. Ritual med ladet skyggeskår +
   enderperler + obsidian → **skyggeportal** åpnes over alteret.
4. **Skyggeverden:** Egen dimensjon (svevende øyer av skyggestein, evig natt).
   Grav *skyggemalm* → *skyggestøv*.
5. **Boss:** Ritual med ladet skyggeskår + skyggestøv + diamant → **Skyggevokteren**
   (boss med bossbar). Dropper *skyggekjernen*.
6. **Toppnivå gear:** Skyggekjerne + netherite + skyggestøv → *skyggestålbarrer* →
   verktøy og rustning ett hakk over netherite.

## Innhold

### Tech & automasjon
| Blokk | Hva den gjør |
|---|---|
| Kullgenerator | Brenner ovnsbrensel → FE, dytter til naboer |
| Elektrisk ovn | FE-drevet smelting (vanilla-oppskrifter), oppgraderbar |
| Knuser | Malmdobling + grus/sand-oppskrifter (data-drevet) |
| Skyggeinfuser | Lader items med energi – broen til magi-pilaren |
| Innhøster | Automatisk gårds- og treplantasje-drift (se under) |
| Uttrekker | Enkel item-transport: trekker bak, dytter foran, kan kjedes |
| Kobber-/jern-/diamantovn | Brenselovner som er 2x/3x/6x raskere enn vanilla, oppgraderbare |
| Kraftkabel | Aksebasert FE-relé – flytter strøm mellom maskiner over avstand |

- **Innhøsteren** høster modne avlinger (potet/hvete/gulrot m.m.), replanter dem,
  feller trær (eik/gran/mørk eik – alt i `#minecraft:logs`) og replanter saplings
  fra bufferen sin. Drives av FE. Området er konfigurerbart.
- **Maskinoppgraderinger:** *fartsoppgradering* (opptil 3) og *parallelloppgradering*
  (smelter/prosesserer flere items per syklus) installeres ved å høyreklikke maskinen.
  Gjelder også tier-ovnene (kobber/jern/diamant), ikke bare knuser/ovn/infuser.

### Lagring
- **Oppgraderbar kiste:** høyreklikk med *kisteoppgradering* jern → gull → diamant
  (3 → 6 rader), og *stabeloppgradering* som dobler stack-størrelsen per slot
  (64 → 128 → 256, konfigurerbart).

### Veinmining (FTB Ultimine-stil)
- Hold **`-tasten** (grave accent, kan endres i keybinds) mens du bryter en blokk i
  `#svartmagi:veinmineable` (alle malmer + stokker, data-drevet tag) → hele åren/treet
  brytes. Maks antall blokker, verktøykrav og sultkostnad er konfigurerbart.
- Nettverkspakke sendes **kun når tastetilstanden endres**, aldri per tick.

### Kommandoer (QoL)
`/tpa <spiller>`, `/tpahere <spiller>`, `/tpaccept`, `/tpdeny`, `/rtp`, `/spawn`,
`/setspawn` (op), `/sethome [navn]`, `/home [navn]`, `/delhome [navn]`, `/homes`,
`/back`, `/invsee <spiller>` (op). Vanilla `/tp` finnes allerede for ops.
Homes/spawn lagres persistent per verden (`SavedData`).

### Magiske gjenstander
- **Gjenkallingsamulett** (ritual-resultat): teleporterer deg hjem, 60 s cooldown.

## Ytelse og server-trygghet

- **Alt config-toggleable per pilar** i `config/svartmagi-server.toml`
  (hot-reloades – admin kan endre uten world-restart).
- **Data-drevet:** oppskrifter (inkl. knusing/infusering/ritualer), loot tables,
  tags, worldgen og dimensjonen er JSON (generert med datagen, ikke håndskrevet).
- **Ingen global skanning:** maskiner ticker kun sin egen blockentity, innhøsteren
  bruker en markør med et begrenset antall sjekker per intervall, ritualer og
  portaler er 100 % event-drevet, oppskrifts-oppslag caches til input endres.
- **Nettverk:** maskin-GUI-data syncers kun via `ContainerData` mens menyen er åpen;
  blockentity-sync skjer kun ved statusendring (LIT-blockstate, oppgraderinger).
- Nabo-capabilities caches med `BlockCapabilityCache`.
- Server-trygt: ingen client-only-antagelser; klientkode er isolert i egne klasser.

## JEI

Knusing, infusering og ritualer har egne JEI-kategorier (JEI er optional dependency).

## Bygging og kjøring

```bash
./gradlew build          # jar i build/libs/
./gradlew runServer      # dedikert utviklingsserver
./gradlew runClient      # utviklingsklient
./gradlew runData        # regenerer datagen-output (src/generated/resources)
```

Krever Java 21. Samme mod + versjon må ligge i `mods/` på både server og klient.
NeoForge-server (Paper/Bukkit er ikke kompatibelt).

## Struktur

- `net.svartmagi.tech` – maskiner, energi, innhøster, transport
- `net.svartmagi.magic` – alter, pidestaller, ritualer, portal, dimensjon
- `net.svartmagi.storage` – oppgraderbar kiste
- `net.svartmagi.command` – QoL-kommandoer + persistente homes
- `net.svartmagi.veinmine` – åregraving (keybind + serverlogikk)
- `net.svartmagi.recipe` – data-drevne oppskriftstyper
- `net.svartmagi.datagen` – recipes/loot/tags/modeller/språk (en_us + nb_no)
- `net.svartmagi.integration.jei` – JEI-kategorier
- `src/main/resources/data/svartmagi/` – dimensjon, biome, worldgen (JSON)
- `tools/generate_textures.py` – regenererer placeholder-teksturene

## Faseplan

- **Fase 1 (denne):** komplett kjerneloop gjennom alle pilarene ✅
- **Fase 2:** flere tiers (kraftlagring, flere maskiner, flere ritualer, flere
  dimensjonsbiomer, flere bosser)
- **Fase 3:** balansering, config-finpuss og ytelsestest med reell spillergruppe
