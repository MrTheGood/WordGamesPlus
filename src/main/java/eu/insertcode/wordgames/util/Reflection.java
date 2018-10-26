package eu.insertcode.wordgames.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Reflection {

    private String version;

    private Method componentCreatorMethod, getHandleMethod, sendPacketMethod;
    private Constructor packetConstructor;
    private Field connectionField;

    public Reflection() {
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException("Failed to get server version from package: " + Bukkit.getServer().getClass().getPackage().getName());
        }

        try {
            packetConstructor = getNmsClass("PacketPlayOutChat").getConstructor(getNmsClass("IChatBaseComponent"));
            componentCreatorMethod = getNmsClass("IChatBaseComponent$ChatSerializer").getDeclaredMethod("a", String.class);

            Class<?> player = getCraftBukkitClass("entity.CraftPlayer");
            getHandleMethod = player.getDeclaredMethod("getHandle");
            getHandleMethod.setAccessible(true);

            connectionField = getHandleMethod.getReturnType().getDeclaredField("playerConnection");
            connectionField.setAccessible(true);

            sendPacketMethod = connectionField.getType().getDeclaredMethod("sendPacket", getNmsClass("Packet"));

            packetConstructor.setAccessible(true);
            componentCreatorMethod.setAccessible(true);
            getHandleMethod.setAccessible(true);
            sendPacketMethod.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to initialize reflection", e);
        }
    }

    private Class<?> getNmsClass(String name) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + version + "." + name);
    }

    private Class<?> getCraftBukkitClass(String name) throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit." + version + "." + name);
    }

    public String getVersion() {
        return version;
    }

    public void sendJson(Player player, String json) {
        try {
            Object component = componentCreatorMethod.invoke(null, json);
            Object packet = packetConstructor.newInstance(component);
            Object handle = getHandleMethod.invoke(player);

            sendPacketMethod.invoke(connectionField.get(handle), packet);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to use reflection", e);
        }
    }

}
