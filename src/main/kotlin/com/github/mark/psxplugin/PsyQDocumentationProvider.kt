package com.github.mark.psxplugin

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.psi.PsiElement
import com.intellij.openapi.diagnostic.Logger

class PsyQDocumentationProvider : AbstractDocumentationProvider() {
    private val LOG = Logger.getInstance(PsyQDocumentationProvider::class.java)

    init {
        LOG.info("[DEBUG_LOG] PsyQDocumentationProvider INSTANTIATED")
    }

    override fun getCustomDocumentationElement(editor: com.intellij.openapi.editor.Editor, file: com.intellij.psi.PsiFile, contextElement: PsiElement?, targetOffset: Int): PsiElement? {
        if (contextElement == null) return null
        val text = contextElement.text
        LOG.info("[DEBUG_LOG] PsyQ getCustomDocElement for: '$text', lang: ${contextElement.language}")
        
        // If it's a number or a likely PsyQ identifier, we want to handle it
        if (tryConvertNumber(text) != null || 
            functionDocs.containsKey(text) || 
            registerDocs.containsKey(text) || 
            constantDocs.containsKey(text) || 
            structDocs.containsKey(text) || 
            macroDocs.containsKey(text) ||
            mipsRegisterDocs.containsKey(text.removePrefix("$").lowercase())) {
            return contextElement
        }
        return null
    }

    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
        val target = originalElement ?: element ?: return null
        val text = target.text.trim()
        
        LOG.info("[DEBUG_LOG] PsyQ generateDoc called for text: '$text', element: $target")
        
        // 1. Handle Numbers
        tryConvertNumber(text)?.let { return it }

        // 2. Try exact match first
        val doc = functionDocs[text] ?: registerDocs[text] ?: constantDocs[text] ?: structDocs[text] ?: macroDocs[text]
        if (doc != null) {
            val link = if (text.startsWith("$") || mipsRegisterDocs.containsKey(text.lowercase())) 
                "https://problemkaputt.de/psx-spx.htm" 
            else 
                "https://psx.arthus.net/sdk/Psy-Q/DOCS/Devrefs/Libovr.pdf"
            return "$doc<br><br><a href=\"$link\">Online Reference</a>"
        }

        // 3. Fallback for functions: If not in map, try to find in common prefix or show as "Function"
        if (text.firstOrNull()?.isUpperCase() == true && text.any { it.isLowerCase() }) {
             return "<b>$text()</b><br><br>PlayStation SDK Function.<br><br><a href=\"https://psx.arthus.net/sdk/Psy-Q/DOCS/Devrefs/Libovr.pdf\">Online Reference</a>"
        }

        // 4. Handle MIPS registers in C files
        val regName = text.removePrefix("$").lowercase()
        mipsRegisterDocs[regName]?.let { return it }
        
        return null
    }

    private fun tryConvertNumber(text: String): String? {
        val cleanText = text.lowercase().removeSuffix("h").removeSuffix("b")
        val longValue: Long = when {
            cleanText.startsWith("0x") -> cleanText.removePrefix("0x").toLongOrNull(16)
            cleanText.startsWith("0b") -> cleanText.removePrefix("0b").toLongOrNull(2)
            cleanText.all { it.isDigit() || it == '-' } -> cleanText.toLongOrNull()
            else -> null
        } ?: return null

        val dec = longValue.toString()
        val hex = "0x" + longValue.toString(16).uppercase()
        val bin = "0b" + longValue.toString(2)
        
        return """
            <b>Numeric Value</b><br>
            <hr>
            <table>
                <tr><td><b>Decimal:</b></td><td>$dec</td></tr>
                <tr><td><b>Hex:</b></td><td>$hex</td></tr>
                <tr><td><b>Binary:</b></td><td>$bin</td></tr>
            </table>
        """.trimIndent()
    }

    companion object {
        // ... existing maps ...
        private val mipsRegisterDocs = mapOf(
            "zero" to "<b>\$zero</b>: Always contains the value 0.",
            "at" to "<b>\$at</b>: Assembler Temporary. Reserved for use by the assembler.",
            "v0" to "<b>\$v0</b>: Expression evaluation and results of a function.",
            "v1" to "<b>\$v1</b>: Expression evaluation and results of a function.",
            "a0" to "<b>\$a0</b>: Argument 0. Used to pass the first parameter to a function.",
            "a1" to "<b>\$a1</b>: Argument 1. Used to pass the second parameter to a function.",
            "a2" to "<b>\$a2</b>: Argument 2. Used to pass the third parameter to a function.",
            "a3" to "<b>\$a3</b>: Argument 3. Used to pass the fourth parameter to a function.",
            "t0" to "<b>\$t0</b>: Temporary. Caller-saved. Subroutines can use without saving.",
            "t1" to "<b>\$t1</b>: Temporary. Caller-saved.",
            "t2" to "<b>\$t2</b>: Temporary. Caller-saved.",
            "t3" to "<b>\$t3</b>: Temporary. Caller-saved.",
            "t4" to "<b>\$t4</b>: Temporary. Caller-saved.",
            "t5" to "<b>\$t5</b>: Temporary. Caller-saved.",
            "t6" to "<b>\$t6</b>: Temporary. Caller-saved.",
            "t7" to "<b>\$t7</b>: Temporary. Caller-saved.",
            "s0" to "<b>\$s0</b>: Saved Temporary. Callee-saved. Must be preserved by subroutines.",
            "s1" to "<b>\$s1</b>: Saved Temporary. Callee-saved.",
            "s2" to "<b>\$s2</b>: Saved Temporary. Callee-saved.",
            "s3" to "<b>\$s3</b>: Saved Temporary. Callee-saved.",
            "s4" to "<b>\$s4</b>: Saved Temporary. Callee-saved.",
            "s5" to "<b>\$s5</b>: Saved Temporary. Callee-saved.",
            "s6" to "<b>\$s6</b>: Saved Temporary. Callee-saved.",
            "s7" to "<b>\$s7</b>: Saved Temporary. Callee-saved.",
            "t8" to "<b>\$t8</b>: Temporary. Caller-saved.",
            "t9" to "<b>\$t9</b>: Temporary. Caller-saved.",
            "k0" to "<b>\$k0</b>: Reserved for Kernel (Interrupt/Exception handling).",
            "k1" to "<b>\$k1</b>: Reserved for Kernel (Interrupt/Exception handling).",
            "gp" to "<b>\$gp</b>: Global Pointer. Points to the middle of the 64K block of static data.",
            "sp" to "<b>\$sp</b>: Stack Pointer. Points to the last location on the stack.",
            "fp" to "<b>\$fp</b>: Frame Pointer (or \$s8). Used to track the stack frame.",
            "ra" to "<b>\$ra</b>: Return Address. Stores the address to return to after a function call."
        )
        private val macroDocs = mapOf(
            "setRECT" to "<b>setRECT(r, x, y, w, h)</b>: Helper macro to initialize a RECT structure.",
            "setVector" to "<b>setVector(v, x, y, z)</b>: Helper macro to initialize a VECTOR structure.",
            "setSVECTOR" to "<b>setSVECTOR(v, x, y, z)</b>: Helper macro to initialize an SVECTOR structure.",
            "setRGB0" to "<b>setRGB0(p, r, g, b)</b>: Helper macro to set the RGB color of a primitive.",
            "setaddr" to "<b>setaddr(p, a)</b>: Helper macro to set the pointer to the next primitive in an ordering table.",
            "setPolyF3" to "<b>setPolyF3(p)</b>: Helper macro to initialize a flat-shaded triangle primitive.",
            "CombSioStatus" to "<b>CombSioStatus()</b>: Returns the current status of the serial controller.",
            "CombReset" to "<b>CombReset()</b>: Resets the serial controller and clears the communication buffers.",
            "CombSetBPS" to "<b>CombSetBPS(bps)</b>: Sets the serial transfer rate (bits per second).",
            "CombSetMode" to "<b>CombSetMode(mode)</b>: Configures the serial communication mode (data bits, stop bits, parity)."
        )
        private val structDocs = mapOf(
            "RECT" to "<b>RECT</b>: Defines a rectangular area. Contains short x, y (top-left) and short w, h (width, height).",
            "RECT32" to "<b>RECT32</b>: Defines a rectangular area using 32-bit integers. Contains long x, y, w, h.",
            "SVECTOR" to "<b>SVECTOR</b>: Short vector (16-bit). Contains short vx, vy, vz and short pad.",
            "VECTOR" to "<b>VECTOR</b>: Long vector (32-bit). Contains long vx, vy, vz and long pad.",
            "CVECTOR" to "<b>CVECTOR</b>: Color vector (8-bit). Contains byte r, g, b, cd (code).",
            "DVECTOR" to "<b>DVECTOR</b>: 2D short vector. Contains short vx, vy.",
            "MATRIX" to "<b>MATRIX</b>: 3x3 rotation matrix and 3x1 translation vector. Used for GTE transformations.",
            "DRAWENV" to "<b>DRAWENV</b>: Drawing environment structure. Defines clipping area, background color, offset, etc.",
            "DISPENV" to "<b>DISPENV</b>: Display environment structure. Defines display area, screen offset, interlace mode, etc.",
            "POLY_F3" to "<b>POLY_F3</b>: Flat-shaded triangle primitive (no texture, single color).",
            "POLY_F4" to "<b>POLY_F4</b>: Flat-shaded quadrangle primitive (no texture, single color).",
            "POLY_FT3" to "<b>POLY_FT3</b>: Flat-shaded textured triangle primitive.",
            "POLY_FT4" to "<b>POLY_FT4</b>: Flat-shaded textured quadrangle primitive.",
            "POLY_G3" to "<b>POLY_G3</b>: Gouraud-shaded triangle primitive (interpolated colors per vertex).",
            "POLY_G4" to "<b>POLY_G4</b>: Gouraud-shaded quadrangle primitive (interpolated colors per vertex).",
            "POLY_GT3" to "<b>POLY_GT3</b>: Gouraud-shaded textured triangle primitive.",
            "POLY_GT4" to "<b>POLY_GT4</b>: Gouraud-shaded textured quadrangle primitive.",
            "LINE_F2" to "<b>LINE_F2</b>: Single-segment flat-shaded line primitive.",
            "LINE_G2" to "<b>LINE_G2</b>: Single-segment Gouraud-shaded line primitive.",
            "LINE_F3" to "<b>LINE_F3</b>: 2-segment (3-point) flat-shaded polyline primitive.",
            "LINE_G3" to "<b>LINE_G3</b>: 2-segment (3-point) Gouraud-shaded polyline primitive.",
            "LINE_F4" to "<b>LINE_F4</b>: 3-segment (4-point) flat-shaded polyline primitive.",
            "LINE_G4" to "<b>LINE_G4</b>: 3-segment (4-point) Gouraud-shaded polyline primitive.",
            "SPRT" to "<b>SPRT</b>: Free-size sprite primitive. Can be any width and height.",
            "SPRT_16" to "<b>SPRT_16</b>: Fixed-size 16x16 sprite primitive.",
            "SPRT_8" to "<b>SPRT_8</b>: Fixed-size 8x8 sprite primitive.",
            "TILE" to "<b>TILE</b>: Free-size flat-shaded rectangle (solid color).",
            "TILE_16" to "<b>TILE_16</b>: Fixed-size 16x16 flat-shaded rectangle.",
            "TILE_8" to "<b>TILE_8</b>: Fixed-size 8x8 flat-shaded rectangle.",
            "TILE_1" to "<b>TILE_1</b>: Fixed-size 1x1 dot primitive.",
            "GsOT" to "<b>GsOT</b>: Ordering Table structure for the high-level libgs library.",
            "GsOT_TAG" to "<b>GsOT_TAG</b>: Single entry in a libgs Ordering Table.",
            "GsDOBJ2" to "<b>GsDOBJ2</b>: 3D object handler structure for libgs.",
            "GsSPRITE" to "<b>GsSPRITE</b>: Sprite handler structure for libgs.",
            "GsBG" to "<b>GsBG</b>: Background handler structure for libgs.",
            "GsLINE" to "<b>GsLINE</b>: Line handler structure for libgs.",
            "GsBOXF" to "<b>GsBOXF</b>: Filled box handler structure for libgs.",
            "CdlFILE" to "<b>CdlFILE</b>: Structure containing file information (name, pos, size) for CD-ROM operations.",
            "TIM_IMAGE" to "<b>TIM_IMAGE</b>: Structure containing TIM image information (VRAM position, CLUT, etc.).",
            "DECDCTENV" to "<b>DECDCTENV</b>: Environment structure for MDEC decoding. Contains quantization tables and IDCT coefficients.",
            "ENCSPUENV" to "<b>ENCSPUENV</b>: Environment structure for SPU ADPCM encoding. Defines source PCM data, destination buffer, and quality settings."
        )

        private val constantDocs = mapOf(
            "GsNONINTER" to "<b>GsNONINTER</b>: Non-interlaced display mode. Provides a flicker-free image but at half the vertical resolution of interlaced.",
            "GsINTER" to "<b>GsINTER</b>: Interlaced display mode. Provides higher vertical resolution but may exhibit flicker on CRT screens.",
            "GsOFSGPU" to "<b>GsOFSGPU</b>: Use GPU offsets for screen centering.",
            "VMODE_NTSC" to "<b>VMODE_NTSC</b>: Video mode for North American and Japanese regions (60Hz).",
            "VMODE_PAL" to "<b>VMODE_PAL</b>: Video mode for European regions (50Hz).",
            "PAD_SELECT" to "<b>PAD_SELECT</b>: Bitmask for the Select button.",
            "PAD_START" to "<b>PAD_START</b>: Bitmask for the Start button.",
            "PAD_UP" to "<b>PAD_UP</b>: Bitmask for the D-Pad Up button.",
            "PAD_TRIANGLE" to "<b>PAD_TRIANGLE</b>: Bitmask for the Triangle button.",
            "PAD_CROSS" to "<b>PAD_CROSS</b>: Bitmask for the Cross (X) button.",
            "PAD_CIRCLE" to "<b>PAD_CIRCLE</b>: Bitmask for the Circle button.",
            "PAD_SQUARE" to "<b>PAD_SQUARE</b>: Bitmask for the Square button.",
            "CdlModeDouble" to "<b>CdlModeDouble</b>: Double speed reading mode (300KB/s).",
            "SPU_ON" to "<b>SPU_ON</b>: Constant to enable SPU voice or reverb."
        )

        private val registerDocs = mapOf(
            "I_STAT" to "<b>I_STAT</b> (0x1F801070): Interrupt status register. Read to see which interrupts are pending.",
            "I_MASK" to "<b>I_MASK</b> (0x1F801074): Interrupt mask register. Set bits to enable specific interrupts.",
            "GP0" to "<b>GP0</b> (0x1F801810): GPU Command/Data register. Used to send drawing commands and vertex data.",
            "GP1" to "<b>GP1</b> (0x1F801814): GPU Control/Status register. Used for display settings and checking GPU state.",
            "GPUSTAT" to "<b>GPUSTAT</b> (0x1F801814): GPU Status register (Read-only).",
            "T0_COUNT" to "<b>T0_COUNT</b> (0x1F801100): Timer 0 Current Count.",
            "T1_COUNT" to "<b>T1_COUNT</b> (0x1F801110): Timer 1 Current Count (Horizontal Retrace).",
            "T2_COUNT" to "<b>T2_COUNT</b> (0x1F801120): Timer 2 Current Count (1/8 of System Clock).",
            "DMA_MADR" to "<b>DMA_MADR</b>: Memory Address register for a DMA channel.",
            "DMA_CHCR" to "<b>DMA_CHCR</b>: Channel Control register for a DMA channel."
        )

        private val functionDocs = mapOf(
            "GsInitGraph" to "<b>void GsInitGraph(short dot, short inter, short mode, short vmode, short vram)</b><br><br>Initializes the graphics system with specified resolution and mode.",
            "GsSwapDisplay" to "<b>void GsSwapDisplay()</b><br><br>Alias for GsSwapDispBuff. Swaps the drawing and display buffers (Double buffering).",
            "GsSwapDispBuff" to "<b>void GsSwapDispBuff()</b><br><br>Swaps the drawing and display buffers (Double buffering).",
            "GsSortObject4" to "<b>void GsSortObject4(GsDOBJ2 *objp, GsOT *ot, int shift, u_long *scratch)</b><br><br>Registers a 3D object for drawing in the ordering table.",
            "ResetGraph" to "<b>void ResetGraph(int mode)</b><br><br>Resets the GPU and clears the VRAM.",
            "PutDrawEnv" to "<b>void PutDrawEnv(DRAWENV *env)</b><br><br>Sends a drawing environment structure to the GPU.",
            "PutDispEnv" to "<b>void PutDispEnv(DISPENV *env)</b><br><br>Sends a display environment structure to the GPU.",
            "DrawOTag" to "<b>void DrawOTag(u_long *ot)</b><br><br>Initiates the drawing of all primitives registered in an ordering table.",
            "ClearOTag" to "<b>void ClearOTag(u_long *ot, int n)</b><br><br>Clears an ordering table.",
            "VSync" to "<b>int VSync(int mode)</b><br><br>Waits for the vertical retrace. Pass 0 to wait for the next retrace.",
            "DrawSync" to "<b>int DrawSync(int mode)</b><br><br>Waits until the GPU has finished all current drawing commands.",
            "CdRead" to "<b>int CdRead(int sectors, u_long *buf, int mode)</b><br><br>Reads sectors from the CD-ROM into memory.",
            "CdSearchFile" to "<b>CdlFILE *CdSearchFile(CdlFILE *fp, char *name)</b><br><br>Searches for a file on the CD-ROM by name.",
            "SpuInit" to "<b>void SpuInit()</b><br><br>Initializes the SPU (Sound Processing Unit).",
            "SpuSetVoiceAttr" to "<b>void SpuSetVoiceAttr(SpuVoiceAttr *arg)</b><br><br>Sets attributes (pitch, volume, address) for an SPU voice.",
            "SsInit" to "<b>void SsInit()</b><br><br>Initializes the sound system (Extended library).",
            "FntPrint" to "<b>int FntPrint(char *fmt, ...)</b><br><br>Adds a string to the internal print buffer for on-screen display.",
            "printf" to "<b>int printf(char *fmt, ...)</b><br><br>Standard C print function (redirected to SIO or on-screen via BIOS).",
            "EnterCriticalSection" to "<b>int EnterCriticalSection()</b><br><br>Disables interrupts. Returns 1 if successful.",
            "ExitCriticalSection" to "<b>void ExitCriticalSection()</b><br><br>Enables interrupts after a critical section.",
            "FixedMul" to "<b>long FixedMul(long a, long b)</b><br><br>Multiplies two 16.16 fixed-point numbers.",
            "rsin" to "<b>long rsin(long a)</b><br><br>Returns the sine of an angle (0-4096 range) in 1.12 fixed-point format.",
            "rcos" to "<b>long rcos(long a)</b><br><br>Returns the cosine of an angle (0-4096 range) in 1.12 fixed-point format.",
            "ratan2" to "<b>long ratan2(long y, long x)</b><br><br>Returns the arc tangent of y/x in 0-4096 range.",
            "_96_init" to "<b>void _96_init()</b><br><br>Initializes the BIOS A0 table and kernel services.",
            "PadRead" to "<b>u_long PadRead(int id)</b><br><br>Reads the current state of the game controllers.",
            "MemCardInit" to "<b>void MemCardInit(int card)</b><br><br>Initializes the Memory Card system.",
            "MemCardOpen" to "<b>int MemCardOpen(int chan, int slot, char *name)</b><br><br>Opens a file on the Memory Card.",
            "DecDCTin" to "<b>void DecDCTin(u_long *buf, int mode)</b><br><br>Feeds bitstream data to the MDEC for decoding.",
            "DecDCTout" to "<b>void DecDCTout(u_long *buf, int size)</b><br><br>Retrieves decoded image data from the MDEC.",
            "InitTAP" to "<b>int InitTAP(u_long *buf1, u_long *buf2)</b><br><br>Initializes the Multi-Tap peripheral support.",
            "GsHMDInit" to "<b>void GsHMDInit(u_long *hmd, GsHMD_ENV *env)</b><br><br>Initializes an HMD model structure for rendering.",
            "sin" to "<b>double sin(double a)</b><br><br>Returns the sine of an angle (standard C math library).",
            "cos" to "<b>double cos(double a)</b><br><br>Returns the cosine of an angle (standard C math library).",
            "tan" to "<b>double tan(double a)</b><br><br>Returns the tangent of an angle (standard C math library).",
            "sqrt" to "<b>double sqrt(double a)</b><br><br>Returns the square root of a number (standard C math library).",
            "pow" to "<b>double pow(double base, double exp)</b><br><br>Returns the value of base raised to the power of exp.",
            "DecDCTReset" to "<b>void DecDCTReset(int mode)</b><br><br>Resets the MDEC (Motion Decoder) to its initial state.",
            "EncSPU" to "<b>int EncSPU(ENCSPUENV *env)</b><br><br>Encodes PCM audio data into PlayStation-standard ADPCM format.",
            "DsInit" to "<b>void DsInit()</b><br><br>Initializes the high-level CD-ROM streaming library.",
            "DsCommand" to "<b>int DsCommand(u_char com, u_char *param, u_char *cbsync, u_char *count)</b><br><br>Sends a command to the CD-ROM drive using the high-level DS library.",
            "AddCOMB" to "<b>int AddCOMB()</b><br><br>Installs the serial communication (Link Cable) interrupt handler.",
            "DelCOMB" to "<b>int DelCOMB()</b><br><br>Uninstalls the serial communication (Link Cable) interrupt handler.",
            "InitGUN" to "<b>int InitGUN(u_long *buf, int count, void (*callback)())</b><br><br>Initializes the Light Gun library and sets up the communication buffers.",
            "StartGUN" to "<b>void StartGUN()</b><br><br>Starts the scanning process for the Light Gun.",
            "StopGUN" to "<b>void StopGUN()</b><br><br>Stops the Light Gun scanning process.",
            "EnableTAP" to "<b>void EnableTAP()</b><br><br>Enables the Multi-Tap adapter interface.",
            "DisableTAP" to "<b>void DisableTAP()</b><br><br>Disables the Multi-Tap adapter interface.",
            "MemCardStart" to "<b>void MemCardStart()</b><br><br>Starts the Memory Card interrupt handler and communication system.",
            "MemCardStop" to "<b>void MemCardStop()</b><br><br>Stops the Memory Card system and releases resources.",
            "MemCardExist" to "<b>int MemCardExist(int chan)</b><br><br>Checks if a Memory Card is physically inserted in the specified slot.",
            "MemCardAccept" to "<b>int MemCardAccept(int chan)</b><br><br>Attempts to identify and 'accept' the Memory Card in the specified slot.",
            "MemCardSync" to "<b>int MemCardSync(int mode, int *cmds, int *rslt)</b><br><br>Waits for the current Memory Card operation to complete.",
            "SetRCnt" to "<b>long SetRCnt(unsigned long t, unsigned short target, long mode)</b><br><br>Sets the target value and mode for a root counter.",
            "GetRCnt" to "<b>long GetRCnt(unsigned long t)</b><br><br>Returns the current value of a root counter.",
            "ResetRCnt" to "<b>long ResetRCnt(unsigned long t)</b><br><br>Resets the current value of a root counter to 0.",
            "StartRCnt" to "<b>long StartRCnt(unsigned long t)</b><br><br>Starts the operation of a root counter.",
            "StopRCnt" to "<b>long StopRCnt(unsigned long t)</b><br><br>Stops the operation of a root counter.",
            "OpenEvent" to "<b>long OpenEvent(unsigned long desc, long spec, long mode, long (*func)())</b><br><br>Opens a system event and registers a callback function.",
            "CloseEvent" to "<b>long CloseEvent(long event)</b><br><br>Closes a previously opened system event.",
            "WaitEvent" to "<b>long WaitEvent(long event)</b><br><br>Waits for a system event to occur (blocking).",
            "TestEvent" to "<b>long TestEvent(long event)</b><br><br>Tests if a system event has occurred (non-blocking).",
            "EnableEvent" to "<b>long EnableEvent(long event)</b><br><br>Enables the interrupt generation for a system event.",
            "DisableEvent" to "<b>long DisableEvent(long event)</b><br><br>Disables the interrupt generation for a system event.",
            "DeliverEvent" to "<b>void DeliverEvent(unsigned long desc, unsigned long spec)</b><br><br>Forcibly delivers a software event.",
            "OpenTh" to "<b>long OpenTh(long (*func)(), unsigned long stack, unsigned long sp)</b><br><br>Creates a new thread and returns its ID.",
            "CloseTh" to "<b>int CloseTh(long thread)</b><br><br>Deletes a thread and releases its resources.",
            "ChangeTh" to "<b>int ChangeTh(long thread)</b><br><br>Switches the execution context to the specified thread.",
            "open" to "<b>long open(char *name, unsigned long mode)</b><br><br>Opens a file and returns a handle.",
            "close" to "<b>long close(long fd)</b><br><br>Closes an open file handle.",
            "lseek" to "<b>long lseek(long fd, long offset, long whence)</b><br><br>Moves the file pointer to a new position.",
            "read" to "<b>long read(long fd, void *buf, long n)</b><br><br>Reads data from a file handle into a buffer.",
            "write" to "<b>long write(long fd, void *buf, long n)</b><br><br>Writes data from a buffer to a file handle.",
            "ioctl" to "<b>long ioctl(long fd, long cmd, long arg)</b><br><br>Performs a device-specific control operation.",
            "format" to "<b>long format(char *fs)</b><br><br>Formats a device (e.g., Memory Card).",
            "rename" to "<b>long rename(char *old, char *new)</b><br><br>Renames a file or directory.",
            "cd" to "<b>long cd(char *path)</b><br><br>Changes the current working directory.",
            "Exec" to "<b>long Exec(struct EXEC *exec, long argc, char **argv)</b><br><br>Executes a program loaded into memory.",
            "LoadExec" to "<b>long LoadExec(char *name, unsigned long stack, unsigned long sp)</b><br><br>Loads and executes an EXE file from a device.",
            "FlushCache" to "<b>void FlushCache(void)</b><br><br>Flushes the instruction and data caches.",
            "SetConf" to "<b>long SetConf(unsigned long events, unsigned long tcb, unsigned long stack)</b><br><br>Configures the kernel system parameters.",
            "GetConf" to "<b>void GetConf(unsigned long *events, unsigned long *tcb, unsigned long *stack)</b><br><br>Retrieves the current kernel system parameters.",
            "FntOpen" to "<b>int FntOpen(int x, int y, int w, int h, int isbg, int n)</b><br><br>Opens a text window for screen printing.",
            "LoadImage" to "<b>int LoadImage(RECT *rect, u_long *p)</b><br><br>Transfers image data from CPU memory to VRAM.",
            "StoreImage" to "<b>int StoreImage(RECT *rect, u_long *p)</b><br><br>Transfers image data from VRAM to CPU memory.",
            "MoveImage" to "<b>int MoveImage(RECT *rect, int x, int y)</b><br><br>Moves an image within VRAM.",
            "GetTPage" to "<b>u_short GetTPage(int tp, int abr, int x, int y)</b><br><br>Calculates a texture page attribute value.",
            "GetClut" to "<b>u_short GetClut(int x, int y)</b><br><br>Calculates a CLUT attribute value.",
            "AddPrim" to "<b>void AddPrim(void *ot, void *p)</b><br><br>Adds a primitive to an ordering table.",
            "SetPolyF3" to "<b>void SetPolyF3(POLY_F3 *p)</b><br><br>Initializes a flat triangle primitive.",
            "SetPolyF4" to "<b>void SetPolyF4(POLY_F4 *p)</b><br><br>Initializes a flat quadrangle primitive.",
            "SetPolyGT3" to "<b>void SetPolyGT3(POLY_GT3 *p)</b><br><br>Initializes a Gouraud-textured triangle primitive.",
            "SetPolyGT4" to "<b>void SetPolyGT4(POLY_GT4 *p)</b><br><br>Initializes a Gouraud-textured quadrangle primitive.",
            "SetSprt" to "<b>void SetSprt(SPRT *p)</b><br><br>Initializes a sprite primitive.",
            "SetTile" to "<b>void SetTile(TILE *p)</b><br><br>Initializes a tile primitive.",
            "InitGeom" to "<b>void InitGeom()</b><br><br>Initializes the Geometry Transformation Engine (GTE).",
            "SetRotMatrix" to "<b>void SetRotMatrix(MATRIX *m)</b><br><br>Sets the rotation matrix in the GTE.",
            "SetTransMatrix" to "<b>void SetTransMatrix(MATRIX *m)</b><br><br>Sets the translation vector in the GTE.",
            "RotTransPers" to "<b>long RotTransPers(SVECTOR *v0, long *sxy, long *p, long *flag)</b><br><br>Transforms and projects a 3D vertex using the GTE.",
            "NormalClip" to "<b>long NormalClip(long sxy0, long sxy1, long sxy2)</b><br><br>Performs 2D backface culling (clipping).",
            "CdPlay" to "<b>int CdPlay(int mode, int *tracks, int offset)</b><br><br>Starts CD-DA audio playback.",
            "CdControl" to "<b>int CdControl(u_char com, u_char *param, u_char *result)</b><br><br>Sends a low-level command to the CD-ROM controller.",
            "SsVabOpenHead" to "<b>short SsVabOpenHead(u_char *addr, short vabh)</b><br><br>Opens a VAB (Voice Bank) header.",
            "SsSeqOpen" to "<b>short SsSeqOpen(u_long *addr, short vab)</b><br><br>Opens a sequence (SEQ) file.",
            "SsSeqStart" to "<b>void SsSeqStart(short seq, short play_mode, short count)</b><br><br>Starts playing a sequence.",
            "SpuSetVoiceVolume" to "<b>void SpuSetVoiceVolume(int v_no, short left, short right)</b><br><br>Sets the volume for an SPU voice.",
            "SpuSetReverbMode" to "<b>void SpuSetReverbMode(int mode)</b><br><br>Sets the SPU reverb mode and parameters.",
            "atoi" to "<b>int atoi(const char *s)</b><br><br>Converts a string to an integer.",
            "toupper" to "<b>char toupper(char c)</b><br><br>Converts a lowercase character to uppercase.",
            "tolower" to "<b>char tolower(char c)</b><br><br>Converts an uppercase character to lowercase.",
            "malloc" to "<b>void *malloc(unsigned int size)</b><br><br>Allocates a block of memory from the heap.",
            "free" to "<b>void free(void *ptr)</b><br><br>Deallocates a previously allocated block of memory."
        )
    }
}
