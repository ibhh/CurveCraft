package com.ibhh.CurveCraft.APIExample;

import com.ibhh.CurveCraft.arena.APIHandler;
import com.ibhh.CurveCraft.arena.CCArena;
import com.ibhh.CurveCraft.arena.CCEventHandler;
import com.ibhh.CurveCraft.arena.CrashReason;
import com.ibhh.CurveCraft.arena.InvalidEventHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.entity.Player;

/**
 * @author ibhh
 */
public class ExampleEventHandler extends CCEventHandler
{

	public ExampleEventHandler()
	{
		try
		{
			APIHandler.addHandler(this);
		}
		catch(InvalidEventHandler ex)
		{
			Logger.getLogger(ExampleEventHandler.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void PlayerCrashedEvent(CCArena a, Player p, CrashReason r)
	{
	}

	@Override
	public void PlayerRoundWinEvent(CCArena a, Player p)
	{
	}

	@Override
	public void PlayerGameWinEvent(CCArena a, Player p, HashMap<Player, Integer> score, ArrayList<Player> lobby)
	{
	}

	@Override
	public void GameStartEvent(CCArena a)
	{
	}

	@Override
	public void RoundStartEvent(CCArena a)
	{
	}

	@Override
	public void PlayerJoinEvent(CCArena a, Player p)
	{
	}

	@Override
	public void PlayerLeaveEvent(CCArena a, Player p)
	{
	}

}
