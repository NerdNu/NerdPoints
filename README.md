# NerdPoints
A Bukkit plugin supporting waypoints, places of interest and a navigation HUD.

Waypoints and places are not yet implemented. Those features will be implemented
on time for PvE (`p.nerd.nu`) rev 24.

## Head Up Display (HUD) Commands

* `/hud help` - Show this help. Equivalent to `/help /hud`.
* `/hud [on|off]` - Turn on/off the HUD. If no argument is specified, the
  HUD state is toggled.
* `/hud format [<format>]` - Show the string that defines the layout of the
  HUD. If the `<format>` argument is specified, set a new format.
* `/hud biome format [<format>]` - Set or show the string that defines the
  `%biome%` value shown by the HUD.
* `/hud chunk format [<format>]` - Set or show the string that
  defines the `%chunk%` value shown by the HUD.
* `/hud compass format [<format>]` - Set or show the string that
  defines the `%compass%` value shown by the HUD.
* `/hud coords format [<format>]` - Set or show the string that
  defines the `%coords%` value shown by the HUD.
* `/hud light format [<format>]` - Set or show the string that
  defines the `%light%` value shown by the HUD.


## HUD Formatting

The layout of the HUD is controlled by various per-player settings. All format
settings can contain two character Minecraft colour code sequences beginning
with '&', followed by 0-9, a-f, k-o or r. See the Minecraft wiki article on
[Colour Codes](https://minecraft.gamepedia.com/Formatting_codes#Color_codes).

Format settings can also contain variables whose name begins and ends with the
percent symbol, e.g. `%x%`. Variables are defined within a context. The player's
current X coordinate, `%x%`, can be referenced when configuring the coordinate
format of the HUD, but not the light level format, for example.

### Overall HUD Format

The following variables are available for use when configuring the overall
layout of the HUD with the `/hud format` command:

| Variable | Description |
| :---     | :---        |
| `%biome%` | The player's current biome, formatted according to `/hud biome format`. |
| `%chunk%` | The player's chunk coordinates, formatted according to `/hud chunk format`. |
| `%compass%` | The player's compass direction, formatted according to `/hud compass format`. |
| `%coords%` | The player's coordinates, formatted according to `/hud coords format`. |
| `%light%` | The light level at the player's coordinates, formatted according to `/hud light format`. |

The default setting is: `%chunk% %light% %biome% %coords% %compass%`

Example: `/hud format &4%biome% &f%light% &6%chunk% &e%coords% &f%compass%`


### Biome Format Variables

The following variables are available for use when configuring the biome
format of the HUD with the `/hud biome format` command:

| Variable | Description | Example |
| :---     | :---        | :---    |
| `%biome%` | The player's current biome in lower case letters. | plains |

The default setting is: `%biome%`

Example: `/hud biome format &4%biome%`


### Chunk Format Variables

The following variables are available for use when configuring the chunk
format of the HUD with the `/hud chunk format` command:

| Variable | Description | Example |
| :---     | :---        | :---    |
| `%cx%`, `%cy%`, `%cz%` | The chunk's X, Y or Z coordinate (player coordinate divided by 16). | -123 |
| `%x%`, `%y%`, `%z%` | The player's X, Y or Z coordinate, within the chunk, modulo 16 (range 0-15). | 15 |

The default setting is: `C %x% %y% %z%`

Example: `/hud chunk format %x% %y% %z% in %cx% %cy% %cz%`


### Compass Format Variables

The following variables are available for use when configuring the compass
format of the HUD with the `/hud compass format` command:

| Variable | Description | Example |
| :---     | :---        | :---    |
| `%octant%` | The player's look direction as a compass octant (N, NE, E, SE, S, SW, W, NW). | SE |
| `%heading%` | The player's heading angle (0 - 359), rounded to the nearest whole number. | 123 |
| `%heading.%`| The player's heading angle (0.0 - 359.9), rounded to one decimal place. | 69.4 |

The default setting is: `%octant%`

Example: `/hud compass format %octant% %heading.%`


### Coordinates Format Variables

The following variables are available for use when configuring the coords
format of the HUD with the `/hud coords format` command:

| Variable | Description | Example |
| :---     | :---        | :---    |
| `%x%`, `%y%`, `%z%` | The player's X, Y or Z coordinate, truncated to a whole number. | -1234 |
| `%x.%`, `%y.%`, `%z.%` | The player's X, Y or Z coordinate, rounded to one decimal place. | -12345 |

The default setting is: `%x% %y% %z%`

Example: `/hud coords format &8X &f%x% &8Y &f%y% &8Z &f%z%`


### Light Format Variables

The following variables are available for use when configuring the light
format of the HUD with the `/hud light format` command:

| Variable | Description | Example |
| :---     | :---        | :---    |
| `%skylight%` | The light level from the sky (0-15). | 15 |
| `%blocklight%` | The light level from nearby blocks (0-15). | 15 |
| `%light%`| The total light level at the player's location (0-15). | 15 |

The default setting is: `L %light%`

Example: `/hud light format L %light% (S %skylight% B %blocklight%)`
