package com.elmakers.mine.bukkit.plugins.spells.builtin;

import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.util.Vector;

import com.elmakers.mine.bukkit.plugins.spells.Spell;
import com.elmakers.mine.bukkit.plugins.spells.Target;

public class ForceSpell extends Spell
{
    int defaultMagnitude = 3;
    
    public ForceSpell()
    {
        addVariant("push", Material.RAILS, getCategory(), "Push things away from you", "push");
    }
    
    @Override
    public boolean onCast(String[] parameters)
    {
        Target target = getTargetEntity();
        if (!target.hasTarget())
        {
            return false;
        }
        
        boolean push = false;
        int magnitude = defaultMagnitude;
        
        for (int i = 0; i < parameters.length; i++)
        {
            String parameter = parameters[i];

            if (parameter.equalsIgnoreCase("push"))
            {
                push = true;
                continue;
            }
            
            // try magnitude
            try
            {
                magnitude = Integer.parseInt(parameter);
                
                // Assume number, ok to continue
                continue;
            } 
            catch(NumberFormatException ex)
            {
            }
        }
        
        Vector targetLoc = new Vector(target.getLocation().getBlockX(), target.getLocation().getBlockY(), target.getLocation().getBlockZ());
        Vector playerLoc = new Vector(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
        Vector aimVector = playerLoc;
        if (push)
        {
            aimVector = getAimVector();
        }
        else
        {
            aimVector.subtract(targetLoc);
        }
        aimVector.normalize();
        aimVector.multiply(magnitude);
        
        CraftEntity ce = (CraftEntity)target.getEntity();
        ce.setVelocity(aimVector);
        
        if (push)
        {
            castMessage(player, "Shove!");
        }
        else
        {
            castMessage(player, "Yoink!");
        }
        return true;
    }

    @Override
    public String getName()
    {
        return "force";
    }

    @Override
    public String getCategory()
    {
        return "help";
    }

    @Override
    public String getDescription()
    {
        return "Pull things toward you";
    }

    @Override
    public Material getMaterial()
    {
        return Material.FISHING_ROD;
    }

}
