# AutoMessenger  
### What does it do?  
A simple server-side mod that allows the server to send custom messages every X ticks. Requires Fabric API.
- [Modrinth Mod page](https://modrinth.com/mod/automessenger)
- [CurseForge Mod page](https://www.curseforge.com/minecraft/mc-mods/automessenger-fabric)



#### Known bugs and stuff
1. Config directory detection to be tested on Linux and Weendows. (aparently im dumb and made the mod only detect \config, and not /config)
3. Requires the config directory generated before using the mod, otherwise it'll crash the server. It should be able to generate the directory and the configuration file, not the latter only.

### Current Minecraft versions supported:
- 1.20.1
- 1.20
- 1.19.4
- 1.19.3
- 1.19.2
- 1.19.1
- 1.19
- Versions below 1.19 don't have support for Simplified Text Format.
