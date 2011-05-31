package com.elmakers.mine.bukkit.plugins.spells.builtin;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import com.elmakers.mine.bukkit.plugins.spells.Spell;

public class LevitateSpell extends Spell
{
    public class LevitateAction implements Runnable
    {
        protected BukkitScheduler scheduler;
        protected Plugin plugin;
        protected Server server;
        protected Player player;

        protected boolean active = false;
        protected Vector force = null;
        protected int hoverHeight = 3;
        protected int maxDistance = 10;
        
        public LevitateAction(Player player, Plugin plugin)
        {
            this.plugin = plugin;
            this.server = plugin.getServer();
            this.scheduler = server.getScheduler();
            this.player = player;
        }
        
        public void activate()
        {
            active = true;
            force  = new Vector(0, 0.1f, 0);
           
            applyForce();
        }
        
        public void applyForce()
        {
            if (player.isDead() || !player.isOnline()) 
            {
                active = false;
            }
            
            Location playerLocation = player.getLocation();
            World world = playerLocation.getWorld();
            
            Block targetBlock = world.getBlockAt(playerLocation);
            targetBlock = targetBlock.getFace(BlockFace.DOWN);
            int playerHeight = 0;
            while (targetBlock.getType() == Material.AIR && playerHeight < maxDistance)
            {
                playerHeight++;
                targetBlock = targetBlock.getFace(BlockFace.DOWN);
            }
            
            float pitch = playerLocation.getPitch();
            float yaw = playerLocation.getYaw();
            
            Vector scaledForce = force.clone();
            
            Vector aim = new Vector((0 - Math.sin(Math.toRadians(yaw))), (0 - Math.sin(Math
                    .toRadians(pitch))), Math.cos(Math.toRadians(yaw)));
            aim.normalize();
            
            if (pitch < -45)
            {
                // Elevate, but don't move
                aim.setX(0);
                aim.setZ(0);
                aim.multiply(4);
                scaledForce.add(aim);
            }
            else if (pitch > 45)
            {
                // Descend, don't move- try to land.
                if (playerHeight >= maxDistance)
                {
                    scaledForce.multiply(-2);
                }
                else if (playerHeight > hoverHeight)
                {
                    scaledForce.multiply(-1);
                }
                else if (playerHeight == 1)
                {
                    scaledForce.multiply(3);
                }
            }
            else if (pitch < 30 && pitch > -45)
            { 
                // faster X/Z speeds at higher altitudes, but keep Y normalized
                double y = aim.getY();
                float multiplier = playerHeight / hoverHeight;
                if (multiplier > 5) multiplier = 5;
                aim.multiply(multiplier);
                aim.setY(y);
                scaledForce.add(aim);
            }
            
            player.setVelocity(scaledForce);
            scheduler.scheduleAsyncDelayedTask(plugin, this, 5);
        }
        
        public void deactivate()
        {
            active = false;
            player.setVelocity(new Vector(0,0,0));
            
            Location playerLocation = player.getLocation();
            World world = playerLocation.getWorld();
            Block targetBlock = world.getBlockAt(playerLocation);
            int playerHeight = 0;
            while (targetBlock.getType() == Material.AIR && playerHeight < maxDistance)
            {
                playerHeight++;
                targetBlock = targetBlock.getFace(BlockFace.DOWN);
            }
            
            playerLocation.setY(targetBlock.getY() + 1);
            player.teleport(playerLocation);
        }

        public void run()
        {
            if (active)
            {
                applyForce();
            }
        }
        
        public boolean isActive()
        {
            return active;
        }
    }
    
    protected HashMap<String, LevitateAction> levitating = new HashMap<String, LevitateAction>();
    
    @Override
    public boolean onCast(String[] parameters)
    {
        LevitateAction action = levitating.get(player.getName());
        if (action == null)
        {
            action = new LevitateAction(player, spells.getPlugin());
            levitating.put(player.getName(), action);
        }
        
        if (action.isActive())
        {
            action.deactivate();
            levitating.remove(player.getName());
            castMessage(player, "You feel heavier");
        }
        else
        {
            action.activate();
            // Special case for player dying / logging out and back in
            if (!action.isActive())
            {
                action = new LevitateAction(player, spells.getPlugin());
                action.activate();
                levitating.put(player.getName(), action);
            }
            castMessage(player, "You feel lighter");
        }
        
        return true;
    }

    @Override
    public String getName()
    {
        return "levitate";
    }

    @Override
    public String getCategory()
    {
        return "exploration";
    }

    @Override
    public String getDescription()
    {
        return "Levitate yourself up into the air";
    }

    @Override
    public Material getMaterial()
    {
        return Material.GOLD_BOOTS;
    }

}
