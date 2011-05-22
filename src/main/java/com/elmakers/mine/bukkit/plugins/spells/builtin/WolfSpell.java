package com.elmakers.mine.bukkit.plugins.spells.builtin;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.entity.CraftWolf;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;

import com.elmakers.mine.bukkit.plugins.spells.Spell;

public class WolfSpell extends Spell
{
	public WolfSpell()
	{
	}
	
	public boolean newWolf()
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
		return true;
	}
	
	@Override
	public boolean onCast(String[] parameters)
	{
		Entity targetWolf = getTargetEntity(CraftWolf.class);
		if (targetWolf == null || !(targetWolf instanceof CraftWolf))
		{
			return newWolf();
		}
		
		CraftWolf wolfie = (CraftWolf)targetWolf;
		
		castMessage(player, "You tame a wolfie!");
		wolfie.setAngry(false);
		wolfie.setHealth(20);
		wolfie.setTamed(true);
		wolfie.setOwner(player);
		return true;
	}
	
	@Override
	public String getName()
	{
		return "wolf";
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

	@Override
	public Material getMaterial()
	{
		return Material.PORK;
	}
	
}
