package com.github.mark.psxplugin

object PsyQHeaderData {
    fun getHeaders(): Map<String, String> {
        return mapOf(
            "sys/types.h" to """
                #ifndef _SYS_TYPES_H
                #define _SYS_TYPES_H
                #ifndef _UCHAR_T
                #define _UCHAR_T
                typedef unsigned char   u_char;
                #endif
                #ifndef _USHORT_T
                #define _USHORT_T
                typedef unsigned short  u_short;
                #endif
                #ifndef _UINT_T
                #define _UINT_T
                typedef unsigned int    u_int;
                #endif
                #ifndef _ULONG_T
                #define _ULONG_T
                typedef unsigned long   u_long;
                #endif
                typedef unsigned char  u_int8;
                typedef unsigned short u_int16;
                typedef unsigned int   u_int32;
                #ifndef _SIZE_T
                #define _SIZE_T
                typedef unsigned int size_t;
                #endif
                typedef long    time_t;
                typedef short   dev_t;
                typedef long    off_t;
                #endif
            """.trimIndent(),
            
            "libgpu.h" to """
                #ifndef _LIBGPU_H_
                #define _LIBGPU_H_
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
                    u_long tag;
                    u_char r0, g0, b0, code;
                    short x0, y0;
                    short x1, y1;
                    short x2, y2;
                } POLY_F3_G;

                typedef struct {
                    u_long tag;
                    u_char r0, g0, b0, code;
                    short x0, y0;
                    short x1, y1;
                    short x2, y2;
                    short x3, y3;
                } POLY_F4;

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
                void InitDisplay(int x, int y);
                u_long *ClearOTagR(u_long *ot, int n);
                void AddPrim(void *ot, void *p);
                #endif
            """.trimIndent(),

            "libgs.h" to """
                #ifndef _LIBGS_H_
                #define _LIBGS_H_
                #include <libgpu.h>

                #define GsNONINTER 0
                #define GsINTER    1
                #define GsVRAMICON 0
                #define GsPAL      1
                #define GsNTSC     0

                void GsInitGraph(short x, short y, short mode, short inter, short vram);
                void GsSwapDisplay(void);
                void GsSetDrawEnv(DRAWENV *env);
                void GsSetDispEnv(DISPENV *env);
                #endif
            """.trimIndent(),

            "libgte.h" to """
                #ifndef _LIBGTE_H_
                #define _LIBGTE_H_
                #include <sys/types.h>

                #define ONE 4096

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
                void SetRotMatrix(MATRIX *m);
                void SetTransMatrix(MATRIX *m);
                #endif
            """.trimIndent(),

            "libetc.h" to """
                #ifndef _LIBETC_H_
                #define _LIBETC_H_
                
                #define PADLup     (1<<12)
                #define PADLdown   (1<<14)
                #define PADLleft   (1<<15)
                #define PADLright  (1<<13)
                #define PADRup     (1<< 4)
                #define PADRdown   (1<< 6)
                #define PADRleft   (1<< 7)
                #define PADRright  (1<< 5)
                #define PADstart   (1<<11)
                #define PADselect  (1<< 8)
                
                int VSync(int mode);
                u_long PadRead(int id);
                void PadInit(int mode);
                #endif
            """.trimIndent(),

            "libapi.h" to """
                #ifndef _LIBAPI_H_
                #define _LIBAPI_H_
                void _96_init(void);
                void _96_remove(void);
                long OpenEvent(u_long desc, long spec, long mode, long (*func)());
                long CloseEvent(long event);
                long WaitEvent(long event);
                long TestEvent(long event);
                long EnableEvent(long event);
                long DisableEvent(long event);
                void DeliverEvent(u_long desc, u_long spec);
                long open(char *name, u_long access);
                long close(long fd);
                long read(long fd, void *buf, long n);
                long write(long fd, void *buf, long n);
                #endif
            """.trimIndent(),

            "kernel.h" to """
                #ifndef _KERNEL_H
                #define _KERNEL_H
                
                typedef struct {
                    unsigned long pc0;
                    unsigned long gp0;
                    unsigned long t_addr;
                    unsigned long t_size;
                    unsigned long d_addr;
                    unsigned long d_size;
                    unsigned long b_addr;
                    unsigned long b_size;
                    unsigned long s_addr;
                    unsigned long s_size;
                    unsigned long sp,fp,gp,ret,base;
                } EXEC;

                typedef struct {
                    char name[20];
                    long attr;
                    long size;
                    void *next;
                    long head;
                    char system[4];
                } DIRENTRY;

                #ifndef NULL
                #define NULL (0)
                #endif
                #endif
            """.trimIndent(),

            "libcd.h" to """
                #ifndef _LIBCD_H_
                #define _LIBCD_H_
                
                typedef struct {
                    u_char minute;
                    u_char second;
                    u_char sector;
                    u_char track;
                } CdlLOC;

                int CdInit(void);
                int CdRead(int nsector, u_long *buf, int mode);
                CdlLOC *CdIntToPos(int i, CdlLOC *p);
                int CdPosToInt(CdlLOC *p);
                #endif
            """.trimIndent(),

            "libsnd.h" to """
                #ifndef _LIBSND_H_
                #define _LIBSND_H_
                #include <sys/types.h>
                
                void SsInit(void);
                void SsStart(void);
                void SsQuit(void);
                #endif
            """.trimIndent(),

            "libspu.h" to """
                #ifndef _LIBSPU_H_
                #define _LIBSPU_H_
                
                #define SPU_ON  1
                #define SPU_OFF 0
                
                void SpuInit(void);
                void SpuSetKey(int on_off, u_long voice_bit);
                #endif
            """.trimIndent()
        )
    }
}
