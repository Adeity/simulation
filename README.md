# simulation
Semester project for ,,Programming in Java" subject. Summer semester 2020/2021. 

Detailed documentation including testing details and strategies: [SimulaceDokumentace.v2.pdf](https://github.com/Adeity/simulation/files/6724896/SimulaceDokumentace.v2.pdf) (only in Czech)


Here you can see a video of it running in distrubuted mode(most left window represents server, other windows represent clients):
https://www.youtube.com/watch?v=AUp1kyzgO08




Simulation simulates life of foxes and hare on a two dimensional square shaped map made of tiles with various terrains (grass, bush, water).

Foxes eat hare, they mate and die of hunger. Hare get eaten by foxes and they mate.
Fox can eat a hare if it stands on a tile(block) next to it. However if the hare stands in a bush tile and fox doesn't then the fox can't see the hare.

The same goes with mating. Two foxes (hare) can mate with each if they stand on a tile(block) next to each. They also must not be hungry and must have enough energy.

If an animal either fox or hare looks around and doesn't see another animal to interact with, it moves. Animals move one tile per simulation cyclus (day). Possible move directions are up, down, left or right.
It is possible to move to a tile if tile exists and there isn't already another animal or if terrain allows it.
## GUI
I am using Java Swing libraries to render GUI.

## Distributed Simulation
Simulation can work in client-server mode. Connection between them uses TCP protocol, therefore there is a socket implementation of this connection.

Clients can connect to the server. Clients will send their simplified representation of their local map to the server, so server can graphically render a global map made of each client's map.

Also clients send requests to the server, e.g. if they need to know what is on a tile (block) that an animal wants to move to, client sends GET_BLOCK request.

Server keeps information about each client's position on a global map. If a request comes, it can tell which client it is from. Server will get the needed information to answer client's request from another client.

Communication protocol:
- SET_BLOCK [UUID] 1 1 [SERIALIZED BLOCK] // client version. e.g. client wants to move animal to another block. at this point client knows that block is free to move to.
- SET_BLOCK [UUID] 0 0 3 3 0 0 [SERIALIZED BLOCK] // server version. server receives SET_BLOCK request from client and it reroutes it to another client who owns the block.
- SET_BLOCK_RESULT [UUID] 0 0 2 2 0 0 TRUE/FALSE // client sends an answer to SET_BLOCK request. either true or false.
- MAP 100 // this is the first message server sends to client when client connects to server. 100 means map size must be 100*100. 
- STATE READY // client sends this to server when it is ready to simulate another day
- STATE GO // client sends this to server when it has started simulating a day
- STATE SET // client sends this to server when it is in state SET. 
- GO // server sends this to each client at once to simulate a day
- 3
- SET // server sends this to client for client to get in SET state
- GET_BLOCK [UUID] 9 9 10 10 0 0 // server version. GET_BLOCK request comes from client and server sends this request to client who owns desired block. 
- GET_BLOCK [UUID] -1 -1 // client version. client needs information about a block
- BLOCK [UUID] 0 0 10 10 0 0 [SERIALIZED_BLOCK] // this is a response to GET_BLOCK request
