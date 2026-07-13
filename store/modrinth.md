# RealyEasyVanish

A fast, cross-platform vanish plugin for **Paper**, **Spigot**, **Folia** and **Velocity** — packaged as a single jar that auto-detects the environment it's loaded into.

## Why RealyEasyVanish?

- ✅ **Real Folia support** — no `BukkitRunnable`, no wrong-thread exceptions. Player-specific actions (teleports, gamemode changes, packets) run on that player's own region via `RegionScheduler`/`entity.getScheduler()`; global/async work uses `GlobalRegionScheduler`/`AsyncScheduler`.
- ✅ **Network-aware** — vanish state syncs across every backend behind your Velocity proxy, so a vanished player never flickers into visibility when switching servers.
- ✅ **Single jar** — the same download works as a Bukkit-family plugin (Paper/Spigot/Folia) and as a Velocity plugin; each platform reads only its own manifest from the jar.
- ✅ **PlaceholderAPI expansion** bundled (soft dependency, no PAPI required to run the plugin itself).

## Commands

- `/vanish [player]` (`/v`) — toggle vanish, optionally targeting another player
- `/vlist` — clickable list of currently vanished players
- `/vchat` — toggle chat-confirm (block your own messages while vanished)
- `/vreload` — reload configuration
- `/vfollow <player|stop>` — silent camera-lock spectating
- `/vspec <player|stop>` — instant spectator mode on a target
- `/isvanish <player>` — check a player's vanish state
- `/vscoreboard` — toggle a sidebar of vanished players

## Placeholders

`%revanish_is_vanished%` · `%revanish_is_vanished_bool%` · `%revanish_vanished_count%` · `%revanish_visible_online%` · `%revanish_prefix%` · `%revanish_pickup%` · `%revanish_vanished_list%` · `%revanish_visible_player_list%`

## Compatibility

| Platform | Support |
|---|---|
| Paper 1.26.2 | ✅ |
| Spigot 1.26.2 | ✅ |
| Folia | ✅ native region scheduling |
| Velocity | ✅ proxy-wide state sync |

Requires Java 25+. PlaceholderAPI is optional.

## Links

Source code, issue tracker and build instructions are available in the linked GitHub repository.
