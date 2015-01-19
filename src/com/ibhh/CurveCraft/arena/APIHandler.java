package com.ibhh.CurveCraft.arena;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;

/**
 * @author ibhh
 */
public class APIHandler
{

	private static final ArrayList<CCEventHandler> list = new ArrayList<>();

	protected static void throwPlayerCrashEvent(final Player p, final CCArena a, final CrashReason r)
	{
		for(CCEventHandler cFEventHandler : list)
		{
			try
			{
				cFEventHandler.PlayerCrashedEvent(a, p, r);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	protected static void throwPlayerRoundWinEvent(final Player p, final CCArena a)
	{
		for(CCEventHandler cFEventHandler : list)
		{
			try
			{
				cFEventHandler.PlayerRoundWinEvent(a, p);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	protected static void throwPlayerJoinEvent(final Player p, final CCArena a)
	{
		for(CCEventHandler cFEventHandler : list)
		{
			try
			{
				cFEventHandler.PlayerJoinEvent(a, p);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	protected static void throwPlayerLeaveEvent(final Player p, final CCArena a)
	{
		for(CCEventHandler cFEventHandler : list)
		{
			try
			{
				cFEventHandler.PlayerLeaveEvent(a, p);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	protected static void throwGameStartEvent(final CCArena a)
	{
		for(CCEventHandler cFEventHandler : list)
		{
			try
			{
				cFEventHandler.GameStartEvent(a);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	protected static void throwRoundStartEvent(final CCArena a)
	{
		for(CCEventHandler cFEventHandler : list)
		{
			try
			{
				cFEventHandler.RoundStartEvent(a);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected static void throwPlayerGameWinEvent(final Player p, final CCArena a, final HashMap<Player, Integer> score, final ArrayList<Player> lobby)
	{
		if(p == null || a == null || lobby == null || score == null) {
			throw new IllegalArgumentException();
		}

		for(CCEventHandler cFEventHandler : list)
		{
			try
			{
				cFEventHandler.PlayerGameWinEvent(a, p, (HashMap<Player, Integer>) score.clone(), (ArrayList<Player>) lobby.clone());
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Adds an Event Handler to the list
	 * @param e CCEventHandler
	 * @throws InvalidEventHandler
	 */
	public static void addHandler(CCEventHandler e) throws InvalidEventHandler
	{
		if(e == null)
		{
			throw new InvalidEventHandler("Object is null");
		}
		if(!(e instanceof CCEventHandler))
		{
			throw new InvalidEventHandler("Not a eventhandler");
		}
		if(list.contains(e))
		{
			throw new InvalidEventHandler("Already registered!");
		}
		list.add(e);
	}

	/**
	 * Removes Handler from the list
	 * @param e CCEventHandler
	 * @throws InvalidEventHandler
	 */
	public static void removeHandler(CCEventHandler e) throws InvalidEventHandler
	{
		if(e == null)
		{
			throw new InvalidEventHandler("Object is null");
		}
		if(!(e instanceof CCEventHandler))
		{
			throw new InvalidEventHandler("Not a eventhandler");
		}
		if(!list.contains(e))
		{
			throw new InvalidEventHandler("Not found");
		}
		list.remove(e);
	}

}
