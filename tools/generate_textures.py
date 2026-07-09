#!/usr/bin/env python3
"""Genererer alle teksturer for Svartmagi-modden (konsistent pixel-art).

Prinsipper:
- Lyskilde oppe til venstre: lysere topp-/venstrekanter, moerkere bunn/hoyre
- Fargeramper med hue-shift: skygger trekkes mot blaalilla, hoylys mot varmgult
- Klynget stoy (2x2) i stedet for per-piksel statisk snoe
- Deterministisk (fast seed) saa regenerering gir identiske filer
"""
import math
import os
import random

from PIL import Image, ImageDraw

ROOT = os.path.join(os.path.dirname(__file__), "..", "src", "main", "resources", "assets", "svartmagi", "textures")
random.seed(42)


def save(img, *path):
    p = os.path.join(ROOT, *path)
    os.makedirs(os.path.dirname(p), exist_ok=True)
    img.save(p)


# --- Fargeverktoy -----------------------------------------------------------

def clamp(v):
    return max(0, min(255, int(v)))


def mul(c, f):
    return tuple(clamp(x * f) for x in c[:3])


def mix(a, b, t):
    return tuple(clamp(a[i] + (b[i] - a[i]) * t) for i in range(3))


COOL = (36, 30, 66)     # skygge-hue (blaalilla)
WARM = (255, 246, 214)  # hoylys-hue (varmgul)


def shadow_of(c):
    return mix(mul(c, 0.52), COOL, 0.30)


def dark_of(c):
    return mix(mul(c, 0.74), COOL, 0.14)


def light_of(c):
    return mix(mul(c, 1.22), WARM, 0.10)


def hi_of(c):
    return mix(mul(c, 1.45), WARM, 0.28)


# --- Palett -----------------------------------------------------------------

STEEL = (86, 89, 102)
COPPER = (176, 102, 62)
IRON = (158, 158, 166)
GOLD = (222, 182, 66)
DIAMOND = (98, 216, 206)
WOOD = (128, 96, 58)
SHADOW = (28, 25, 40)
SHADOWSTONE = (34, 31, 48)
PURPLE = (128, 64, 208)
PURPLE_BRIGHT = (186, 128, 255)
PURPLE_GLOW = (226, 190, 255)
BLACKSTONE = (44, 40, 52)
SKYGGESTAAL = (72, 64, 104)


# --- Flate-verktoy ----------------------------------------------------------

def textured_tile(base, var=7, size=16, cluster=2):
    """Flate med klynget verdivariasjon - roligere enn per-piksel stoy."""
    img = Image.new("RGBA", (size, size))
    px = img.load()
    for by in range(0, size, cluster):
        for bx in range(0, size, cluster):
            v = random.randint(-var, var)
            for y in range(by, min(by + cluster, size)):
                for x in range(bx, min(bx + cluster, size)):
                    vv = v + random.randint(-2, 2)
                    px[x, y] = tuple(clamp(c + vv) for c in base[:3]) + (255,)
    return img


def vgrad(img, strength=14):
    """Vertikal gradient: lysere oeverst (lys ovenfra)."""
    px = img.load()
    w, h = img.size
    for y in range(h):
        add = int(strength * (1 - 2 * y / (h - 1)))
        for x in range(w):
            r, g, b, a = px[x, y]
            px[x, y] = (clamp(r + add), clamp(g + add), clamp(b + add), a)
    return img


def bevel(img, base):
    """1px ramme: topp/venstre lys, bunn/hoyre skygge."""
    d = ImageDraw.Draw(img)
    w, h = img.size
    lt = light_of(base) + (255,)
    dk = shadow_of(base) + (255,)
    d.line([(0, 0), (w - 1, 0)], fill=lt)
    d.line([(0, 0), (0, h - 1)], fill=lt)
    d.line([(0, h - 1), (w - 1, h - 1)], fill=dk)
    d.line([(w - 1, 0), (w - 1, h - 1)], fill=dk)
    return img


def rivets(img, base, positions=((2, 2), (13, 2), (2, 13), (13, 13))):
    px = img.load()
    for (x, y) in positions:
        px[x, y] = dark_of(base) + (255,)
        px[x - 1, y - 1] = hi_of(base) + (255,)
    return img


def cracks(img, color, count=3):
    """Korte, sammenhengende sprekker i stedet for enkeltpiksel-prikker."""
    px = img.load()
    for _ in range(count):
        x, y = random.randint(2, 12), random.randint(2, 12)
        length = random.randint(3, 5)
        for _ in range(length):
            px[x, y] = color + (255,)
            x += random.choice((-1, 0, 1))
            y += random.choice((0, 1))
            x = max(1, min(14, x))
            y = max(1, min(14, y))
    return img


# --- Maskinblokker (flat design, inspirert av Iron Furnaces) ----------------
# Iron Furnaces bruker rene flater: solid tier-farge, en diagonal "sheen"-
# stripe, og faa geometriske detaljer i stedet for stoy. Vi bruker samme
# tilnaerming her i stedet for tettpakket per-piksel-stoy.

def flat_fill(base, size=16):
    return Image.new("RGBA", (size, size), base + (255,))


def diagonal_sheen(img, offset=2, width=3, strength=30):
    """En myk diagonal lysstripe fra nede-venstre til oppe-hoyre."""
    px = img.load()
    w, h = img.size
    for y in range(h):
        for x in range(w):
            diff = (x - (h - 1 - y)) - offset
            if -width <= diff <= width:
                t = 1 - abs(diff) / (width + 1)
                r, g, b, a = px[x, y]
                add = int(strength * t)
                px[x, y] = (clamp(r + add), clamp(g + add), clamp(b + add), a)
    return img


def brushed_bands(base, band=2):
    """Horisontale lys/moerk-striper - brushed metal-topp (som Iron Furnaces)."""
    img = Image.new("RGBA", (16, 16))
    px = img.load()
    for y in range(16):
        tone = light_of(base) if (y // band) % 2 == 0 else dark_of(base)
        for x in range(16):
            px[x, y] = tone + (255,)
    return img


def machine_side(accent):
    img = flat_fill(STEEL)
    diagonal_sheen(img)
    bevel(img, STEEL)
    d = ImageDraw.Draw(img)
    # liten statuslampe - identifiserer maskinen fra siden
    d.rectangle([12, 2, 13, 3], fill=dark_of(accent) + (255,))
    d.point((12, 2), fill=accent + (255,))
    return img


def machine_top(accent):
    img = brushed_bands(dark_of(STEEL))
    bevel(img, dark_of(STEEL))
    d = ImageDraw.Draw(img)
    d.rectangle([6, 6, 9, 9], fill=dark_of(accent) + (255,))
    d.rectangle([7, 7, 8, 8], fill=accent + (255,))
    return img


def machine_front(accent, on):
    img = flat_fill(STEEL)
    diagonal_sheen(img)
    bevel(img, STEEL)
    d = ImageDraw.Draw(img)
    d.rounded_rectangle([3, 3, 12, 12], radius=2, fill=shadow_of(STEEL) + (255,))
    if on:
        d.rounded_rectangle([4, 4, 11, 11], radius=2, fill=accent + (255,))
        d.rounded_rectangle([5, 5, 10, 10], radius=1, fill=light_of(accent) + (255,))
        d.rectangle([7, 7, 8, 8], fill=hi_of(accent) + (255,))
    else:
        d.rounded_rectangle([4, 4, 11, 11], radius=2, fill=(24, 22, 30, 255))
        d.rectangle([7, 7, 8, 8], fill=dark_of(accent) + (255,))
    return img


TECH_MACHINES = {
    "kullgenerator": (235, 130, 44),
    "elektrisk_ovn": (226, 74, 58),
    "knuser": (168, 158, 66),
    "skyggeinfuser": PURPLE,
    "innhoster": (92, 186, 78),
}

for name, accent in TECH_MACHINES.items():
    save(machine_side(accent), "block", f"{name}_side.png")
    save(machine_top(accent), "block", f"{name}_top.png")
    save(machine_front(accent, False), "block", f"{name}_front.png")
    save(machine_front(accent, True), "block", f"{name}_front_on.png")


# --- Tier-ovner (flat design, direkte inspirert av Iron Furnaces) -----------
# Solid materialfarge + diagonal sheen paa sidene, brushed-metal topp, og
# to runde ventilspalter paa fronten der den nederste fylles med flamme.

def furnace_side(material):
    img = flat_fill(material)
    diagonal_sheen(img, strength=34)
    bevel(img, material)
    return img


def furnace_top(material):
    img = brushed_bands(material)
    bevel(img, material)
    return img


def draw_flame(d, x0, y0, x1, y1):
    cx = (x0 + x1) // 2
    d.polygon([(x0 + 1, y1), (cx, y0), (x1 - 1, y1)], fill=(255, 140, 30, 255))
    d.polygon([(x0 + 3, y1), (cx, y0 + 3), (x1 - 3, y1)], fill=(255, 214, 96, 255))
    d.point((cx, y0 + 1), fill=(255, 245, 200, 255))


def furnace_front(material, on):
    img = flat_fill(material)
    diagonal_sheen(img, strength=30)
    bevel(img, material)
    d = ImageDraw.Draw(img)
    dark = (20, 18, 24, 255)
    d.rounded_rectangle([3, 3, 12, 6], radius=1, fill=dark)   # oevre ventilspalte
    d.rounded_rectangle([3, 9, 12, 13], radius=1, fill=dark)  # nedre ovnsmunn
    if on:
        draw_flame(d, 4, 10, 11, 13)
    return img


FURNACES = {"kobberovn": COPPER, "jernovn": IRON, "diamantovn": DIAMOND}
for name, material in FURNACES.items():
    save(furnace_side(material), "block", f"{name}_side.png")
    save(furnace_top(material), "block", f"{name}_top.png")
    save(furnace_front(material, False), "block", f"{name}_front.png")
    save(furnace_front(material, True), "block", f"{name}_front_on.png")

# Uttrekker - samme flate stil som maskinene
img = flat_fill(dark_of(STEEL))
diagonal_sheen(img, strength=24)
bevel(img, dark_of(STEEL))
save(img, "block", "uttrekker_side.png")

img = flat_fill(dark_of(STEEL))
diagonal_sheen(img, strength=24)
bevel(img, dark_of(STEEL))
d = ImageDraw.Draw(img)
arrow = (236, 208, 96)
d.polygon([(3, 10), (8, 3), (12, 10)], fill=arrow + (255,))
d.polygon([(5, 10), (8, 6), (10, 10)], fill=dark_of(arrow) + (255,))
d.rectangle([6, 10, 9, 12], fill=arrow + (255,))
save(img, "block", "uttrekker_front.png")


# --- Kraftkabel (aksebasert, som en stokk) ----------------------------------
# Metallkappe med en glodende energi-kjerne synlig langs siden og som et
# rundt "uttak" i endene - lett gjenkjennelig som roer/kabel i verden.

CABLE_CORE = (255, 176, 64)


def cable_side():
    img = flat_fill(dark_of(STEEL))
    diagonal_sheen(img, strength=14)
    d = ImageDraw.Draw(img)
    d.rectangle([0, 0, 2, 15], fill=STEEL + (255,))
    d.rectangle([13, 0, 15, 15], fill=STEEL + (255,))
    d.rectangle([6, 0, 9, 15], fill=dark_of(CABLE_CORE) + (255,))
    d.rectangle([7, 0, 8, 15], fill=CABLE_CORE + (255,))
    for y in (1, 5, 9, 13):
        d.point((7, y), fill=light_of(CABLE_CORE) + (255,))
    return img


def cable_end():
    img = flat_fill(dark_of(STEEL))
    d = ImageDraw.Draw(img)
    d.ellipse([1, 1, 14, 14], fill=STEEL + (255,), outline=shadow_of(STEEL) + (255,))
    d.ellipse([4, 4, 11, 11], fill=(20, 18, 24, 255))
    d.ellipse([6, 6, 9, 9], fill=CABLE_CORE + (255,))
    d.point((6, 6), fill=light_of(CABLE_CORE) + (255,))
    return img


save(cable_side(), "block", "kraftkabel_side.png")
save(cable_end(), "block", "kraftkabel_end.png")


# --- Chunklaster -------------------------------------------------------------

def chunklaster_texture(active=False):
    img = flat_fill(dark_of(IRON))
    diagonal_sheen(img, strength=10)
    d = ImageDraw.Draw(img)
    grid = shadow_of(STEEL)
    for gx in range(2):
        for gy in range(2):
            x0, y0 = 3 + gx * 6, 3 + gy * 6
            d.rectangle([x0, y0, x0 + 4, y0 + 4], fill=grid + (255,), outline=STEEL + (255,))
    d.rectangle([6, 6, 9, 9], outline=STEEL + (255,))
    core = PURPLE_BRIGHT if active else dark_of(PURPLE)
    d.rectangle([7, 7, 8, 8], fill=core + (255,))
    if active:
        d.point((7, 7), fill=PURPLE_GLOW + (255,))
    return img


save(chunklaster_texture(False), "block", "chunklaster.png")
save(chunklaster_texture(True), "block", "chunklaster_on.png")


# --- Oppgraderbar kiste (inspirert av Sophisticated Storage) ---------------
# SS bygger tiered chests som: treverkskropp + metallhoop-baand +
# tier-fargede hjoernebeslag lagt paa som en overlay. Vi gjenskaper samme
# lagdelte prinsipp: samme trekropp for alle tiers, med hjoernebeslag som
# eneste ting som forteller tieren fra hverandre.

def corner_bracket(d, x, y, dx, dy, color, length=5, thickness=2):
    """Solid L-formet hjoernebeslag - som Sophisticated Storage sin korner-decal.
    (x, y) er selve hjoernepikselen; dx/dy peker innover mot blokkens senter."""
    dark = dark_of(color)
    hx0, hx1 = sorted((x, x + dx * (length - 1)))
    hy0, hy1 = sorted((y, y + dy * (thickness - 1)))
    d.rectangle([hx0, hy0, hx1, hy1], fill=color + (255,))
    vx0, vx1 = sorted((x, x + dx * (thickness - 1)))
    vy0, vy1 = sorted((y, y + dy * (length - 1)))
    d.rectangle([vx0, vy0, vx1, vy1], fill=color + (255,))
    d.point((x, y), fill=light_of(color) + (255,))
    d.point((x + dx * (length - 1), y), fill=dark + (255,))
    d.point((x, y + dy * (length - 1)), fill=dark + (255,))


def kiste_texture(bracket_color):
    img = flat_fill(WOOD)
    px = img.load()
    # loddrette bordskiller (planke-kropp)
    for x in (0, 5, 10, 15):
        for y in range(16):
            px[x, y] = shadow_of(WOOD) + (255,)
    d = ImageDraw.Draw(img)
    # metallhoop-baand oeverst og nederst (som en tonne/kasse)
    band = dark_of(STEEL)
    for by in (2, 12):
        d.rectangle([0, by, 15, by + 1], fill=band + (255,))
        d.line([(0, by), (15, by)], fill=light_of(band) + (255,))
    # laasplate i midten
    d.rectangle([6, 6, 9, 9], fill=dark_of(STEEL) + (255,))
    d.rectangle([7, 7, 8, 8], fill=STEEL + (255,))
    d.point((7, 7), fill=light_of(STEEL) + (255,))
    # hjoernebeslag - eneste tier-indikator, som i Sophisticated Storage
    if bracket_color is not None:
        corner_bracket(d, 0, 0, 1, 1, bracket_color)
        corner_bracket(d, 15, 0, -1, 1, bracket_color)
        corner_bracket(d, 0, 15, 1, -1, bracket_color)
        corner_bracket(d, 15, 15, -1, -1, bracket_color)
    bevel(img, WOOD)
    return img


save(kiste_texture(None), "block", "oppgraderbar_kiste.png")
save(kiste_texture(IRON), "block", "oppgraderbar_kiste_jern.png")
save(kiste_texture(GOLD), "block", "oppgraderbar_kiste_gull.png")
save(kiste_texture(DIAMOND), "block", "oppgraderbar_kiste_diamant.png")


# Skyggestein - moerk stein med sprekker og svakt lilla skjaer
def shadowstone_tile():
    img = textured_tile(SHADOWSTONE, 6)
    cracks(img, shadow_of(SHADOWSTONE), 3)
    px = img.load()
    for _ in range(4):
        x, y = random.randint(1, 14), random.randint(1, 14)
        px[x, y] = mix(SHADOWSTONE, PURPLE, 0.35) + (255,)
    return img


save(shadowstone_tile(), "block", "skyggestein.png")

# Skyggemalm - skyggestein med krystallklynger (ikke enkeltpiksler)
img = shadowstone_tile()
px = img.load()
for (cx, cy) in ((4, 4), (11, 6), (6, 11)):
    px[cx, cy] = PURPLE_GLOW + (255,)
    px[cx - 1, cy] = PURPLE_BRIGHT + (255,)
    px[cx + 1, cy] = PURPLE + (255,)
    px[cx, cy - 1] = PURPLE_BRIGHT + (255,)
    px[cx, cy + 1] = dark_of(PURPLE) + (255,)
    px[cx + 1, cy + 1] = shadow_of(PURPLE) + (255,)
save(img, "block", "skyggemalm.png")

# Skyggeportal - prosedyremessig virvel rundt sentrum
img = Image.new("RGBA", (16, 16))
px = img.load()
for y in range(16):
    for x in range(16):
        dx, dy = x - 7.5, y - 7.5
        dist = math.hypot(dx, dy)
        angle = math.atan2(dy, dx)
        swirl = math.sin(angle * 2 + dist * 1.1) * 0.5 + 0.5
        t = max(0.0, min(1.0, swirl * (1.0 - dist / 12.0)))
        base = mix((26, 10, 48), PURPLE_BRIGHT, t * t)
        if dist < 2.0:
            base = mix(base, PURPLE_GLOW, 0.7)
        px[x, y] = base + (255,)
save(img, "block", "skyggeportal.png")

# Ritualalter - hugget svartstein med runer
img = textured_tile(BLACKSTONE, 7)
vgrad(img, 8)
bevel(img, BLACKSTONE)
d = ImageDraw.Draw(img)
d.line([(1, 2), (14, 2)], fill=light_of(BLACKSTONE) + (255,))
d.line([(1, 13), (14, 13)], fill=shadow_of(BLACKSTONE) + (255,))
for gx in (3, 7, 11):  # runer
    d.line([(gx, 6), (gx, 9)], fill=PURPLE + (255,))
    d.point((gx + 1, 7), fill=dark_of(PURPLE) + (255,))
save(img, "block", "ritualalter_side.png")

img = textured_tile(dark_of(BLACKSTONE), 6)
bevel(img, dark_of(BLACKSTONE))
save(img, "block", "ritualalter_bottom.png")

img = textured_tile(BLACKSTONE, 7)
bevel(img, BLACKSTONE)
d = ImageDraw.Draw(img)
d.ellipse([2, 2, 13, 13], outline=dark_of(PURPLE) + (255,))
d.ellipse([4, 4, 11, 11], outline=PURPLE + (255,))
d.rectangle([7, 7, 8, 8], fill=PURPLE_GLOW + (255,))
d.point((6, 7), fill=PURPLE_BRIGHT + (255,))
d.point((9, 8), fill=PURPLE_BRIGHT + (255,))
save(img, "block", "ritualalter_top.png")

# Pidestall - soyle med fot og kapitel
img = textured_tile(BLACKSTONE, 7)
d = ImageDraw.Draw(img)
d.rectangle([1, 0, 14, 2], fill=light_of(BLACKSTONE) + (255,))
d.line([(1, 2), (14, 2)], fill=shadow_of(BLACKSTONE) + (255,))
d.rectangle([2, 12, 13, 15], fill=dark_of(BLACKSTONE) + (255,))
d.line([(2, 12), (13, 12)], fill=light_of(BLACKSTONE) + (255,))
d.rectangle([5, 3, 10, 11], fill=BLACKSTONE + (255,))
d.line([(5, 3), (5, 11)], fill=light_of(BLACKSTONE) + (255,))
d.line([(10, 3), (10, 11)], fill=shadow_of(BLACKSTONE) + (255,))
save(img, "block", "pidestall_side.png")

img = textured_tile(light_of(BLACKSTONE), 6)
bevel(img, BLACKSTONE)
d = ImageDraw.Draw(img)
d.ellipse([4, 4, 11, 11], outline=dark_of(BLACKSTONE) + (255,))
save(img, "block", "pidestall_top.png")


# --- Items ------------------------------------------------------------------

def item_canvas():
    return Image.new("RGBA", (16, 16), (0, 0, 0, 0))


def from_ascii(rows, palette):
    img = item_canvas()
    px = img.load()
    for y, row in enumerate(rows):
        for x, ch in enumerate(row):
            if ch != ".":
                px[x, y] = palette[ch] + (255,)
    return img


def dust(base):
    """Liten haug med skyggelagt kjegleform + loese korn."""
    img = item_canvas()
    px = img.load()
    for y in range(9, 14):
        half = (y - 7)  # haugen blir bredere nedover
        for x in range(8 - half, 8 + half):
            if not (0 <= x <= 15):
                continue
            t = (x - (8 - half)) / max(1, 2 * half - 1)
            c = mix(light_of(base), shadow_of(base), t * 0.8 + (y - 9) * 0.05)
            v = random.randint(-8, 8)
            px[x, y] = tuple(clamp(ch + v) for ch in c) + (255,)
    for _ in range(5):  # loese korn rundt haugen
        x, y = random.randint(3, 12), random.randint(7, 9)
        if img.getpixel((x, y))[3] == 0:
            px[x, y] = base + (255,)
    return img


def crystal(base, charged):
    pal = {
        "d": shadow_of(base), "B": base, "L": light_of(base),
        "H": hi_of(base), "G": PURPLE_GLOW,
    }
    rows = [
        "................",
        ".......dH.......",
        "......dLH.......",
        ".....dBLLH......",
        ".....dBLH.......",
        "....dBBLLH......",
        "....dBBLH.......",
        "...dBBBLH.......",
        "...dBBLH........",
        "...dBBLH........",
        "....dBLH........",
        "....dBH.........",
        ".....dH.........",
        "................",
        "................",
        "................",
    ]
    img = from_ascii(rows, pal)
    if charged:
        px = img.load()
        for (x, y) in ((3, 3), (11, 6), (2, 10), (10, 12)):
            px[x, y] = PURPLE_GLOW + (255,)
        px[6, 6] = PURPLE_GLOW + (255,)
    return img


def ingot(base):
    img = item_canvas()
    d = ImageDraw.Draw(img)
    top, front, side = light_of(base), base, dark_of(base)
    d.polygon([(3, 8), (8, 4), (13, 6), (8, 10)], fill=top + (255,))
    d.polygon([(3, 8), (8, 10), (8, 13), (3, 11)], fill=front + (255,))
    d.polygon([(8, 10), (13, 6), (13, 9), (8, 13)], fill=side + (255,))
    d.line([(3, 8), (8, 4)], fill=hi_of(base) + (255,))
    d.line([(8, 4), (13, 6)], fill=hi_of(base) + (255,))
    d.point((6, 7), fill=hi_of(base) + (255,))
    return img


def orb(base, glow):
    img = item_canvas()
    px = img.load()
    for y in range(16):
        for x in range(16):
            dx, dy = x - 7.5, y - 7.5
            dist = math.hypot(dx, dy)
            if dist > 5.4:
                continue
            # Radiell skygge med lys forskjoevet mot oppe-venstre
            t = max(0.0, min(1.0, (math.hypot(dx + 1.6, dy + 1.6)) / 7.4))
            c = mix(glow, shadow_of(base), t)
            px[x, y] = c + (255,)
    px[6, 5] = (255, 255, 255, 255)
    px[5, 6] = PURPLE_GLOW + (255,)
    return img


def upgrade_chip(accent):
    img = item_canvas()
    d = ImageDraw.Draw(img)
    board = (38, 66, 46)
    d.rectangle([3, 4, 12, 12], fill=board + (255,))
    d.line([(3, 4), (12, 4)], fill=light_of(board) + (255,))
    d.line([(3, 4), (3, 12)], fill=light_of(board) + (255,))
    d.line([(3, 12), (12, 12)], fill=shadow_of(board) + (255,))
    d.line([(12, 4), (12, 12)], fill=shadow_of(board) + (255,))
    # Kretsbaner
    trace = (86, 130, 92)
    d.line([(4, 10), (7, 10), (7, 7)], fill=trace + (255,))
    d.line([(11, 6), (9, 6)], fill=trace + (255,))
    # Kjernebrikke i aksentfarge
    d.rectangle([6, 6, 9, 9], fill=accent + (255,))
    d.point((6, 6), fill=hi_of(accent) + (255,))
    d.point((9, 9), fill=dark_of(accent) + (255,))
    # Pinner
    for x in (4, 7, 10):
        d.line([(x, 2), (x, 3)], fill=(198, 198, 208, 255))
        d.line([(x, 13), (x, 14)], fill=(148, 148, 158, 255))
    return img


save(dust(mix(SHADOW, PURPLE, 0.4)), "item", "skyggestov.png")
save(crystal((70, 46, 128), False), "item", "skyggeskaar.png")
save(crystal(PURPLE, True), "item", "ladet_skyggeskaar.png")
save(orb(SHADOW, PURPLE_BRIGHT), "item", "skyggekjerne.png")
save(ingot(SKYGGESTAAL), "item", "skyggestaal_barre.png")
save(dust(IRON), "item", "knust_jern.png")
save(dust(GOLD), "item", "knust_gull.png")
save(dust(COPPER), "item", "knust_kobber.png")

# Maskinkjerne - jernramme rundt pulserende redstone-kjerne
img = item_canvas()
d = ImageDraw.Draw(img)
d.rectangle([2, 2, 13, 13], fill=STEEL + (255,))
d.line([(2, 2), (13, 2)], fill=light_of(STEEL) + (255,))
d.line([(2, 2), (2, 13)], fill=light_of(STEEL) + (255,))
d.line([(2, 13), (13, 13)], fill=shadow_of(STEEL) + (255,))
d.line([(13, 2), (13, 13)], fill=shadow_of(STEEL) + (255,))
for (x, y) in ((4, 4), (11, 4), (4, 11), (11, 11)):
    d.point((x, y), fill=dark_of(STEEL) + (255,))
core = (214, 62, 48)
d.rectangle([5, 5, 10, 10], fill=dark_of(core) + (255,))
d.rectangle([6, 6, 9, 9], fill=core + (255,))
d.rectangle([7, 7, 8, 8], fill=hi_of(core) + (255,))
save(img, "item", "maskinkjerne.png")

save(upgrade_chip((250, 214, 74)), "item", "fartsoppgradering.png")
save(upgrade_chip((104, 196, 240)), "item", "parallelloppgradering.png")
save(upgrade_chip(IRON), "item", "kisteoppgradering_jern.png")
save(upgrade_chip(GOLD), "item", "kisteoppgradering_gull.png")
save(upgrade_chip(DIAMOND), "item", "kisteoppgradering_diamant.png")
save(upgrade_chip(PURPLE_BRIGHT), "item", "stabeloppgradering.png")

# Gjenkallingsamulett - gullkjede med skyggejuvel
img = item_canvas()
d = ImageDraw.Draw(img)
chain, cd = GOLD, dark_of(GOLD)
d.arc([4, 1, 11, 8], 160, 380, fill=chain + (255,))
d.point((4, 2), fill=hi_of(GOLD) + (255,))
d.line([(4, 5), (4, 7)], fill=cd + (255,))
d.line([(11, 5), (11, 7)], fill=cd + (255,))
d.ellipse([4, 7, 11, 14], fill=dark_of(PURPLE) + (255,), outline=cd + (255,))
d.ellipse([6, 9, 9, 12], fill=PURPLE + (255,))
d.point((6, 9), fill=PURPLE_GLOW + (255,))
save(img, "item", "gjenkallingsamulett.png")


# --- Verktoy (pixel-art fra ASCII) ------------------------------------------

TOOL_PAL = {
    "o": shadow_of(SKYGGESTAAL),   # omriss/moerkeste
    "B": SKYGGESTAAL,              # blad
    "L": light_of(SKYGGESTAAL),    # lys flate
    "H": hi_of(SKYGGESTAAL),       # egg/hoylys
    "G": PURPLE_BRIGHT,            # rune-glod
    "S": (124, 92, 55),            # skaft
    "s": (86, 62, 38),             # skaft skygge
    "h": (164, 126, 78),           # skaft hoylys
}

SWORD = [
    "............oH..",
    "...........oLHo.",
    "..........oLHo..",
    ".........oLHo...",
    "........oLHo....",
    ".......oLHo.....",
    "......oLHo......",
    ".oo..oLHo.......",
    ".oGooLHo........",
    "..oGBHo.........",
    "...oso..........",
    "..oshso.........",
    ".oshso..........",
    "oshso...........",
    "oso.............",
    ".o..............",
]

PICKAXE = [
    "....oBBLLBBo....",
    "..ooBLooooLBoo..",
    ".oBLo......oLBo.",
    ".oBo...oh...oBo.",
    ".oo...ohs....oo.",
    "......ohs.......",
    ".....ohs........",
    "....ohs.........",
    "...ohs..........",
    "..ohs...........",
    ".ohs............",
    "ohs.............",
    "oso.............",
    ".o..............",
    "................",
    "................",
]

AXE = [
    "....oBBo........",
    "..ooBLLBo.......",
    ".oBBLLLLB.......",
    ".oBLLoLLB.......",
    ".oBLohsLB.......",
    "..ooohsoo.......",
    ".....ohs........",
    "....ohs.........",
    "...ohs..........",
    "..ohs...........",
    ".ohs............",
    "ohs.............",
    "oso.............",
    ".o..............",
    "................",
    "................",
]

SHOVEL = [
    "......oLLo......",
    ".....oLLLLo.....",
    ".....oLBBLo.....",
    ".....oBBBBo.....",
    "......oBBo......",
    "......ohso......",
    ".....ohs........",
    "....ohs.........",
    "...ohs..........",
    "..ohs...........",
    ".ohs............",
    "ohs.............",
    "oso.............",
    ".o..............",
    "................",
    "................",
]

HOE = [
    "...oBBLLBo......",
    "...oBoooLBo.....",
    "....o...oBo.....",
    ".......ohso.....",
    "......ohs.......",
    ".....ohs........",
    "....ohs.........",
    "...ohs..........",
    "..ohs...........",
    ".ohs............",
    "ohs.............",
    "oso.............",
    ".o..............",
    "................",
    "................",
    "................",
]

save(from_ascii(SWORD, TOOL_PAL), "item", "skyggestaal_sverd.png")
save(from_ascii(PICKAXE, TOOL_PAL), "item", "skyggestaal_hakke.png")
save(from_ascii(AXE, TOOL_PAL), "item", "skyggestaal_oks.png")
save(from_ascii(SHOVEL, TOOL_PAL), "item", "skyggestaal_spade.png")
save(from_ascii(HOE, TOOL_PAL), "item", "skyggestaal_greip.png")


# --- Rustning-items ----------------------------------------------------------

ARMOR_PAL = {
    "o": shadow_of(SKYGGESTAAL),
    "B": SKYGGESTAAL,
    "L": light_of(SKYGGESTAAL),
    "H": hi_of(SKYGGESTAAL),
    "G": PURPLE_BRIGHT,
    "d": (16, 14, 24),
}

HELMET = [
    "................",
    "................",
    "....oooooo......",
    "...oLLLLLLo.....",
    "..oLLBBBBBBo....",
    "..oLBBBBBBBo....",
    "..oBBBBBBBBo....",
    "..oBoddoddBo....",
    "..oBoddoddBo....",
    "..oBBBBBBBBo....",
    "...oo.oo..o.....",
    "................",
    "................",
    "................",
    "................",
    "................",
]

CHESTPLATE = [
    "................",
    "................",
    "..oo......oo....",
    ".oLLo....oBBo...",
    ".oLBoooooBBBo...",
    ".oLBBLLLBBBBo...",
    ".ooBBLGLBBBoo...",
    "..oBBLLLBBBo....",
    "..oBBBBBBBBo....",
    "..oBBBBBBBBo....",
    "..oBBBBBBBBo....",
    "...oBBBBBBo.....",
    "....oooooo......",
    "................",
    "................",
    "................",
]

LEGGINGS = [
    "................",
    "................",
    "...oooooooo.....",
    "..oLLLLLLLLo....",
    "..oBBBBBBBBo....",
    "..oBBBoBBBBo....",
    "..oBBo.oBBBo....",
    "..oBBo.oBBBo....",
    "..oBBo.oBBBo....",
    "..oBBo.oBBBo....",
    "..oBBo.oBBBo....",
    "..oooo.ooooo....",
    "................",
    "................",
    "................",
    "................",
]

BOOTS = [
    "................",
    "................",
    "................",
    "................",
    "...ooo....ooo...",
    "..oLBo...oLBo...",
    "..oBBo...oBBo...",
    "..oBBo...oBBo...",
    "..oBBoo..oBBoo..",
    "..oBBBBo.oBBBBo.",
    "..oooooo.oooooo.",
    "................",
    "................",
    "................",
    "................",
    "................",
]

save(from_ascii(HELMET, ARMOR_PAL), "item", "skyggestaal_hjelm.png")
save(from_ascii(CHESTPLATE, ARMOR_PAL), "item", "skyggestaal_brynje.png")
save(from_ascii(LEGGINGS, ARMOR_PAL), "item", "skyggestaal_bukser.png")
save(from_ascii(BOOTS, ARMOR_PAL), "item", "skyggestaal_stovler.png")


# --- Rustning-layers (64x32) --------------------------------------------------
# Vanilla humanoid-armor-UV: hjelm (0,0)-(32,16), kropp (16,16)-(40,32),
# armer (40,16)-(56,32), bein (0,16)-(16,32). Flat panel + bevel + soem
# i stedet for stoy, saa rustningen faar synlig form paa spilleren -
# ikke bare en ensfarget blob.

def armor_panel(d, x0, y0, x1, y1, base):
    d.rectangle([x0, y0, x1 - 1, y1 - 1], fill=base + (255,))
    lt = light_of(base) + (255,)
    dk = shadow_of(base) + (255,)
    d.line([(x0, y0), (x1 - 1, y0)], fill=lt)
    d.line([(x0, y0), (x0, y1 - 1)], fill=lt)
    d.line([(x0, y1 - 1), (x1 - 1, y1 - 1)], fill=dk)
    d.line([(x1 - 1, y0), (x1 - 1, y1 - 1)], fill=dk)


def armor_seam(d, x0, x1, y, base):
    d.line([(x0, y), (x1 - 1, y)], fill=dark_of(base) + (255,))


for layer in (1, 2):
    img = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    if layer == 1:
        # Hjelm - visir med glodende oyne paa front-flaten
        armor_panel(d, 0, 0, 32, 16, SKYGGESTAAL)
        armor_seam(d, 0, 32, 8, SKYGGESTAAL)
        d.rectangle([9, 11, 14, 12], fill=(18, 16, 24, 255))
        d.point((10, 11), fill=PURPLE_GLOW + (255,))
        d.point((13, 11), fill=PURPLE_GLOW + (255,))

        # Brystplate - loddrett rune paa brystet
        armor_panel(d, 16, 16, 40, 32, SKYGGESTAAL)
        armor_seam(d, 16, 40, 24, SKYGGESTAAL)
        d.line([(23, 21), (23, 27)], fill=PURPLE + (255,))
        d.line([(21, 24), (25, 24)], fill=PURPLE + (255,))
        d.point((23, 21), fill=PURPLE_GLOW + (255,))

        # Armer
        armor_panel(d, 40, 16, 56, 32, SKYGGESTAAL)
        armor_seam(d, 40, 56, 24, SKYGGESTAAL)

        # Oevre boot-del (dekkes av layer 1 paa foettene)
        armor_panel(d, 0, 16, 16, 32, SKYGGESTAAL)
        armor_seam(d, 0, 16, 24, SKYGGESTAAL)
    else:
        # Bein
        armor_panel(d, 0, 16, 16, 32, dark_of(SKYGGESTAAL))
        armor_seam(d, 0, 16, 24, dark_of(SKYGGESTAAL))

        # Laar/belte med liten spenne
        armor_panel(d, 16, 16, 40, 32, dark_of(SKYGGESTAAL))
        armor_seam(d, 16, 40, 22, dark_of(SKYGGESTAAL))
        d.rectangle([26, 20, 29, 22], fill=PURPLE_BRIGHT + (255,))
        d.point((26, 20), fill=PURPLE_GLOW + (255,))
    save(img, "models", "armor", f"skyggestaal_layer_{layer}.png")


# --- Entity: Skyggevokter (zombie-layout 64x64) --------------------------------

img = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
px = img.load()
ROBE = (30, 27, 44)
ROBE_DARK = (22, 20, 34)


def fill_region(x0, y0, x1, y1, base, var=6):
    for y in range(y0, y1):
        for x in range(x0, x1):
            v = random.randint(-var, var)
            c = mix(base, light_of(base), max(0.0, 1 - (y - y0) / max(1, y1 - y0)) * 0.2)
            px[x, y] = tuple(clamp(ch + v) for ch in c) + (255,)


# Hode (8px kube paa (0,0)) + hette som hat-layer (32,0) med aapen front
fill_region(0, 0, 32, 16, SHADOW)
fill_region(32, 0, 64, 16, ROBE_DARK)
for y in range(8, 16):  # hattens frontflate holdes aapen saa ansiktet synes
    for x in range(40, 48):
        px[x, y] = (0, 0, 0, 0)
# Ansikt: moerkere felt + glodende oyne
for y in range(9, 15):
    for x in range(9, 15):
        v = random.randint(-4, 4)
        px[x, y] = tuple(clamp(c + v) for c in ROBE_DARK) + (255,)
for (x, y) in ((10, 11), (11, 11), (13, 11), (14, 11)):
    px[x, y] = PURPLE_GLOW + (255,)
for (x, y) in ((10, 12), (14, 12)):
    px[x, y] = PURPLE + (255,)

# Kropp (16,16)-(40,32) med rune paa brystet
fill_region(16, 16, 40, 32, SHADOW)
for (x, y) in ((23, 22), (23, 23), (23, 24), (24, 23), (24, 25)):
    px[x, y] = PURPLE + (255,)
px[23, 22] = PURPLE_BRIGHT + (255,)

# Armer og bein - moerkere kappe
fill_region(40, 16, 56, 32, ROBE)   # hoyre arm
fill_region(0, 16, 16, 32, ROBE)    # hoyre bein
fill_region(16, 48, 32, 64, ROBE)   # venstre bein
fill_region(32, 48, 48, 64, ROBE)   # venstre arm
save(img, "entity", "skyggevokter.png")


# --- GUI-teksturer (256x256, panel 176x166) ------------------------------------

def gui_base():
    img = Image.new("RGBA", (256, 256), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.rectangle([0, 0, 175, 165], fill=(198, 198, 198, 255))
    d.rectangle([0, 0, 175, 165], outline=(0, 0, 0, 255))
    d.line([(1, 1), (174, 1)], fill=(255, 255, 255, 255))
    d.line([(1, 1), (1, 164)], fill=(255, 255, 255, 255))
    d.line([(1, 164), (174, 164)], fill=(85, 85, 85, 255))
    d.line([(174, 1), (174, 164)], fill=(85, 85, 85, 255))
    # Avrundede hjorner som vanilla
    for (x, y) in ((0, 0), (175, 0), (0, 165), (175, 165)):
        d.point((x, y), fill=(0, 0, 0, 0))
    return img, d


def slot(d, x, y):
    """Slot med topp-venstre paa (x-1, y-1), 18x18 (vanilla-stil)."""
    d.rectangle([x - 1, y - 1, x + 16, y + 16], fill=(139, 139, 139, 255))
    d.line([(x - 1, y - 1), (x + 16, y - 1)], fill=(55, 55, 55, 255))
    d.line([(x - 1, y - 1), (x - 1, y + 16)], fill=(55, 55, 55, 255))
    d.line([(x - 1, y + 16), (x + 16, y + 16)], fill=(255, 255, 255, 255))
    d.line([(x + 16, y - 1), (x + 16, y + 16)], fill=(255, 255, 255, 255))


def inset(d, x0, y0, x1, y1):
    """Innsunket felt (for maalere/indikatorer)."""
    d.rectangle([x0, y0, x1, y1], fill=(139, 139, 139, 255))
    d.line([(x0, y0), (x1, y0)], fill=(55, 55, 55, 255))
    d.line([(x0, y0), (x0, y1)], fill=(55, 55, 55, 255))
    d.line([(x0, y1), (x1, y1)], fill=(255, 255, 255, 255))
    d.line([(x1, y0), (x1, y1)], fill=(255, 255, 255, 255))


def player_inv(d, y0=84):
    for row in range(3):
        for col in range(9):
            slot(d, 8 + col * 18, y0 + row * 18)
    for col in range(9):
        slot(d, 8 + col * 18, y0 + 58)


# processing.png: input (56,35), output (116,35), pil (79,34)-(103,42), energi (10,17)-(18,69)
img, d = gui_base()
player_inv(d)
slot(d, 56, 35)
slot(d, 116, 35)
inset(d, 78, 33, 104, 43)
inset(d, 9, 16, 19, 70)
save(img, "gui", "processing.png")

# generator.png: brensel-slot (80,40), brennindikator (81,24)-(95,38), energi (10,17)-(18,69)
img, d = gui_base()
player_inv(d)
slot(d, 80, 40)
inset(d, 80, 23, 96, 39)
inset(d, 9, 16, 19, 70)
save(img, "gui", "generator.png")

print("Teksturer generert i", os.path.abspath(ROOT))
