![example](https://github.com/user-attachments/assets/4b644ee7-1685-435d-bdee-b18cf5cb2ca8)

## Features
- Name tags will no longer render through opaque blocks and objects, but will remain visible through transparent ones like glass.

- If a name tag is partially obstructed, only the visible part will be drawn.

- Name tags will be hidden for entities with the Invisibility effect.

- The implementation is designed to be as close to vanilla behavior as possible.






## Configuration
You can configure the mod by editing the `config/realisticnametag-client.toml` in your Minecraft folder.

- **`enableMod`**: Set to `false` to completely disable the mod's features. (Default: `true`)

- **`disableInSpectator`**: Set this to `false` if you want the mod's effects to remain active while in spectator mode. (Default: `true`)