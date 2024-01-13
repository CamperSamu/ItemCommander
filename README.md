# Item Commander
> Shiny item do stuff

A simple Serverside Fabric mod that allows you to assign commands to Item(Stacks).
It works by adding NBT data to the item, then when a player uses an item the NBT data for that item is checked, if it contains the `ItemCommander` tag tree it executes the specified command.

To embed a command in an item, hold it in your main hand and run `/commander create "<command>" [CONSUME_ACTION] [COMMAND_SOURCE] [cooldownTicks] [CustomItemNBT]`

_consume actions:_

| Action      | Description       |
|-------------|-------------------|
| **CONSUME** | Consumes the item |
| KEEP        | Keeps the item    |

_command sources:_

| Action           | Description                                                                                                                                                                    |
|------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **SERVER**       | Command executed by the server                                                                                                                                                 |
| PLAYER           | Command executed by the player                                                                                                                                                 |
| OP               | Command executed by the player with OP                                                                                                                                         |
| SERVER_AS_PLAYER | Command executed by the server as the player (like OP)                                                                                                                         |
| DANGEROUSLY_OP   | Command executed by the player whilst being OP [⚠️](https://github.com/CamperSamu/ItemCommander/ "THIS CONTEXT CAN BE DANGEROUS AND MIGHT LEAD TO EXPLOITS, USE WITH CAUTION") |

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
To save a Commander Item, hold it in your main hand and run `/commander save <fileName>`
To load/give a Commander Item, run `/commander give <fileName> [quantity] [player(s)]`

### This mod supports [Patbox' Text Placeholders API](https://pb4.eu/#placeholder-api).
- You can use `%modid:type%` or `%modid:type/data%` inside a Commander Item to make use of a custom placeholder
- Example: `commander create `

### This mod supports [LuckPerms' Fabric Permission API](https://luckperms.net/).
- `commander.command.create` allows a player to use this mod to embed commands in items
- `commander.command.append` allows a player to use this mod to append commands to Commander Items
- `commander.command.give` allows a player to use this mod to give itself Commander Items
- `commander.command.save` allows a player to use this mod to save Commander Items to a file on the server

### Examples
#### Creating a Commander Item
```mcfunction
/commander create "say Hello myself (@s), you clicked at @ix, @iy, @iz!" KEEP OP 20 paper{display:{Name:'[{"text":"Commander Test Item!","italic":false}]'}}
```
#### Appending an extra command to a Commander Item
```mcfunction
/commander append "If you have Text Placeholder API, this will be your name: %player:name%"
```
#### Saving a Commander Item
```mcfunction
/commander save commander_test_item 
```
#### Giving/loading a saved item to a player
```mcfunction
/commander give commander_test_item 1 @p
```

## Bleeding edge builds
Upstream builds are available via [GitHub Actions](https://github.com/CamperSamu/ItemCommander/actions).

___

## Setup

For setup instructions please see the [fabric wiki page](https://fabricmc.net/wiki/tutorial:setup) that relates to the IDE that you are using.

## License

This template is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects.
