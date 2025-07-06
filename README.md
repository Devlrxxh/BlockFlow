# 🧱 BlockFlow

**BlockFlow** is a lightweight and flexible library for creating **client-side block arenas** using packets. It's perfect for minigames, arenas, or any system where you want dynamic, per-match changes such as placed / broken blocks without having a big performance impact.

---

## 📦 Installation

### Maven
```xml
<dependency>
    <groupId>dev.lrxh</groupId>
    <artifactId>blockflow</artifactId>
    <version>1.0.0</version>
</dependency>
```
---

## 🚀 Example Usage

```java
BlockFlow blockFlow = new BlockFlow(plugin);

Location min = ...;
Location max = ...;

// Create a stage, this copies all the blocks between both locations
FlowStage stage = blockFlow.createStage(min, max);

// Offset the stage by 100 blocks on the X axis
stage.offset(100, 0, 0);

// Add a player to view the stage
stage.addViewer(player);

// Remove a player from viewing the stage
stage.removeViewer(player);

// Clone the stage to create a new instance with the same blocks
FlowStage stage = stage.clone();
```

---

## 📢 Events

### 🔨 FlowBreakEvent

Fired when a player breaks a block in a `FlowStage`.

**Fields:**
- `Player player` – the player breaking the block
- `FlowPosition position` – the position of the block
- `FlowBlock block` – the block being broken
- `FlowStage stage` – the stage where the event occurs
- `boolean cancelled` – whether the event is cancelled

---

### 🧱 FlowPlaceEvent

Fired when a player places a block in a `FlowStage`.

**Fields:**
- `Player player` – the player placing the block
- `FlowPosition position` – the position of the placed block
- `BlockData blockData` – the data of the block being placed
- `FlowStage stage` – the stage where the event occurs
- `boolean cancelled` – whether the event is cancelled

---

### 📦 FlowBlockItemDropEvent

Fired when a block drops an item in a `FlowStage`.

**Fields:**
- `Player player` – the player responsible for the drop
- `FlowPosition position` – the block’s position
- `Material material` – the dropped material
- `FlowStage stage` – the stage where the event occurs
- `boolean cancelled` – whether the event is cancelled

---

### 🔄 FlowItemPickupEvent

Fired when a player picks up an item in a `FlowStage`.

**Fields:**
- `Player player` – the player picking up the item
- `ItemStack itemStack` – the item being picked up
- `FlowStage stage` – the stage where the event occurs
- `boolean cancelled` – whether the event is cancelled

---

### 📦 FlowPlayerItemDropEvent

Fired when a player drops an item in a `FlowStage`.

**Fields:**
- `Player player` – the player who dropped the item
- `FlowPosition position` – the player’s position when dropping the item
- `Material material` – the material of the item being dropped
- `FlowStage stage` – the stage where the event occurs
- `boolean cancelled` – whether the event is cancelled

---

## 🗺️ Roadmap

- [x] Item drop simulation
- [x] Item pickup handling
- [x] Block placing
- [x] Block breaking
- [ ] Explosion handling
- [ ] Ender pearl interactions
- [ ] Liquid flow support
- [ ] Block ticking
- [ ] Knockback

---

## 📄 Dependencies
- [PacketEvents](https://github.com/retrooper/packetevents) - For packet manipulation
- [EntityLib](https://github.com/Tofaa2/EntityLib) - For entity handling

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
