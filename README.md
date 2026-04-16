# PSX MIPS and PsyQ Support for CLion

### 🎮 Welcome, Mark!
This plugin adds comprehensive support for **PlayStation 1 (PSX)** development to CLion, focusing on **MIPS R3000A Assembly** and the **Sony PsyQ SDK**.

## ✨ Features

### 🛠 MIPS Assembly Support (`.asm`, `.s`)
*   **Syntax Highlighting:** Full color support for instructions, registers, and directives.
*   **GTE & COP0 Support:** Specialized highlighting for Geometry Transformation Engine (COP2) and System Control (COP0) opcodes.
*   **Documentation Hovers:** Hover over any MIPS register (including GTE/COP0) to see its purpose and PSX-specific usage.
*   **Live Templates:** Type `func` or `main` to quickly scaffold assembly routines.

### 🍱 PsyQ C/C++ Support
*   **Full SDK Completion:** Auto-completion for hundreds of functions from `libgpu`, `libgs`, `libcd`, `libspu`, `libetc`, `libmath`, `libmcrd`, `libpress`, and more.
*   **Hardware Registers:** Direct completion and documentation for memory-mapped I/O addresses (e.g., `GP0`, `I_STAT`).
*   **BIOS Symbols:** Support for low-level BIOS A0, B0, and C0 table functions.
*   **SDK Constants & Structs:** Auto-completion for PSX-specific types like `RECT`, `SVECTOR`, and constants like `GsNONINTER`.
*   **Live Templates:**
    *   `psxinit`: Generates basic graphics initialization code.
    *   `psxloop`: Creates a standard VSync-based main loop.
    *   `psxregs`: Inserts hardware register `#define` pointers.

---

## 🚀 Installation Instructions

Since we are in the development phase, you can run the plugin directly from the source to test it out!

### 1. Prerequisites
*   **CLion 2026.1** (or compatible version).
*   **Java 21** (JDK) installed on your machine.

### 2. Build and Run (For Developers)
If you want to test the plugin immediately without installing it:
1.  Open this project folder (`C:\dev\psx-plugin`) in CLion.
2.  Wait for Gradle to sync (check the progress bar at the bottom).
3.  Open the **Gradle tool window** (usually on the right side).
4.  Navigate to: `psx-plugin` -> `Tasks` -> `intellij platform` -> `runIde`.
5.  **Double-click `runIde`**.
6.  A **new instance of CLion** will launch with the plugin pre-installed.

### 3. Create a Plugin Zip (For Installation)
If you want to generate a `.zip` file to install into your primary CLion:
1.  Open the **Gradle tool window** on the right.
2.  Navigate to: `psx-plugin` -> `Tasks` -> `intellij platform` -> `buildPlugin`.
3.  **Double-click `buildPlugin`**.
4.  Once the task finishes, navigate to the following folder in your file explorer:
    `C:\dev\psx-plugin\build\distributions\`
5.  You will find a file named `psx-plugin-1.0-SNAPSHOT.zip`.

### 4. Install the Plugin in CLion
1.  Open your main **CLion 2026.1** instance.
2.  Go to **File** -> **Settings** (or `Ctrl+Alt+S`).
3.  Select **Plugins** from the left-hand menu.
4.  Click the ⚙️ (gear icon) at the top of the Plugins window.
5.  Choose **Install Plugin from Disk...**.
6.  Select the `.zip` file from the `build\distributions\` folder.
7.  **Restart CLion** if prompted.

---

### 📝 Notes for Mark:
*   **PsyQ Headers:** For the C/C++ completion to work best, make sure your project includes the PsyQ header files (`.h`) so CLion can resolve the types.
*   **Feedback:** If you find any missing registers or functions while you're coding, let me know!

Happy coding on the PSX! 🚀🎮
