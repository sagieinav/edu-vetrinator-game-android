---
notetoolbar: Default
tags:
created: 2025-12-09T20:36:16+02:00
modified: 2025-12-11T21:28:55+02:00
---
> [!toc] Table of Contents
> ```toc
> ```

[[1 משחקון מירוץ מכשולים]]
[[VetrinatorGame - Initial Workflow]]
## 1 Work Log
### 1.1 GameActivity

#### 1.1.1 UI Implementation
##### Arrows for movement
- I use material buttons with XML icons inside of them for the arrows.
- I place both buttons inside a parent RelativeLayout container
- I set padding for the parent container, to act as margins from the screen edge.
- Colors: Parliament logo's colors
##### Lives
- I use LLC as a parent container
- I use XML icons as images for 3 hearts
- I set margins for the 2nd heart, to act as padding between the hearts.
- Colors: my XML icon's default. No need to change.
##### Background
- Made a pattern background, with parliament-themed colors and symbols
##### Game Grid
-  Using `GridLayout`
- The grid itself will be constructed in `GameActivity`, during `initViews()`

#### 1.1.2 Logic Backbones
1. `findViews()`
2. `initViews()`
3. Initialize Views of dynamic elements (player, obstacles):
	1. `initPlayer()`
	2. `initObstacles()`
4. Create the `GameManager` Class
5. `initGrid()`
6. `refreshUI`

#### 1.1.3 Game-running Logic
1. `movementInitiated()` (Activity) + `movePlayer` (Manager) 
2. `advanceObstacle()` + `advanceGame()`
3. `spawnObstacle()`
4. `movePlayer()`
5. `isCollision()`
##### Timer Job (for advancing the game)
- Using Kotlin Coroutines
- `while(isActive)`: For safety. It ends when the scope dies.
- `onResume`, `onPause`: For freezing the game's activity while the app is not focused.

#### 1.1.4 Refinements
##### Object-Oriented Refinements
- **Data Classes:** `Player`, `Obstacle`
- **Enum Class** for Obstacle Type

##### Logic Refinements
- Life reduction to happen instanly on collision
- Spawn new obstacles every two turns instead of one
- Make obstacles disappear one row before the player's row
- Make obstacles switch resource IMG to crash effect on collision
- Improve modularity
- Add `GameGridRenderer` for improving modularity more
- File-tree structure organization
##### UI Refinements
- Add a pattern background
- Replace buttons to ImageButton (PixelArt style)
- Add shadow to hearts container & buttons
- Add a separator line at top of bottom bar
- Change NETA's obstacle sprite (remove body)

#### 1.1.5 More Game Logic & Features
- Add option for endless game (default for now, until I make a home screen)
- `GameGridRenderer.render()`: separate into `renderPlayer()` and `renderObstacles`
- Make `updateLivesUI()` for modularity
- `refreshUI()` now calls these 3:
	- `updateLivesUI()`
	- `renderPlayer()`
	- `renderObstacles()`
- `movementInitiated()` now only re-renders player, not whole UI

#### 1.1.6 Add Toast Messages & Vibration
- Add toast message for collision (based on colliding obstacle type)
- Add vibration permission in `AndroidManifest.xml`
- Initialize a `Vibrator` object in `GameActivity`
- Create a `vibrate()` function

#### 1.1.7 Final Fine-tuning
- Create callback event functions:
	- `onCollision`: invokes short vibration and a toast message
	- `onGameOver`: invokes long vibration, stops timer, and introduces a small delay before changing activity
- Modularize functions in `onCreate` to reduce clutter:
	- `initVibrator`
	- `initGame`
- Extract hardcoded resources present in the `.kt` files
- Change the app's logo
- Renamed layout IDs in `game_activity.xml` to better follow naming conventions
- Check for any hard coded resource and extract them
- Check for any un-used code left from changes, and remove it

### 1.2 HomeActivity
#### 1.2.1 UI Design
- Background: plain parliament offwhite color
- Header
	- Logo: app logo
	- Title: app name
- Game option buttons:
	- Normal Mode
	- Endless Mode
#### 1.2.2 Logic
- `findViews()` + `initViews()` for the menu buttons
- `startGame` that changes activity to `GameActivity` with the selected game mode


### 1.3 ResultActivity
#### 1.3.1 UI Design
- Background: the game's background, slightly reduced opacity
- Header
	- Font: grabbed a PixelArt font (`pixel_purl`)
	- Small text: "Et ha'pali..."
	- Main text: "GAME OVER"
- Menu options buttons:
	- Restart (same game mode)
	- Back to Home
#### 1.3.2 Logic
- `findViews()` + `initViews()` for the menu buttons
- `restartGame` that changes activity to `GameActivity` with the current game mode
- `changeToHome` that changes activity to `HomeActivity`


---

## 2 Notes
### 2.1 Naming Conventions
#### 2.1.1 Activities
PascalCase: `PuposeActivity`
**Examples:** `HomeActivity`, `GameActivity`, `ResultActivity`

#### 2.1.2 Layout Files
snake_case: `activity_purpose`
**Examples:** `activity_home.xml`, `activity_game.xml`, `activity_result.xml`

#### 2.1.3 Inner Views & Layouts
##### For ID in XML
snake_case: `type_purpose`
**Examples:** `container_controls`, `btn_move_left`, `iv_app_icon`
##### For Variables in Kotlin
camelCase: `descriptionType`
**Examples:** `leftButtonView`, `heartsView`, 
#### 2.1.4 Drawables
snake_case: `category_content_state`
Examples:
- **Icons:** `ic_arrow_left`, `ic_heart`...
- **Player sprite:** `player_george.webp`
- **Obstacle sprites:** `obstacle_neta.webp`
### 2.2 assets vs drawable (LLM-Generated)

| Feature | `res/drawable` | `assets` |
| :--- | :--- | :--- |
| **Structure** | Flat (no folders allowed) | Hierarchy (folders allowed) |
| **Access** | `R.drawable.name` (ID) | `"path/filename"` (String) |
| **Verification** | Checked at compile time | Checked at runtime (risk of typos) |
| **Best For** | UI icons, buttons, background | Game levels, custom data, organized sprites |

### 2.3 Size Units Cheat Sheet (LLM-Generated)

#### 2.3.1 `dp` (Density-independent Pixels) - **Use for Everything Visual**
*   **What it is:** A physical unit of size. `1dp` is roughly 1/160th of an inch.
*   **Why use it:** It guarantees your button looks like a button (finger-sized) on both a tiny phone and a giant tablet. It automatically scales based on the screen's pixel density (`dpi`).
*   **Rule:** Use `dp` for **widths**, **heights**, **margins**, **padding**, and coordinate math [1][2].

#### 2.3.2 `sp` (Scale-independent Pixels) - **Use for Text ONLY**
*   **What it is:** Same as `dp`, but it *also* scales based on the user's "Font Size" setting in Android settings.
*   **Why use it:** If a user is visually impaired and sets their phone text to "Huge," your app's text will grow if you use `sp`. If you use `dp` for text, you are breaking accessibility accessibility rules.
*   **Rule:** Use `sp` **only for `textSize`** [3][4].

#### 2.3.3 `px` (Pixels) - **Avoid in XML, Use in Code**
*   **What it is:** A raw screen pixel.
*   **Why use it:** You generally *don't* want to use this in XML designs because `50px` is huge on an old phone but microscopic on a new Pixel 8 Pro.
*   **Exception:** In your **Java/Kotlin code**, almost all drawing functions (like `canvas.drawRect` or `view.getX()`) return or expect **pixels**. You will often need to calculate: `pixels = dp * density`.

#### 2.3.4 Quick Conversion Math
When you are coding your game logic, you'll think in `dp` (e.g., "The lane is 100dp wide"), but the code needs `px`.

```kotlin
// Formula to convert dp to px in Kotlin
val density = resources.displayMetrics.density
val pixels = (dpValue * density).toInt()
```

#### 2.3.5 Summary Table
| Unit | Full Name | Use For | Scaling |
| :--- | :--- | :--- | :--- |
| **dp** | Density-independent Pixel | Layouts, Buttons, Icons, Margins | Screen Density (DPI) |
| **sp** | Scale-independent Pixel | **Text Size Only** | Screen Density + User Font Preference |
| **px** | Pixel | **Code Logic** (drawing, collisions) | None (Raw) |
