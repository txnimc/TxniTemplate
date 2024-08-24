---
outline: deep
---

# Dependencies

Here you will find a list of everything included by this template, what's needed at runtime, and some 
alternatives if you choose not to use these things.

If you want a list of the changes made by the different flavors, that's [outlined over here](/flavors).

## Forge Config API Port (Fabric)

To provide a unified config system with Server -> Client syncing, this template uses the Forge Config API Fabric 
port from Fuzs. By default, it's JarJar'd into the mod, but you can remove this and add it as a dependency on Curse/Modrinth
if you would like.

This uses NeoForge's config namespaces on 1.21+