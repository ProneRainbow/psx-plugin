#ifndef _LIBAPI_H_
#define _LIBAPI_H_

/*
 *	File:libapi.h
 *	Copyright (C) 1997 by Sony Computer Entertainment Inc.
 *			All rights Reserved
 */
/*
 * $PSLibId: Run-time Library Release 4.6$
 */

#ifndef _KERNEL_H
#include "kernel.h"
#endif

/* don't change these macros and structures which is referred in controler code */

/*
 * Prototypes
 */

#if defined(_LANGUAGE_C_PLUS_PLUS)||defined(__cplusplus)||defined(c_plusplus)
extern "C" {
#endif

/* prototypes added by suzu 96/03/01 and changed by hakama 96/06/06*/

/**
 * Set the root counter.
 * @param rcnt Root counter number (RCntCNT0-RCntCNT3)
 * @param target Target value
 * @param mode Counter mode
 * @return 1 on success, 0 on failure
 */
extern long SetRCnt(unsigned long, unsigned short, long);

/**
 * Get the current root counter value.
 * @param rcnt Root counter number
 * @return Current counter value
 */
extern long GetRCnt(unsigned long);

/**
 * Reset the root counter.
 * @param rcnt Root counter number
 * @return 1 on success, 0 on failure
 */
extern long ResetRCnt(unsigned long);

/**
 * Start the root counter.
 * @param rcnt Root counter number
 * @return 1 on success, 0 on failure
 */
extern long StartRCnt(unsigned long);

/**
 * Stop the root counter.
 * @param rcnt Root counter number
 * @return 1 on success, 0 on failure
 */
extern long StopRCnt(unsigned long);

/**
 * Open a system event.
 * @param desc Event descriptor (HwVBLANK, SwCARD, etc.)
 * @param spec Event specification
 * @param mode Event mode
 * @param func Callback function
 * @return Event ID
 */
extern long OpenEvent(unsigned long,long,long,long (*func)());

/**
 * Close a system event.
 * @param event Event ID
 * @return 1 on success, 0 on failure
 */
extern long CloseEvent(long);

/**
 * Wait for a system event to occur.
 * @param event Event ID
 * @return 1 on success, 0 on failure
 */
extern long WaitEvent(long);

/**
 * Test if a system event has occurred.
 * @param event Event ID
 * @return 1 on success, 0 on failure
 */
extern long TestEvent(long);

/**
 * Enable a system event.
 * @param event Event ID
 * @return 1 on success, 0 on failure
 */
extern long EnableEvent(long);

/**
 * Disable a system event.
 * @param event Event ID
 * @return 1 on success, 0 on failure
 */
extern long DisableEvent(long);

/**
 * Deliver a system event.
 * @param desc Event descriptor
 * @param spec Event specification
 */
extern void DeliverEvent(unsigned long, unsigned long);

/**
 * Un-deliver a system event.
 * @param desc Event descriptor
 * @param spec Event specification
 */
extern void UnDeliverEvent(unsigned long, unsigned long);

/**
 * Open a new thread.
 * @param func Thread entry function
 * @param stack Top of stack address
 * @param gp Initial GP register value
 * @return Thread ID
 */
extern long OpenTh(long (*func)(), unsigned long , unsigned long);

/**
 * Close a thread.
 * @param thread Thread ID
 * @return 1 on success, 0 on failure
 */
extern int CloseTh(long);

/**
 * Change the current thread.
 * @param thread Thread ID
 * @return 1 on success, 0 on failure
 */
extern int ChangeTh(long);

/**
 * Open a file.
 * @param name File name (e.g., "cdrom:\\FILE.BIN;1" or "sim:\\FILE.BIN")
 * @param access Open mode (O_RDONLY, O_WRONLY, etc.)
 * @return File descriptor or -1 on failure
 */
extern long open(char *, unsigned long);

/**
 * Close a file.
 * @param fd File descriptor
 * @return 0 on success, -1 on failure
 */
extern long close(long);

/**
 * Seek within a file.
 * @param fd File descriptor
 * @param offset Byte offset
 * @param whence Seek mode (SEEK_SET, SEEK_CUR, SEEK_END)
 * @return New file position or -1 on failure
 */
extern long lseek(long, long, long);
/**
 * Read from a file.
 * @param fd File descriptor
 * @param buf Destination buffer
 * @param n Byte count
 * @return Number of bytes read or -1 on failure
 */
extern long read(long, void *, long);

/**
 * Write to a file.
 * @param fd File descriptor
 * @param buf Source buffer
 * @param n Byte count
 * @return Number of bytes written or -1 on failure
 */
extern long write(long, void *, long);

/**
 * Control device parameters.
 * @param fd File descriptor
 * @param cmd Command
 * @param arg Argument
 * @return Command-specific return value
 */
extern long ioctl(long, long, long);

/**
 * Find the first file matching a pattern.
 * @param name Search pattern (e.g., "cdrom:\\*;1")
 * @param dir Destination DIRENTRY structure
 * @return Pointer to dir or NULL if not found
 */
extern struct DIRENTRY * firstfile(char *, struct DIRENTRY *);

/**
 * Find the next file matching a pattern.
 * @param dir DIRENTRY structure from firstfile
 * @return Pointer to dir or NULL if not found
 */
extern struct DIRENTRY * nextfile(struct DIRENTRY *);

/**
 * Delete a file.
 * @param name File name
 * @return 1 on success, 0 on failure
 */
extern long erase(char *);


extern long undelete(char *);
extern long format(char *);
extern long rename(char *, char *);
extern long cd(char *);

extern long LoadTest(char *, struct EXEC *);
/**
 * Load a PS-X executable into memory.
 * @param name File name
 * @param exec EXEC structure to fill
 * @return 1 on success, 0 on failure
 */
extern long Load(char *, struct EXEC *);

/**
 * Execute a loaded program.
 * @param exec EXEC structure
 * @param argc Argument count
 * @param argv Argument vector
 * @return 0 on success
 */
extern long Exec(struct EXEC *, long, char **);

/**
 * Load and execute a program.
 * @param name File name
 * @param stack Top of stack address
 * @param stack_size Stack size
 * @return 1 on success, 0 on failure
 */
extern long LoadExec(char *, unsigned long, unsigned long);

extern long InitPAD(char *,long ,char *,long);
extern long StartPAD(void);
extern void StopPAD(void);
extern void EnablePAD(void);
extern void DisablePAD(void);

/**
 * Flush the CPU instruction and data caches.
 */
extern void FlushCache(void);

/**
 * Return from a software exception.
 */
extern void ReturnFromException(void);

/**
 * Disable interrupts and enter a critical section.
 * @return Previous status register value
 */
extern int  EnterCriticalSection(void);

/**
 * Enable interrupts and exit a critical section.
 */
extern void ExitCriticalSection(void);
extern void Exception(void);
extern void SwEnterCriticalSection(void);
extern void SwExitCriticalSection(void);

extern unsigned long SetSp(unsigned long);
extern unsigned long GetSp( void );
extern unsigned long GetGp( void );
extern unsigned long GetCr( void );
extern unsigned long GetSr( void );
extern unsigned long GetSysSp(void);

extern long SetConf(unsigned long,unsigned long,unsigned long);
extern void GetConf(unsigned long *,unsigned long *,unsigned long *);

extern long _get_errno(void);
extern long _get_error(long);

extern void SystemError( char, long);
extern void SetMem(long);

extern long Krom2RawAdd( unsigned long );
extern long Krom2RawAdd2(unsigned short);

extern void _96_init(void);
extern void _96_remove(void);
extern void _boot(void);

extern void ChangeClearPAD( long );

/* prototypes added by shino 96/05/22 */
extern void InitCARD(long val);
extern long StartCARD(void);
extern long StopCARD(void);
extern void _bu_init(void);
extern long _card_info(long chan);
extern long _card_clear(long chan);
extern long _card_load(long chan);
extern long _card_auto(long val);
extern void _new_card(void);
extern long _card_status(long drv);
extern long _card_wait(long drv);
extern unsigned long _card_chan(void);
extern long _card_write(long chan, long block, unsigned char *buf);
extern long _card_read(long chan, long block, unsigned char *buf);
extern long _card_format(long chan);	/* added by iwano 98/03/24 */



#if defined(_LANGUAGE_C_PLUS_PLUS)||defined(__cplusplus)||defined(c_plusplus)
}
#endif

#endif /* _LIBAPI_H_ */

/* don't add stuff after this */

