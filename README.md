<a id="readme-top"></a>


<h1 align="center">
  <a href="https://github.com/sagieinav/edu-vetrinator-game-android"><img src="https://github.com/user-attachments/assets/f40e2544-97ac-4f3d-ba43-0545080cc45d" alt="Logo" width="200"></a>
  <br>
  George The Vetrinator
  <br>
</h1>


<p align="center">
	<b>An educational Android arcade game featuring dynamic obstacle avoidance and pixel-art aesthetics.</b>
</p>
<p align="center">
	<a href="https://github.com/sagieinav/edu-vetrinator-game-android/issues">Report Bug</a>
	•
	<a href="https://github.com/sagieinav/edu-vetrinator-game-android/issues">Request Feature</a>
	•
	<a href="#demonstration">View Demo</a>
</p>

---

  <!-- Single simplified row of key tech badges -->
  <p align="center">
    <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=kotlin&logoColor=white" alt="Kotlin" />
    <img src="https://img.shields.io/badge/Android-3DDC84?style=flat&logo=android&logoColor=white" alt="Android" />
    <img src="https://img.shields.io/badge/License-MIT-blue.svg" alt="License" />
  </p>



<!-- ABOUT THE PROJECT -->
## About The Project
George The Vetrinator is an Android arcade game developed as part of the 'UI Development' Course in my Computer Science BSc.

The game is built using Kotlin in Android Studio, and features a custom grid-based engine, Android-optimized MVC architecture, and retro pixel-art aesthetics.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- KEY FEATURES -->

## Key Features
- **Dynamic Grid Engine**: The game's grid is configured as GridLayout and initialized in runtime with Kotlin.
- **Optimized MVC Architecture**: Implements a clean separation of concerns:
	- **Model**: Decoupled into  entities  (Data/State) and  logic  (Business Rules).
	- **View**: Composed of XML layouts, the GameGridRenderer , and the UI-management logic within the Activities.
	- **Controller**: The Activities (GameActivity, etc.), which handle user input and lifecycle events to bridge the Model and View.
- **Haptic Feedback**: Vibration and toast messages on collisions.
- **PixelArt-style UI Design**: The UI is Parliament-themed, and uses PixelArt style.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- STRUCTURE -->
## Structure
The project follows a modular optimized MVC structure designed for scalability and readability:
```
├── model                  # Model Layer
│   ├── entities           # Data classes (Player, Obstacle, GameMode)
│   └── logic              # Game logic (GameManager)
├── ui                     # View & Controller Layers
│   ├── game               # Game loop, Activity, and Grid Renderer
│   ├── home               # Main Menu
│   └── result             # Result screen
└── utilities              # Shared constants and helpers
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- CLASSES OVERVIEW -->
## Classes Overview

The project relies on three main Activities and several helper classes to manage game logic.

Below is a table detailing the **key components** and their roles.

| Class Name          | Layer                 | Description                                                                              |
| :------------------ | :-------------------- | :--------------------------------------------------------------------------------------- |
| `GameManager`       | **Model (Logic)**     | The core engine. Calculates movement, collisions, and state changes.                     |
| `Player / Obstacle` | **Model (Entity)**    | Data classes holding state (position, type, health) without behavior.                    |
| `GameActivity`      | **Controller / View** | Acts as the main controller (input/sensors) while also managing the high-level UI state. |
| `GameGridRenderer`  | **View Helper**       | A dedicated class that handles the complex rendering logic for the game grid.            |
| `HomeActivity`      | **Controller / View** | Handles navigation and game mode selection.                                              |
| `ResultActivity`    | **Controller / View** | Handles 'Game Over' scenario, restart/back to home selection.                            |

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- DEMONSTRATION -->
## Demonstration
https://github.com/user-attachments/assets/444a7d7a-eb16-4d80-a552-5ec852fc135a

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- GETTING STARTED -->
## Getting Started

To get a local copy up and running follow these simple example steps.
### Prerequisites

*   **Android Studio**: Ladybug or newer (https://developer.android.com/studio)
*   **Kotlin**: Version 1.9+
*   **Min SDK**: 26

<p align="right">(<a href="#readme-top">back to top</a>)</p>
  

### Installation
1. Clone the repo
   ```sh
   git clone https://github.com/sagieinav/edu-vetrinator-game-android.git
   ```
2. Open the project in Android Studio
   	- Can review the project's source code
   	- Can run the game in the IDE's Android Emulator

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- USAGE EXAMPLES -->
## Usage
**How to Play:**
- Controls: Use the left/right arrows to change lanes.
- Goal: Avoid obstacles and survive as long as possible.
- Game Over: In Normal Mode, the game ends when you run out of lives.
<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- ROADMAP -->
## Roadmap

- [ ] Score system
	- [ ] Leaderboards
 	- [ ] Odometer
	- [ ] Coins on road
- [ ] Player movement improvements
	- [ ] Tilt left/right for player movement
	- [ ] Tilt forward/backwards for speed control
- [ ] Game grid improvements
	- [ ] A wider 5-lane road
	- [ ] A longer road
- [ ] Crash sound
- [ ] Different game difficulties
 

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- CONTRIBUTORS -->
## Contributors

<div align="center">
	<a href="https://github.com/sagieinav/edu-vetrinator-game-android/graphs/contributors">
	<img src="https://contrib.rocks/image?repo=sagieinav/edu-vetrinator-game-android" alt="contrib.rocks image" />
	</a>
	</br>
	Sagi Einav
</div>

<p align="right">(<a href="#readme-top">back to top</a>)</p>
