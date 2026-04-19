package com.github.mark.psxplugin

import com.intellij.ide.util.projectWizard.ProjectSettingsStepBase
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.DirectoryProjectGenerator
import com.intellij.platform.DirectoryProjectGeneratorBase
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.vfs.VfsUtil
import java.io.InputStream
import javax.swing.Icon

class PsxProjectGenerator : DirectoryProjectGeneratorBase<Any>() {
    override fun getName(): String = "PSX Project"

    override fun getDescription(): String = "A project template for Sony PlayStation 1 development. Includes a pre-configured Makefile, the full PsyQ SDK headers for IntelliSense, and an automated SDK setup script."
    override fun getLogo(): Icon? = PsxIcons.PsxLogo

    override fun generateProject(project: Project, baseDir: VirtualFile, settings: Any, module: Module) {
        ApplicationManager.getApplication().runWriteAction {
            try {
                // 1. Create include directory
                val includeDir = baseDir.createChildDirectory(this, "include")

                // 2. Unpack PsyQ Headers from resources
                unpackHeaders(includeDir)

                // 3. Create Makefile
                val makefile = baseDir.createChildData(this, "Makefile")
                VfsUtil.saveText(makefile, createMakefileContent(project.name))

                // 4. Create main.c
                val mainC = baseDir.createChildData(this, "main.c")
                VfsUtil.saveText(mainC, createMainCContent())
                
                // 5. Create a detailed README
                val readme = baseDir.createChildData(this, "README.md")
                VfsUtil.saveText(readme, createReadmeContent(project.name))

                // 6. Create bin2exe.py
                val bin2exe = baseDir.createChildData(this, "bin2exe.py")
                VfsUtil.saveText(bin2exe, createBin2ExeContent())

                // 7. Create the main asm file
                val asmFile = baseDir.createChildData(this, "${project.name.lowercase()}.asm")
                VfsUtil.saveText(asmFile, createAsmContent(project.name))

                // 8. Create setup_psyq.ps1
                val setupPs1 = baseDir.createChildData(this, "setup_psyq.ps1")
                VfsUtil.saveText(setupPs1, createSetupPsyQContent())

                // 9. Create CMakeLists.txt for IDE Indexing
                val cmakeFile = baseDir.createChildData(this, "CMakeLists.txt")
                VfsUtil.saveText(cmakeFile, createCMakeListsContent(project.name))

                // 10. Create .gitignore
                val gitignore = baseDir.createChildData(this, ".gitignore")
                VfsUtil.saveText(gitignore, createGitignoreContent())

            } catch (e: Exception) {
                // Log error or handle appropriately
            }
        }
    }

    private fun createGitignoreContent(): String {
        return """
            # PSX Build Artifacts
            build/
        """.trimIndent()
    }

    private fun unpackHeaders(targetDir: VirtualFile) {
        val resourcePath = "/psyq_include"
        val headers = listOf(
            "ABS.H", "ASM.H", "ASSERT.H", "CONVERT.H", "CTYPE.H", "FS.H", "GTEMAC.H", "GTENOM.H",
            "GTEREG.H", "GTEREG_S.H", "INLINE_A.H", "INLINE_C.H", "INLINE_O.H", "INLINE_S.H",
            "KERNEL.H", "LIBAPI.H", "LIBCD.H", "LIBCOMB.H", "LIBDS.H", "LIBETC.H", "LIBGPU.H",
            "LIBGS.H", "LIBGTE.H", "LIBGUN.H", "LIBHMD.H", "LIBMATH.H", "LIBMCRD.H", "LIBMCX.H",
            "LIBPAD.H", "LIBPRESS.H", "LIBSIO.H", "LIBSN.H", "LIBSND.H", "LIBSPU.H", "LIBTAP.H",
            "LIMITS.H", "MALLOC.H", "MCGUI.H", "MEMORY.H", "QSORT.H", "R3000.H", "RAND.H",
            "ROMIO.H", "SETJMP.H", "STDARG.H", "STDDEF.H", "STDIO.H", "STDLIB.H", "STRING.H",
            "STRINGS.H",
            "SYS/ERRNO.H", "SYS/FCNTL.H", "SYS/FILE.H", "SYS/IOCTL.H", "SYS/TYPES.H"
        )

        for (header in headers) {
            val inputStream = javaClass.getResourceAsStream("$resourcePath/$header")
            if (inputStream != null) {
                try {
                    val content = inputStream.bufferedReader().readText()
                    
                    var currentTarget = targetDir
                    val parts = header.split("/")
                    if (parts.size > 1) {
                        for (i in 0 until parts.size - 1) {
                            val subDirName = parts[i].lowercase()
                            val existing = currentTarget.findChild(subDirName)
                            currentTarget = existing ?: currentTarget.createChildDirectory(this, subDirName)
                        }
                    }
                    
                    val fileName = parts.last()
                    val file = currentTarget.createChildData(this, fileName)
                    VfsUtil.saveText(file, content)
                } finally {
                    inputStream.close()
                }
            }
        }
    }

    private fun createMakefileContent(projectName: String): String {
        val name = projectName.lowercase()
        return """
# Use armips and bin2exe.py to build PSX binaries

ASM = armips
PYTHON = python
BIN2EXE = bin2exe.py
INCLUDE_PATH = include
BUILD_DIR = build

TARGET_BIN = $(BUILD_DIR)/$name.bin
TARGET_EXE = $(BUILD_DIR)/$name.ps-exe
SRC = $name.asm

all: $(BUILD_DIR) $(TARGET_EXE)

$(BUILD_DIR):
	@if not exist "$(BUILD_DIR)" mkdir "$(BUILD_DIR)"

$(TARGET_EXE): $(TARGET_BIN)
	$(PYTHON) $(BIN2EXE) $(TARGET_BIN) $(TARGET_EXE)

$(TARGET_BIN): $(SRC)
	$(ASM) $(SRC) -definelabel IS_BUILD_DIR 1

clean:
	@if exist "$(BUILD_DIR)" rd /s /q "$(BUILD_DIR)"
        """.trimIndent()
    }

    private fun createReadmeContent(projectName: String): String {
        return """
# $projectName

This project was generated by the **PSX MIPS and PsyQ Support** plugin for CLion.

## ✨ Plugin Features
- **MIPS Assembly Support:** Syntax highlighting, instruction descriptions, and register tooltips for `.asm` and `.s` files.
- **PsyQ C Support:** Auto-completion and documentation for standard PsyQ functions (`GsInitGraph`, `VSync`, etc.) and structures (`RECT`, `DRAWENV`).
- **Hardware Documentation:** Hover over PSX-specific registers (GPU, COP0, GTE) to see their hardware purpose.
- **Numeric Conversions:** Hover over any number (Decimal, Hex, or Binary) to see it converted across all three bases.
- **Project Templates:** One-click generation of PSX projects with pre-configured Makefiles and SDK setup scripts.

## 📦 Getting Started

### 1. Setup the PsyQ SDK
Before building, you need the PsyQ SDK installed on your system. This project includes a script to automate the process for you.

Open **PowerShell** in the project root and run:
```powershell
./setup_psyq.ps1
```
*This script will download the SDK to `C:\psyq`, extract it, and add the necessary environment variables to your system.*

### 2. Install Prerequisites
Ensure you have the following tools installed and added to your system **PATH**:
- **Python:** Required for the `bin2exe.py` conversion script.
- **armips:** The MIPS assembler used to build the project.

### 3. IDE Configuration (CLion)
To ensure that CLion properly recognizes the PsyQ headers and provides full IntelliSense:
1.  **Reload CMake:** If you see "File not found" errors on your `#include` statements, right-click `CMakeLists.txt` and select **Reload CMake Project**.
2.  **Restart CLion:** In some cases, a full restart of the IDE is required for the new include paths to be correctly indexed by the C/C++ engine.

## 🚀 Build Instructions

Use the following commands in your terminal to manage the build:

- **Build Project:** Compiles the assembly into a `.bin` file in the `build/` directory and then converts it to a PlayStation `.ps-exe`.
  ```bash
  make
  ```
- **Clean Build:** Removes the `build/` directory and all its contents.
  ```bash
  make clean
  ```

## 📁 Project Structure
- `main.c`: Sample C code demonstrating PsyQ initialization.
- `${projectName.lowercase()}.asm`: The main MIPS assembly source file.
- `include/`: Bundled PsyQ header files for IDE IntelliSense and auto-completion.
- `build/`: Contains generated build artifacts (`.bin`, `.ps-exe`).
- `Makefile`: Automates the assembly and conversion process, outputting to `build/`.
- `CMakeLists.txt`: Configures CLion's IntelliSense to find the PsyQ headers.
- `bin2exe.py`: Utility script to convert raw binaries to the PlayStation EXE format.
- `setup_psyq.ps1`: Automated SDK installation script.
- `.gitignore`: Configured to ignore the `build/` directory.
        """.trimIndent()
    }

    private fun createMainCContent(): String {
        return """
#include <sys/types.h>
#include <libetc.h>
#include <libgte.h>
#include <libgpu.h>
#include <libgs.h>
#include <stdio.h>

int main() {
    printf("Hello, PSX!\n");
    
    // 1. Initialize Graphics
    GsInitGraph(320, 240, GsNONINTER, 0, 0);
    
    // Add your game loop here
    while(1) {
        VSync(0);
        GsSwapDisplay();
    }
    
    return 0;
}
        """.trimIndent()
    }

    private fun createBin2ExeContent(): String {
        return """
#!/usr/bin/env python
from __future__ import print_function
import os
import sys
import struct
import math

usage = '''
python bin2exe.py infile outfile
'''

def main(argv):
    if len(argv) != 2:
        print(usage, file=sys.stderr)
        sys.exit(1)

    max_size = 0x200000
    infile_size = os.path.getsize(argv[0])
    if infile_size > max_size:
        print("Error: Input file %s longer than %d bytes" % (argv[0], max_size), file=sys.stderr)
        sys.exit(1)

    ofile = open(argv[1], 'wb')
    
    with open(argv[0], 'rb') as ifile:
        # Write header
        if sys.version_info >= (3, 0):
            ofile.write(bytes('PS-X EXE', 'ascii'))
        else:
            ofile.write('PS-X EXE')
        # Entry point
        ofile.seek(0x10)
        ofile.write(struct.pack('<I',0x80010000))
        # Initial GP/R28 (crt0.S currently sets this)
        ofile.write(struct.pack('<I',0xFFFFFFFF))
        # Destination address in RAM
        ofile.write(struct.pack('<I',0x80010000))
        # Initial SP/R29 & FP/R30
        ofile.seek(0x30)
        ofile.write(struct.pack('<I',0x801FFF00))
        # SP & FP offset added to    ^^^^^^^^^^ just use 0
        #ofile.write(struct.pack('<I',0x00000000))
        # Zero fill rest of the header
        ofile.seek(0x800)

        # Copy input to output
        buffer_size = 0x2000
        for i in range(0,int(math.ceil(float(infile_size)/buffer_size))):
            buffer = ifile.read(buffer_size)
            ofile.write(buffer)
        # ofile.write(ifile.read())

        # Pad binary to 0x800 boundary
        exe_size = ofile.tell()
        if exe_size % 0x800 != 0:
            exe_size += (0x800 - (exe_size % 0x800))
            ofile.seek(exe_size-1)
            ofile.write(struct.pack('B',0))

        # Filesize excluding 0x800 byte header
        ofile.seek(0x1C)
        ofile.write(struct.pack('<I', exe_size - 0x800))

    ofile.close()

if __name__ == '__main__':
    main(sys.argv[1:])
    sys.exit(0)
        """.trimIndent()
    }

    private fun createAsmContent(projectName: String): String {
        val name = projectName.lowercase()
        return """
            .psx
            .if defined(IS_BUILD_DIR)
                .create "build/$name.bin", 0x80010000
            .else
                .create "$name.bin", 0x80010000
            .endif

            .org 0x80010000
        """.trimIndent()
    }

    private fun createSetupPsyQContent(): String {
        return """
# PSX PsyQ SDK Setup Script
# This script downloads and installs the PsyQ SDK to C:\psyq

${'$'}url = "https://psx.arthus.net/sdk/Psy-Q/PSYQ_SDK.zip"
${'$'}dest = "C:\psyq"
${'$'}zip = "${'$'}dest\psyq.zip"

Write-Host "--- PlayStation 1 PsyQ SDK Setup ---" -ForegroundColor Cyan

# 1. Create Destination
if (!(Test-Path ${'$'}dest)) {
    Write-Host "[*] Creating directory ${'$'}dest..."
    New-Item -ItemType Directory -Path ${'$'}dest | Out-Null
}

# 2. Download SDK
Write-Host "[*] Downloading PsyQ SDK from ${'$'}url..." -NoNewline
try {
    Invoke-WebRequest -Uri ${'$'}url -OutFile ${'$'}zip
    Write-Host " Done!" -ForegroundColor Green
} catch {
    Write-Host " Failed!" -ForegroundColor Red
    Write-Error ${'$'}_
    exit
}

# 3. Extract SDK
Write-Host "[*] Extracting SDK to ${'$'}dest..." -NoNewline
try {
    Expand-Archive -Path ${'$'}zip -DestinationPath ${'$'}dest -Force
    Write-Host " Done!" -ForegroundColor Green
    Remove-Item ${'$'}zip
} catch {
    Write-Host " Failed!" -ForegroundColor Red
    Write-Error ${'$'}_
    exit
}

# 4. Set Environment Variables (User level)
Write-Host "[*] Setting environment variables..."
try {
    # Set PSYQ_SDK variable
    [Environment]::SetEnvironmentVariable("PSYQ_SDK", ${'$'}dest, "User")
    
    # Add to PATH
    ${'$'}oldPath = [Environment]::GetEnvironmentVariable("PATH", "User")
    if (${'$'}oldPath -notlike "*${'$'}dest\bin*") {
        ${'$'}newPath = "${'$'}oldPath;${'$'}dest\bin"
        [Environment]::SetEnvironmentVariable("PATH", ${'$'}newPath, "User")
        Write-Host "[+] C:\psyq\bin added to PATH." -ForegroundColor Green
    } else {
        Write-Host "[!] C:\psyq\bin is already in PATH." -ForegroundColor Yellow
    }
} catch {
    Write-Host "[-] Failed to set environment variables." -ForegroundColor Red
    Write-Error ${'$'}_
}

Write-Host "`n[SUCCESS] PsyQ SDK has been installed to ${'$'}dest" -ForegroundColor Green
Write-Host "[NOTE] You may need to restart your terminal or IDE for PATH changes to take effect." -ForegroundColor Cyan
        """.trimIndent()
    }

    private fun createCMakeListsContent(projectName: String): String {
        return """
cmake_minimum_required(VERSION 3.10)
project($projectName C)

# --- PSX MIPS & PsyQ Plugin Configuration ---
# Note: This file is primarily used for IDE Indexing (IntelliSense).
# Use the Makefile for the actual build process.

# 1. Include bundled PsyQ headers (using absolute path for reliability)
include_directories("${'$'}{CMAKE_CURRENT_SOURCE_DIR}/include")

# 2. Include actual PsyQ SDK if available
if(EXISTS "C:/psyq/include")
    include_directories("C:/psyq/include")
endif()

# 3. Define the project executable
# We include all .c files in the project root to ensure they are all indexed
file(GLOB SOURCES "*.c")
add_executable($projectName ${'$'}{SOURCES})

# 4. Compiler definitions for PSX
add_definitions(-D__psx__)

# 5. Fix for Clangd and CLion 'u_long' and other common PSX types
# This ensures that both Clangd and CLion's static analyzer recognize these common types.
add_definitions("-Du_char=unsigned char")
add_definitions("-Du_short=unsigned short")
add_definitions("-Du_int=unsigned int")
add_definitions("-Du_long=unsigned long")
add_definitions("-DUSHORT=unsigned short")
add_definitions("-DULONG=unsigned long")
add_definitions("-DUINT=unsigned int")
add_definitions("-DUCHAR=unsigned char")
add_definitions("-DMATRIX=struct MATRIX")
add_definitions("-DSVECTOR=struct SVECTOR")
add_definitions("-DVECTOR=struct VECTOR")
add_definitions("-DCVECTOR=struct CVECTOR")
add_definitions("-DDVECTOR=struct DVECTOR")
add_definitions("-DRECT=struct RECT")
add_definitions("-DRECT32=struct RECT32")
add_definitions("-DDRAWENV=struct DRAWENV")
add_definitions("-DDISPENV=struct DISPENV")
add_definitions("-DPOLY_F3=struct POLY_F3")
add_definitions("-DPOLY_F4=struct POLY_F4")
add_definitions("-DPOLY_FT3=struct POLY_FT3")
add_definitions("-DPOLY_FT4=struct POLY_FT4")
add_definitions("-DPOLY_G3=struct POLY_G3")
add_definitions("-DPOLY_G4=struct POLY_G4")
add_definitions("-DPOLY_GT3=struct POLY_GT3")
add_definitions("-DPOLY_GT4=struct POLY_GT4")
add_definitions("-DLINE_F2=struct LINE_F2")
add_definitions("-DLINE_G2=struct LINE_G2")
add_definitions("-DLINE_F3=struct LINE_F3")
add_definitions("-DLINE_G3=struct LINE_G3")
add_definitions("-DLINE_F4=struct LINE_F4")
add_definitions("-DLINE_G4=struct LINE_G4")
add_definitions("-DSPRT=struct SPRT")
add_definitions("-DSPRT_16=struct SPRT_16")
add_definitions("-DSPRT_8=struct SPRT_8")
add_definitions("-DTILE=struct TILE")
add_definitions("-DTILE_16=struct TILE_16")
add_definitions("-DTILE_8=struct TILE_8")
add_definitions("-DTILE_1=struct TILE_1")
add_definitions("-DGsOT=struct GsOT")
add_definitions("-DGsOT_TAG=struct GsOT_TAG")
add_definitions("-DGsDOBJ2=struct GsDOBJ2")
add_definitions("-DGsSPRITE=struct GsSPRITE")
add_definitions("-DGsBG=struct GsBG")
add_definitions("-DGsLINE=struct GsLINE")
add_definitions("-DGsBOXF=struct GsBOXF")
        """.trimIndent()
    }
}
