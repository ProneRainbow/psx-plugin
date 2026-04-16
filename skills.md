# 🛠 Developer Skills: PSX Plugin & Game Development

This document outlines the specialized skills utilized and available for the **PSX MIPS and PsyQ Support** project.

## 🧩 CLion & IntelliJ Plugin Development
- **Language Support Integration:**
    - Custom Language definition (`PSX_MIPS`).
    - Granular PSI (Program Structure Interface) tree parsing for assembly and C.
    - Lexer and Parser implementation using IntelliJ Platform APIs.
- **Advanced Editor Features:**
    - **Documentation Providers:** Implementation of `DocumentationProvider` for cross-language support (Assembly and C).
    - **Live Templates:** Creation and management of context-aware snippets for PSX-specific boilerplate.
    - **Syntax Highlighting:** Multi-token highlighting for opcodes, registers, and directives.
    - **Completion Contributors:** Tailored auto-completion for SDK-specific symbols and hardware registers.
- **Diagnostics & Tooling:**
    - Integrated multi-base numeric conversion engine (Dec/Hex/Bin).
    - Strategic use of IntelliJ logging for "radar" style debugging of plugin internals.

## 🎮 PlayStation 1 (PSX) Game Development
- **MIPS R3000A Architecture:**
    - Deep understanding of the R3000A instruction set and pipeline (delay slots).
    - Register file management (Standard, COP0, and COP2/GTE).
- **Hardware-Level Knowledge:**
    - **GTE (Geometry Transformation Engine):** Specialized knowledge of COP2 registers and opcodes for 3D calculations (fixed-point math, vector transformations).
    - **COP0 (System Control):** Interrupt handling, exception management, and memory-mapped I/O.
    - **GPU Communication:** Direct interaction with `GP0` (Commands/Data) and `GP1` (Status/Control) registers.
- **SDK & Libraries:**
    - **PsyQ SDK:** Proficiency in `libgpu`, `libgs`, `libcd`, `libspu`, and BIOS-level calls.
    - **Modern SDKs:** Familiarity with `nolibgs` and Nugget for contemporary PSX development.
- **Technical Documentation:**
    - Proficient in interpreting hardware specifications from resources like **psx-spx** (ConsoleDev) and official Sony documentation.

## 📚 Reference Resources
- **psx-spx:** [Hardware Specifications](https://psx-spx.consoledev.net/)
- **nolibgs:** [Modern PSX Toolchain](https://schnappy.xyz/nolibgs/)
- **PsyQ SDK Reference:** Sony's original developer manuals and libraries.
