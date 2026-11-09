# Gogeta: Ascension — Minecraft 1.21.1 NeoForge Mod

An anime-accurate transformation mod where the player ascends as **Gogeta** from *Dragon Ball Super: Broly*, equipped with signature Ki attacks, mob progression, terrain destruction, and the legendary **Bankai Blade**.

---

## 🌟 Key Features

### 1. Playable Gogeta Transformation & 3D Model
- The player directly transforms into Gogeta.
- High-detail 3D model layer rendering over the player:
  - Spiky Gogeta/Saiyan hair with the iconic single front bang falling over the forehead.
  - Black vest with padded orange/yellow neck & shoulder trim.
  - Cyan/blue waist sash.
  - Black/blue wristbands.
  - Baggy white trousers.
- Dynamically rendered and colored matching each transformation tier!

### 2. Transformations & Power Multipliers
Transformations feature progressive damage multipliers and distinct auras:
- **Base Gogeta** (1× Power): Saiyan strength, speed, and base black hairstyle.
- **Super Saiyan (SSJ)** (3× Power): Golden hair, gold aura, strength & speed II, damage resistance I, creative flight.
- **Super Saiyan 2 (SSJ2)** (7× Power): Intense golden hair, crackling electrical sparks, jump boost II, damage resistance II.
- **Super Saiyan Blue (SSB)** (20× Power): Vivid cyan hair, soul fire flame aura, regeneration, resistance III.
- **Super Saiyan 4 (SS4)** (30× Power): Crimson fur, dark wild hair, fire resistance, regeneration II, resistance IV.
- **Ultra Instinct (UI)** (75× Power): Ethereal silver/white hair, glowing end-rod shimmer aura, automatic dodge evasion, and 90% incoming damage absorption!

### 3. Mob Kill Progression System
- **Ascension Chronicle Book**: Awarded automatically on first world join/spawn! Right-click it to awaken Gogeta with on-screen titles, thunder, and burst visuals.
- Unlock transformations through combat progression:
  - Slay **Zombies / Hostile Mobs** ➔ Unlocks **Super Saiyan (SSJ)**
  - Slay **Endermen / Wither Skeletons** ➔ Unlocks **Super Saiyan 2 (SSJ2)**
  - Slay the **Ender Dragon** ➔ Unlocks **Super Saiyan Blue (SSB)**
  - Slay **The Wither** ➔ Unlocks **Super Saiyan 4 (SS4)**
  - Slay **The Warden** ➔ Unlocks **Ultra Instinct (UI)**

### 4. Signature Ki Attacks
Available as Ki Scrolls in creative/survival or via `/gogeta attack <name>`:
- **Stardust Breaker (Soul Punisher)**: Colorful rainbow sphere with authentic rainbow trail particles and explosive rainbow vaporization.
- **Kamehameha**: High-velocity cyan energy blast.
- **Big Bang Kamehameha**: Massive golden-blue explosive sphere with extreme kinetic impact.
- **Meteor Combination**: Rapid dash strike barrage hitting all enemies along the path.
*Damage and blast radius scale dynamically with your active transformation multiplier!*

### 5. Configurable Block & Terrain Destruction
- Ki blasts and Getsuga create realistic craters and vaporize blocks.
- Toggle terrain damage to protect builds:
  ```
  /gamerule gogetaTerrainDestruction true
  /gamerule gogetaTerrainDestruction false
  ```

### 6. Legendary Bankai Blade
- **80 HP Base Attack Damage** (40 hearts per swing).
- Instantly slices through virtually all Minecraft blocks.
- **Held Effect**: Grants Enhanced Strength & Resistance.
- **Inventory Effect**: Enables super-speed creative flight accompanied by a silver aura.
- **Charged Getsuga Tensho**: Hold right-click for 5 seconds to unleash a massive black-and-gold projectile dealing **800 HP Damage** and leaving a massive crater.

---

## 🕹️ In-Game Commands
- `/gogeta form <base|ssj|ssj2|blue|ss4|ultra|none>`: Directly set form (available for creative/sandbox mode).
- `/gogeta status`: Displays current active form and damage multiplier.
- `/gogeta clear`: Clears transformation and returns player to normal.
- `/gogeta attack <stardust_breaker|kamehameha|big_bang_kamehameha|meteor_combination>`: Fires signature attack.

---

## 📦 Installation Instructions
1. Install **Minecraft 1.21.1**.
2. Install **NeoForge 21.1.250** (or compatible 21.1.x loader).
3. Ensure **Java 21 (64-bit)** is installed.
4. Download or copy `build/libs/gogeta_ascension-0.1.0.jar` into your `.minecraft/mods/` directory.
5. Launch Minecraft and ascend!

---

## 🛠️ Testing & Verification Summary
- **Compilation**: Verified clean build using `./gradlew build`.
- **Runtime Test**: Verified on NeoForge 1.21.1 dedicated headless server; confirmed mod discovery, registry initialization, recipe loading, and world generation without crashes.
- **Client Safety**: Registered invisible entity renderers for Ki blasts and Getsuga to prevent client crashes.
- **Memory Optimized**: Lightweight asset footprints and efficient particle scheduling suitable for 8 GB RAM systems.
