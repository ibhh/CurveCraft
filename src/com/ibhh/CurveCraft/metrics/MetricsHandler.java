package com.ibhh.CurveCraft.metrics;

import com.ibhh.CurveCraft.CurveCraft;
import java.io.IOException;

/**
 * @author ibhh
 */
public class MetricsHandler
{

	private final CurveCraft plugin;
	private Metrics metrics;
	private int matches_started = 0;
	private int players_finished = 0;
	private int players_played = 0;

	public MetricsHandler(CurveCraft plugin)
	{
		this.plugin = plugin;
	}

	public void start() throws IOException
	{
		if(metrics == null)
		{
			metrics = new Metrics(plugin);
		}
		initializeOthers();
		metrics.start();
	}

	public int getPlayers_finished()
	{
		return players_finished;
	}

	public int getMatches_started()
	{
		return matches_started;
	}

	public int getPlayers_played()
	{
		return players_played;
	}

	public Metrics getMetrics()
	{
		return metrics;
	}

	public void addPlayersPlayed(int i)
	{
		players_played += i;
	}

	public void addPlayersFinished(int i)
	{
		players_finished += i;
	}

	public void addMatchesStarted()
	{
		matches_started++;
	}

	public void initializeOthers()
	{
		Metrics.Graph MatchCountGraph = metrics.createGraph("Matches");
		MatchCountGraph.addPlotter(new Metrics.Plotter("Started")
		{
			@Override
			public int getValue()
			{
				return matches_started;
			}

			@Override
			public void reset()
			{
				matches_started = 0;
			}
		});

		Metrics.Graph PlayerCountGraph = metrics.createGraph("Player");

		PlayerCountGraph.addPlotter(new Metrics.Plotter("Played")
		{
			@Override
			public int getValue()
			{
				return players_played;
			}

			@Override
			public void reset()
			{
				players_played = 0;
			}
		});

		PlayerCountGraph.addPlotter(new Metrics.Plotter("Finished")
		{
			@Override
			public int getValue()
			{
				return players_finished;
			}

			@Override
			public void reset()
			{
				players_finished = 0;
			}
		});
	}

}
