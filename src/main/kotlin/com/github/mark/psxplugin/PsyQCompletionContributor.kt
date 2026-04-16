package com.github.mark.psxplugin

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext

class PsyQCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(),
            object : CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    result: CompletionResultSet
                ) {
                    val file = parameters.originalFile
                    // Only provide completions for C/C++ files
                    if (file.name.endsWith(".c") || file.name.endsWith(".cpp") || file.name.endsWith(".h")) {
                        psyqFunctions.forEach {
                            result.addElement(LookupElementBuilder.create(it))
                        }

                        psyqConstants.forEach {
                            result.addElement(LookupElementBuilder.create(it).withBoldness(true))
                        }

                        psyqMacros.forEach {
                            result.addElement(
                                LookupElementBuilder.create(it)
                                    .withIcon(com.intellij.util.PlatformIcons.FUNCTION_ICON)
                                    .withTailText("(...)")
                                    .withTypeText("macro")
                            )
                        }

                        psyqStructs.forEach {
                            result.addElement(
                                LookupElementBuilder.create(it)
                                    .withIcon(com.intellij.util.PlatformIcons.CLASS_ICON)
                                    .withTypeText("struct")
                            )
                        }
                        
                        hardwareRegisters.forEach { (name, address) ->
                            result.addElement(
                                LookupElementBuilder.create(name)
                                    .withTypeText(address)
                                    .withPresentableText(name)
                                    .withTailText(" ($address)", true)
                            )
                        }
                    }
                }
            }
        )
    }

    companion object {
        private val hardwareRegisters = mapOf(
            "I_STAT" to "0x1F801070", "I_MASK" to "0x1F801074",
            "DMA0_MADR" to "0x1F801080", "DMA0_BCR" to "0x1F801084", "DMA0_CHCR" to "0x1F801088",
            "DMA1_MADR" to "0x1F801090", "DMA1_BCR" to "0x1F801094", "DMA1_CHCR" to "0x1F801098",
            "DMA2_MADR" to "0x1F8010A0", "DMA2_BCR" to "0x1F8010A4", "DMA2_CHCR" to "0x1F8010A8",
            "DMA3_MADR" to "0x1F8010B0", "DMA3_BCR" to "0x1F8010B4", "DMA3_CHCR" to "0x1F8010B8",
            "DMA4_MADR" to "0x1F8010C0", "DMA4_BCR" to "0x1F8010C4", "DMA4_CHCR" to "0x1F8010C8",
            "DMA5_MADR" to "0x1F8010D0", "DMA5_BCR" to "0x1F8010D4", "DMA5_CHCR" to "0x1F8010D8",
            "DMA6_MADR" to "0x1F8010E0", "DMA6_BCR" to "0x1F8010E4", "DMA6_CHCR" to "0x1F8010E8",
            "DPCR" to "0x1F8010F0", "DICR" to "0x1F8010F4",
            "T0_COUNT" to "0x1F801100", "T0_MODE" to "0x1F801104", "T0_TARGET" to "0x1F801108",
            "T1_COUNT" to "0x1F801110", "T1_MODE" to "0x1F801114", "T1_TARGET" to "0x1F801118",
            "T2_COUNT" to "0x1F801120", "T2_MODE" to "0x1F801124", "T2_TARGET" to "0x1F801128",
            "GP0" to "0x1F801810", "GP1" to "0x1F801814", "GPUSTAT" to "0x1F801814", "GPUREAD" to "0x1F801810",
            "MDEC_IN" to "0x1F801820", "MDEC_OUT" to "0x1F801820", "MDEC_CTRL" to "0x1F801824", "MDEC_STAT" to "0x1F801824",
            "SPU_STAT" to "0x1F801DAE", "SPU_CNT" to "0x1F801DAA",
            "SIO_DATA" to "0x1F801040", "SIO_STAT" to "0x1F801044", "SIO_MODE" to "0x1F801048", "SIO_CTRL" to "0x1F80104A", "SIO_BAUD" to "0x1F80104E",
            "JOY_DATA" to "0x1F801040", "JOY_STAT" to "0x1F801044", "JOY_MODE" to "0x1F801048", "JOY_CTRL" to "0x1F80104A", "JOY_BAUD" to "0x1F80104E",
            "COM_DELAY" to "0x1F801020", "COM_CTRL" to "0x1F801014"
        )

        private val psyqFunctions = listOf(
            // --- libapi (Kernel / System) ---
            "_96_init", "_96_remove", "_96_exit",
            "open", "lseek", "read", "write", "close", "ioctl", "exit",
            "set_debug_handler", "set_memsize", "get_memsize", "set_conf", "get_conf",
            "EnterCriticalSection", "ExitCriticalSection",
            "ChangeThread", "GetThreadStatus", "InitThread", "StartThread", "StopThread",
            "OpenEvent", "CloseEvent", "WaitEvent", "TestEvent", "EnableEvent", "DisableEvent",
            "DeliverEvent", "UnDeliverEvent", "AddQueuedEvent",

            // --- libgpu (Basic Graphics) ---
            "ResetGraph", "SetGraphDebug", "GetGraphDebug", "PutDrawEnv", "PutDispEnv",
            "SetDefDrawEnv", "SetDefDispEnv", "DrawSync", "VSync", "SetDispMask",
            "DrawOTag", "ClearOTag", "ClearOTagR", "SetDrawEnv", "SetDispEnv",
            "GetDrawEnv", "GetDispEnv", "LoadImage", "StoreImage", "MoveImage",
            "DrawPrim", "SetPolyF3", "SetPolyFT3", "SetPolyG3", "SetPolyGT3",
            "SetPolyF4", "SetPolyFT4", "SetPolyG4", "SetPolyGT4",
            "SetLineF2", "SetLineG2", "SetLineF3", "SetLineG3", "SetLineF4", "SetLineG4",
            "SetSprt8", "SetSprt16", "SetSprt", "SetTile8", "SetTile16", "SetTile",
            "FntOpen", "FntLoad", "FntPrint", "FntFlush",

            // --- libgs (Extended Graphics) ---
            "GsInitGraph", "GsSetContext", "GsGetContext", "GsSwapDisplay",
            "GsInitOt", "GsClearOt", "GsDrawOt", "GsSetWorkBase",
            "GsDefDispBuff", "GsGetActiveBuff", "GsSetDrawBuff",
            "GsInit3D", "GsSetProjection", "GsSetView2D", "GsSetView3D",
            "GsSetLight", "GsSetAmbient", "GsSetFog",
            "GsSortObject4", "GsSortSprite", "GsSortLine",

            // --- libgte (Geometry Transformation Engine) ---
            "InitGeom", "SetGeomOffset", "SetGeomScreen", "SetRotMatrix", "SetTransMatrix",
            "RotTrans", "RotTransPers", "NormalClip", "AverageZ3", "AverageZ4",
            "ApplyMatrix", "ApplyMatrixV", "CompMatrix", "MulMatrix0", "MulMatrix",

            // --- libcd (CD-ROM / Streaming) ---
            "CdInit", "CdSearchFile", "CdRead", "CdReadSync", "CdGetStatus",
            "CdControl", "CdControlB", "CdControlF", "CdMode", "CdStatus",
            "CdPlay", "CdPause", "CdStop", "CdSetDebug",

            // --- libspu (Basic Sound) ---
            "SpuInit", "SpuSetVoiceAttr", "SpuGetVoiceAttr", "SpuSetKey",
            "SpuGetKeyStatus", "SpuSetVoiceAddr", "SpuGetVoiceAddr",
            "SpuSetReverbMode", "SpuSetReverbVoice", "SpuGetReverbMode",
            "SpuMalloc", "SpuFree", "SpuSetTransferMode", "SpuWrite", "SpuRead",

            // --- libsnd (Extended Sound) ---
            "SsInit", "SsStart", "SsStop", "SsSetTable", "SsSetTickMode",
            "SsSeqOpen", "SsSeqPlay", "SsSeqStop", "SsSeqPause", "SsSeqSetVol",
            "SsUtKeyOn", "SsUtKeyOff", "SsUtSetVibrate",

            // --- libetc (Peripherals / Misc) ---
            "ResetCallback", "StopCallback", "SetVideoMode", "GetVideoMode",
            "PadInit", "PadRead", "PadStartCom", "PadStopCom",
            "InitPad", "StartPad", "StopPad",

            // --- libmath (Fixed-point Math) ---
            "FixedMul", "FixedDiv", "isqrt", "ratan2", "rcos", "rsin", "rsqrt",
            "VectorNormal", "VectorSize", "OuterProduct0", "ApplyMatrixSV",

            // --- libmcrd (Extended Memory Card) ---
            "MemCardInit", "MemCardStart", "MemCardStop", "MemCardExist", "MemCardAccept",
            "MemCardOpen", "MemCardClose", "MemCardSync", "MemCardReadData", "MemCardWriteData",
            "MemCardReadFile", "MemCardWriteFile", "MemCardCreateFile", "MemCardFormat", "MemCardGetDirentry",

            // --- libpress (Data Compression) ---
            "DecDCTReset", "DecDCTin", "DecDCTout", "DecDCTinSync", "DecDCToutSync",
            "DecDCTvlc", "DecDCTvlc2", "DecDCTvlcSize", "DecDCTvlcSize2", "EncSPU",

            // --- libtap (Multi Tap) ---
            "InitTAP", "StartTAP", "StopTAP",

            // --- libhmd (Hierarchical Model) ---
            "GsHMDInit", "GsHMDDisplay", "GsHMDFree",

            // --- libc / libc2 (Standard Library) ---
            "printf", "sprintf", "strcat", "strncat", "strcmp", "strncmp", "strcpy", "strncpy", "strlen",
            "memcpy", "memset", "memmove", "memcmp", "memchr", "bcopy", "bzero", "bcmp",
            "malloc", "free", "realloc", "calloc", "rand", "srand", "abs"
        )

        private val psyqConstants = listOf(
            // Graphics Modes
            "GsNONINTER", "GsINTER", "GsOFSGPU",
            "Gs320x240", "Gs320x480", "Gs512x240", "Gs512x480", "Gs640x240", "Gs640x480",
            "VMODE_NTSC", "VMODE_PAL",

            // Controller Buttons
            "PAD_SELECT", "PAD_START", "PAD_UP", "PAD_RIGHT", "PAD_DOWN", "PAD_LEFT",
            "PAD_L2", "PAD_R2", "PAD_L1", "PAD_R1",
            "PAD_TRIANGLE", "PAD_CIRCLE", "PAD_CROSS", "PAD_SQUARE",

            // CD-ROM Modes
            "CdlModeSpeed", "CdlModeRT", "CdlModeSize1", "CdlModeSize0",
            "CdlModeSF", "CdlModeReport", "CdlModeDouble", "CdlModeAP",

            // CD-ROM Commands
            "CdlNop", "CdlSetloc", "CdlPlay", "CdlForward", "CdlBackward",
            "CdlReadN", "CdlStandby", "CdlStop", "CdlPause", "CdlMute", "CdlDemute",
            "CdlSetfilter", "CdlSetmode", "CdlGetparam", "CdlGetlocL", "CdlGetlocP",

            // SPU Modes
            "SPU_OFF", "SPU_ON", "SPU_REV", "SPU_MUTE",
            "SPU_VOICE_VOLL", "SPU_VOICE_VOLR", "SPU_VOICE_PITCH", "SPU_VOICE_ADSR1", "SPU_VOICE_ADSR2",

            // libmcrd constants
            "McErrNone", "McErrNoCard", "McErrNotFormat", "McErrFull",
            "McErrExist", "McErrBlock", "McErrNonex",

            // libpress constants
            "DecDCT_VLC", "DecDCT_RL", "DecDCT_RGB",

            // libhmd constants
            "HMD_TYPE_POLY", "HMD_TYPE_SHARED", "HMD_TYPE_MESH", "HMD_TYPE_ANIM",

            // Misc
            "NULL", "TRUE", "FALSE"
        )

        private val psyqMacros = listOf(
            "setRECT", "setVector", "setSVECTOR", "setRGB0", "setaddr", "setlen", "setcode",
            "setPolyF3", "setPolyFT3", "setPolyG3", "setPolyGT3",
            "setPolyF4", "setPolyFT4", "setPolyG4", "setPolyGT4",
            "setLineF2", "setLineG2", "setLineF3", "setLineG3", "setLineF4", "setLineG4",
            "setSprt", "setSprt8", "setSprt16", "setTile", "setTile1", "setTile8", "setTile16"
        )

        private val psyqStructs = listOf(
            "RECT", "RECT16", "Point", "DVECTOR", "SVECTOR", "VECTOR",
            "DRAWENV", "DISPENV", "P_TAG", "P_CODE",
            "POLY_F3", "POLY_F4", "POLY_FT3", "POLY_FT4", "POLY_G3", "POLY_G4", "POLY_GT3", "POLY_GT4",
            "LINE_F2", "LINE_F3", "LINE_F4", "LINE_G2", "LINE_G3", "LINE_G4",
            "SPRT", "SPRT_8", "SPRT_16", "TILE", "TILE_1", "TILE_8", "TILE_16",
            "DR_ENV", "DR_MASK", "DR_TPAGE", "DR_STP",
            "GsOT", "GsOT_TAG", "GsBG", "GsMAP", "GsCELL", "GsSCREEN", "GsRVIEW2", "GsFVIEW2",
            "GsDRAWENV", "GsDISPENV", "GsIMAGE", "GsSPRITE", "GsLINE", "GsBOXF",
            "GsCOORDINATE2", "GsCOORD2PARAM", "GsBOX2D",
            "CdlFILE", "CdlLOC", "CdlFILTER", "CdlATV",
            "SpuVoiceAttr", "SpuReverbAttr", "SsSeqAttr", "SsVabHeader",
            "TIM_IMAGE", "TMD_STRUCT"
        )
    }
}
