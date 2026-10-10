# HavenMine

**A Minecraft Paper plugin built with Kotlin.**

HavenMine brings together utility commands and reusable gameplay systems in a single plugin, with configurable messages, sound feedback, and cooldown handling.

This is a personal hobby and portfolio project currently under development.

- [Features](#features)
- [Preview](#preview)
- [Technologies Used](#technologies-used)
- [Architecture](#architecture)
- [Disclaimer](#disclaimer)
- [Contact](#contact)

## Features

### Utility Commands

| Command           | Description                                                                    |
| ----------------- | ------------------------------------------------------------------------------ |
| `/food`           | Restores hunger, saturation, and exhaustion.                                   |
| `/health`         | Restores the player's health.                                                  |
| `/craft`          | Opens a virtual crafting table.                                                |
| `/light`          | Toggles night vision. The effect can be disabled while its cooldown is active. |
| `/linkedin`       | View my LinkedIn profile.                                                      |
| `/github`         | View my GitHub profile.                                                        |

Hunger and health commands are restricted to Survival and Adventure modes. The light command is available in all game modes.

### Shared Systems

* **Configurable cooldowns:** Set the default cooldown duration through `config.yml`.
* **Customizable messages:** Configure command messages using MiniMessage formatting.
* **Sound feedback:** Reusable sound presets for success, errors, and other actions.
* **Command infrastructure:** Shared cooldown handling and game mode validation.

## Preview

### Gameplay Demo
https://github.com/user-attachments/assets/bfa5b388-f25f-4c6f-a6b2-2f88eea4fc9e

### Social Commands
![LinkedIn and GitHub commands](https://github.com/user-attachments/assets/c4429050-ccc5-4bcb-868e-82b326265869)

## Technologies Used

| Category             | Technology                                                                                                                                       |
| -------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------ |
| Language             | [![Kotlin](https://img.shields.io/badge/Kotlin-2.4.20-7F52FF?style=flat\&logo=kotlin\&logoColor=white)](https://kotlinlang.org/)                 |
| Runtime              | [![Java](https://img.shields.io/badge/Java-25%2B-ED8B00?style=flat\&logo=openjdk\&logoColor=white)](https://openjdk.org/)                        |
| Server API           | [![Paper API](https://img.shields.io/badge/Paper%20API-26.3-222222?style=flat\&logo=minecraft\&logoColor=white)](https://papermc.io/)            |
| Build System         | [![Gradle Kotlin DSL](https://img.shields.io/badge/Gradle-Kotlin%20DSL-02303A?style=flat\&logo=gradle\&logoColor=white)](https://gradle.org/)    |
| Dependency Packaging | [![Shadow](https://img.shields.io/badge/Shadow-9.6.1-02303A?style=flat\&logo=gradle\&logoColor=white)](https://gradleup.com/shadow/)             |
| Text Formatting      | [![Adventure MiniMessage](https://img.shields.io/badge/Adventure-MiniMessage-5865F2?style=flat)](https://docs.papermc.io/adventure/minimessage/) |

## Architecture

HavenMine uses a feature-oriented package structure with shared components for common functionality.

* **Feature-based organization:** Commands are grouped by feature, such as `food`, `health`, `craft`, `light`, and `social`.
* **Reusable command handling:** A shared `CooldownCommand` base class and `CommandCooldownManager` handle cooldown behavior without duplicating the same logic across commands.
* **Centralized feedback:** `MessageService` and `PlayerFeedbackService` manage configurable messages, MiniMessage formatting, and reusable sound presets.
* **Configuration:** Command cooldown durations and player-facing messages can be customized through `config.yml`.


## Disclaimer
HavenMine is an unofficial community project. It is not an official Minecraft product and is not
approved by or associated with Mojang or Microsoft.

## Contact
<a href="https://www.linkedin.com/in/gblrodrigues">
  <img src="https://img.shields.io/badge/LinkedIn-100%2B-0077B5?style=flat&logo=linkedin&logoColor=white" alt="My LinkedIn Profile"/>
</a>
