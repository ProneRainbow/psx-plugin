/* $PSLibId: Run-time Library Release 4.6$ */
#ifndef _LIBGUN_H_
#define _LIBGUN_H_

/*
 *  Copyright (C) 1996 Sony Computer Entertainment America. All Rights Reserved
 *  libetc.h: Light Gun Interface
 */

/*
 * Prototypes
 */

#if defined(_LANGUAGE_C_PLUS_PLUS)||defined(__cplusplus)||defined(c_plusplus)
extern "C" {
#endif
/**
 * Initialize Light Gun library.
 *
 * @param bufA Buffer for port A.
 * @param lenA Length of port A buffer.
 * @param bufB Buffer for port B.
 * @param lenB Length of port B buffer.
 * @param buf1 Working buffer 1.
 * @param buf2 Working buffer 2.
 * @param len Length of working buffers.
 */
void InitGUN(char *bufA, long lenA, char *bufB, long lenB, char *buf1, char *buf2, long len);

/**
 * Stop Light Gun library and release resources.
 */
void RemoveGUN(void);

/**
 * Select Light Gun port and mask.
 *
 * @param ch Port number (0 or 1).
 * @param mask Channel mask.
 */
void SelectGUN(int ch, unsigned char mask);

/**
 * Start Light Gun scanning.
 *
 * @return 0 if successful.
 */
long StartGUN(void);

/**
 * Stop Light Gun scanning.
 */
void StopGUN(void);
#if defined(_LANGUAGE_C_PLUS_PLUS)||defined(__cplusplus)||defined(c_plusplus)
}
#endif
#endif /* _LIBGUN_H_ */
