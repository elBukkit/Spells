package com.elmakers.mine.bukkit.plugins.spells.builtin;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.elmakers.mine.bukkit.persistence.dao.BlockList;
import com.elmakers.mine.bukkit.plugins.spells.Spell;
import com.elmakers.mine.bukkit.plugins.spells.utilities.PluginProperties;
import com.elmakers.mine.bukkit.utilities.SimpleBlockAction;

public class FrostSpell extends Spell
{
	private int				defaultRadius			= 2;
	private int				maxRadius				= 32;
	private int				defaultSearchDistance	= 32;
	private int				verticalSearchDistance	= 8;
	
	public class FrostAction extends SimpleBlockAction
    {
        public boolean perform(Block block)
        {

            if (block.getType() == Material.AIR || block.getType() == Material.SNOW)
            {
                return false;
            }
            Material material = Material.SNOW;
            if (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER)
            {
                material = Material.ICE;
            }
            else if (block.getType() == Material.LAVA)
            {
                material = Material.COBBLESTONE;
            }
            else if (block.getType() == Material.STATIONARY_LAVA)
            {
                material = Material.OBSIDIAN;
            }
            else if (block.getType() == Material.FIRE)
            {
                material = Material.AIR;
            }
            else
            {
                block = block.getFace(BlockFace.UP);
            }
            super.perform(block);
            block.setType(material);
            return true;
        }
    }
	 
	@Override
	public boolean onCast(String[] parameters)
	{
		Block target = getTargetBlock();
		if (target == null)
		{
			castMessage(player, "No target");
			return false;
		}
		if (defaultSearchDistance > 0 && getDistance(player, target) > defaultSearchDistance)
		{
			castMessage(player, "Can't frost that far away");
			return false;
		}
		
		int radius = defaultRadius;
		if (parameters.length > 0)
		{
			try
			{
				radius = Integer.parseInt(parameters[0]);
				if (radius > maxRadius && maxRadius > 0)
				{
					radius = maxRadius;
				}
			} 
			catch(NumberFormatException ex)
			{
				radius = defaultRadius;
			}
		}
		
	   FrostAction action = new FrostAction();

        if (radius <= 1)
        {
            action.perform(target);
        }
        else
        {
            this.coverSurface(target.getLocation(), radius, action);
        }

        spells.addToUndoQueue(player, action.getBlocks());
        castMessage(player, "Frosted " + action.getBlocks().size() + " blocks");
        
        return true;
	}
	
	public void frostBlock(int dx, int dy, int dz, Block centerPoint, int radius, BlockList frostedBlocks)
	{
		int x = centerPoint.getX() + dx - radius;
		int y = centerPoint.getY() + dy - radius;
		int z = centerPoint.getZ() + dz - radius;
		Block block = player.getWorld().getBlockAt(x, y, z);
		int depth = 0;
		
		if (block.getType() == Material.AIR)
		{
			while (depth < verticalSearchDistance && block.getType() == Material.AIR)
			{
				depth++;
				block = block.getFace(BlockFace.DOWN);
			}	
		}
		else
		{
			while (depth < verticalSearchDistance && block.getType() != Material.AIR)
			{
				depth++;
				block = block.getFace(BlockFace.UP);
			}
			block = block.getFace(BlockFace.DOWN);
		}

		if (block.getType() == Material.AIR || block.getType() == Material.SNOW)
		{
			return;
		}
		Material material = Material.SNOW;
		Block target = block;
		if (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER)
		{
			material = Material.ICE;
		}
		else if (block.getType() == Material.LAVA)
		{
			material = Material.COBBLESTONE;
		}
		else if (block.getType() == Material.STATIONARY_LAVA)
		{
			material = Material.OBSIDIAN;
		}
		else if (block.getType() == Material.FIRE)
		{
			material = Material.AIR;
		}
		else
		{
			target = target.getFace(BlockFace.UP);
		}
		frostedBlocks.add(target);
		target.setType(material);
	}

	public int checkPosition(int x, int z, int R)
	{
		return (x * x) +  (z * z) - (R * R);
	}	
	
	@Override
	public String getName()
	{
		return "frost";
	}

	@Override
	public String getCategory()
	{
		return "nature";
	}

	@Override
	public String getDescription()
	{
		return "Freeze water and create snow";
	}
	
	@Override
	public void onLoad(PluginProperties properties)
	{
		defaultRadius = properties.getInteger("spells-frost-radius", defaultRadius);
		maxRadius = properties.getInteger("spells-frost-max-radius", maxRadius);
		defaultSearchDistance = properties.getInteger("spells-frost-search-distance", defaultSearchDistance);
		verticalSearchDistance = properties.getInteger("spells-frost-vertical-search-distance", verticalSearchDistance);
	}

	@Override
	public Material getMaterial()
	{
		return Material.SNOW_BALL;
	}

}
