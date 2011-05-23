package com.elmakers.mine.bukkit.plugins.spells.builtin;

import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.LightningStrike;

import com.elmakers.mine.bukkit.plugins.spells.Spell;
import com.elmakers.mine.bukkit.plugins.spells.Target;

public class LightningSpell extends Spell
{

    @Override
    public boolean onCast(String[] parameters)
    {
        CraftWorld craftWorld = ((CraftWorld)player.getWorld());
        Target target = getTarget();
        if (!target.hasTarget())
        {
            sendMessage(player, "No target");
            return false;
        }
       
        LightningStrike strike = craftWorld.strikeLightning(target.getLocation());
        castMessage(player, "ZAP!");
        return strike != null; 
    }

    @Override
    public String getName()
    {
        return "lightning";
    }

    @Override
    public String getCategory()
    {
        return "combat";
    }

    @Override
    public String getDescription()
    {
        return "Strike lighting at your target";
    }

    @Override
    public Material getMaterial()
    {
        return Material.COOKED_FISH;
    }

}
