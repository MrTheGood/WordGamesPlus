package eu.insertcode.wordgames.compatibility;

import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_9_R2.IChatBaseComponent;
import net.minecraft.server.v1_9_R2.PacketPlayOutChat;

public class Compatibility_1_9_R2 implements Compatibility {
	
	@Override
	public void sendJson(Player p, String json) {
		IChatBaseComponent component = IChatBaseComponent.ChatSerializer.a(json);
		
		
		PacketPlayOutChat packet = new PacketPlayOutChat(component);
		
		
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
	}
}
