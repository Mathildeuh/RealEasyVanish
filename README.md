# RealyEasyVanish

[![Release](https://github.com/Mathildeuh/RealEasyVanish/actions/workflows/release.yml/badge.svg)](https://github.com/Mathildeuh/RealEasyVanish/actions/workflows/release.yml)
[![Qodana](https://github.com/Mathildeuh/RealEasyVanish/actions/workflows/qodana_code_quality.yml/badge.svg)](https://github.com/Mathildeuh/RealEasyVanish/actions/workflows/qodana_code_quality.yml)
[![](https://jitpack.io/v/Mathildeuh/RealEasyVanish.svg)](https://jitpack.io/#Mathildeuh/RealEasyVanish)

A vanish plugin for Paper/Folia, with vanish state synced live across a Velocity proxy network.

## Commands

| Command       | Description                                                    |
|---------------|------------------------------------------------------------------|
| `/vanish [player]` | Toggle vanish for yourself or another player                |
| `/vlist`      | List currently vanished players                                  |
| `/vchat`      | Toggle whether your chat messages are blocked while vanished     |
| `/vreload`    | Reload the RealyEasyVanish configuration                         |
| `/vfollow <player\|stop>` | Silently lock your camera onto a player               |
| `/vspec <player\|stop>`   | Quickly enter spectator mode on a player               |
| `/isvanish <player>`      | Check whether a player is vanished                     |
| `/vscoreboard`            | Toggle the vanished-players sidebar                     |
| `/list`                   | Vanilla `/list`, shadowed to exclude vanished players    |

## Modules

- **api** — public interfaces shared across platforms, published standalone (see below).
- **common** — platform-agnostic vanish logic, config, and sync protocol.
- **bukkit** — Paper/Folia implementation (listeners, commands, scoreboard, PlaceholderAPI expansion).
- **velocity** — Velocity proxy implementation, relays vanish state between backends.
- **plugin** — shades `bukkit` + `velocity` into the distributable jar.

## Building

```bash
./gradlew build
```

The shaded plugin jar is produced at `plugin/build/libs/RealyEasyVanish-<version>.jar`.

## Using the API

The `api` module is published on [JitPack](https://jitpack.io/#Mathildeuh/RealEasyVanish) for anyone building against RealyEasyVanish.

```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.github.Mathildeuh.RealEasyVanish:api:v1.0.0")
}
```

## License

Apache License 2.0, see [LICENSE](LICENSE).
