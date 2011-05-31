package com.elmakers.mine.bukkit.plugins.spells.builtin;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.elmakers.mine.bukkit.plugins.spells.Spell;
import com.elmakers.mine.bukkit.plugins.spells.Target;

public class GotoSpell extends Spell
{

    @Override
    public boolean onCast(String[] parameters)
    {
        targetEntity(Player.class);
        boolean gather = false;
        Player targetPlayer = player;
        Target target = getTarget();
        Entity targetEntity = target.getEntity();
        if (targetEntity != null && targetEntity instanceof Player)
        {
            targetPlayer = (Player)targetEntity;
        }
        else
        {
            if (getYRotation() > 80)
            {
                gather = true;
            }
        }
        
        List<Player> players = targetPlayer.getWorld().getPlayers();
        Player destination = null;
        double destDistance = 0;
        
        for (Player d : players)
        {
            if (d != targetPlayer)
            {
                double dd = getDistance(d.getLocation(), targetPlayer.getLocation());
                if (destination == null || dd > destDistance)
                {
                    destDistance = dd;
                    destination = d;
                }
            }
        }
        if (destination == null)
        {
            return false;
        }
        
        if (gather)
        {
            targetPlayer = destination;
            destination = player;
        }
        targetPlayer.teleport(destination);
        castMessage(player, "Teleporting " + targetPlayer.getName() + " to " + destination.getName());
        
        return true;
    }

    @Override
    public String getName()
    {
        return "goto";
    }

    @Override
    public String getCategory()
    {
        return "exploration";
    }

    @Override
    public String getDescription()
    {
        return "Send you to the farthest away player";
    }

    @Override
    public Material getMaterial()
    {
        return Material.GLOWSTONE_DUST;
    }

}
