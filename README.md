# 🧱 BlockFlow

**BlockFlow** is a lightweight and flexible library for creating **client-side block arenas** using packets. It’s perfect for minigames, arenas, or any system where you want dynamic, per-match changes such as placed / broken blocks without having a big performance impact.

---

## 🚀 Example Usage

```java
BlockFlow blockFlow = new BlockFlow(plugin);

// Create a stage using two corners of an arena
FlowStage stage = blockFlow.createStage(standAloneArena.getMin(), standAloneArena.getMax());

// Offset the stage by 100 blocks on the X axis
stage.offset(100, 0, 0);

// Add a player to view the stage
stage.addViewer(player);
```

---

## 📢 Events

### 🔨 FlowBreakEvent

Fired when a player breaks a block in a `FlowStage`.

**Fields:**
- `Player player` – the player breaking the block
- `BlockPosition position` – the position of the broken block
- `Block block` – the block being broken
- `FlowStage stage` – the stage where the event occurs

---

### 🧱 FlowPlaceEvent

Fired when a player places a block in a `FlowStage`.

**Fields:**
- `Player player` – the player placing the block
- `BlockPosition position` – the position of the placed block
- `BlockData blockData` – the data of the block being placed
- `FlowStage stage` – the stage where the event occurs

---

## 🗺️ Roadmap

- [ ] Item drop simulation
- [ ] Explosion handling
- [ ] Ender pearl interactions
- [ ] Liquid flow support
- [ ] Block ticking
- [x] Block placing
- [x] Block breaking

---

## 📄 Dependencies
- [PacketEvents](https://github.com/retrooper/packetevents)
