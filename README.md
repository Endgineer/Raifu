# Raifu

Raifu is a NeoForge 1.21.1 library mod for per-player lives and opt-in hardcore
states. Other mods can toggle hardcore independently for each player through
`com.endgineer.raifu.api.RaifuApi`. Each integration supplies a namespaced
source, and a player is hardcore while at least one source is active:

```java
ResourceLocation source = ResourceLocation.fromNamespaceAndPath("silentmoon", "silentmoon");
RaifuApi.setHardcore(player, source, true);
RaifuApi.setHardcore(player, source, false);
```

Disabling one source does not affect requests made by other mods. Source
registrations are persisted with the player's Raifu data.

Features include persistent lives, a configurable lives HUD, rare Heart
Containers, and optional pooled lives for Open Parties and Claims parties.
