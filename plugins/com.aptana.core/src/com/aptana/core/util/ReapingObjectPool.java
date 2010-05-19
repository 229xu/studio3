package com.aptana.core.util;

import java.util.Enumeration;
import java.util.Hashtable;

class ConnectionReaper extends Thread
{

	private ReapingObjectPool pool;
	private final long delay = 300000;

	ConnectionReaper(ReapingObjectPool pool)
	{
		this.pool = pool;
	}

	public void run()
	{
		while (true)
		{
			try
			{
				sleep(delay);
			}
			catch (InterruptedException e)
			{
			}
			pool.reap();
		}
	}
}

/**
 * An object pool that spawns off a reaper thread. This pool doesn't do any validation or expiration on checkout, it
 * solely manages a listing o locked and unlocked instances. The reaper manages testing expiration and validation of the
 * unlocked instances. This type of pool is handy when validation might be costly.
 * 
 * @author cwilliams
 * @param <T>
 */
public abstract class ReapingObjectPool<T> implements IObjectPool<T>
{

	private long expirationTime;
	private Hashtable<T, Long> locked, unlocked;
	private ConnectionReaper reaper;
	private int poolsize = 10;

	public ReapingObjectPool()
	{
		this(30000);
	}

	// TODO Enforce pool size!
	public ReapingObjectPool(int expirationTime)
	{
		this.expirationTime = expirationTime;
		this.locked = new Hashtable<T, Long>(poolsize);
		this.unlocked = new Hashtable<T, Long>(poolsize);
		if (expirationTime != -1)
		{
			// no need to reap if the instances can never expire.
			this.reaper = new ConnectionReaper(this);
			reaper.start();
		}
	}

	/**
	 * Expires all unlocked instances that have past expiration time and don't validate.
	 */
	public synchronized void reap()
	{
		long now = System.currentTimeMillis();
		Enumeration<T> e = unlocked.keys();
		while ((e != null) && (e.hasMoreElements()))
		{
			T t = e.nextElement();
			if ((expirationTime != -1 && (now - unlocked.get(t)) > expirationTime) && (!validate(t)))
			{
				unlocked.remove(t);
				expire(t);
				t = null;
			}
		}
	}

	/**
	 * Expires all unlocked instances.
	 */
	public synchronized void cleanup()
	{
		Enumeration<T> connlist = unlocked.keys();
		while ((connlist != null) && (connlist.hasMoreElements()))
		{
			T t = connlist.nextElement();
			expire(t);
		}
	}

	public abstract void expire(T o);

	public abstract T create();

	public abstract boolean validate(T o);

	/**
	 * This simply looks for an "unlocked" instance and returns it. Otherwise it generates a new instance and returns
	 * that.
	 * 
	 * @return
	 */
	public synchronized T checkOut()
	{
		long now = System.currentTimeMillis();
		for (T c : unlocked.keySet())
		{
			locked.put(c, now);
			return c;
		}

		T c = create();
		locked.put(c, now);
		return c;
	}

	/**
	 * "Unlocks" this instance.
	 * 
	 * @param t
	 */
	public synchronized void checkIn(T t)
	{
		locked.remove(t);
		unlocked.put(t, System.currentTimeMillis());
	}
}