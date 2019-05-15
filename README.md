# no-idea-engine
No-idea-engine is an OpenGL 3.3 game engine heavily inspired by the architecture of GameMaker Studio. The goal is to create an engine suitable for rapid prototyping, as well as releasing moderately-scoped games. Target platforms are PC and Linux. Mac support theoretically exists but as they've deprecated OpenGL, I do not test on that platform.

### Features:
* Automatic image atlasing
* Dynamic sprite batching
* Tunable collision broad phase
* Linux + Windows support
* User-defined GLSL shaders
* Customizable loading screens and state transitions
* Automatic loading of resources defined in configuration JSON file

### Pending items for 1.0 release:
* Javadocs
* Documentation
* 2D Background rendering
* Resource loading groups

### Pending items after 1.0:
* Better 3D support
* Built-in level editor
* Particle Systems

### Development:

The engine should be compatible with Java all the way down to Java 8. This engine uses Project Lombok to cut down on boilerplate, so you will probably want to install a plugin for your IDE before making changes.

### Maven:
```
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
        <version>0.2.0</version>
    </dependency>
</dependencies>
```
