#!/usr/bin/env python3
"""Genererer alle teksturer for Svartmagi-modden (enkel, konsistent pixel-art)."""
import os
import random
from PIL import Image, ImageDraw

ROOT = os.path.join(os.path.dirname(__file__), "..", "src", "main", "resources", "assets", "svartmagi", "textures")
random.seed(42)


def save(img, *path):
    p = os.path.join(ROOT, *path)
    os.makedirs(os.path.dirname(p), exist_ok=True)
    img.save(p)


def noise_tile(base, variation=12, size=16):
    img = Image.new("RGBA", (size, size))
    px = img.load()
    for y in range(size):
        for x in range(size):
            v = random.randint(-variation, variation)
            px[x, y] = tuple(max(0, min(255, c + v)) for c in base[:3]) + (255,)
    return img


def add_border(img, color, width=1):
    d = ImageDraw.Draw(img)
    w, h = img.size
    for i in range(width):
        d.rectangle([i, i, w - 1 - i, h - 1 - i], outline=color + (255,))
    return img


def speckle(img, color, count=8):
    px = img.load()
    for _ in range(count):
        x, y = random.randint(1, 14), random.randint(1, 14)
        px[x, y] = color + (255,)
    return img


# --- Palett ---
STEEL = (70, 72, 80)
STEEL_DARK = (48, 50, 58)
COPPER = (160, 95, 60)
IRON = (150, 150, 155)
GOLD = (200, 170, 60)
DIAMOND = (95, 200, 195)
STONE = (110, 110, 112)
WOOD = (120, 90, 55)
WOOD_DARK = (85, 62, 38)
SHADOW = (24, 22, 34)
PURPLE = (122, 60, 200)
PURPLE_BRIGHT = (170, 110, 255)
BLACKSTONE = (38, 34, 40)


def machine_side(tint):
    img = noise_tile(STEEL)
    add_border(img, STEEL_DARK)
    d = ImageDraw.Draw(img)
    d.rectangle([3, 3, 12, 12], outline=tint + (255,))
    return img


def machine_top(tint):
    img = noise_tile(STEEL_DARK)
    add_border(img, STEEL_DARK)
    d = ImageDraw.Draw(img)
    d.rectangle([4, 4, 11, 11], fill=tint + (255,))
    return img


def machine_front(tint, on=False):
    img = noise_tile(STEEL)
    add_border(img, STEEL_DARK)
    d = ImageDraw.Draw(img)
    d.rectangle([3, 3, 12, 12], outline=STEEL_DARK + (255,))
    core = tuple(min(255, c + 70) for c in tint) if on else tint
    d.rectangle([5, 5, 10, 10], fill=core + (255,))
    if on:
        d.rectangle([6, 6, 9, 9], fill=(255, 240, 180, 255) if tint == (230, 120, 40) else
                    tuple(min(255, c + 120) for c in tint) + (255,))
    return img


MACHINES = {
    "kullgenerator": (230, 120, 40),
    "elektrisk_ovn": (220, 60, 50),
    "knuser": (140, 140, 60),
    "skyggeinfuser": PURPLE,
    "innhoster": (80, 170, 70),
    "kobberovn": COPPER,
    "jernovn": IRON,
    "diamantovn": DIAMOND,
}

for name, tint in MACHINES.items():
    save(machine_side(tint), "block", f"{name}_side.png")
    save(machine_top(tint), "block", f"{name}_top.png")
    save(machine_front(tint, False), "block", f"{name}_front.png")
    save(machine_front(tint, True), "block", f"{name}_front_on.png")

# Uttrekker
img = noise_tile(STEEL_DARK)
add_border(img, (30, 30, 36))
save(img, "block", "uttrekker_side.png")
img = noise_tile(STEEL_DARK)
add_border(img, (30, 30, 36))
d = ImageDraw.Draw(img)
d.polygon([(4, 11), (8, 4), (12, 11)], fill=(220, 200, 90, 255))
save(img, "block", "uttrekker_front.png")

# Kiste
img = noise_tile(WOOD)
add_border(img, WOOD_DARK)
d = ImageDraw.Draw(img)
d.rectangle([2, 7, 13, 8], fill=WOOD_DARK + (255,))
d.rectangle([7, 6, 8, 9], fill=(200, 180, 90, 255))
save(img, "block", "oppgraderbar_kiste.png")

# Skyggemalm / skyggestein / portal
img = noise_tile(SHADOW, 8)
speckle(img, PURPLE_BRIGHT, 10)
speckle(img, PURPLE, 8)
save(img, "block", "skyggemalm.png")

save(noise_tile(SHADOW, 10), "block", "skyggestein.png")

img = noise_tile((40, 15, 70), 25)
speckle(img, PURPLE_BRIGHT, 14)
save(img, "block", "skyggeportal.png")

# Alter / pidestall
img = noise_tile(BLACKSTONE, 8)
add_border(img, (20, 18, 24))
save(img, "block", "ritualalter_side.png")
save(noise_tile((28, 26, 32), 6), "block", "ritualalter_bottom.png")
img = noise_tile(BLACKSTONE, 8)
d = ImageDraw.Draw(img)
d.rectangle([3, 3, 12, 12], outline=PURPLE + (255,))
d.rectangle([6, 6, 9, 9], fill=PURPLE_BRIGHT + (255,))
save(img, "block", "ritualalter_top.png")

img = noise_tile(BLACKSTONE, 8)
d = ImageDraw.Draw(img)
d.rectangle([0, 0, 15, 1], fill=(55, 50, 60, 255))
d.rectangle([0, 14, 15, 15], fill=(55, 50, 60, 255))
d.rectangle([5, 2, 10, 13], fill=(45, 42, 50, 255))
save(img, "block", "pidestall_side.png")
img = noise_tile((50, 46, 55), 6)
add_border(img, (30, 28, 34))
save(img, "block", "pidestall_top.png")


# --- Items ---
def item_canvas():
    return Image.new("RGBA", (16, 16), (0, 0, 0, 0))


def dust(colors):
    img = item_canvas()
    px = img.load()
    for _ in range(46):
        x = random.randint(3, 12)
        y = random.randint(7, 13)
        px[x, y] = random.choice(colors) + (255,)
    return img


def crystal(base, glow):
    img = item_canvas()
    d = ImageDraw.Draw(img)
    d.polygon([(8, 1), (12, 6), (10, 14), (6, 14), (4, 6)], fill=base + (255,), outline=glow + (255,))
    d.line([(8, 3), (8, 12)], fill=glow + (255,))
    return img


def ingot(base, hi):
    img = item_canvas()
    d = ImageDraw.Draw(img)
    d.polygon([(2, 9), (7, 5), (14, 8), (9, 12)], fill=base + (255,))
    d.line([(2, 9), (7, 5), (14, 8)], fill=hi + (255,))
    return img


def orb(base, glow):
    img = item_canvas()
    d = ImageDraw.Draw(img)
    d.ellipse([3, 3, 12, 12], fill=base + (255,), outline=(10, 8, 14, 255))
    d.ellipse([6, 6, 9, 9], fill=glow + (255,))
    d.point((5, 5), (255, 255, 255, 255))
    return img


def upgrade_chip(accent):
    img = item_canvas()
    d = ImageDraw.Draw(img)
    d.rectangle([3, 3, 12, 12], fill=(35, 60, 40, 255), outline=(20, 35, 25, 255))
    d.rectangle([6, 6, 9, 9], fill=accent + (255,))
    for x in (4, 7, 10):
        d.line([(x, 1), (x, 3)], fill=(180, 180, 190, 255))
        d.line([(x, 12), (x, 14)], fill=(180, 180, 190, 255))
    return img


save(dust([SHADOW, PURPLE, (60, 50, 90)]), "item", "skyggestov.png")
save(crystal((40, 30, 70), PURPLE), "item", "skyggeskaar.png")
save(crystal((90, 50, 160), PURPLE_BRIGHT), "item", "ladet_skyggeskaar.png")
save(orb(SHADOW, PURPLE_BRIGHT), "item", "skyggekjerne.png")
save(ingot((60, 55, 85), (140, 120, 210)), "item", "skyggestaal_barre.png")
save(dust([(120, 120, 128), (150, 150, 155), (90, 90, 95)]), "item", "knust_jern.png")
save(dust([(180, 150, 50), (200, 170, 60), (140, 115, 40)]), "item", "knust_gull.png")
save(dust([(150, 90, 55), (170, 105, 65), (120, 70, 45)]), "item", "knust_kobber.png")

img = item_canvas()
d = ImageDraw.Draw(img)
d.rectangle([2, 2, 13, 13], fill=STEEL + (255,), outline=STEEL_DARK + (255,))
d.rectangle([5, 5, 10, 10], fill=(200, 60, 50, 255))
d.rectangle([7, 7, 8, 8], fill=(255, 170, 150, 255))
save(img, "item", "maskinkjerne.png")

save(upgrade_chip((250, 220, 80)), "item", "fartsoppgradering.png")
save(upgrade_chip((120, 200, 240)), "item", "parallelloppgradering.png")
save(upgrade_chip(IRON), "item", "kisteoppgradering_jern.png")
save(upgrade_chip(GOLD), "item", "kisteoppgradering_gull.png")
save(upgrade_chip(DIAMOND), "item", "kisteoppgradering_diamant.png")
save(upgrade_chip(PURPLE_BRIGHT), "item", "stabeloppgradering.png")

# Gjenkallingsamulett
img = item_canvas()
d = ImageDraw.Draw(img)
d.arc([4, 1, 11, 8], 180, 360, fill=GOLD + (255,))
d.line([(4, 5), (4, 8)], fill=GOLD + (255,))
d.line([(11, 5), (11, 8)], fill=GOLD + (255,))
d.ellipse([5, 7, 10, 12], fill=PURPLE + (255,), outline=GOLD + (255,))
d.point((7, 9), (240, 220, 255, 255))
save(img, "item", "gjenkallingsamulett.png")


# --- Verktoy (pixel-art fra ASCII) ---
def from_ascii(rows, palette):
    img = item_canvas()
    px = img.load()
    for y, row in enumerate(rows):
        for x, ch in enumerate(row):
            if ch != ".":
                px[x, y] = palette[ch] + (255,)
    return img


TOOL_PAL = {
    "B": (60, 55, 85),      # skyggestaal
    "b": (110, 95, 170),    # highlight
    "S": (110, 84, 50),     # skaft
    "s": (80, 60, 36),
    "P": PURPLE_BRIGHT,
}

SWORD = [
    "..........bB....",
    ".........bBB....",
    "........bBB.....",
    ".......bBB......",
    "......bBB.......",
    ".....bBB........",
    "....bBB.........",
    "...PBB..........",
    "..sPs...........",
    ".sSs............",
    "sSs.............",
    "Ss..............",
    "................",
    "................",
    "................",
    "................",
]

PICKAXE = [
    "....bBBBBBb.....",
    "...bBB...BBb....",
    "..bB......BBb...",
    "..B........BB...",
    ".........sB.....",
    "........sS......",
    ".......sS.......",
    "......sS........",
    ".....sS.........",
    "....sS..........",
    "...sS...........",
    "..sS............",
    ".sS.............",
    "sS..............",
    "................",
    "................",
]

AXE = [
    ".....bBBb.......",
    "....bBBBBb......",
    "...bBBBBBB......",
    "...BBBsBBB......",
    "...bBBSs........",
    "....bsSs........",
    ".....sS.........",
    "....sS..........",
    "...sS...........",
    "..sS............",
    ".sS.............",
    "sS..............",
    "................",
    "................",
    "................",
    "................",
]

SHOVEL = [
    ".......bBb......",
    "......bBBBb.....",
    "......BBBBB.....",
    "......bBBBb.....",
    ".......sBs......",
    "......sS........",
    ".....sS.........",
    "....sS..........",
    "...sS...........",
    "..sS............",
    ".sS.............",
    "sS..............",
    "................",
    "................",
    "................",
    "................",
]

HOE = [
    "....bBBBBb......",
    "....bB..........",
    ".....B..........",
    ".....sB.........",
    "....sS..........",
    "...sS...........",
    "..sS............",
    ".sS.............",
    "sS..............",
    "................",
    "................",
    "................",
    "................",
    "................",
    "................",
    "................",
]

save(from_ascii(SWORD, TOOL_PAL), "item", "skyggestaal_sverd.png")
save(from_ascii(PICKAXE, TOOL_PAL), "item", "skyggestaal_hakke.png")
save(from_ascii(AXE, TOOL_PAL), "item", "skyggestaal_oks.png")
save(from_ascii(SHOVEL, TOOL_PAL), "item", "skyggestaal_spade.png")
save(from_ascii(HOE, TOOL_PAL), "item", "skyggestaal_greip.png")


# --- Rustning-items ---
def helmet():
    img = item_canvas()
    d = ImageDraw.Draw(img)
    d.rectangle([3, 5, 12, 11], fill=(60, 55, 85, 255), outline=(110, 95, 170, 255))
    d.rectangle([3, 2, 12, 5], fill=(60, 55, 85, 255))
    d.rectangle([5, 8, 6, 10], fill=(20, 18, 28, 255))
    d.rectangle([9, 8, 10, 10], fill=(20, 18, 28, 255))
    return img


def chestplate():
    img = item_canvas()
    d = ImageDraw.Draw(img)
    d.rectangle([4, 3, 11, 13], fill=(60, 55, 85, 255), outline=(110, 95, 170, 255))
    d.rectangle([2, 3, 4, 7], fill=(60, 55, 85, 255))
    d.rectangle([11, 3, 13, 7], fill=(60, 55, 85, 255))
    d.point((7, 7), PURPLE_BRIGHT + (255,))
    d.point((8, 7), PURPLE_BRIGHT + (255,))
    return img


def leggings():
    img = item_canvas()
    d = ImageDraw.Draw(img)
    d.rectangle([4, 2, 11, 5], fill=(60, 55, 85, 255), outline=(110, 95, 170, 255))
    d.rectangle([4, 5, 6, 13], fill=(60, 55, 85, 255))
    d.rectangle([9, 5, 11, 13], fill=(60, 55, 85, 255))
    return img


def boots():
    img = item_canvas()
    d = ImageDraw.Draw(img)
    d.rectangle([3, 7, 6, 12], fill=(60, 55, 85, 255))
    d.rectangle([3, 11, 8, 12], fill=(60, 55, 85, 255))
    d.rectangle([9, 7, 12, 12], fill=(60, 55, 85, 255))
    d.rectangle([9, 11, 14, 12], fill=(60, 55, 85, 255))
    return img


save(helmet(), "item", "skyggestaal_hjelm.png")
save(chestplate(), "item", "skyggestaal_brynje.png")
save(leggings(), "item", "skyggestaal_bukser.png")
save(boots(), "item", "skyggestaal_stovler.png")

# --- Rustning-layers (64x32, enkel fylt maskering over hele) ---
for layer in (1, 2):
    img = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
    px = img.load()
    # Fyll standard humanoid-armor-omraader med skyggestaal-farge + noise
    base = (60, 55, 85)
    regions_l1 = [(0, 0, 32, 16), (32, 16, 64, 32), (0, 16, 24, 32), (40, 0, 56, 16)]
    regions_l2 = [(0, 16, 24, 32), (16, 0, 40, 16)]
    for (x0, y0, x1, y1) in (regions_l1 if layer == 1 else regions_l2):
        for y in range(y0, y1):
            for x in range(x0, x1):
                v = random.randint(-10, 10)
                px[x, y] = tuple(max(0, min(255, c + v)) for c in base) + (255,)
    save(img, "models", "armor", f"skyggestaal_layer_{layer}.png")

# --- Entity: Skyggevokter (zombie-layout 64x64) ---
img = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
px = img.load()
BODY = SHADOW
def fill_region(x0, y0, x1, y1, base, var=8):
    for y in range(y0, y1):
        for x in range(x0, x1):
            v = random.randint(-var, var)
            px[x, y] = tuple(max(0, min(255, c + v)) for c in base) + (255,)

# Hode (0,0)-(32,16), kropp (16,16)-(40,32), armer/bein-regioner
fill_region(0, 0, 32, 16, BODY)
fill_region(16, 16, 40, 32, BODY)
fill_region(40, 16, 56, 32, (30, 27, 42))   # hoyre arm
fill_region(0, 16, 16, 32, (30, 27, 42))    # hoyre bein
fill_region(16, 48, 48, 64, (30, 27, 42))   # venstre bein/arm (1.8-layout)
fill_region(32, 48, 64, 64, (30, 27, 42))
# Lysende oyne paa hodets front (8,8)-(16,16)
px[10, 11] = PURPLE_BRIGHT + (255,)
px[11, 11] = PURPLE_BRIGHT + (255,)
px[13, 11] = PURPLE_BRIGHT + (255,)
px[14, 11] = PURPLE_BRIGHT + (255,)
save(img, "entity", "skyggevokter.png")


# --- GUI-teksturer (256x256, panel 176x166) ---
def gui_base():
    img = Image.new("RGBA", (256, 256), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.rectangle([0, 0, 175, 165], fill=(198, 198, 198, 255))
    d.rectangle([0, 0, 175, 165], outline=(0, 0, 0, 255))
    d.line([(1, 1), (174, 1)], fill=(255, 255, 255, 255))
    d.line([(1, 1), (1, 164)], fill=(255, 255, 255, 255))
    d.line([(1, 164), (174, 164)], fill=(85, 85, 85, 255))
    d.line([(174, 1), (174, 164)], fill=(85, 85, 85, 255))
    return img, d


def slot(d, x, y):
    """Slot med topp-venstre paa (x-1, y-1), 18x18 (vanilla-stil)."""
    d.rectangle([x - 1, y - 1, x + 16, y + 16], fill=(139, 139, 139, 255))
    d.line([(x - 1, y - 1), (x + 16, y - 1)], fill=(55, 55, 55, 255))
    d.line([(x - 1, y - 1), (x - 1, y + 16)], fill=(55, 55, 55, 255))
    d.line([(x - 1, y + 16), (x + 16, y + 16)], fill=(255, 255, 255, 255))
    d.line([(x + 16, y - 1), (x + 16, y + 16)], fill=(255, 255, 255, 255))


def player_inv(d, y0=84):
    for row in range(3):
        for col in range(9):
            slot(d, 8 + col * 18, y0 + row * 18)
    for col in range(9):
        slot(d, 8 + col * 18, y0 + 58)


# processing.png: input (56,35), output (116,35), energibar-omraade
img, d = gui_base()
player_inv(d)
slot(d, 56, 35)
slot(d, 116, 35)
d.rectangle([9, 16, 19, 70], outline=(55, 55, 55, 255))
save(img, "gui", "processing.png")

# generator.png: brensel-slot (80,40)
img, d = gui_base()
player_inv(d)
slot(d, 80, 40)
d.rectangle([9, 16, 19, 70], outline=(55, 55, 55, 255))
save(img, "gui", "generator.png")

print("Teksturer generert i", os.path.abspath(ROOT))
