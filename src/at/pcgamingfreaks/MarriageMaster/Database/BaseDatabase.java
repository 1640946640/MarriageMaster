/*
 *   Copyright (C) 2019 GeorgH93
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.MarriageMaster.Database;

import at.pcgamingfreaks.ConsoleColor;
import at.pcgamingfreaks.MarriageMaster.API.Marriage;
import at.pcgamingfreaks.MarriageMaster.API.MarriageMasterPlugin;
import at.pcgamingfreaks.MarriageMaster.API.MarriagePlayer;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;
import java.util.logging.Logger;

public abstract class BaseDatabase<MARRIAGE_MASTER extends MarriageMasterPlugin, MARRIAGE_PLAYER_DATA extends MarriagePlayer, MARRIAGE extends Marriage, MARRIAGE_DATA extends MARRIAGE>
{
	//region Messages
	protected static final String MESSAGE_UPDATE_UUIDS = "Start updating database to UUIDs ...", MESSAGE_UPDATED_UUIDS = "Updated %d accounts to UUIDs.";
	protected static final String MESSAGE_FILES_NO_LONGER_SUPPORTED = ConsoleColor.RED + "File based storage is no longer supported." + ConsoleColor.YELLOW + " Migrating to SQLite.";
	protected static final String MESSAGE_UNKNOWN_DB_TYPE = ConsoleColor.RED + "Unknown database type \"%s\"!" + ConsoleColor.RESET;
	protected static final String MESSAGE_CLEANING_DB_CACHE = "Cleaning database cache.", MESSAGE_DB_CACHE_CLEANED = "Database cache cleaned.";
	//endregion

	protected final boolean useUUIDs, useUUIDSeparators, useOnlineUUIDs;
	protected final Logger logger;
	protected final MARRIAGE_MASTER plugin;
	protected final Cache<MARRIAGE_PLAYER_DATA, MARRIAGE_DATA> cache = new Cache<>();

	protected BaseDatabase(MARRIAGE_MASTER plugin, Logger logger, boolean useUUIDs, boolean useUUIDSeparators, boolean useOnlineUUIDs)
	{
		this.plugin = plugin;
		this.logger = logger;
		this.useUUIDs = useUUIDs;
		this.useOnlineUUIDs = useOnlineUUIDs;
		this.useUUIDSeparators = useUUIDSeparators;
	}

	protected void startup() throws Exception
	{
		if(useUUIDs) checkUUIDs();
		loadAll();
		cache.reCacheSurnames();
	}

	protected void close()
	{
		logger.info(MESSAGE_CLEANING_DB_CACHE);
		cache.close();
		logger.info(MESSAGE_DB_CACHE_CLEANED);
	}

	public Cache<MARRIAGE_PLAYER_DATA, MARRIAGE_DATA> getCache()
	{
		return cache;
	}

	public Collection<String> getSurnames()
	{
		return cache.getSurnames();
	}

	protected String getUsedPlayerIdentifier(MARRIAGE_PLAYER_DATA player)
	{
		if(useUUIDs)
		{
			return useUUIDSeparators ? player.getUUID().toString() : player.getUUID().toString().replaceAll("-", "");
		}
		else
		{
			return player.getName();
		}
	}


	protected void runAsync(@NotNull Runnable runnable)
	{
		runAsync(runnable, 0);
	}

	protected abstract void runAsync(@NotNull Runnable runnable, long delay);


	//region abstract stuff
	public abstract MARRIAGE_PLAYER_DATA getPlayer(UUID uuid);

	protected abstract void checkUUIDs();

	protected abstract void loadAll();

	public abstract String getDatabaseTypeName();

	protected abstract void load(final @NotNull MARRIAGE_PLAYER_DATA player);
	//endregion
}