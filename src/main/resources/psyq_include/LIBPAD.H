#ifndef _LIBPAD_H_
#define _LIBPAD_H_

/*
 *	File:libpad.h
 *	Copyright (C) 1997 by Sony Computer Entertainment Inc.
 *			All rights Reserved
 */
/*
 * $PSLibId: Run-time Library Release 4.6$
 */

/* don't change these macros and structures which is referred in controler code */ 

#ifndef TRUE
#define TRUE		1
#endif
#ifndef FALSE
#define FALSE		0
#endif
#ifndef NULL
#define NULL		0
#endif


#define PadStateDiscon		0
#define PadStateFindPad		1
#define PadStateFindCTP1	2
#define PadStateFindCTP2	3
#define PadStateReqInfo		4
#define PadStateExecCmd		5
#define PadStateStable		6

#define InfoModeCurID		1
#define InfoModeCurExID		2
#define InfoModeCurExOffs	3
#define InfoModeIdTable		4

#define InfoActFunc		1
#define InfoActSub		2
#define InfoActSize		3
#define InfoActCurr		4
#define InfoActSign		5

#define PadMaxCurr		60	/* PS maximum current supply */
#define PadCurrCTP1		10	/* SCPH-1150 biblator current */


/*
 * Prototypes
 */

#if defined(_LANGUAGE_C_PLUS_PLUS)||defined(__cplusplus)||defined(c_plusplus)
extern "C" {
#endif

/**
 * Initialize controller buffers.
 * @param buf1 Primary controller buffer (34 bytes)
 * @param buf2 Secondary controller buffer (34 bytes)
 */
void PadInitDirect(unsigned char *, unsigned char *);

/**
 * Initialize multi-tap controller buffers.
 * @param buf1 Buffer 1
 * @param buf2 Buffer 2
 */
void PadInitMtap(unsigned char *, unsigned char *);

/**
 * Get the current controller state.
 * @param port Port number (0x00 for P1, 0x10 for P2)
 * @return Controller state (PadStateDiscon, PadStateStable, etc.)
 */
int PadGetState(int);

/**
 * Get information about controller mode.
 * @param port Port number
 * @param mode Information mode (InfoModeCurID, etc.)
 * @param offs Offset
 * @return Mode information
 */
int PadInfoMode(int, int, int);

/**
 * Configure controller vibration/actuators.
 * @param port Port number
 * @param align Alignment buffer
 * @param size Buffer size
 * @return 1 on success, 0 on failure
 */
int PadSetActAlign(int, unsigned char *);

/**
 * Set the main controller mode (Analog/Digital).
 * @param socket Socket number
 * @param offs Mode offset
 * @param lock Lock status
 * @return 1 on success, 0 on failure
 */
int PadSetMainMode(int socket, int offs, int lock);
void PadSetAct(int, unsigned char *, int);


#if defined(_LANGUAGE_C_PLUS_PLUS)||defined(__cplusplus)||defined(c_plusplus)
}
#endif

#endif /* _LIBPAD_H_ */

/* don't add stuff after this */

