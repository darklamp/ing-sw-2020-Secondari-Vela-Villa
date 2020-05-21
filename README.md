# ing-sw-2020-Secondari-Vela-Villa

client jar options:
* -port : specifies Server port (default: 1337)
* -cli  : cli true/false        (default: false)
* -ip   : defines server IP     (default: localhost)
* -v    : print verbose output  (default: no)


example: java --enable-preview -jar client.jar -port=1337 -cli -ip=127.0.0.1 -v

server jar options:
* -port : specifies Server port            (default: 1337)
* -ip   : defines IP to listen on (v4/v6)  (default: localhost)
* -console : starts debug console on stdin (default: no)
* -v    : print verbose output  (default: no)

example: java --enable-preview -jar server.jar -port=1337 -ip=::1

debug console commands:

* kick [gameIndex] [playerName]    : kicks player from game
* players [gameIndex]              : returns list of players for given gameIndex