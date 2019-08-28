# no-idea-engine
<img src="https://i.imgur.com/aIGEXcp.png" align="right" title="no-idea-engine logo">

No‑idea‑engine is an OpenGL 3.3 game engine for Java heavily inspired by the architecture of GameMaker Studio. The goal is to create an engine suitable for rapid prototyping, as well as releasing moderately-scoped games. Target platforms are PC and Linux.

### Documentation:
View the wiki [here](https://github.com/retrogamer500/no-idea-engine/wiki) for more details. Documentation is an ongoing effort.

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

### Future Features:
* Improved 3D support
* Bullet Physics and Box2D physics support
* Improved arbitrary 2D primitive rendering beyond lines, boxes, and circles

### Forking:

The engine should be compatible with Java all the way down to Java 8. This engine uses Project Lombok to cut down on boilerplate, so you will probably want to install a plugin for your IDE before making changes.

### Maven:

To create a game using this engine, add the following to your POM.xml:

```xml
<repositories>
    <repository>
        <id>noideaengine-mvn-repo</id>
        <url>https://github.com/retrogamer500/no-idea-engine/raw/mvn-repo</url>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>net.loganford.noideaengine</groupId>
        <artifactId>noideaengine</artifactId>
        <version>0.3.0</version>
    </dependency>
</dependencies>
```
