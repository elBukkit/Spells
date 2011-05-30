package com.elmakers.mine.bukkit.plugins.spells.builtin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerEvent;

import com.elmakers.mine.bukkit.plugins.spells.Spell;
import com.elmakers.mine.bukkit.plugins.spells.SpellEventType;
import com.elmakers.mine.bukkit.plugins.spells.utilities.PluginProperties;

public class FamiliarSpell extends Spell
{
	private String DEFAULT_FAMILIARS = "chicken,sheep,cow,pig,wolf";
	private String DEFAULT_MONSTERS = "creeper,pigzombie,skeleton,spider,squid,zombie,ghast,giant";
	
	private List<String> defaultFamiliars = new ArrayList<String>();
	private List<String> defaultMonsters = new ArrayList<String>();
	private final Random rand = new Random();
	private HashMap<String, PlayerFamiliar> familiars = new HashMap<String, PlayerFamiliar>();
	   
    public FamiliarSpell()
    {
        addVariant("monster", Material.PUMPKIN, "combat", "Call a monster to your side", "monster");
        addVariant("mob", Material.JACK_O_LANTERN, "combat", "Call a monster to your side", "monster 20");
        addVariant("farm", Material.WHEAT, "farming", "Create a herd", "30");
    }
    
	public enum FamiliarClass
	{
	    SPECIFIC,
	    ANY,
	    FRIENDLY,
	    MONSTER
	}
	
	public class PlayerFamiliar
	{
		public List<LivingEntity> familiars = null;
		
		public boolean hasFamiliar()
		{
			return familiars != null;
		}
		
		public void setFamiliars(List<LivingEntity> f)
		{
		    familiars = f;
		}
		
		public void releaseFamiliar()
		{
			if (familiars != null)
			{
			    for (LivingEntity familiar : familiars)
			    {
			        familiar.setHealth(0);
			    }
				familiars = null;
			}
		}
	}
	
	public enum FamiliarType
	{
		CHICKEN,
		SHEEP,
		COW,
		PIG,
		CREEPER,
		PIGZOMBIE,
		SKELETON,
		SPIDER,
		SQUID,
		ZOMBIE,
		GHAST,
		GIANT,
		WOLF,
		//FISH,
		//SLIME,
		UNKNOWN;
		
		public static FamiliarType parseString(String s)
		{
			return parseString(s, UNKNOWN);
		}
		
		public static FamiliarType parseString(String s, FamiliarType defaultFamiliarType)
		{
			FamiliarType foundType = defaultFamiliarType;
			for (FamiliarType t : FamiliarType.values())
			{
				if (t.name().equalsIgnoreCase(s))
				{
					foundType = t;
				}
			}
			return foundType;
		}
		
	};
	
	@Override
	public boolean onCast(String[] parameters)
	{
		PlayerFamiliar fam = getFamiliar(player.getName());
		if (fam.hasFamiliar())
		{
			fam.releaseFamiliar();
			castMessage(player, "You release your familiar(s)");
			checkListener();
			return true;
		}
		else
		{
			noTargetThrough(Material.STATIONARY_WATER);
			noTargetThrough(Material.WATER);
			
			Block target = getTargetBlock();
			if (target == null)
			{
				castMessage(player, "No target");
				return false;
			}
			target = target.getFace(BlockFace.UP);
			
			FamiliarType famType = FamiliarType.UNKNOWN;
			FamiliarClass famClass = FamiliarClass.FRIENDLY;
			int famCount = 1;
			for (String parameter : parameters)
			{
			    try
			    {
			        famCount = Integer.parseInt(parameter);
			    }
			    catch (NumberFormatException e)
			    {
			        famCount = 1;
			        if (parameter.equalsIgnoreCase("any"))
	                {
	                    famClass = FamiliarClass.ANY;
	                }
	                else if (parameter.equalsIgnoreCase("monster"))
	                {
	                    famClass = FamiliarClass.MONSTER;
	                }
	                else
	                {
	                    famType = FamiliarType.parseString(parameters[0]);
	                    famClass = FamiliarClass.SPECIFIC;
	                }
			    }	
			}
			
			if (target.getType() == Material.WATER || target.getType() == Material.STATIONARY_WATER)
			{
				famType = FamiliarType.SQUID;
			}
			
			List<LivingEntity> familiars = new ArrayList<LivingEntity>();
			int spawnCount = 0;
			for (int i = 0; i < famCount; i++)
			{
                if (famClass != FamiliarClass.SPECIFIC)
                {
                    if (famClass == FamiliarClass.ANY)
                    {
                        int randomFamiliar = rand.nextInt(FamiliarType.values().length - 1);
                        famType = FamiliarType.values()[randomFamiliar];                        
                    }
                    else
                    {
                        List<String> types = defaultFamiliars;
                        if (famClass == FamiliarClass.MONSTER)
                        {
                            types = defaultMonsters;
                        }
                        int randomFamiliar = rand.nextInt(types.size());
                        famType = FamiliarType.parseString(types.get(randomFamiliar));
                    }
                }      

			    Location targetLoc = target.getLocation();
			    if (famCount > 1)
			    {
			        targetLoc.setX(targetLoc.getX() + rand.nextInt(2 * famCount) - famCount);
			        targetLoc.setZ(targetLoc.getZ() + rand.nextInt(2 * famCount) - famCount);
			    }
			    LivingEntity entity =  spawnFamiliar(targetLoc, famType);
			    if (entity != null)
			    {
			        familiars.add(entity);
			        spawnCount++;
			    }
			}
			
			String typeMessage = "";
			if (famClass == FamiliarClass.SPECIFIC)
			{
			    typeMessage = " " + famType.name().toLowerCase();
			}
			else if (famClass != FamiliarClass.ANY)
			{
			    typeMessage = " " + famClass.name().toLowerCase();
			}
			castMessage(player, "You create " + famCount + typeMessage +" familiar(s)!");
			fam.setFamiliars(familiars);
			checkListener();
			return true;
		}
	}
		
	protected LivingEntity spawnFamiliar(Location target, FamiliarType famType)
	{
		LivingEntity e = null;
		
		/// ARRRGGG!
		switch (famType)
		{
			case CHICKEN: e = player.getWorld().spawnCreature(target, CreatureType.CHICKEN); break;
			case SHEEP: e = player.getWorld().spawnCreature(target, CreatureType.SHEEP); break;
			case COW: e = player.getWorld().spawnCreature(target, CreatureType.COW); break;
			case PIG: e = player.getWorld().spawnCreature(target, CreatureType.PIG); break;
			case CREEPER: e = player.getWorld().spawnCreature(target, CreatureType.CREEPER); break;
			case PIGZOMBIE: e = player.getWorld().spawnCreature(target, CreatureType.PIG_ZOMBIE); break;
			case SKELETON: e = player.getWorld().spawnCreature(target, CreatureType.SKELETON); break;
			case SPIDER: e = player.getWorld().spawnCreature(target, CreatureType.SPIDER); break;
			case SQUID: e = player.getWorld().spawnCreature(target, CreatureType.SQUID); break;
			case ZOMBIE: e = player.getWorld().spawnCreature(target, CreatureType.ZOMBIE); break;
			case GHAST: e = player.getWorld().spawnCreature(target, CreatureType.GHAST); break;
			case GIANT: e = player.getWorld().spawnCreature(target, CreatureType.GIANT); break;
			case WOLF: e = player.getWorld().spawnCreature(target, CreatureType.WOLF); break;
		}
		
		return e;
	}

	protected PlayerFamiliar getFamiliar(String playerName)
	{
		PlayerFamiliar familiar = familiars.get(playerName);
		if (familiar == null)
		{
			familiar = new PlayerFamiliar();
			familiars.put(playerName, familiar);
		}
		return familiar;
	}
	
	
	protected void checkListener()
	{
		boolean anyFamiliars = false;
		for (PlayerFamiliar familiar : familiars.values())
		{
			if (familiar.hasFamiliar())
			{
				anyFamiliars = true;
				break;
			}
		}
		if (anyFamiliars)
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
		return "familiar";
	}

	@Override
	public String getCategory()
	{
		return "farming";
	}

	@Override
	public String getDescription()
	{
		return "Create an animal familiar to follow you around";
	}

	@Override
	public void onLoad(PluginProperties properties)
	{
		defaultFamiliars = properties.getStringList("spells-familiar-animals", DEFAULT_FAMILIARS);
		defaultMonsters = properties.getStringList("spells-familiar-monsters", DEFAULT_MONSTERS);
	}
	
	public void onPlayerQuit(PlayerEvent event)
	{
		PlayerFamiliar fam = getFamiliar(event.getPlayer().getName());
		if (fam.hasFamiliar())
		{
			fam.releaseFamiliar();
			checkListener();
		}
	}

	@Override
	public Material getMaterial()
	{
		return Material.EGG;
	}
	
}
