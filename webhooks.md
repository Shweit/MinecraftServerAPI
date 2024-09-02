# MinecraftServerAPI WebHooks

## Overview
MinecraftServerAPI allows you to configure various WebHooks that are triggered by specific events on your Minecraft server. These WebHooks can be used to send notifications or execute specific actions when events occur in the game.

Below is a list of all possible WebHooks supported by MinecraftServerAPI, along with their default activation status.

## WebHooks List

| WebHook                | Description                                                                      | Default Status |
|------------------------|----------------------------------------------------------------------------------|----------------|
| `server_start`         | Triggered when the server starts.                                                | Enabled        |
| `server_stop`          | Triggered when the server stops.                                                 | Enabled        |
| `plugin_disable`       | Triggered when a plugin is disabled.                                             | Enabled        |
| `plugin_enable`        | Triggered when a plugin is enabled.                                              | Enabled        |
| `block_break`          | Triggered when a block is broken.                                                | Disabled       |
| `block_place`          | Triggered when a block is placed.                                                | Disabled       |
| `block_burn`           | Triggered when a block burns.                                                    | Disabled       |
| `block_redstone`       | Triggered when a redstone event occurs.                                          | Disabled       |
| `note_play`            | Triggered when a note block is played.                                           | Enabled        |
| `sign_change`          | Triggered when a sign is changed.                                                | Enabled        |
| `enchant_item`         | Triggered when an item is enchanted.                                             | Enabled        |
| `creeper_power`        | Triggered when a creeper is struck by lightning and becomes charged.             | Enabled        |
| `creature_spawn`       | Triggered when a creature spawns.                                                | Disabled       |
| `entity_death`         | Triggered when an entity dies.                                                   | Disabled       |
| `entity_explode`       | Triggered when an entity explodes.                                               | Enabled        |
| `entity_shoot_bow`     | Triggered when an entity shoots a bow.                                           | Enabled        |
| `entity_tame`          | Triggered when an entity is tamed.                                               | Enabled        |
| `explosion_prime`      | Triggered when an explosion is primed.                                           | Enabled        |
| `player_death`         | Triggered when a player dies.                                                    | Enabled        |
| `brew`                 | Triggered when a brewing event occurs.                                           | Enabled        |
| `craft_item`           | Triggered when an item is crafted.                                               | Disabled       |
| `furnace_burn`         | Triggered when a furnace starts burning.                                         | Disabled       |
| `furnace_smelt`        | Triggered when a furnace smelts an item.                                         | Disabled       |
| `player_chat`          | Triggered when a player sends a chat message.                                    | Disabled       |
| `player_login`         | Triggered when a player logs in.                                                 | Enabled        |
| `player_command`       | Triggered when a player issues a command.                                        | Disabled       |
| `player_gamemode_change` | Triggered when a player's game mode changes.                                   | Enabled        |
| `player_item_break`    | Triggered when a player breaks an item.                                          | Enabled        |
| `player_join`          | Triggered when a player joins the server.                                        | Enabled        |
| `player_kick`          | Triggered when a player is kicked from the server.                               | Enabled        |
| `player_quit`          | Triggered when a player quits the server.                                        | Enabled        |
| `player_respawn`       | Triggered when a player respawns.                                                | Enabled        |
| `lightning_strike`     | Triggered when a lightning strike occurs.                                        | Enabled        |
| `weather_change`       | Triggered when the weather changes.                                              | Enabled        |
| `thunder_change`       | Triggered when thunder starts or stops.                                          | Enabled        |
| `world_load`           | Triggered when a world is loaded.                                                | Enabled        |
| `world_save`           | Triggered when a world is saved.                                                 | Enabled        |
| `world_unload`         | Triggered when a world is unloaded.                                              | Enabled        |

## Configuration
You can enable or disable these WebHooks by modifying the `config.yml` file located in the `plugins/MinecraftServerAPI` directory. Each WebHook can be toggled individually, allowing you to control which events trigger notifications.

For more details on configuring WebHooks, please refer to the [main README](README.md).

