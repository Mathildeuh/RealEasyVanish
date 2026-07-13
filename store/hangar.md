# RealyEasyVanish

A fast, cross-platform vanish plugin built for **Paper**, **Spigot**, **Folia** and **Velocity** — from a single jar.

## Highlights

- **Native Folia support** — every per-player mutation (teleport, gamemode, packets) runs through the region/entity scheduler, never the classic `BukkitRunnable`. No "called from wrong thread" warnings, no plugin.yml `folia-supported` lies.
- **Proxy-wide sync** — vanish state follows a player across every backend server on your Velocity network, pushed the instant they connect so there is never a visibility gap.
- **One jar, every platform** — the same file loads as a Bukkit plugin on Paper/Spigot/Folia and as a Velocity plugin on your proxy, each via its own manifest bundled inside.
- **PlaceholderAPI expansion** included, soft-depend — only registers if PAPI is present.

## Commands

| Command | Description |
|---|---|
| `/vanish [player]` (alias `/v`) | Toggle vanish for yourself or another player |
| `/vlist` | Interactive, clickable list of vanished players |
| `/vchat` | Toggle whether your chat is blocked while vanished (configurable) |
| `/vreload` | Reload the configuration |
| `/vfollow <player\|stop>` | Silently lock your camera onto a player |
| `/vspec <player\|stop>` | Quick spectator mode on a target |
| `/isvanish <player>` | Check whether a player is vanished |
| `/vscoreboard` | Toggle a sidebar listing vanished players |

## Placeholders

```
%revanish_is_vanished%          Yes / No
%revanish_is_vanished_bool%     true / false
%revanish_vanished_count%       number of vanished players
%revanish_visible_online%       number of visible players
%revanish_prefix%               configurable vanish prefix
%revanish_pickup%                item pickup status while vanished
%revanish_vanished_list%        comma-separated vanished names
%revanish_visible_player_list%  comma-separated visible names
```

## Requirements

- Java 25+
- Minecraft 26.2 (Paper / Spigot / Folia) or a compatible Velocity proxy build
- PlaceholderAPI (optional, for placeholders)

## Permissions

`revanish.vanish`, `revanish.vanish.others`, `revanish.vlist`, `revanish.vchat`, `revanish.vreload`, `revanish.vfollow`, `revanish.vspec`, `revanish.isvanish`, `revanish.vscoreboard`, `revanish.see` (bypass vanish visibility).

## Source

Fully open source: see the repository linked on this page for the source, issue tracker and build instructions.
