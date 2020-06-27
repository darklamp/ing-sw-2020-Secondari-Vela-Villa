#     SANTORINI : Progetto Ing.SW 2020   [![codecov](https://codecov.io/gh/darklamp/ing-sw-2020-Secondari-Vela-Villa/branch/master/graph/badge.svg?token=PF3WCGV0B5)](https://codecov.io/gh/darklamp/ing-sw-2020-Secondari-Vela-Villa)

![asd](https://i.imgur.com/g7BuRZO.png)


JavaDoc: [**here**](https://server.santorini.cf/javadoc) User: GC46, pwd: santorini  
  
Online CLI (ASCII not 100% working): [**here**](https://server.santorini.cf)   
   
Test game server :  **server.santorini.cf**  


| Functionality | State |
|:-----------------------|:------------------------------------:|
| Complete rules | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| GUI (JavaFX) | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| CLI | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Multiple games | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Persistence | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Undo | [![RED](https://placehold.it/15/f03c15/f03c15)](#) |
| Additional gods | [![RED](https://placehold.it/15/f03c15/f03c15)](#) |

---
## Unit Tests

An accurate coverage report can be found [on CodeCov](https://codecov.io/gh/darklamp/ing-sw-2020-Secondari-Vela-Villa/tree/master/src/main/java/it/polimi/ingsw).
Intellij's coverage report is also available in the deliverables folder. This is the coverage percentage for model and controller, extracted from IntelliJ's report.


![IntelliJ Coverage](https://i.imgur.com/qQQj9CO.png)

---


## Deliverables

[Signature key](https://darklamp.github.io/ale/assets/pubkey.asc).
Pre-packaged multi-platform JARs can be found under the deliverables folder,  under the [***Release***](https://github.com/darklamp/ing-sw-2020-Secondari-Vela-Villa/releases) tab, or under the [***Actions***](https://github.com/darklamp/ing-sw-2020-Secondari-Vela-Villa/actions?query=branch:master) tab.


---


## Building
 
#### Requirements:

* Java 14 (because of [switch expressions](https://openjdk.java.net/jeps/361))
* Maven 3.6.0+

Maven is set up so as to create three jars: a server one and a two client ones: one contains both gui and cli, whereas the other is cli-only. By default, the pom detects the OS on which Maven is running and packages the client jar with the needed JavaFX libraries only.
In case you want to create multi-platform jars, just add ``` -Pmulti ``` to the Maven command below.

```
mvn clean package
```

---


## Server options:

| Option | Description | Default | Possible values
|---------|:----------:|:------------:|-----------:|
| port | specifies Server port | 1337 | |
| ip   | defines IP to listen on | 0.0.0.0 | any v4/v6, or any hostname |
| console | starts debug console on stdin | false |  |
| verb | verbosity | info | debug > info > warn > error |
| disk | persistence flag | false | true/false |
| time | set allowed time for each turn | 2 minutes (2,m) | s, m, h |
| motd | change MOTD | "Have fun!" | |
| maxPlayers | maximum number of players | 3 | 2 or 3 |
| gods | choose available gods | default basic 9 gods | any composition of size >= 2 |

All options (except the gods list) can be provided via command line and via configuration file. In case both are provided, command line takes precedence.
An example configuration file [is provided](https://github.com/darklamp/ing-sw-2020-Secondari-Vela-Villa/blob/master/santorini.yaml).
An error log file (error.log) is used to keep track of errors/traces.

command line example:

```
java -jar server.jar -verb=info -port=1337 -ip=server.santorini.cf -time=55,s -motd="Hey!" -console -disk
```

#### debug console commands:
| Command | Parameters | Description |
|---------|:----------:|------------:|
| kick | [gameIndex] [playerName] | kicks player from game |
| players | [gameIndex] | returns list of players for given gameIndex / for all games if index not given |
| save | n/a | saves current games' state |
| motd | [message] | if a string is provided, the motd is set to the string, else the current motd is printed |
| close | n/a | stop console |
| kill | n/a | stop server |


---


## Client options:

Note: especially on Linux/MacOS, setting the JVM ```-Dprism.forceGPU=true``` flag might help in case the graphics look slow / misaligned.

| Command | Description | Default |
|---------|:----------:|------------:|
| port    | specifies Server port | 1337 |
| cli     | CLI true/false | false |
| ip      | defines server IP | localhost |
| v       | print verbose output | no |

example: 

```
java -jar client.jar -port=1337 -ip=1:5ee:bad::c0de -cli
```
