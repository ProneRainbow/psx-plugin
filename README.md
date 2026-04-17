# PSX MIPS and PsyQ Support for CLion

### 🎮 Welcome!
This plugin adds comprehensive support for **PlayStation 1 (PSX)** development to CLion, focusing on **MIPS R3000A Assembly** and the **Sony PsyQ SDK**.

## ✨ Features

### 🛠 MIPS Assembly Support (`.asm`, `.s`)
*   **Syntax Highlighting:** Full color support for instructions, registers, and directives.
*   **GTE & COP0 Support:** Specialized highlighting for Geometry Transformation Engine (COP2) and System Control (COP0) opcodes.
*   **Documentation Hovers:** Hover over any MIPS register (including GTE/COP0) to see its purpose and PSX-specific usage.
*   **Live Templates:** Type `func` or `main` to quickly scaffold assembly routines.
*   **Numeric Conversions:** Hover over any number (Hex, Dec, Bin) to see its value in other bases automatically.

### 🍱 PsyQ C/C++ Support
*   **Full SDK Completion:** IntelliSense and auto-completion for the **complete PsyQ SDK** headers. Includes inline documentation for functions in `libgpu`, `libgs`, `libcd`, `libspu`, `libetc`, and more.
*   **Hardware Registers:** Direct completion and documentation for memory-mapped I/O addresses (e.g., `GP0`, `I_STAT`).
*   **BIOS Symbols:** Support for low-level BIOS A0, B0, and C0 table functions.
*   **SDK Constants & Structs:** Auto-completion for PSX-specific types like `RECT`, `SVECTOR`, and constants like `GsNONINTER`.
*   **Live Templates:**
    *   `psxinit`: Generates basic graphics initialization code.
    *   `psxloop`: Creates a standard VSync-based main loop.
    *   `psxregs`: Inserts hardware register `#define` pointers.

### 🏗 Project Templates
*   **PSX Project:** A new project template in the "New Project" menu that scaffolds a `Makefile`, `main.c`, `bin2exe.py`, the full set of PsyQ headers, and an automated SDK setup script.

---

## 🛠 Prerequisites (For Generated Projects)
If you are using the **PSX Project** template, ensure you have the following installed:
*   **Python:** Required for the `bin2exe.py` post-build script.
*   **armips:** A versatile MIPS assembler (often used for PSX development).
*   **PsyQ SDK:** The official Sony SDK. If you don't have it, you can use the included `setup_psyq.ps1` script (see below).

## 📦 Setting up the PsyQ SDK
Each new project includes a `setup_psyq.ps1` PowerShell script to automate the installation of the official PsyQ SDK.

### Using the Setup Script:
1.  Open **PowerShell** as Administrator (required to modify Environment Variables).
2.  Navigate to your project root.
3.  Run the script:
    ```powershell
    .\setup_psyq.ps1
    ```
This script will:
*   Download the **PsyQ SDK (140MB)** to `C:\psyq`.
*   Unzip the SDK automatically.
*   Add `C:\psyq\bin` to your **User PATH** so you can run tools like `psylib` from anywhere.
*   Set the `PSYQ_SDK` environment variable.

*Note: You may need to restart CLion after running this script for the new PATH to be recognized.*

## 🚀 Build Instructions (For Generated Projects)
Once you've created a new project from the **PSX Project** template:

### 1. Building the `.bin` and `.ps-exe`
Open your terminal in the project root and run:
```bash
make
```
This will:
1.  Use `armips` to assemble your `.asm` files into a `.bin` file.
2.  Use `python` and `bin2exe.py` to convert that `.bin` into a bootable PlayStation `.ps-exe` file.

### 2. Cleaning up
To remove the built binaries and start fresh:
```bash
make clean
```

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

#### **A. Using the Terminal (Recommended)**
Since this project requires **Java 21**, run the following command in your terminal to ensure the correct version is used for the build:
```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-21.0.10"; ./gradlew buildPlugin
```

#### **B. Using the Gradle Window in CLion**
1.  Open the **Gradle tool window** on the right.
2.  Navigate to: `psx-plugin` -> `Tasks` -> `intellij platform` -> `buildPlugin`.
3.  **Double-click `buildPlugin`**.

#### **Output Location**
Once the task finishes, you will find the installation package here:
`C:\dev\psx-plugin\build\distributions\psx-plugin-1.0-SNAPSHOT.zip`

### 4. Install the Plugin in CLion
1.  Open your main **CLion 2026.1** instance.
2.  Go to **File** -> **Settings** (or `Ctrl+Alt+S`).
3.  Select **Plugins** from the left-hand menu.
4.  Click the ⚙️ (gear icon) at the top of the Plugins window.
5.  Choose **Install Plugin from Disk...**.
6.  Select the `.zip` file from the `build\distributions\` folder.
7.  **Restart CLion** if prompted.

---

### 📝 Notes:
*   **PsyQ Headers:** For the C/C++ completion to work best, make sure your project includes the PsyQ header files (`.h`) so CLion can resolve the types.
*   **Feedback:** If you find any missing registers or functions while you're coding, let me know!

Happy coding on the PSX! 🚀🎮
