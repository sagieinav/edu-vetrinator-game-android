<a id="readme-top"></a>

<h1 align="center">
  <a href="https://github.com/sagieinav/edu-vetrinator-game-android"><img src="https://github.com/user-attachments/assets/f40e2544-97ac-4f3d-ba43-0545080cc45d" alt="Logo" width="200"></a>
  <br>
  George The Vetrinator
  <br>
</h1>

<p align="center">
	<b>An educational Android arcade game featuring dynamic obstacle avoidance, sensor-based controls, and pixel-art aesthetics.</b>
</p>
<p align="center">
	<a href="https://github.com/sagieinav/edu-vetrinator-game-android/issues">Report Bug</a>
	•
	<a href="https://github.com/sagieinav/edu-vetrinator-game-android/issues">Request Feature</a>
	•
	<a href="#demonstration">View Demo</a>
</p>

---

  <p align="center">
    <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=kotlin&logoColor=white" alt="Kotlin" />
    <img src="https://img.shields.io/badge/Android-3DDC84?style=flat&logo=android&logoColor=white" alt="Android" />
    <img src="https://img.shields.io/badge/Google_Maps-4285F4?style=flat&logo=googlemaps&logoColor=white" alt="Google Maps" />
    <img src="https://img.shields.io/badge/License-MIT-blue.svg" alt="License" />
  </p>

## About The Project
George The Vetrinator is an Android arcade game developed as part of the 'UI Development' Course in my Computer Science BSc.  It is the first app I've ever developed.

The game is built using Kotlin in Android Studio and features a custom grid-based engine, Android-optimized MVC architecture, and retro pixel-art aesthetics.

<p align="right">(<a href="#readme-top">back to top</a>)</p>


## Screenshots
<div align="center">
  <table style="width:100%">
    <tr>
      <td align="center"><img src="https://github.com/user-attachments/assets/c0938090-77f7-474e-b32f-505f1eca87cd" width="250" /></td>
      <td align="center"><img src="https://github.com/user-attachments/assets/1e3a4f45-2820-4dd4-940c-0cdb2347c532" width="250" /></td>
      <td align="center"><img src="https://github.com/user-attachments/assets/b5edb66e-ac11-47c2-9f8e-7df00271c3c0" width="250" /></td>
    </tr>
    <tr>
      <td align="center"><img src="https://github.com/user-attachments/assets/4d9c2989-a7ef-4291-b5b9-94392faf4449" width="250" /></td>
      <td align="center"><img src="https://github.com/user-attachments/assets/3edb2604-ff1a-4bc5-85f2-b1b62cb8328c" width="250" /></td>
      <td align="center"><img src="https://github.com/user-attachments/assets/8f33fb8d-d527-4f32-8de6-0e71494e6dc1" width="250" /></td>
    </tr>
  </table>
</div>


<p align="right">(<a href="#readme-top">back to top</a>)</p>


## Key Features
- **Dynamic Control Systems**: Toggle between classic button-based navigation and an immersive **Tilt Sensor (Accelerometer)** mode.
- **Persistent Leaderboard**: Tracks the top 10 scores locally using SharedPreferences, recording player names, scores, and physical locations.
- **Location Services**: Integrated Google Maps API to visualize exactly where high scores were achieved.
- **Optimized MVC Architecture**: Implements a clean separation between data entities, game logic, and UI controllers.
- **Advanced Asset Loading**: Utilizes **Glide** and a custom **SignalImageLoader** for high-performance bitmap management.
- **Haptic Feedback**: Intelligent vibration patterns and visual indicators synchronized with game events (collisions, boosts, collections).

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Structure
The project utilizes a feature-layered package structure to separate concerns and improve maintainability:

**data** # Data persistence (Local Repository, SharedPreferences)  
**interfaces** # Shared callbacks and listener interfaces  
**logic** # Business logic (GameManager, Audio, Sensors, Haptics)  
**model** # Domain entities (Player, Obstacle, Record) and Enums  
**ui** # View & Controller Layers  
... **adapters** # RecyclerView adapters for Leaderboards  
... **game** # Game engine, Fragments (Game Over, Register), and Grid Renderer  
... **home** # Main Menu and Settings View logic  
... **leaderboards** # Map and Scoreboard Fragment implementations  
**utilities** # Global Constants, Navigation Helpers, and ImageLoader  

<p align="right">(<a href="#readme-top">back to top</a>)</p>


## Demonstration

https://github.com/user-attachments/assets/fbf5c029-22ac-4165-a999-15e18e41ee94





<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Getting Started

### Prerequisites
* **Android Studio**: Ladybug or newer
* **Kotlin**: Version 1.9+
* **Min SDK**: 26

### Installation
1. Clone the repo: `git clone https://github.com/sagieinav/edu-vetrinator-game-android.git`
2. Open the project in Android Studio.
3. Add your Google Maps API Key to your `local.properties` or manifest.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Usage
**How to Play:**
- **Controls**: Use on-screen arrows or enable **Tilt Mode** in settings to move by tilting your device.
- **Boost**: Tilt the device forward to increase speed.
- **Goal**: Collect coins and avoid obstacles to survive as long as possible.
- **Leaderboard**: View your rank and the map location of your best runs in the High Scores section.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Roadmap

- [x] App icon
- [x] Game grid improvements
    - [x] A wider 5-lane road
    - [x] A longer road
- [x] Player movement improvements
    - [x] Tilt left/right for player movement
    - [x] Tilt forward/backwards for speed control
- [x] Score system
    - [x] Odometer
    - [x] Coins on road
- [x] Add game options to home screen
	- [x] Game difficulty - slow/fast toggle
	- [x] Sensor mode (tilt movement) - ON/OFF toggle
- [x] Sound effects
	- [x] Crash sound
	- [x] Boost sound
	- [x] Coin collect sound
	- [x] Background music
- [x] Leaderboards
	- [x] Add button to access leaderboards from home screen
	- [x] Leaderboards will open as a fragment
	- [x] Use recycler view for the list
	- [x] Shows current top 10. Each row includes:
		- [x] Player name
		- [x] Score
	- [x] Location will open as another (map) fragment in the same screen
- [x] Modularize XML layouts
	- [x] `activity_home`
	- [x] `activity_game`
- [x] Replace `activity_result` with a modularized fragment
- [x] Improve game spawn engine
- [x] Modular `GameActivity` class. Introduce these classes:
	- [x] `MyApp`
	- [x] `TiltDetector`
	- [x] `VibrationManager`
- [x] Change external custom callbacks from functions to inner interfaces
- [x] Implement Glide and ImageLoader for loading images

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Contributors

<div align="center">
	<a href="https://github.com/sagieinav/edu-vetrinator-game-android/graphs/contributors">
	<img src="https://contrib.rocks/image?repo=sagieinav/edu-vetrinator-game-android" alt="contrib.rocks image" />
	</a>
	</br>
	Sagi Einav
</div>

<p align="right">(<a href="#readme-top">back to top</a>)</p>
