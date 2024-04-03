# no-idea-engine
<img src="https://i.imgur.com/aIGEXcp.png" align="right" title="no-idea-engine logo">

No‑idea‑engine is an OpenGL 3.3 game engine for Java heavily inspired by the architecture of GameMaker Studio. The goal is to create an engine suitable for rapid prototyping, as well as releasing moderately-scoped games. Target platforms are PC and Linux.

### Documentation:
View the wiki [here](https://github.com/retrogamer500/no-idea-engine/wiki) for more details. Documentation is an ongoing and some parts may be out of date.

### Features:
* Automatic image atlasing
* Dynamic sprite batching
* Entity component system support
* Customizable collusion resolution + collidable geometry
* Linux + Windows support
* User-defined GLSL shaders
* Customizable loading screens and state transitions
* Game resource management via JSON, ability to define resource groups to load or unload between scenes
* Store resources in directories, inside ZIP files, or within the JAR
* Sandboxed Javascript scripting with ability to extend Java entities
* 3D swept sphere physics and character controller
* Level editor support via [nie-editor](https://github.com/retrogamer500/nie-editor)

### Maven:

To create a game using this engine, add the following to your POM.xml:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.retrogamer500</groupId>
        <artifactId>no-idea-engine</artifactId>
        <version>0.5.1</version>
    </dependency>
</dependencies>
```
