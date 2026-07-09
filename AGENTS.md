# AGENTS.md

## Cursor Cloud specific instructions

Svartmagi is a single-product repo: a NeoForge mod for Minecraft 1.21.1 (Java 21,
Gradle wrapper). There is no database, web service, or `.env`/secrets. Standard dev
commands live in `README.md` (`./gradlew build|runServer|runClient|runData`).

Non-obvious notes for running/testing in the cloud VM:

- **Java 21 is required and preinstalled.** The Gradle wrapper (`./gradlew`) handles
  Gradle 8.10.2 and downloads NeoForge/Minecraft/JEI on first use. The update script
  runs `./gradlew createMinecraftArtifacts` to pre-warm that cache.
- **Lint:** there is no dedicated linter (no checkstyle/spotless); `./gradlew check`
  only runs tests and there are no tests. Treat `./gradlew build` (javac compile) as
  the effective lint/build gate.
- **Dedicated server** (`./gradlew runServer`): starts headless with `--nogui` and
  auto-accepts the EULA in dev — no manual `eula.txt` step needed. It runs until
  killed (it blocks on the console). Run it under a timeout or in a tmux session; do
  not expect it to exit on its own. Run directory is `run/`; the mod's config is
  generated at `run/config/svartmagi-server.toml` and the custom dimension loads as
  `svartmagi:skyggeverden`.
- **GUI client** (`./gradlew runClient`): the VM has a virtual display (`:1`) but no
  GPU. Launch with software OpenGL or the window fails to create:
  `LIBGL_ALWAYS_SOFTWARE=1 MESA_GL_VERSION_OVERRIDE=4.6 ./gradlew runClient`.
  Rendering is slow (software rasterizer) so allow extra time for world gen and menu
  transitions. ALSA "cannot find card" / `audio_open_alsa` errors are harmless (no
  sound card).
- **Mod content check:** in a Creative world, open the inventory search and look up
  Svartmagi items (e.g. "generator" → Coal Generator) to confirm the mod loaded, then
  place a block to verify gameplay end-to-end.
