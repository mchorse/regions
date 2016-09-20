# Regions mod

Regions is a Minecraft mod that allows you to define a region within your wold with `/region` command, and save and restore defined regions.

Regions is create for Forge and Minecraft 1.9.4. This mod is useful for damage control and region versioning.

It's not a really good idea to define big regions (as it may crash the game or create really big files), so stay safe in range of ~`48`x`48`x`48` blocks. That's my assumption, I didn't tested that.

## Command Syntax

`/region` command has three subcommands:

* `/region define <region> <x1> <y1> <z1> <x2> <y2> <z2>` – defines a region that can be saved or restored.
* `/region save <region> <state> [save_entities]` saves region `<region>` with state id `<state>` and optionally saves entities in that region. By default, `/region save` saves entities.
* `/region restore <region> <state>` restores given `<region>` with given `<state>`.

Basically, first you define a region. Then you save a region with given state. When region is being saved, the command copies all blocks, tile entities and entities (depending on `[save_entities]` argument) to a folder in world's save, and when you restore a region, those data are being copied back into the world.
