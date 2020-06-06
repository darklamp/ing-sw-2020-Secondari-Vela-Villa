# SANTORINI : Progetto Ing.SW 2020



[![codecov](https://codecov.io/gh/darklamp/ing-sw-2020-Secondari-Vela-Villa/branch/master/graph/badge.svg?token=PF3WCGV0B5)](https://codecov.io/gh/darklamp/ing-sw-2020-Secondari-Vela-Villa)

Test game server :  **server.santorini.cf**

Online CLI: [**here**](http://server.santorini.cf:4200)

Before using this web server and the online CLI,  make sure to [click on this link](http://server.santorini.cf:1338) three times.


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

## Deliverables

Github is set up to run the building routine on every push. Hence, pre-packaged multiplatform JARs can be found under the [***Actions***](https://github.com/darklamp/ing-sw-2020-Secondari-Vela-Villa/actions?query=branch:master) tab. 

## Building
 
#### Requirements:

* Java 14 (because of [switch expressions](https://openjdk.java.net/jeps/361))
* Maven 3.6.0+

Maven is set up so as to create two jars: a server one and a client one. By default, the pom detects the OS on which Maven is running and packages the client jar with the needed JavaFX libraries only.
In case you want to create multi-platform jars, just add ``` -Pmulti ``` to the Maven command below.

```
mvn clean package
```

---

## Server options:

* -port : specifies Server port            (default: 1337)
* -ip   : defines IP to listen on (v4/v6)  (default: localhost)
* -console : starts debug console on stdin (default: no)
* -v    : print verbose output  (default: no)
* -disk : persistence flag (default: no)

example:

```
java -jar server.jar -port=1337 -ip=0.0.0.0
```

#### debug console commands:

* kick [gameIndex] [playerName]    : kicks player from game
* players [gameIndex]              : returns list of players for given gameIndex / for all games if index not given
* save                             : saves current games' state

## Client options:

* -port : specifies Server port (default: 1337)
* -cli  : cli true/false        (default: false)
* -ip   : defines server IP     (default: localhost)
* -v    : print verbose output  (default: no)

example: 

```
java -jar client.jar -port=1337 -ip=::1
```
