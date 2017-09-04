package eu.insertcode.wordgames.compatibility;

import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R1.ChatSerializer;
import net.minecraft.server.v1_8_R1.IChatBaseComponent;
import net.minecraft.server.v1_8_R1.PacketPlayOutChat;

public class Compatibility_1_8_R1 implements Compatibility {
	
	@Override
	public void sendJson(Player p, String json) {
		IChatBaseComponent component = ChatSerializer.a(json);
		
		
		PacketPlayOutChat packet = new PacketPlayOutChat(component);
		
		
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
	}
}
