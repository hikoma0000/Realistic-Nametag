![example](https://github.com/user-attachments/assets/4b644ee7-1685-435d-bdee-b18cf5cb2ca8)

---

## Features
- Name tags will no longer render through opaque blocks and objects, but will remain visible through transparent ones like glass.

- If a name tag is partially obstructed, only the visible part will be drawn.

- If this mod is installed on the server, clients must also have it installed to join. This ensures fairness.

---

## Configuration
You can configure the mod by editing the `[Server Folder]/saves/[World Name]/serverconfig/realisticnametag-server.toml` in your Minecraft folder.

- **`disableMod`**: Set to `true` to completely disable the mod's features. (Default: `false`)

- **`disableInSpectator`**: Set this to `false` if you want the mod's effects to remain active while in spectator mode. (Default: `true`)

---

## Known Issues
Currently, there is an incompatibility with the `namepain` mod. A solution is being investigated.