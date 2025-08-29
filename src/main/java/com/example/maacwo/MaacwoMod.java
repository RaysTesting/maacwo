package com.example.maacwo;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

import java.util.LinkedList;
import java.util.Queue;

@Mod(modid = MaacwoMod.MODID, name = MaacwoMod.NAME, version = MaacwoMod.VERSION)
public class MaacwoMod {
    public static final String MODID = "maacwo";
    public static final String NAME = "Maacwo";
    public static final String VERSION = "1.0";

    private Queue<BlockPos> path = new LinkedList<>();

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // Example: fill path with some coordinates. In practice you'd compute a path here.
        path.add(new BlockPos(10, 64, 10));
        path.add(new BlockPos(20, 64, 20));
        path.add(new BlockPos(30, 64, 30));
        // Register this class to listen for tick events
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) {
            return;
        }
        EntityPlayer player = mc.thePlayer;
        if (path.isEmpty()) {
            return;
        }
        BlockPos next = path.peek();
        // compute horizontal difference
        double dx = (next.getX() + 0.5) - player.posX;
        double dz = (next.getZ() + 0.5) - player.posZ;
        double dist = Math.sqrt(dx * dx + dz * dz);
        if (dist < 0.5) {
            path.remove();
            return;
        }
        float forward = (float)(dz / dist);
        float strafe = (float)(dx / dist);
        // check vertical difference for jumping
        if ((next.getY() + 0.1) > player.posY) {
            player.jump();
        }
        // adjust yaw to face direction (optional for realism)
        float yaw = (float)(Math.atan2(dz, dx) * 180.0F / Math.PI) - 90.0F;
        player.rotationYaw = yaw;
        // move
        player.moveEntityWithHeading(strafe * 0.3F, forward * 0.3F);
    }
}
