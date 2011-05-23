package com.elmakers.mine.bukkit.plugins.spells.builtin;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LightningStrike;

import com.elmakers.mine.bukkit.plugins.spells.Spell;

public class LightningSpell extends Spell
{

    @Override
    public boolean onCast(String[] parameters)
    {
        CraftWorld craftWorld = ((CraftWorld)player.getWorld());
        Entity entity = getTargetEntity();
        if (entity == null)
        {
            Block targetBlock = getTargetBlock();
            if (targetBlock == null)
            {
                sendMessage(player, "No target");
                return false;
            }
            LightningStrike strike = craftWorld.strikeLightning(targetBlock.getLocation());
            castMessage(player, "ZAP!");
            return strike != null;
        }
        LightningStrike strike = craftWorld.strikeLightning(entity.getLocation());
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
