# Item Commander
> Shiny item do stuff

A simple Serverside Fabric mod that allows you to assign commands to Item(Stacks).
It works by adding NBT data to the item, then when a player uses an item the NBT data for that item is checked, if it contains the `ItemCommander` tag tree it executes the specified command.

To embed a command in an item, hold it in your main hand and run `/commander create "<command>" [CONSUME_ACTION] [COMMAND_SOURCE] [cooldownTicks]` 

_consume actions:_

| Action      | Description       |
|-------------|-------------------|
| **CONSUME** | Consumes the item |
| KEEP        | Keeps the item    |

_command sources:_

| Action      | Description                    |
|-------------|--------------------------------|
| **SERVER**  | Command executed by the server |
| PLAYER      | Command executed by the player |

_defaults in **bold***_

### Command placeholders
_when creating a command you can use the following vanilla-like placeholders:_

| Placeholder   | Action                              |
|---------------|-------------------------------------|
| `@itemname`   | Item Name                           |
| `@pich`       | Player pitch                        |
| `@yaw`        | Player yaw                          |
| `@ix`         | Item use X coordinate               |
| `@iy`         | Item use Y coordinate               |
| `@iz`         | Item use Z coordinate               |
| `@x`          | Player use X coordinate             |
| `@y`          | Player use Y coordinate             |
| `@z`          | Player use Z coordinate             |
| `@p` and `@s` | The player using the Commander Item |

To append more commands to one item, hold a Commander Item in your main hand and run `/commander append "<command>"`


### This mod supports [LuckPerms' Fabric Permission API](https://luckperms.net/).
- `commander.command.create` allows a player to use this mod to embed commands in items
- `commander.command.append` allows a player to use this mod to append commands to a Commander Items

## Bleeding edge builds
Upstream builds are available via [GitHub Actions](https://github.com/CamperSamu/ItemCommander/actions).

___

## Setup

For setup instructions please see the [fabric wiki page](https://fabricmc.net/wiki/tutorial:setup) that relates to the IDE that you are using.

## License

This template is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects.
