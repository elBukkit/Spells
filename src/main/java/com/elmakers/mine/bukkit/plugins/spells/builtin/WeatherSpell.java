package com.elmakers.mine.bukkit.plugins.spells.builtin;

import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftWorld;

import com.elmakers.mine.bukkit.plugins.spells.Spell;

public class WeatherSpell extends Spell
{

    @Override
    public boolean onCast(String[] parameters)
    {
        CraftWorld craftWorld = ((CraftWorld)player.getWorld());
        boolean hasStorm = craftWorld.hasStorm();
        craftWorld.setStorm(!hasStorm);
        if (hasStorm)
        {
            castMessage(player, "You calm the storm");
        }
        else
        {
            castMessage(player, "You stir up a storm");
        }
        return true;
    }

    @Override
    public String getName()
    {
        return "weather";
    }

    @Override
    public String getCategory()
    {
        return "farming";
    }

    @Override
    public String getDescription()
    {
        return "Change the weather";
    }

    @Override
    public Material getMaterial()
    {
        return Material.WATER;
    }

}
