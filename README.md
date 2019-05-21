# NerdPoints
A Bukkit plugin supporting waypoints, places of interest and a navigation HUD.

Waypoints and places are not yet implemented. Those features will be implemented
on time for PvE (`p.nerd.nu`) rev 24.

## Head Up Display (HUD) Commands

* `/hud help` - Show this help. Equivalent to `/help /hud`.
* `/hud [on|off]` - Turn on/off the HUD. If no argument is specified, the
  HUD state is toggled.
* `/hud format [default | <format>]` - Show the string that defines the layout
  of the HUD. If the `<format>` argument is specified, set a new format. The
  word "default" signifies the default format.

* `/hud biome [on|off]` - Turn on/off the biome display.
* `/hud biome format [default | <format>]` - Set or show the string that defines
  the `%biome%` value shown by the HUD. The word "default" signifies the default
  format.

* `/hud chunk [on|off]` - Turn on/off the chunk display.
* `/hud chunk format [default | <format>]` - Set or show the string that
  defines the `%chunk%` value shown by the HUD. The word "default" signifies the
  default format.

* `/hud compass [on|off]` - Turn on/off the compass display.
* `/hud compass format [default | <format>]` - Set or show the string that
  defines the `%compass%` value shown by the HUD. The word "default" signifies
  the default format.

* `/hud coords [on|off]` - Turn on/off the coords display.
* `/hud coords format [default | <format>]` - Set or show the string that
  defines the `%coords%` value shown by the HUD. The word "default" signifies
  the default format.

* `/hud light [on|off]` - Turn on/off the light display.
* `/hud light format [default | <format>]` - Set or show the string that
  defines the `%light%` value shown by the HUD. The word "default" signifies the
  default format.

* `/hud time [on|off]` - Turn on/off the time display.
* `/hud time format [default | <format>]` - Set or show the string that
  defines the `%time%` value shown by the HUD. The word "default" signifies the
  default format.


## HUD Visibility

By default, the HUD is disabled for new players. To turn on the HUD, you
can run `/hud on`, or toggle it on or off with `/hud`.

Individual parts of the HUD can be turned on and off. By default, only the
coordinates and compass sections of the HUD are enabled. You can use
`/hud biome` to toggle on/off the biome section of the HUD display (note
that the HUD must also be visible overall to see the biome section). You can
also explicitly specify the visibility of a section, e.g. `/hud light on`.


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
| `%time%` | The time of day, formatted according to `/hud time format`. |

The default setting is: `%chunk%  %light%  %biome%  %time%  %coords%  %compass%`

Example: `/hud format &4%biome% &f%light% &6%chunk% &e%coords% &f%compass% &b%time%`


### Biome Format Variables

The following variables are available for use when configuring the biome
format of the HUD with the `/hud biome format` command:

| Variable | Description | Example |
| :---     | :---        | :---    |
| `%biome%` | The player's current biome in lower case letters. | `plains` |

The default setting is: `&6%biome%`

Example: `/hud biome format &eIn: %biome%`


### Chunk Format Variables

The following variables are available for use when configuring the chunk
format of the HUD with the `/hud chunk format` command:

| Variable | Description | Example |
| :---     | :---        | :---    |
| `%cx%`, `%cy%`, `%cz%` | The chunk's X, Y or Z coordinate (player coordinate divided by 16). | `-123` |
| `%x%`, `%y%`, `%z%` | The player's X, Y or Z coordinate, within the chunk, modulo 16 (range 0-15). | `15` |

The default setting is: `C %x% %y% %z%`

Example: `/hud chunk format %x% %y% %z% in %cx% %cy% %cz%`


### Compass Format Variables

The following variables are available for use when configuring the compass
format of the HUD with the `/hud compass format` command:

| Variable | Description | Example |
| :---     | :---        | :---    |
| `%octant%` | The player's look direction as a compass octant (N, NE, E, SE, S, SW, W, NW). | `SE` |
| `%heading%` | The player's heading angle (0 - 359), rounded to the nearest whole number. | `123` |
| `%heading.%`| The player's heading angle (0.0 - 359.9), rounded to one decimal place. | `69.4` |

The default setting is: `&e%octant%`

Example: `/hud compass format &e%octant% %heading.%`


### Coordinates Format Variables

The following variables are available for use when configuring the coords
format of the HUD with the `/hud coords format` command:

| Variable | Description | Example |
| :---     | :---        | :---    |
| `%x%`, `%y%`, `%z%` | The player's X, Y or Z coordinate, truncated to a whole number. | `-1234` |
| `%x.%`, `%y.%`, `%z.%` | The player's X, Y or Z coordinate, rounded to one decimal place. | `-1234.5` |

The default setting is: `&7X &f%x% &7Y &f%y% &7Z &f%z%`

Example: `/hud coords format %x.% %y.% %z.%`


### Light Format Variables

The following variables are available for use when configuring the light
format of the HUD with the `/hud light format` command:

| Variable | Description | Example |
| :---     | :---        | :---    |
| `%skylight%` | The light level from the sky (0-15). | `15` |
| `%blocklight%` | The light level from nearby blocks (0-15). | `15` |
| `%light%`| The total light level at the player's location (0-15). | `15` |

The default setting is: `&6L %light% &f(&7B %blocklight% &bS %skylight%&f)`

Example: `/hud light format &6L %light%`


### Time Format Variables

The following variables are available for use when configuring the time
format of the HUD with the `/hud time format` command:

| Variable | Description | Example |
| :---     | :---        | :---    |
| `%ampm%` | The text "a.m." before midday and "p.m." after midday. | `p.m.` |
| `%AMPM%` | The text "A.M." before midday and "P.M." after midday. | `A.M.` |
| `%h%` | The current hour from ` 1` to `12`, padded to two characters with a leading space. | ` 5` |
| `%hh%` | The current hour from `01` to `12`, padded to two characters with a leading zero. | `07` |
| `%H%` | The current hour from ` 0` to `23`, padded to two characters with a leading space. | `21` |
| `%HH%` | The current hour from `00` to `23`, padded to two characters with a leading space. | `09` |
| `%mm%` | The current minutes from `00` to `59`. | `42` |
| `%orb%` | An indicator of the orb in the sky - `☼` for the sun and `☾` for the moon.  | `☾` |
| `%updown%` | Indicates a rising sun or moon with `↑` and a setting sun or moon with `↓`. At other times, the indicator is just a single space. | `↑` |

The `%updown%` indication times are based on the Minecraft wiki
[Day-night cycle](https://minecraft.gamepedia.com/Day-night_cycle) article
and have the following properties:

| Description | Time | Ticks | Notes |
| :---        | :--- | :--- | :--- |
| Sunrise     | 4:33 a.m. | 22550 | The horizon starts to brighten. |
| Day         | 6:27 a.m. | 450 | Time when sunrise ends. |
| Sunset      | 5:37 p.m. | 11617 | Start of sunset. |
| Moonrise    | 6:33 p.m. | 12550 | Time when beds become usable. |
| Night       | 7:11 p.m. | 13183 | Time when monsters start spawning outdoors in clear weather. Moonrise ends. |
| Moonset     | 3:30 a.m. | 21500 | Time when the moon begins setting. |

The default setting is: `%h%:%mm% %ampm% %orb%&6%updown%&f`

Example: `/hud time format &b%HH%:%mm% &f%orb%&6%updown%&f`


