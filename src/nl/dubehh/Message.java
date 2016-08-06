package nl.dubehh;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public enum Message {
	
	PREFIX("prefix", "&7[&6Trade&7] &e"),
	TRADE_SUCCESS("trade-success", "&eTrade has been succesfully processed!"),
	TRADE_CANCEL("trade-cancel", "&eThe trade got cancelled"),
	NOT_SEND_REQUESTS("not-send-request", "&eSorry, %TARGET% hasn't sent you an request."),
	IGNORING_STATE("ignoring-state", "&eIgnoring has been set to: "),
	CONFIG_RELOADED("config-reloaded", "&eThe configuration file has been reloaded!"),
	TRADE_WITH_SELF("trade-with-self", "&eYou cannot trade with yourself."),
	ALREADY_REQUEST_SEND("already-requested", "&eYou already send %TARGET% an trading request."),
	DONT_WANT_TRADE("not-want-trade", "&eSorry, %TARGET% doesn't want to trade right now."),
	REQUEST_SENT("request-sent", "&eYou've sent an trading request to %TARGET%"),
	REQUEST_RECEIVED("request-received", "&eYou received an trading request from %TARGET%"),
	PLAYER_NOT_ONLINE("player-not-online", "&eSorry, %TARGET% is currently not online."),
	USED_ALL_TRADING_SPACE("no-more-trading-space", "&eYou used all the trading space."),
	WAIT_FOR_OTHER_PLAYER("wait-for-other-player", "&eWait for the other player to proceed."),
	NO_PERM("no-permission", "&4No permission.");
	
	private String path;
	private String _default;
	private Message(String path, String _default){
		this.path = path;
		this._default = _default;
	}
	
	public String getDefault(){
		return _default;
	}
	
	public String getPath(){
		return path;
	}
	
	public String toString(){
		FileConfiguration c = Main.getInstance().getConfig();
		try{
			String message = ChatColor.translateAlternateColorCodes('&', c.getString(path));
			if(!path.equalsIgnoreCase(PREFIX.getPath())) return ChatColor.translateAlternateColorCodes('&', c.getString(PREFIX.getPath()))+" "+message;
			return message;
		}catch(Exception e){
			return null;
		}
	}
	
	public String toString(String change){
		return toString().replaceAll("%TARGET%", change);
	}
	
}
