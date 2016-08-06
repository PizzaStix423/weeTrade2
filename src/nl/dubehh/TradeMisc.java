package nl.dubehh;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.entity.Player;

public class TradeMisc {
	
	private static HashMap<UUID, UUID> tradeReq = new HashMap<UUID, UUID>();
	private static HashSet<UUID> ignored = new HashSet<UUID>();
	public static boolean hasRequest(Player p, Player requester){
		return tradeReq.containsKey(requester.getUniqueId()) && tradeReq.get(requester.getUniqueId()) == p.getUniqueId();
	}
	
	public static void requestTrade(Player requester, Player p){
		tradeReq.put(requester.getUniqueId(), p.getUniqueId());
	}
	
	public static void resetTrade(Player requester){
		tradeReq.remove(requester.getUniqueId());
	}
	
	public static void toggleIgnored(Player p){
		if(isIgnoring(p)){
			ignored.remove(p.getUniqueId());
		}else{
			ignored.add(p.getUniqueId());
		}
	}
	
	public static boolean isIgnoring(Player p){
		return ignored.contains(p.getUniqueId());
	}
}
