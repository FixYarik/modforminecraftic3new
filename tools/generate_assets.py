#!/usr/bin/env python3
"""Rebuild Industrial Evolution's original stage-one assets with Python 3.

Uses only the standard library. Pixel art is authored below, with no external
images or copied game/mod textures. Run from any working directory.
"""

from __future__ import annotations

import json
from pathlib import Path
import struct
import zlib


ROOT = Path(__file__).resolve().parents[1] / "src/main/resources"
MOD_ID = "industrialevolution"
SIZE = 16


def write_json(path: str, value: dict) -> None:
    target = ROOT / path
    target.parent.mkdir(parents=True, exist_ok=True)
    target.write_text(json.dumps(value, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def write_png(path: str, pixels: list[list[tuple[int, int, int, int]]]) -> None:
    def chunk(kind: bytes, data: bytes) -> bytes:
        return struct.pack(">I", len(data)) + kind + data + struct.pack(">I", zlib.crc32(kind + data))

    assert len(pixels) == SIZE and all(len(row) == SIZE for row in pixels)
    data = b"".join(b"\x00" + bytes(channel for pixel in row for channel in pixel) for row in pixels)
    png = b"\x89PNG\r\n\x1a\n"
    png += chunk(b"IHDR", struct.pack(">IIBBBBB", SIZE, SIZE, 8, 6, 0, 0, 0))
    png += chunk(b"IDAT", zlib.compress(data, 9))
    png += chunk(b"IEND", b"")
    target = ROOT / path
    target.parent.mkdir(parents=True, exist_ok=True)
    target.write_bytes(png)


def casing_texture(face: str) -> list[list[tuple[int, int, int, int]]]:
    dark = (29, 35, 42, 255)
    edge = (58, 67, 75, 255)
    light = (126, 140, 149, 255)
    orange = (233, 130, 44, 255)
    pixels = [[(71 + (x + y) % 3 * 3, 81 + (x + y) % 3 * 3,
                91 + (x + y) % 3 * 3, 255) for x in range(SIZE)] for y in range(SIZE)]
    for y in range(SIZE):
        for x in range(SIZE):
            if x in (0, 15) or y in (0, 15):
                pixels[y][x] = dark
            elif x in (1, 14) or y in (1, 14):
                pixels[y][x] = light if x == 1 or y == 1 else edge
    for x, y in ((2, 2), (13, 2), (2, 13), (13, 13)):
        pixels[y][x] = (172, 179, 179, 255)
        pixels[y + 1][x] = dark

    if face == "side":
        for y in range(4, 12):
            for x in range(4, 12):
                pixels[y][x] = dark if x in (4, 11) or y in (4, 11) else (52, 61, 70, 255)
        for y in (6, 8):
            for x in range(6, 10):
                pixels[y][x] = (21, 27, 33, 255)
                pixels[y + 1][x] = (91, 102, 109, 255)
        for y in (5, 6, 7):
            pixels[y][2] = orange
            pixels[y][13] = orange
    elif face == "top":
        for y in range(4, 12):
            for x in range(4, 12):
                pixels[y][x] = dark if x in (4, 11) or y in (4, 11) else edge
        for y in (6, 9):
            for x in range(6, 10):
                pixels[y][x] = orange
        for y in range(7, 9):
            pixels[y][6] = orange
            pixels[y][9] = orange
    else:
        for y in (5, 8, 11):
            for x in range(4, 12):
                pixels[y][x] = dark
                pixels[y - 1][x] = edge
    return pixels


def rubber_texture() -> list[list[tuple[int, int, int, int]]]:
    # An asymmetrical folded rubber sheet, 16 x 16 pixels.
    rows = (
        "................", "................", "......aaaa......", "....aabbbbaa....",
        "...abbccccbba...", "..abbcdddccba...", "..abcddcccbbba..", ".aabcddccbbccba.",
        ".abbccbbbccdcba.", "..abbbbccddccba.", "..aabcccddccba..", "...aabbccccbaa..",
        ".....aabbbbaa...", ".......aaaa.....", "................", "................",
    )
    palette = {
        ".": (0, 0, 0, 0), "a": (22, 26, 30, 255), "b": (43, 51, 57, 255),
        "c": (72, 85, 91, 255), "d": (113, 132, 136, 255),
    }
    return [[palette[pixel] for pixel in row] for row in rows]


def recipe_advancement(recipe: str, ingredient: str) -> dict:
    recipe_id = f"{MOD_ID}:{recipe}"
    return {
        "parent": "minecraft:recipes/root",
        "criteria": {
            "has_ingredient": {"trigger": "minecraft:inventory_changed", "conditions": {
                "items": [{"items": [ingredient]}],
            }},
            "has_the_recipe": {"trigger": "minecraft:recipe_unlocked", "conditions": {"recipe": recipe_id}},
        },
        "requirements": [["has_ingredient", "has_the_recipe"]],
        "rewards": {"recipes": [recipe_id]},
    }


def main() -> None:
    assets = f"assets/{MOD_ID}"
    data = f"data/{MOD_ID}"
    write_json(f"{assets}/blockstates/machine_casing.json", {
        "variants": {"": {"model": f"{MOD_ID}:block/machine_casing"}},
    })
    write_json(f"{assets}/models/block/machine_casing.json", {
        "parent": "minecraft:block/cube_bottom_top",
        "textures": {face: f"{MOD_ID}:block/machine_casing_{face}" for face in ("side", "top", "bottom")},
    })
    write_json(f"{assets}/models/item/machine_casing.json", {"parent": f"{MOD_ID}:block/machine_casing"})
    write_json(f"{assets}/models/item/rubber.json", {
        "parent": "minecraft:item/generated", "textures": {"layer0": f"{MOD_ID}:item/rubber"},
    })
    for language, casing, rubber in (("en_us", "Machine Casing", "Rubber"),
                                     ("ru_ru", "Корпус механизма", "Резина")):
        write_json(f"{assets}/lang/{language}.json", {
            "itemGroup.industrialevolution.main": "Industrial Evolution",
            "block.industrialevolution.machine_casing": casing,
            "item.industrialevolution.rubber": rubber,
        })
    for face in ("side", "top", "bottom"):
        write_png(f"{assets}/textures/block/machine_casing_{face}.png", casing_texture(face))
    write_png(f"{assets}/textures/item/rubber.png", rubber_texture())
    write_json(f"{data}/recipes/rubber.json", {
        "type": "minecraft:crafting_shapeless", "category": "misc",
        "ingredients": [{"item": "minecraft:slime_ball"}, {"item": "minecraft:dried_kelp"}],
        "result": {"item": f"{MOD_ID}:rubber", "count": 2},
    })
    write_json(f"{data}/recipes/machine_casing.json", {
        "type": "minecraft:crafting_shaped", "category": "building",
        "pattern": ["III", "IRI", "III"],
        "key": {"I": {"tag": "forge:ingots/iron"}, "R": {"tag": "forge:rubber"}},
        "result": {"item": f"{MOD_ID}:machine_casing"},
    })
    for recipe, ingredient in (("rubber", "minecraft:slime_ball"), ("machine_casing", f"{MOD_ID}:rubber")):
        write_json(f"{data}/advancements/recipes/misc/{recipe}.json", recipe_advancement(recipe, ingredient))
    write_json(f"{data}/loot_tables/blocks/machine_casing.json", {
        "type": "minecraft:block",
        "pools": [{"rolls": 1, "entries": [{"type": "minecraft:item", "name": f"{MOD_ID}:machine_casing"}],
                   "conditions": [{"condition": "minecraft:survives_explosion"}]}],
        "random_sequence": f"{MOD_ID}:blocks/machine_casing",
    })
    for tag in ("mineable/pickaxe", "needs_stone_tool"):
        write_json(f"data/minecraft/tags/blocks/{tag}.json", {"replace": False, "values": [f"{MOD_ID}:machine_casing"]})
    write_json("data/forge/tags/items/rubber.json", {"replace": False, "values": [f"{MOD_ID}:rubber"]})


if __name__ == "__main__":
    main()
