<a id="readme-top"></a>


<h1 align="center">
  <a href="https://github.com/sagieinav/edu-vetrinator-game-android"><img src="https://github.com/user-attachments/assets/f40e2544-97ac-4f3d-ba43-0545080cc45d" alt="Logo" width="200"></a>
  <br>
  George The Vetrinator
  <br>
</h1>

<p align="center">
	<b>An educational Android arcade game featuring dynamic obstacle avoidance and pixel-art aesthetics.</b>
	<br />
	<a href="https://github.com/sagieinav/edu-vetrinator-game-android/issues">Report Bug</a>
	·
	<a href="https://github.com/sagieinav/edu-vetrinator-game-android/issues">Request Feature</a>
	·
	<a href="#4-demonstration">View Demo</a>
</p>

  <!-- Single simplified row of key tech badges -->
  <p align="center">
    <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=kotlin&logoColor=white" alt="Kotlin" />
    <img src="https://img.shields.io/badge/Android-3DDC84?style=flat&logo=android&logoColor=white" alt="Android" />
    <img src="https://img.shields.io/badge/License-MIT-blue.svg" alt="License" />
  </p>




<!-- ABOUT THE PROJECT -->
## 1 About The Project
This repository contains the source code for George The Vetrinator, an Android arcade-style game made for educational purposes

The app features a pixel-art aesthetic, dynamic obstacle avoidance, and a custom game engine built using Kotlin in Android Studio.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## 2 Key Features
- **Dynamic Grid Engine**: The game's grid is configured as GridLayout and initialized in runtime with Kotlin.
- **MVC Pattern**: Strict separation between **M**odels (data), **V**iews, and **C**ontroller (logic).
- **Haptic Feedback**: Vibration and toast messages on collisions.
- **PixelArt-style UI Design**: The UI is Parliament-themed, and uses PixelArt style.

## 3 Classes Overview

The project relies on three main Activities and several helper classes to manage game logic.

Below is a table detailing the **key components** and their roles.

| Class Name | Type | Description |
| :--- | :--- | :--- |
| `HomeActivity` | Activity | The entry point. Handles navigation and game mode selection (Normal/Endless). |
| `GameActivity` | Activity | The main game screen. Manages UI initialization, sensors (Vibrator), and user input. |
| `ResultActivity` | Activity | Displays the "Game Over" state and allows the user to restart or return home. |
| `GameManager` | Class | Logic backbone. Manages the grid state, player movement, and collision detection. |
| `GameGridRenderer` | Class | Handles the visual updates of the grid, separating rendering logic from game calculations. |

## 4 Demonstration
https://github.com/user-attachments/assets/444a7d7a-eb16-4d80-a552-5ec852fc135a



<!-- GETTING STARTED -->
## 5 Getting Started

To get a local copy up and running follow these simple example steps.

### 5.1 Prerequisites

* Android Studio
  https://developer.android.com/studio
  

### 5.2 Installation
1. Clone the repo
   ```sh
   git clone https://github.com/sagieinav/edu-vetrinator-game-android.git
   ```
2. Open the project in Android Studio
<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- USAGE EXAMPLES -->
## 6 Usage

This app is only made for educational purposes.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- ROADMAP -->
## 7 Roadmap

- [ ] Score system
	- [ ] Leaderboards
- [ ] Different game difficulties 

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## 8 Contributors

<div align="center">
	<a href="https://github.com/sagieinav/edu-vetrinator-game-android/graphs/contributors">
	<img src="https://contrib.rocks/image?repo=sagieinav/edu-vetrinator-game-android" alt="contrib.rocks image" />
	</a>
	</br>
	Sagi Einav
</div>

<p align="right">(<a href="#readme-top">back to top</a>)</p>

