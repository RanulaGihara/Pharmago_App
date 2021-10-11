package com.example.pharmago.util;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/** @author Ceylon Linux (pvt) Ltd
 * */
public class LockProvider {
	private static final ReentrantReadWriteLock.ReadLock READ_LOCK;
	private static final ReentrantReadWriteLock.WriteLock WRITE_LOCK;

	static {
		ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock(true);
		READ_LOCK = reentrantReadWriteLock.readLock();
		WRITE_LOCK = reentrantReadWriteLock.writeLock();
	}

	public static ReentrantReadWriteLock.ReadLock getReadLock() {
		return READ_LOCK;
	}

	public static ReentrantReadWriteLock.WriteLock getWriteLock() {
		return WRITE_LOCK;
	}
}
