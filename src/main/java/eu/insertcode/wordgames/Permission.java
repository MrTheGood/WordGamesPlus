package eu.insertcode.wordgames;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum Permission {
	
	STOP("wordgames.stop"),
	RELOAD("wordgames.reload"),
	
	START_ALL("wordgames.start"),
	START_HOVER("wordgames.start.hover"),
	START_TIMED("wordgames.start.timed"),
	START_UNMUTE("wordgames.start.unmute"),
	START_REORDER("wordgames.start.reorder"),
	START_CALCULATE("wordgames.start.calculate"),
	
	PLAY_ALL("wordgames.play"),
	PLAY_HOVER("wordgames.play.hover"),
	PLAY_TIMED("wordgames.play.timed"),
	PLAY_UNMUTE("wordgames.play.unmute"),
	PLAY_REORDER("wordgames.play.reorder"),
	PLAY_CALCULATE("wordgames.play.calculate");
	
	private final String permission;
	
	Permission(String perm) {
		this.permission = perm;
	}
	
	public String toString() {
		return permission;
	}
	
	public boolean forSender(CommandSender s, Permission alternative) {
		return s.hasPermission(toString()) || alternative.forSender(s);
	}
	
	public boolean forSender(CommandSender s) {
		return s.hasPermission(toString());
	}
	
	public boolean forPlayer(Player p, Permission alternative) {
		return p.hasPermission(toString()) || alternative.forPlayer(p);
	}
	
	public boolean forPlayer(Player p) {
		return p.hasPermission(toString());
	}
}
