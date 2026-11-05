# ChronoArena

A real-time multiplayer arena game in Java. It uses a multithreaded **TCP/UDP client-server** design. Players move around a shared map, capture and hold control zones, collect items and freeze each other. The server owns the game state and runs a fixed-tick loop that processes actions in a fair order. Clients render the match in a **Swing** GUI.

Built with plain Java (no external libraries or build tool).

---

## Table of Contents

1. [Features](#features)
2. [Gameplay & Rules](#gameplay--rules)
3. [Controls](#controls)
4. [Architecture](#architecture)
5. [Multithreading & Concurrency](#multithreading--concurrency)
6. [Networking Protocol](#networking-protocol)
7. [Project Structure](#project-structure)
8. [Requirements](#requirements)
9. [How to Run (A–Z)](#how-to-run-az)
10. [Configuration](#configuration)
11. [Server Console Commands](#server-console-commands)
12. [Troubleshooting](#troubleshooting)

---

## Features

- **The server owns the game state.** Clients only send inputs, and the server decides every outcome.
- **Two transports:**
  - **TCP**: join handshake, game state snapshots and server notices (reliable)
  - **UDP**: player actions such as movement and attacks (fast, low latency)
- **Fixed-tick game loop** at 20 ticks per second, with snapshots sent every 100 ms
- **Fair action queue:** actions are sorted by server receive time, then player ID, then sequence number, so they resolve in a fixed order
- **Zone control:** capturing, contested, controlled and grace-period states
- **Items:** Energy (points) and Freeze (lets you freeze a nearby opponent)
- **Kill switch:** the server operator can remove a player from the console
- **Swing client:** game canvas, HUD with scores, timer and notices, and keyboard input

---

## Gameplay & Rules

| Rule | Value |
|---|---|
| Map size | 1000 × 700 |
| Match length | 180 seconds |
| Match starts | When **2 or more** players have joined |
| Player speed | 8 px per move action |
| Zones | `ZONE-A` (180,150) and `ZONE-B` (650,350), each 140×140 |
| Zone capture time | Stand alone in a zone for **3 s** |
| Zone points | **+5 points every second** while you hold your zone |
| Zone grace period | If the owner leaves, they keep the zone for **5 s** before it resets |
| Contested zone | 2 or more players inside, or a challenger inside an owned zone. No capture progress and no points |
| Energy item | **+10 points** |
| Freeze item | Grants one freeze attack |
| Freeze attack | Freezes the nearest opponent within **90 px** for **2 s**. 5 s cooldown |
| Item spawning | Every 4 s, up to 6 items on the map |
| Winner | Highest score when the timer reaches zero |

---

## Controls

| Key | Action |
|---|---|
| `W` / `↑` | Move up |
| `S` / `↓` | Move down |
| `A` / `←` | Move left |
| `D` / `→` | Move right |
| `Space` / `F` | Freeze attack (needs a Freeze item) |

---

## Architecture

```
┌────────────────────────┐                    ┌──────────────────────────────┐
│        CLIENT          │                    │            SERVER            │
│                        │   TCP: JoinRequest │                              │
│  GameFrame (Swing)     │ ─────────────────▶ │  TcpClientHandler (1/client) │
│   ├─ GamePanel         │ ◀───────────────── │   └─ registers ClientSession │
│   └─ HudPanel          │   TCP: JoinResponse│                              │
│                        │                    │                              │
│  InputController ──▶   │   UDP: actions     │  UdpListener                 │
│  UdpSender             │ ─────────────────▶ │   └─▶ FairActionQueue        │
│                        │                    │            │                 │
│  TcpServerListener ◀── │   TCP: snapshots   │  GameLoop (20 ticks/s)       │
│   └─▶ ClientState      │ ◀───────────────── │   ├─ drainSorted()           │
│                        │                    │   ├─ GameStateManager        │
│                        │                    │   └─ broadcast snapshot      │
└────────────────────────┘                    └──────────────────────────────┘
```

**Package overview**

- `common`: shared, serializable models and protocol messages used by both sides (`GameSnapshot`, `PlayerState`, `ZoneStateModel`, `ItemState`, `TcpMessage`, `UdpActionMessage`, `Constants`, `PropertiesLoader`, and others)
- `server`: server runtime, game rules, the fair action queue, spawning, the kill switch and the operator console
- `client`: networking, local state cache, Swing rendering and input handling

---

## Multithreading & Concurrency

The server and client each run several threads at once:

**Server threads**

| Thread | Role |
|---|---|
| `main` | Accepts TCP connections in a loop |
| `tcp-client-<port>` | One per connected client. Handles the join handshake and TCP messages |
| `udp-listener` | Receives UDP action packets and adds them to the `FairActionQueue` |
| `game-loop` | Fixed 20 Hz tick: takes all queued actions, applies the rules and sends snapshots |
| `server-console` | Daemon thread that reads operator commands from stdin |
| Shutdown hook | Shuts the server down cleanly on Ctrl+C |

**Client threads**

| Thread | Role |
|---|---|
| `main` | Connects to the server and starts the GUI |
| `tcp-server-listener` | Receives snapshots and notices and updates `ClientState` |
| Swing EDT | Draws the game. A Swing `Timer` sends held movement keys every 60 ms |

**How thread safety is handled**

- `ConcurrentHashMap` stores sessions, players, zones and items, plus the kill-switch set
- `ConcurrentLinkedQueue` is the lock-free action queue, written by the UDP thread and read by the game loop
- `synchronized` methods in `GameStateManager` make each tick's state changes atomic, and also guard client registration
- `synchronized (outputStream)` stops two threads from writing to the same TCP stream at once (snapshot broadcasts vs. direct messages)
- `AtomicInteger` / `AtomicLong` / `AtomicBoolean` hold player ID sequences, UDP sequence numbers and flags
- `volatile` running flags let threads shut down cleanly
- Defensive `copy()` calls: snapshots hold copies of the game state, so serializing them never races with the game loop

---

## Networking Protocol

**TCP (Java object streams, `TcpMessage` envelope)**

| Type | Direction | Payload |
|---|---|---|
| `JOIN_REQUEST` | client → server | `JoinRequest` (player name, client UDP port) |
| `JOIN_RESPONSE` | server → client | `JoinResponse` (accepted flag, assigned player ID, spawn position, message) |
| `GAME_SNAPSHOT` | server → client | `GameSnapshot` (players, zones, items, timer, notice, winner) |
| `SERVER_NOTICE` | server → client | Text notice |
| `CLIENT_DISCONNECT` | client → server | Sent on a clean disconnect |

**UDP (`UdpActionMessage`, serialized with `SerializationUtils`)**

`MOVE_UP`, `MOVE_DOWN`, `MOVE_LEFT`, `MOVE_RIGHT`, `STOP`, `FREEZE_ATTACK`. Each packet carries the player ID, a sequence number and the client timestamp.

---

## Project Structure

```
ChronoArena/
├── config/
│   ├── server.properties      # server host/ports
│   └── client.properties      # server address, player name, local UDP port
├── src/
│   ├── Main.java              # IntelliJ placeholder (not used)
│   ├── common/                # shared models, protocol, constants
│   ├── server/
│   │   ├── ServerMain.java    # ▶ server entry point
│   │   ├── GameServer.java
│   │   ├── GameLoop.java
│   │   ├── GameStateManager.java
│   │   ├── FairActionQueue.java / QueuedPlayerAction.java
│   │   ├── TcpClientHandler.java / UdpListener.java / ClientSession.java
│   │   ├── SpawnManager.java / KillSwitchManager.java
│   │   └── ServerConsole.java
│   └── client/
│       ├── ClientMain.java    # ▶ client entry point
│       ├── GameClient.java / ClientState.java
│       ├── TcpServerListener.java / UdpSender.java
│       ├── InputController.java
│       └── GameFrame.java / GamePanel.java / HudPanel.java
└── README.md
```

---

## Requirements

- **JDK 17 or newer** (tested with JDK 23)
- Any OS that can show a Swing window (macOS, Windows, Linux with a desktop)
- No external dependencies

Check your Java version:

```bash
java -version
javac -version
```

---

## How to Run (A–Z)

### A. Get the code

```bash
git clone https://github.com/ranjot001/ChronoArena.git
cd ChronoArena
```

### B. Compile

Run this from the project root (the folder that contains `src/` and `config/`):

**macOS / Linux**
```bash
javac -d out $(find src -name "*.java")
```

**Windows (PowerShell)**
```powershell
javac -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
```

**Windows (cmd)**
```cmd
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
```

### C. Start the server (terminal 1)

```bash
java -cp out server.ServerMain
```

You should see:
```
ChronoArena server started
TCP server listening on 0.0.0.0:5005
UDP server listening on 0.0.0.0:6005
server>
```

### D. Start client #1 (terminal 2)

```bash
java -cp out client.ClientMain
```

A game window opens. The HUD shows **"Waiting for players..."** until a second player joins.

### E. Start client #2 (terminal 3)

To give each player their own name, copy the client config:

```bash
cp config/client.properties config/client2.properties
# edit client.player.name=Player2 in config/client2.properties
java -cp out client.ClientMain config/client2.properties
```

You can also run `java -cp out client.ClientMain` again with the default config. The match starts as soon as 2 players are connected.

### F. Play

Click the game window to give it keyboard focus. Then use WASD or the arrow keys to move and Space or F to freeze. Capture zones and collect items to score.

### G. Play across multiple computers (LAN)

1. On the server machine, find its LAN IP (e.g. `192.168.1.20`).
2. Make sure the firewall allows **TCP 5005** and **UDP 6005**.
3. On each client machine, set `server.host=192.168.1.20` in `config/client.properties`.
4. Run the client as in step D.

### H. Run from IntelliJ IDEA (optional)

1. Open the `ChronoArena` folder as a project.
2. Mark `src` as the Sources Root if it isn't already.
3. Create two **Application** run configurations:
   - Main class `server.ServerMain`, working directory = project root
   - Main class `client.ClientMain`, working directory = project root
4. In the client configuration, turn on **Allow multiple instances** so you can launch several clients.
5. Run the server first, then the clients.

> The working directory must be the project root, because config paths are relative (`config/...`).

### I. Stop

- Type `shutdown` in the server console, or press `Ctrl+C`.
- Close the client windows.

---

## Configuration

Each entry point takes an optional path to a properties file as its first argument:

```bash
java -cp out server.ServerMain path/to/server.properties
java -cp out client.ClientMain path/to/client.properties
```

**`config/server.properties`**

| Key | Default | Meaning |
|---|---|---|
| `server.host` | `0.0.0.0` | Host shown in the startup log |
| `server.tcp.port` | `5005` | TCP port for joins and snapshots |
| `server.udp.port` | `6005` | UDP port for player actions |
| `match.duration.seconds` | `180` | *Reserved.* The match length currently comes from `common/Constants.java` |
| `server.tick.rate` | `20` | *Reserved.* The tick rate currently comes from `common/Constants.java` |

**`config/client.properties`**

| Key | Default | Meaning |
|---|---|---|
| `server.host` | `127.0.0.1` | Server address |
| `server.tcp.port` | `5005` | Must match the server |
| `server.udp.port` | `6005` | Must match the server |
| `client.player.name` | `Player` | Display name |
| `client.local.udp.port` | `0` | Local UDP port (`0` = pick automatically) |

Gameplay tuning (speed, zone timings, scores, item spawn rate, freeze range) is in `src/common/Constants.java`. Recompile after changing it.

---

## Server Console Commands

Type these at the `server>` prompt:

| Command | Description |
|---|---|
| `help` | List the commands |
| `players` | List connected players and their IDs |
| `kill <id>` | Kill switch: disconnect a player and remove them from the match |
| `shutdown` | Stop the server |

---

## Troubleshooting

| Problem | Fix |
|---|---|
| `Failed to load properties from: config/...` | Run the command from the project root, or pass the config path as an argument |
| `Address already in use` | Another server is running. Stop it or change the ports in both config files |
| Client connects but the match never starts | A match needs **at least 2** connected players |
| Keys do nothing | Click the game window so it has keyboard focus |
| LAN clients can't connect | Check `server.host` on the client and the firewall rules for TCP 5005 / UDP 6005 |
| `Server failed: Socket closed` after `shutdown` | Expected. The accept loop exits when the socket closes |
