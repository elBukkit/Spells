package com.elmakers.mine.bukkit.plugins.spells.builtin;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.entity.CraftWolf;
import org.bukkit.entity.CreatureType;
import org.bukkit.event.player.PlayerEvent;

import com.elmakers.mine.bukkit.plugins.spells.Spell;
import com.elmakers.mine.bukkit.plugins.spells.SpellEventType;

public class WolfSpell extends Spell
{
	private HashMap<String, CraftWolf> familiars = new HashMap<String, CraftWolf>();

	public WolfSpell()
	{
		addVariant("wolf", Material.GRILLED_PORK, getCategory(), "Create another wolf", "new");
	}
	
	@Override
	public boolean onCast(String[] parameters)
	{
		CraftWolf fam = familiars.get(player.getName());
		if (fam != null && parameters.length == 0)
		{
			Location playerLocation = player.getLocation();
			Location wolfLocation = fam.getLocation();
			if (playerLocation.getWorld() != wolfLocation.getWorld() || getDistance(player.getLocation(), fam.getLocation()) > 10)
			{
				castMessage(player, "Here, boy!");
				wolfLocation = playerLocation;
				wolfLocation.setX(wolfLocation.getX() + 2);
				fam.teleport(wolfLocation);
			}
			else
			if (fam.isAngry())
			{
				castMessage(player, "Whoah!");
				fam.setAngry(false);
			}
			else
			/*
			if (fam.isSitting())
			{
				castMessage(player, "Sick 'em, boy!");
				fam.setSitting(false);
				fam.setAngry(true);
			}
			else 
			*/
			{
				castMessage(player, "Sit!");
				fam.setSitting(true);
			}
			checkListener();
			return true;
		}
		else
		{
			Block target = getTargetBlock();
			if (target == null)
			{
				castMessage(player, "No target");
				return false;
			}
			target = target.getFace(BlockFace.UP);
			
			CraftWolf entity = (CraftWolf)player.getWorld().spawnCreature(target.getLocation(), CreatureType.WOLF);
			if (entity == null)
			{
				sendMessage(player, "Your wolfie is DOA");
				return false;
			}
			castMessage(player, "You summon a wolfie!");
			familiars.put(player.getName(), entity);
			checkListener();
			return true;
		}
	}
		
	protected void checkListener()
	{
		if (familiars.size() > 0)
		{
			spells.registerEvent(SpellEventType.PLAYER_QUIT, this);
		}
		else
		{
			spells.unregisterEvent(SpellEventType.PLAYER_QUIT, this);
		}
	}
	
	@Override
	public String getName()
	{
		return "wolfie";
	}

	@Override
	public String getCategory()
	{
		return "combat";
	}

	@Override
	public String getDescription()
	{
		return "Create a wolf familiar to follow you around";
	}
	
	public void onPlayerQuit(PlayerEvent event)
	{
		CraftWolf fam = familiars.get(event.getPlayer().getName());
		if (fam != null)
		{
			fam.setHealth(0);
			familiars.remove(event.getPlayer().getName());
		}
	}

	@Override
	public Material getMaterial()
	{
		return Material.PORK;
	}
	
}
