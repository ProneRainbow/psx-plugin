package com.github.mark.psxplugin

object PsyQHeaderData {
    fun getHeaders(): Map<String, String> {
        return mapOf(
            "sys/types.h" to """
                #ifndef _SYS_TYPES_H
                #define _SYS_TYPES_H
                typedef unsigned char  u_char;
                typedef unsigned short u_short;
                typedef unsigned int   u_int;
                typedef unsigned long  u_long;
                typedef unsigned char  u_int8;
                typedef unsigned short u_int16;
                typedef unsigned int   u_int32;
                #endif
            """.trimIndent(),
            
            "libgpu.h" to """
                #ifndef _LIBGPU_H
                #define _LIBGPU_H
                #include <sys/types.h>

                typedef struct {
                    short x, y;
                    short w, h;
                } RECT;

                typedef struct {
                    u_long tag;
                    u_char r0, g0, b0, code;
                    short x0, y0;
                } POLY_F3;

                typedef struct {
                    RECT clip;
                    short offx, offy;
                    u_short dtd, dfe;
                    u_char isbg;
                    u_char r0, g0, b0;
                } DRAWENV;

                typedef struct {
                    RECT screen;
                    u_short isinter;
                    u_short isrgb24;
                    short pad0, pad1;
                } DISPENV;

                void ResetGraph(int mode);
                void SetDefDrawEnv(DRAWENV *env, int x, int y, int w, int h);
                void SetDefDispEnv(DISPENV *env, int x, int y, int w, int h);
                void PutDrawEnv(DRAWENV *env);
                void PutDispEnv(DISPENV *env);
                int  DrawSync(int mode);
                #endif
            """.trimIndent(),

            "libgs.h" to """
                #ifndef _LIBGS_H
                #define _LIBGS_H
                #include <libgpu.h>

                #define GsNONINTER 0
                #define GsINTER    1

                void GsInitGraph(short x, short y, short mode, short inter, short vram);
                void GsSwapDisplay(void);
                #endif
            """.trimIndent(),

            "libgte.h" to """
                #ifndef _LIBGTE_H
                #define _LIBGTE_H
                #include <sys/types.h>

                typedef struct {
                    short vx, vy, vz, pad;
                } SVECTOR;

                typedef struct {
                    long vx, vy, vz, pad;
                } VECTOR;

                typedef struct {
                    short m[3][3];
                    long  t[3];
                } MATRIX;

                void InitGeom(void);
                void SetGeomOffset(long x, long y);
                void SetGeomScreen(long h);
                #endif
            """.trimIndent(),

            "libetc.h" to """
                #ifndef _LIBETC_H
                #define _LIBETC_H
                int VSync(int mode);
                int PadRead(int id);
                #endif
            """.trimIndent(),

            "libapi.h" to """
                #ifndef _LIBAPI_H
                #define _LIBAPI_H
                void _96_init(void);
                void _96_remove(void);
                #endif
            """.trimIndent()
        )
    }
}
