package com.ibhh.CurveCraft.config;

import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.ibhh.CurveCraft.CurveCraft;
import com.ibhh.CurveCraft.locales.LocaleHandler;
import com.ibhh.CurveCraft.locales.Localizer;
import com.ibhh.CurveCraft.locales.PluginLocale;
import com.ibhh.CurveCraft.logger.LoggerUtility;

/**
 * @author ibhh
 */
public class ConfigurationHandler
{

	private LocaleHandler language_configs;
	private final CurveCraft plugin;

	/**
	 * Creates a new ConfigurationHandler
	 * @param plugin Needed for saving configs
	 */
	public ConfigurationHandler(CurveCraft plugin)
	{
		this.plugin = plugin;
		// loading main config
		try
		{
			plugin.getConfig().options().copyDefaults(true);
			plugin.saveConfig();
			plugin.reloadConfig();
		}
		catch(Exception e)
		{
			System.out.println("Cannot create arenaconfig!");
			e.printStackTrace();
		}
		language_configs = new LocaleHandler(plugin);
		ConfigFix.fixConfig(plugin);
	}

	public LocaleHandler getLanguage_configs()
	{
		return language_configs;
	}

	/**
	 * Returns the current language configuration
	 * @return YamlConfiguration
	 */
	public PluginLocale getLanguage_config(String type)
	{
		return language_configs.get(type);
	}

	public String getLanguageString(Player player, String path, boolean notfoundmessage)
	{
		return getLanguageString(getPlayerLanguage(player), path, notfoundmessage);
	}

	public String getLanguageString(Player player, String path)
	{
		return getLanguageString(getPlayerLanguage(player), path);
	}

	/**
	 * @param type z.B. system, de, en, fr
	 * @param path
	 * @return String
	 */
	public String getLanguageString(String type, String path)
	{
		return getLanguageString(type, path, true);
	}

	/**
	 * @param type z.B. system, de, en, fr
	 * @param path
	 * @return String
	 */
	public String getLanguageString(String type, String path, boolean notfoundmessage)
	{
		String ret = "";
		if(type == null || type.equals("system"))
		{
			type = getConfig().getString("language");
		}
		PluginLocale config = getLanguage_config(type);
		if(config != null)
		{
			if(config.getString(path) == null)
			{
				ret += ChatColor.RED + "ERROR: ";
				plugin.getLoggerUtility().log("arenaconfig.getLanguageString(type, path) == null", LoggerUtility.Level.DEBUG);
				PluginLocale config_sys = getLanguage_config("system");
				if(config_sys != null && config.getString(path) != null)
				{
					ret += getLanguageString("system", path);
					ret += String.format(getLanguageString("system", "configuration.language.pathnotfound", false), type, path);
				}
				else
				{
					ret += "Nothing FOUND. Contact developer";
				}
			}
			else
			{
				return config.getString(path);
			}
		}
		return ret;
	}

	/**
	 * @return plugin.getConifg();
	 */
	public FileConfiguration getConfig()
	{
		return plugin.getConfig();
	}

	public void setPlayerLanguage(Player p, String language) throws IOException
	{
		if(language_configs.get(language) != null)
		{
			language_configs.getPlayer_language().set(p.getUniqueId().toString(), language);
			language_configs.savePlayer_language();
		}
		else if(language_configs.get(Localizer.getCodebyLanguage(language)) != null && language_configs.get(Localizer.getCodebyLanguage(language)) != null)
		{
			language_configs.getPlayer_language().set(p.getUniqueId().toString(), Localizer.getCodebyLanguage(language));
			language_configs.savePlayer_language();
		}
		else
		{
			throw new IllegalArgumentException(String.format(getLanguageString(p, "configuration.language.languagenotfound"), language));
		}
	}

	public String getPlayerLanguage(Player p)
	{
		if(language_configs.getPlayer_language().contains(p.getUniqueId().toString()))
		{
//			plugin.getLoggerUtility().log("Player " + p.getName() + " has manually defined locale: " + language_configs.getPlayer_language().getString(p.getUniqueId().toString()), Level.FINE);
			return language_configs.getPlayer_language().getString(p.getUniqueId().toString());
		}
		Localizer locale = Localizer.getLocalizer(p);
		if(language_configs.containsKey(locale.getCode()))
		{
//			plugin.getLoggerUtility().log("Player " + p.getName() + " has locale: " + locale.getCode(), Level.FINE);
			return locale.getCode();
		}
		else
		{
//			try
//			{
//				plugin.getLoggerUtility().log("Player " + p.getName() + " has locale: en_CA because LOCALE not found: " + locale.getCode() + " Read: " + Localizer.getLanguage(p), Level.FINE);
//			}
//			catch(NoSuchFieldException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
//			{
//				e.printStackTrace();
//			}
			return "en_CA";
		}
	}
}
