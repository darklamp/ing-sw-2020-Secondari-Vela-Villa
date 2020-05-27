# SANTORINI : Progetto Ing.SW 2020 [![codecov](https://codecov.io/gh/darklamp/ing-sw-2020-Secondari-Vela-Villa/branch/master/graph/badge.svg?token=PF3WCGV0B5)](https://codecov.io/gh/darklamp/ing-sw-2020-Secondari-Vela-Villa)


| Functionality | State |
|:-----------------------|:------------------------------------:|
| Complete rules | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| GUI (JavaFX) | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| CLI | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Multiple games | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Persistence | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Undo | [![RED](https://placehold.it/15/f03c15/f03c15)](#) |
| Additional gods | [![RED](https://placehold.it/15/f03c15/f03c15)](#) |


### Maven

Maven is set up so as to create two jars: a server one and a client one. The client one is, by choice, multiplatform:
the additional weight is due to it including JavaFX libraries for every OS. It is possible to get a lighter client jar by removing other OS's JavaFX libraries from the pom.xml file. 

```
mvn clean package
```

### Server
Requires java 14 AND the --enable-preview flag (**NOTE**: the only use of this is the "Text Blocks" feature, which makes it easier and cleaner to handle long &&/|| complex strings).

#### options:

* -port : specifies Server port            (default: 1337)
* -ip   : defines IP to listen on (v4/v6)  (default: localhost)
* -console : starts debug console on stdin (default: no)
* -v    : print verbose output  (default: no)
* -disk : on startup, the server tries to load previously saved games (default: no)

example:

```
java --enable-preview -jar server.jar -port=1337 -ip=0.0.0.0
```

##### debug console commands:

* kick [gameIndex] [playerName]    : kicks player from game
* players [gameIndex]              : returns list of players for given gameIndex
* save                             : saves current games' state

### Client

Requires java 14 AND the --enable-preview flag (see server). Note: using ```-Dprism.forceGPU=true``` may resolve javafx-related problems (this is not related to the project, and apparently only useful on linux). 

#### options:

* -port : specifies Server port (default: 1337)
* -cli  : cli true/false        (default: false)
* -ip   : defines server IP     (default: localhost)
* -v    : print verbose output  (default: no)

example: 

```
java --enable-preview -jar client.jar -port=1337 -ip=::1
```
