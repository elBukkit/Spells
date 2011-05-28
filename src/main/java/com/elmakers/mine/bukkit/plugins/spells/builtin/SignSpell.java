package com.elmakers.mine.bukkit.plugins.spells.builtin;

import org.bukkit.Material;

import com.elmakers.mine.bukkit.plugins.spells.Spell;

public class SignSpell extends Spell
{

    @Override
    public boolean onCast(String[] parameters)
    {
        castMessage(player, "Have some signs!");
        return giveMaterial(Material.SIGN, 8, (short)0, (byte)0);
    }

    @Override
    public String getName()
    {
        return "sign";
    }

    @Override
    public String getCategory()
    {
        return "help";
    }

    @Override
    public String getDescription()
    {
        return "Give yourself some signs";
    }

    @Override
    public Material getMaterial()
    {
        return Material.SIGN_POST;
    }

}
