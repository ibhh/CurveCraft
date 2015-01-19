package com.ibhh.CurveCraft.arena;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.entity.Player;

/**
 * @author ibhh
 */
public abstract class CCEventHandler
{
	/**
	 * Extend this class to use the Event handing API
	 */

	/**
	 * Fired if player crashs
	 * @param a CCArena
	 * @param p Player
	 * @param r Reason for crashing
	 */
	public abstract void PlayerCrashedEvent(CCArena a, Player p, CrashReason r);

	/**
	 * Fired if a player wins a round
	 * @param a CCArena
	 * @param p Player
	 */
	public abstract void PlayerRoundWinEvent(CCArena a, Player p);

	/**
	 * Fired if a player wins a game
	 * @param a CCArena
	 * @param p Player
	 * @param score The scores of the players
	 * @param lobby all players that finished the game
	 */
	public abstract void PlayerGameWinEvent(CCArena a, Player p, HashMap<Player, Integer> score, ArrayList<Player> lobby);

	/**
	 * Fired if a new game starts
	 * @param a CCArena
	 */
	public abstract void GameStartEvent(CCArena a);

	/**
	 * Fired if a new round starts
	 * @param a CCArena
	 */
	public abstract void RoundStartEvent(CCArena a);

	/**
	 * Fired if a player joins a lobby
	 * @param a CCArena
	 * @param p Player
	 */
	public abstract void PlayerJoinEvent(CCArena a, Player p);

	/**
	 * Fired if a player leaves a game
	 * @param a CCArena
	 * @param p Player
	 */
	public abstract void PlayerLeaveEvent(CCArena a, Player p);
}
