package com.elmakers.mine.bukkit.plugins.spells.builtin;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.elmakers.mine.bukkit.persistence.dao.BlockList;
import com.elmakers.mine.bukkit.persistence.dao.BoundingBox;
import com.elmakers.mine.bukkit.persistence.dao.MaterialList;
import com.elmakers.mine.bukkit.plugins.nether.NetherManager;
import com.elmakers.mine.bukkit.plugins.spells.Spell;
import com.elmakers.mine.bukkit.plugins.spells.utilities.PluginProperties;
import com.elmakers.mine.bukkit.utilities.CSVParser;

public class PortalSpell extends Spell
{
	private int				defaultSearchDistance	= 32;
    protected static final String DEFAULT_DESTRUCTIBLES = "0,1,2,3,4,10,11,12,13,14,15,16,21,51,56,78,79,82,87,88,89";
    protected static MaterialList destructible          = null;
    protected static MaterialList needsPlatform         = null;
    
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
			castMessage(player, "Can't create a portal that far away");
			return false;
		}
		
		Material blockType = target.getType();
		Block portalBase = target.getFace(BlockFace.UP);
		blockType = portalBase.getType();
		if (blockType != Material.AIR)
		{
			portalBase = getFaceBlock();
		}
		
		blockType = portalBase.getType();
		if (blockType != Material.AIR)
		{
			castMessage(player, "Can't create a portal there");
			return false;
		
		}
		
		BlockList portalBlocks = new BlockList();
		portalBlocks.setTimeToLive(10000);
		spells.disablePhysics(10000);
        buildPortalBlocks(portalBase, BlockFace.NORTH, portalBlocks);
        
        spells.scheduleCleanup(portalBlocks);
		
		return true;
	}

    protected static void buildFrame(Block centerBlock, BlockFace facing, BlockList blockList)
    {
        BoundingBox leftSide = new BoundingBox(centerBlock.getX() - 2, centerBlock.getY() - 1, centerBlock.getZ() - 1, centerBlock.getX() - 1, centerBlock.getY() + 4, centerBlock.getZ());
        BoundingBox rightSide = new BoundingBox(centerBlock.getX() + 2, centerBlock.getY() - 1, centerBlock.getZ() - 1, centerBlock.getX() + 1, centerBlock.getY() + 4, centerBlock.getZ());
        BoundingBox top = new BoundingBox(centerBlock.getX() - 1, centerBlock.getY() + 3, centerBlock.getZ() - 1, centerBlock.getX() + 1, centerBlock.getY() + 4, centerBlock.getZ());
        BoundingBox bottom = new BoundingBox(centerBlock.getX() - 1, centerBlock.getY() - 1, centerBlock.getZ() - 1, centerBlock.getX() + 1, centerBlock.getY(), centerBlock.getZ());
        
        leftSide.fill(centerBlock.getWorld(), Material.OBSIDIAN, destructible, blockList);
        rightSide.fill(centerBlock.getWorld(), Material.OBSIDIAN, destructible, blockList);
        top.fill(centerBlock.getWorld(), Material.OBSIDIAN, destructible, blockList);
        bottom.fill(centerBlock.getWorld(), Material.OBSIDIAN, destructible, blockList);
    }

    protected static void buildPlatform(Block centerBlock, BlockList blockList)
    {
        BoundingBox platform = new BoundingBox(centerBlock.getX() - 3, centerBlock.getY() - 1, centerBlock.getZ() - 3, centerBlock.getX() + 2, centerBlock.getY(), centerBlock.getZ() + 2);

        platform.fill(centerBlock.getWorld(), Material.OBSIDIAN, needsPlatform, blockList);
    }

    protected static void clearPortalArea(Block centerBlock, BlockList blockList)
    {
        BoundingBox container = new BoundingBox(centerBlock.getX() - 3, centerBlock.getY(), centerBlock.getZ() - 3, centerBlock.getX() + 2, centerBlock.getY() + 4, centerBlock.getZ() + 2);

        container.fill(centerBlock.getWorld(), Material.AIR, destructible, blockList);
    }
    
    protected void buildPortalBlocks(Block centerBlock, BlockFace facing, BlockList blockList)
    {
        BoundingBox container = new BoundingBox(centerBlock.getX() - 1, centerBlock.getY(), centerBlock.getZ() - 1, centerBlock.getX() + 1, centerBlock.getY() + 3, centerBlock.getZ());
        container.fill(centerBlock.getWorld(), Material.PORTAL, destructible, blockList);
    }
		
	protected void setBlock(BlockList blocks, Block baseBlock, int x, int y, int z, Material material)
	{
		Block block = baseBlock.getRelative(x, y, z);
		if (block.getType() == Material.AIR)
		{
			blocks.add(block);
			block.setType(material);
		}		
	}

	@Override
	public String getName()
	{
		return "portal";
	}

	@Override
	public String getCategory()
	{
		return "nether";
	}

	@Override
	public String getDescription()
	{
		return "Create a temporary portal";
	}

	@Override
	public Material getMaterial()
	{
		return Material.PORTAL;
	}
	
	public void onLoad(PluginProperties properties)
    {
	 if (destructible == null)
     {
         destructible = new MaterialList();
         needsPlatform = new MaterialList();

         needsPlatform.add(Material.WATER);
         needsPlatform.add(Material.STATIONARY_WATER);
         needsPlatform.add(Material.LAVA);
         needsPlatform.add(Material.STATIONARY_LAVA);

         CSVParser csv = new CSVParser();
         destructible = csv.parseMaterials(DEFAULT_DESTRUCTIBLES);
     }
    }
	
	protected NetherManager nether;

}
