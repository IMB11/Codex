***Codex has been archived. This is because of a lack of interest in it's development.***

# Codex

Codex allows you to gain information and do quick calculations in a Satisfactory-style codex screen.

Currently, the mod is a proof of concept - but it is being actively developed.

I wouldn't consider it a replacement for Roughly Enough Items or Too Many Items - as those mods are more focused on the data part of the items.

Codex is more useful if you want to figure out **what** to do with an item, not **how**.

## Setup

Codex will guide you through it's setup screen when you launch the game for the first time with the mod:

![](https://i.imgur.com/3Lg6or9.gif)

These data sources are from [the data repository](https://github.com/mineblock11/Codex/tree/data) - and respect the license of the source.

## Showcase

### Quickmath Parser

The quickmath parser allows you to quickly evaluate arithmetic expressions.
This is especially useful for technical minecrafters - as it allows you to calculate various things such as shulker box capacity/redstone tick math using built in functions.

![](https://i.imgur.com/Gj1csmT.gif)

The quickmath parser also supports:

- [String Concatenation](https://imgur.com/qza71OB)
- [Boolean Evaluation](https://imgur.com/QAle7gw)

#### Quickmath Functions And Constants

**Functions**

- `stacksin(shulkers) = shulkers * 30`
- `shulkersof(stacks) = stacks / 30`
- `stacksof(items) = items / 64`
- `itemsof(stacks) = stacks * 64`
- `secsin(ticks) = ticks / 20`
- `ticksin(secs) = secs * 20`

**Constants**

- `STACK = 64`
- `SHULKER = 1920`
- `DAY =  72 * 24000`
- `HOUR = (72 * 24000) / 24`
- `MIN = (72 * 24000) / 24 / 60`
- `SEC = (72 * 24000) / 24 / 60 / 60`
- `MS =  (72 * 24000) / 24 / 60 / 60 / 1000`
- `MC_DAY = 24000`
- `MC_HOUR = 24000 / 24`
- `MC_MIN = 24000 / 24 / 60`
- `MC_SEC = 24000 / 24 / 60 / 60`
- `MC_MS = 24000 / 24 / 60 / 60 / 1000`


### Quicksearch

The quicksearch functionality allows you to quickly search for items - you can scroll the searchbar by dragging outside the list or using your scrollwheel.

Clicking on a search element opens it's codex page.

![](https://i.imgur.com/fQxQqrF.gif)

Item descriptions are from the sources you chose during codex setup.

## Contributing

Want to add support for your mod? Check out the [data repository](https://github.com/mineblock11/Codex/tree/data) which stores all the item descriptions and other miscelaneous content. 

*PS: The mod is All Rights Reserved currently as I do not see it fit for contributions. The data branch of the repository though is licensed by-file, not entirely as a whole. Please see `_index.json` for licensing of sources.*
