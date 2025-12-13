# George The Vetrinator

This repository contains the source code for **George The Vetrinator**, an Android arcade-style game made for educational purposes

The app features a pixel-art aesthetic, dynamic obstacle avoidance, and a custom game engine built using Kotlin in Android Studio.

## 1 Game Demonstration
...

## 2 Classes Overview

The project relies on three main Activities and several helper classes to manage game logic.

Below is a table detailing the key components and their roles.

| Class Name | Type | Description |
| :--- | :--- | :--- |
| `HomeActivity` | Activity | The entry point. Handles navigation and game mode selection (Normal/Endless). |
| `GameActivity` | Activity | The main game screen. Manages UI initialization, sensors (Vibrator), and user input. |
| `ResultActivity` | Activity | Displays the "Game Over" state and allows the user to restart or return home. |
| `GameManager` | Class | Logic backbone. Manages the grid state, player movement, and collision detection. |
| `GameGridRenderer` | Class | Handles the visual updates of the grid, separating rendering logic from game calculations. |


## 3 Activity Breakdown

Below is a detailed look at how each specific Activity functions and the logic implemented within it.

#### 3.1.1 Activity: `HomeActivity`

*   **Description:** acts as the main menu for the application.
*   **UI Design:**
    *   Features a clean "Parliament off-white" background.
    *   Displays the App Logo and Title.
    *   Provides buttons for **Normal Mode** and **Endless Mode**.
*   **Logic:**
    *   Initializes views programmatically.
    *   Passes the selected game mode to `GameActivity` via Intent extras when a button is clicked.

#### 3.1.2 Activity: `GameActivity`

*   **Description:** The core gameplay loop occurs here. It couples the UI inputs with the `GameManager` logic.
*   **UI Implementation:**
    *   **Controls:** Two ImageButtons (PixelArt style) in a `RelativeLayout` container for movement (Left/Right).
    *   **Lives:** A display of 3 hearts using XML icons with specific margins.
    *   **Background:** Custom pattern background with Parliament-themed symbols.
    *   **Grid:** A dynamic `GridLayout`, **initialized during runtime**.
*   **Key Logic & Features:**
    *   **Timer Job:** Uses Kotlin Coroutines (`while(isActive)`) to advance the game loop.
    *   **Lifecycle Management:** Pauses the game loop in `onPause` and resumes in `onResume`.
    *   **Vibration:** Initializes the `Vibrator` service to provide haptic feedback on collisions (short) and Game Over (long).

#### 3.1.3 Activity: `ResultActivity`

*   **Description:** The end-of-game screen displayed when lives reach zero.
*   **UI Design:**
    *   **Background:** The game background with reduced opacity.
    *   **Typography:** Uses a custom PixelArt font (`pixel_purl`).
    *   **Text:** Displays "GAME OVER" and the subtext "Et ha'pali...".
*   **Logic:**
    *   **Restart:** Retains the previous game mode and launches a fresh `GameActivity`.
    *   **Home:** Returns the user to the `HomeActivity` to choose a different mode.
