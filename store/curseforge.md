# RealyEasyVanish

Cross-platform vanish plugin for **Paper**, **Spigot**, **Folia** and **Velocity**, distributed as a single jar.

## Features

- **Folia-native scheduling** — per-player actions (teleport, gamemode, packets) always run on that player's region thread via the Folia region/entity schedulers, never on a plain `BukkitRunnable`. Falls back cleanly to the classic scheduler on Paper/Spigot.
- **Proxy synchronization** — vanish status is kept consistent across every backend server on a Velocity network, pushed to a server the moment a player connects to it.
- **Single-jar packaging** — one download, loads natively as a Bukkit plugin (Paper/Spigot/Folia) or a Velocity plugin depending on where it's dropped in.
- **PlaceholderAPI expansion** included as a soft dependency.

## Commands

- `/vanish [player]` (alias `/v`) — toggle vanish for yourself or a target
- `/vlist` — interactive list of vanished players
- `/vchat` — toggle chat-confirm while vanished (configurable)
- `/vreload` — reload the config
- `/vfollow <player|stop>` — silently follow a player's camera
- `/vspec <player|stop>` — quick spectator mode
- `/isvanish <player>` — check vanish status
- `/vscoreboard` — toggle a vanished-players sidebar

## Placeholders

```
%revanish_is_vanished%
%revanish_is_vanished_bool%
%revanish_vanished_count%
%revanish_visible_online%
%revanish_prefix%
%revanish_pickup%
%revanish_vanished_list%
%revanish_visible_player_list%
```

## Requirements

Java 25+, Minecraft 26.2 (Paper/Spigot/Folia) or a compatible Velocity build. PlaceholderAPI is optional.

## Source

Open source — see the linked repository for code, issues and build instructions.
