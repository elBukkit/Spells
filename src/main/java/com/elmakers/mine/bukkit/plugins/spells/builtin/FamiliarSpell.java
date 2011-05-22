package com.elmakers.mine.bukkit.plugins.spells.builtin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

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
	
	public class PlayerFamiliar
	{
		public LivingEntity familiar = null;
		
		public boolean hasFamiliar()
		{
			return familiar != null;
		}
		
		public void setFamiliar(LivingEntity f)
		{
			familiar = f;
		}
		
		public void releaseFamiliar()
		{
			if (familiar != null)
			{
				familiar.setHealth(0);
				familiar = null;
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
	
	public FamiliarSpell()
	{
		addVariant("monster", Material.PUMPKIN, "combat", "Call a monster to your side", "monster");
	}
	
	@Override
	public boolean onCast(String[] parameters)
	{
		PlayerFamiliar fam = getFamiliar(player.getName());
		if (fam.hasFamiliar())
		{
			fam.releaseFamiliar();
			castMessage(player, "You release your familiar");
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
			if (parameters.length > 0)
			{
				if (parameters[0].equalsIgnoreCase("any"))
				{
					int randomFamiliar = rand.nextInt(FamiliarType.values().length - 1);
					famType = FamiliarType.values()[randomFamiliar];
				}
				else if (parameters[0].equalsIgnoreCase("monster"))
				{
					int randomFamiliar = rand.nextInt(defaultMonsters.size());
					famType = FamiliarType.parseString(defaultMonsters.get(randomFamiliar));
				}
				else
				{
					famType = FamiliarType.parseString(parameters[0]);
				}
			}
			
			if (famType == FamiliarType.UNKNOWN)
			{
				int randomFamiliar = rand.nextInt(defaultFamiliars.size());
				famType = FamiliarType.parseString(defaultFamiliars.get(randomFamiliar));
			}
			
			if (target.getType() == Material.WATER || target.getType() == Material.STATIONARY_WATER)
			{
				famType = FamiliarType.SQUID;
			}
			
			LivingEntity entity =  spawnFamiliar(target, famType);
			if (entity == null)
			{
				sendMessage(player, "Your familiar is DOA");
				return false;
			}
			castMessage(player, "You create a " + famType.name().toLowerCase() + " familiar!");
			fam.setFamiliar(entity);
			checkListener();
			return true;
		}
	}
		
	protected LivingEntity spawnFamiliar(Block target, FamiliarType famType)
	{
		LivingEntity e = null;
		
		/// ARRRGGG!
		switch (famType)
		{
			case CHICKEN: e = player.getWorld().spawnCreature(target.getLocation(), CreatureType.CHICKEN); break;
			case SHEEP: e = player.getWorld().spawnCreature(target.getLocation(), CreatureType.SHEEP); break;
			case COW: e = player.getWorld().spawnCreature(target.getLocation(), CreatureType.COW); break;
			case PIG: e = player.getWorld().spawnCreature(target.getLocation(), CreatureType.PIG); break;
			case CREEPER: e = player.getWorld().spawnCreature(target.getLocation(), CreatureType.CREEPER); break;
			case PIGZOMBIE: e = player.getWorld().spawnCreature(target.getLocation(), CreatureType.PIG_ZOMBIE); break;
			case SKELETON: e = player.getWorld().spawnCreature(target.getLocation(), CreatureType.SKELETON); break;
			case SPIDER: e = player.getWorld().spawnCreature(target.getLocation(), CreatureType.SPIDER); break;
			case SQUID: e = player.getWorld().spawnCreature(target.getLocation(), CreatureType.SQUID); break;
			case ZOMBIE: e = player.getWorld().spawnCreature(target.getLocation(), CreatureType.ZOMBIE); break;
			case GHAST: e = player.getWorld().spawnCreature(target.getLocation(), CreatureType.GHAST); break;
			case GIANT: e = player.getWorld().spawnCreature(target.getLocation(), CreatureType.GIANT); break;
			case WOLF: e = player.getWorld().spawnCreature(target.getLocation(), CreatureType.WOLF); break;
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
