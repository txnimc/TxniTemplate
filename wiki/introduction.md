---
outline: deep
---

# Introduction 

*Hello, and welcome to multiversion hell!*

## Why Multiversion?

Instead of having separated Git branches, **multiversion** Minecraft mods build all jars from a single monorepo.
While it makes Gradle scripts more complicated to set up, it massively simplifies updates across many supported versions.

Instead of having separated Git repos for Forge and Fabric, **multiloader** Minecraft mods build from one repo,
typically with a lot of shared code.

This template combines both, and is also set up with preprocessors to build from a single `src` directory.
This means no separated `common`, `forge`, and `fabric` sourcesets like with traditional Architectury
multiloader mods, which makes development much easier. I want to encourage more people to build mods this way,
because it's better for everyone if projects are built with wide loader and version support from the start!

## What's in Here?

This template uses [Essential Gradle Toolkit](https://github.com/EssentialGG/essential-gradle-toolkit),
and is set up with the following:

- Documentation for every part of the build toolchain
- Fabric, Forge, and NeoForge in one sourceset
- Preconfigured support for all modern 'LTS' versions, in one sourceset (1.18.2, 1.19.2, 1.20.1, 1.21 by default, other versions can be added)
- GitHub Actions workflows to automatically deploy published jars
- Mixins for all platforms
- Access Wideners / Transformers
- Cross-platform Forge Config (FC API Port)
- Cross-platform Registrate
- Porting Lib for Forge
- Forge and Fabric Datagen
- Shadow

In addition to all that, I've also set up some helper scripts to make forking this template easier (more on that below),
and set up Manifold preprocessor directives (which are much easier to use than ReplayMod preprocessor).

## Who can Use This?

This template is specifically built for the community! It is MIT licensed and allows anyone to fork and relicense,
and it's easy to do so if you click the "Use this template" button in the top right corner:

![img.png](assets/usethistemplate.png)

You can also depend on my personal library, [TxniLib](/lib), which contains some multiloader/multiversion
abstractions that I find helpful. I do my best to document it, though it isn't a top priority.

However, this is definitely a more advanced project setup, and I would not recommend it to someone just getting started
on mod development. If you are not already familiar with both Forge & Fabric toolchains, events, APIs, etc, you will
likely have a harder time getting things working because of the barrier to entry inherent with multiloader mods.

With that said, I hope this readme covers most of the things you will need to know, but you can always [contact me on Discord](https://discord.gg/kS7auUeYmc)
at `toni.toni.chopper` for questions and help setting things up.