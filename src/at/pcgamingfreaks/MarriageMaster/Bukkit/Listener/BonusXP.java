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

package at.pcgamingfreaks.MarriageMaster.Bukkit.Listener;

import at.pcgamingfreaks.MarriageMaster.Bukkit.API.Events.BonusXPDropEvent;
import at.pcgamingfreaks.MarriageMaster.Bukkit.API.Marriage;
import at.pcgamingfreaks.MarriageMaster.Bukkit.API.MarriagePlayer;
import at.pcgamingfreaks.MarriageMaster.Bukkit.MarriageMaster;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class BonusXP implements Listener
{
	private final MarriageMaster plugin;
	private final double range, multiplier;

	public BonusXP(MarriageMaster marriagemaster)
	{
		plugin = marriagemaster;
		range = marriagemaster.getConfiguration().getRangeSquared("BonusXP");
		multiplier = marriagemaster.getConfiguration().getBonusXpMultiplier();
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityDeath(EntityDeathEvent event)
	{
		if(event.getEntityType() != EntityType.PLAYER)
		{
			Player killer = event.getEntity().getKiller();
			if(killer != null)
			{
				MarriagePlayer player = plugin.getPlayerData(killer);
				Marriage marriage = player.getNearestPartnerMarriageData();
				if(marriage != null)
				{
					MarriagePlayer partner = marriage.getPartner(player);
					if(partner != null && partner.isOnline() && marriage.inRangeSquared(range))
					{
						int xp = (int) Math.round((event.getDroppedExp() * multiplier));
						BonusXPDropEvent bonusXPDropEvent = new BonusXPDropEvent(player, marriage, xp);
						plugin.getServer().getPluginManager().callEvent(bonusXPDropEvent);
						if(!bonusXPDropEvent.isCancelled())
						{
							event.setDroppedExp(bonusXPDropEvent.getAmount());
						}
					}
				}
			}
		}
	}
}