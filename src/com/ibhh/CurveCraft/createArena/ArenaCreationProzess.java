package com.ibhh.CurveCraft.createArena;

import com.ibhh.CurveCraft.CurveCraft;
import com.ibhh.CurveCraft.arena.CCArena;
import java.io.File;
import org.bukkit.Location;

public class ArenaCreationProzess
{

	private final CurveCraft plugin;
	private String name = "";

	private Location corner1 = null;
	private Location corner2 = null;
	private Location lobbyloc = null;
	private Location endloc = null;
	private Location exitloc = null;

	public ArenaCreationProzess(CurveCraft plugin)
	{
		this.plugin = plugin;
	}

	public void setCorner1(Location corner1)
	{
		this.corner1 = corner1;
	}

	public void setCorner2(Location corner2)
	{
		this.corner2 = corner2;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Location getCorner1()
	{
		return this.corner1;
	}

	public Location getCorner2()
	{
		return this.corner2;
	}

	public Location getEndloc()
	{
		return this.endloc;
	}

	public Location getLobbyloc()
	{
		return this.lobbyloc;
	}

	public Location getExitloc()
	{
		return this.exitloc;
	}

	public void setExitloc(Location exitloc)
	{
		this.exitloc = exitloc;
	}

	public void setEndloc(Location endloc)
	{
		this.endloc = endloc;
	}

	public void setLobbyloc(Location lobbyloc)
	{
		this.lobbyloc = lobbyloc;
	}

	public String getName()
	{
		return this.name;
	}

	public void finishProzess() throws NotFinishedYetException
	{
		if(this.name.equals(""))
		{
			throw new NotFinishedYetException(plugin.getConfigHandler().getLanguageString("system", "create.notfinished.noname"));
		}

		if((this.corner1 == null) || (this.corner2 == null))
		{
			throw new NotFinishedYetException(plugin.getConfigHandler().getLanguageString("system", "create.notfinished.nocorner"));
		}

		if(this.lobbyloc == null)
		{
			throw new NotFinishedYetException(plugin.getConfigHandler().getLanguageString("system", "create.notfinished.lobbyloc"));
		}

		if(this.endloc == null)
		{
			throw new NotFinishedYetException(plugin.getConfigHandler().getLanguageString("system", "create.notfinished.endloc"));
		}

		if(this.exitloc == null)
		{
			throw new NotFinishedYetException(plugin.getConfigHandler().getLanguageString("system", "create.notfinished.exitloc"));
		}

		if(this.corner1.getBlockY() != this.corner2.getBlockY())
		{
			throw new NotFinishedYetException(plugin.getConfigHandler().getLanguageString("system", "create.notfinished.wrongY"));
		}
		CCArena arena = new CCArena(this.name, this.corner1, this.corner2, this.lobbyloc, this.endloc, this.exitloc);
		File f = arena.saveToFolder(this.plugin);
		if(f != null)
		{
			plugin.getArenaHandler().loadArena(f, corner1.getWorld());
		}
	}
}
