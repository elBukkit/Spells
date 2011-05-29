package com.elmakers.mine.bukkit.plugins.spells.builtin;

import net.minecraft.server.EntityArrow;

import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftArrow;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Arrow;

import com.elmakers.mine.bukkit.plugins.spells.Spell;

public class ArrowSpell extends Spell
{
    public ArrowSpell()
    {
        addVariant("arrowrain", Material.BOW, getCategory(), "Fire a volley of arrows", "20");
    }
    
	@Override
	public boolean onCast(String[] parameters)
	{
	    int arrowCount = 1;
        if (parameters.length > 0)
        {
            try
            {
                arrowCount = Integer.parseInt(parameters[0]);
            }
            catch (NumberFormatException ex)
            {
                arrowCount = 1;
            }
        }
		CraftPlayer cp = (CraftPlayer)player;
		
		for (int ai = 0; ai < arrowCount; ai++)
		{
		    Arrow arrow = cp.shootArrow();
		    if (arrow == null)
		    {
		        sendMessage(player, "One of your arrows fizzled");
		        return false;
		    }
		    if (ai != 0 && (arrow instanceof CraftArrow))
		    {
		        CraftArrow ca = (CraftArrow)arrow;
		        EntityArrow ea = (EntityArrow)ca.getHandle();
		        ea.setPosition
		        (
	                ea.locX + Math.random() * arrowCount - arrowCount / 2,
	                ea.locY + Math.random() * arrowCount - arrowCount / 2,
	                ea.locZ + Math.random() * arrowCount - arrowCount / 2
		        );	                
		    }
		}
	
		castMessage(player, "You fire some magical arrows");
		
		return true;
	}

	@Override
	public String getName()
	{
		return "arrow";
	}

	@Override
	public String getCategory()
	{
		return "combat";
	}

	@Override
	public String getDescription()
	{
		return "Throws a magic arrow";
	}

	@Override
	public Material getMaterial()
	{
		return Material.ARROW;
	}

}
