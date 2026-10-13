# HavenMine

**A Minecraft Paper plugin built with Kotlin.**

HavenMine brings together utility commands and reusable gameplay systems in a single plugin, with configurable 
messages, sound feedback, cooldown handling, and persistent cosmetic selections.

This is a personal hobby and portfolio project currently under development.

- [Features](#features)
- [Preview](#preview)
- [Testing](#testing)
- [Technologies Used](#technologies-used)
- [Architecture](#architecture)
- [Disclaimer](#disclaimer)
- [Contact](#contact)

## Features

### Cosmetics

| Command      | Description              |
|--------------|--------------------------|
| `/cosmetics` | Opens the cosmetics GUI. |

* **Movement particle trails:** Select and equip particle effects that appear near the player's feet while walking, running, and jumping.
* **Cosmetics GUI:** Browse movement trails and toggle the currently equipped effect.
* **Single active trail:** Each player can equip one movement trail at a time.
* **Persistent selections:** Equipped movement trails are stored in SQLite and restored when players rejoin or the server restarts.

### Utility Commands
| Command   | Description                                                                    |
|-----------|--------------------------------------------------------------------------------|
| `/food`   | Restores hunger, saturation, and exhaustion.                                   |
| `/health` | Restores the player's health.                                                  |
| `/craft`  | Opens a virtual crafting table.                                                |
| `/light`  | Toggles night vision. The effect can be disabled while its cooldown is active. |

Hunger and health commands are restricted to Survival and Adventure modes. The light command is available in all game modes.

### Social Commands

| Command     | Description                                       |
|-------------|---------------------------------------------------|
| `/linkedin` | Displays a clickable link to my LinkedIn profile. |
| `/github`   | Displays a clickable link to my GitHub profile.   |

### Shared Systems

- **Configurable cooldowns:** Set the default cooldown duration through `config.yml`.
- **Customizable messages and GUI text:** Configure player-facing messages, menu titles, item names, and lore using `config.yml` and MiniMessage.
- **Sound feedback:** Reusable sound presets for success, errors, menu interactions, and cosmetic selection.
- **Command infrastructure:** Shared cooldown handling, game mode validation, and command registration.

## Preview

### Cosmetics Demo
https://github.com/user-attachments/assets/cf7d27d2-c085-4b69-ad0f-7bb538d6cbdc

### Utility Commands Demo
https://github.com/user-attachments/assets/bfa5b388-f25f-4c6f-a6b2-2f88eea4fc9e

## Testing
HavenMine includes automated unit and integration tests for movement trail persistence.

* **Unit tests:** Verify trail selection, switching, unequipping, restoration, stale revision rejection, and in-memory state cleanup in `MovementTrailService`.
* **Integration tests:** Verify SQLite persistence, selection updates, deletion, missing selections, unknown stored keys, and independent player selections in `SqliteMovementTrailRepository`.

## Technologies Used

| Category             | Technology                                                                                                                                                                   |
|----------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Language             | [![Kotlin](https://img.shields.io/badge/Kotlin-2.4.20-7F52FF?style=flat\&logo=kotlin\&logoColor=white)](https://kotlinlang.org/)                                             |
| Runtime              | [![Java](https://img.shields.io/badge/Java-25%2B-ED8B00?style=flat\&logo=openjdk\&logoColor=white)](https://openjdk.org/)                                                    |
| Database             | [![SQLite](https://img.shields.io/badge/SQLite-3.53.4.0-003B57?style=flat&logo=sqlite&logoColor=white)](https://www.sqlite.org/)                                             |
| Testing              | [![JUnit 6](https://img.shields.io/badge/JUnit-6-25A162?style=flat)](https://junit.org/) [![MockK](https://img.shields.io/badge/MockK-FF9800?style=flat)](https://mockk.io/) |
| Server API           | [![Paper API](https://img.shields.io/badge/Paper%20API-26.3-222222?style=flat\&logo=minecraft\&logoColor=white)](https://papermc.io/)                                        |
| Build System         | [![Gradle Kotlin DSL](https://img.shields.io/badge/Gradle-Kotlin%20DSL-02303A?style=flat\&logo=gradle\&logoColor=white)](https://gradle.org/)                                |
| Dependency Packaging | [![Shadow](https://img.shields.io/badge/Shadow-9.6.1-02303A?style=flat\&logo=gradle\&logoColor=white)](https://gradleup.com/shadow/)                                         |
| Text Formatting      | [![Adventure MiniMessage](https://img.shields.io/badge/Adventure-MiniMessage-5865F2?style=flat)](https://docs.papermc.io/adventure/minimessage/)                             |

## Architecture

HavenMine uses a feature-oriented package structure with shared components for common functionality.

* **Feature-based organization:** Commands and gameplay systems are grouped by feature, including `food`, `health`, `craft`, `light`, `social`, and `cosmetics`.
* **Reusable command handling:** A shared `CooldownCommand` base class and `CommandCooldownManager` handle cooldown behavior without duplicating the same logic across commands.
* **Cosmetics system:** Dedicated GUI components manage menu creation, item presentation, navigation, and movement trail selection.
* **Movement particles:** A movement listener detects player movement and jumping to emit the selected particle effect.
* **Persistent cosmetic selections:** `MovementTrailService` manages active selections, while `MovementTrailRepository` abstracts persistence and `SqliteMovementTrailRepository` handles database operations. Selection loading and saving run asynchronously.
* **Centralized feedback:** `MessageService` and `PlayerFeedbackService` manage configurable messages, MiniMessage formatting, and reusable sound presets.
* **Configuration:** Command cooldowns, player-facing messages, and cosmetics GUI text can be customized through `config.yml`.

## Disclaimer
HavenMine is an unofficial community project. It is not an official Minecraft product and is not
approved by or associated with Mojang or Microsoft.

## Contact
<a href="https://www.linkedin.com/in/gblrodrigues">
  <img src="https://img.shields.io/badge/LinkedIn-100%2B-0077B5?style=flat&logo=linkedin&logoColor=white" alt="My LinkedIn Profile"/>
</a>
