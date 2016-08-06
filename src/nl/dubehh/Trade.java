package nl.dubehh;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.primitives.Ints;

public class Trade extends TradeInventory{
	
	private static HashMap<UUID, Trade> trade = null;
	private Player p1;
	private Player p2;
	
	public Trade(Player p1, Player p2){
		super(p1, p2);
		this.p1 = p1;
		this.p2 = p2;
		if(trade == null) trade = new HashMap<UUID, Trade>();
		trade.put(p1.getUniqueId(), this);
		trade.put(p2.getUniqueId(), this);
	}
	
	
	public static boolean isTrading(Player p){
		return trade != null && trade.containsKey(p.getUniqueId());
	}
	
	public static Trade getTrade(Player p){
		if(isTrading(p)){
			return trade.get(p.getUniqueId());
		}
		return null;
	}
	
	public final boolean contains(final int[] array, final int key) {
	    return Ints.contains(array, key);
	}
	
	public void clear(){
		trade.remove(p1.getUniqueId());
		trade.remove(p2.getUniqueId());
		p1.closeInventory();
		p2.closeInventory();
	}
	
	@SuppressWarnings("deprecation")
	public boolean canContinue(ItemStack clicked, Player playerThatClicked){
		DyeColor left = DyeColor.getByData(getInventory().getItem(3).getData().getData());
		DyeColor right = DyeColor.getByData(getInventory().getItem(5).getData().getData());
		int leftStep = -1;
		int rightStep = -1;
		for(TradeOption o : TradeOption.values()){
			if(o.getDye() == left) leftStep = o.getStep();
			if(o.getDye() == right) rightStep = o.getStep();
			continue;
		}
		if(clicked.getData().getData() == DyeColor.BLUE.getData()){
			playerThatClicked.sendMessage(Message.WAIT_FOR_OTHER_PLAYER.toString());
			return false;
		}
		if((left == DyeColor.ORANGE && right == DyeColor.BLUE) || (left == DyeColor.BLUE && right == DyeColor.ORANGE)){
			endTrade(TradeClose.FINISH);
			return false;
		}
		if((clicked.getData().getData() == left.getData() && leftStep-rightStep > 0) || (clicked.getData().getData() == right.getData() && rightStep-leftStep > 0)){
			playerThatClicked.sendMessage(Message.WAIT_FOR_OTHER_PLAYER.toString());
			return false;
		}
		return true;
	}
	
	/*@SuppressWarnings("deprecation")
	private boolean checkBlue(byte clicked, byte other){
		return clicked == DyeColor.BLUE.getData() && other != DyeColor.ORANGE.getData() && other != ;
	}*/
	public enum TradeClose{ CANCEL, FINISH }
	public void endTrade(TradeClose close){
		TradeMisc.resetTrade(p1);TradeMisc.resetTrade(p2);
		for(int i = 0; i < getInventory().getSize(); i++){
			if(getInventory().getItem(i)== null || getInventory().getItem(i).getType() == Material.AIR) continue;
			if(close == TradeClose.CANCEL){
				if(contains(getTotalSlots(p1), i)){
					changeItem(p1, getInventory().getItem(i), ChangeOption.REMOVE, getInventory());
				}else if(contains(getTotalSlots(p2), i)){
					changeItem(p2, getInventory().getItem(i), ChangeOption.REMOVE, getInventory());
				}
			}else{
				if(contains(getTotalSlots(p1), i))p2.getInventory().addItem(getInventory().getItem(i));
				else if(contains(getTotalSlots(p2), i)) p1.getInventory().addItem(getInventory().getItem(i));
			}
		}
		if(close == TradeClose.FINISH){
			p1.sendMessage(Message.TRADE_SUCCESS.toString());
			p2.sendMessage(Message.TRADE_SUCCESS.toString());
			p1.playSound(p1.getLocation(), Sound.ENTITY_VILLAGER_YES, 30, 2);
			p2.playSound(p2.getLocation(), Sound.ENTITY_VILLAGER_YES, 30, 2);
		}else{
			p1.sendMessage(Message.TRADE_CANCEL.toString());
			p2.sendMessage(Message.TRADE_CANCEL.toString());
			p1.playSound(p1.getLocation(), Sound.ENTITY_VILLAGER_NO, 30, 2);
			p2.playSound(p2.getLocation(), Sound.ENTITY_VILLAGER_NO, 30, 2);
		}
		clear();
	}
	
	public enum ChangeOption{ ADD, REMOVE }
	
	public void changeItem(Player p, ItemStack i, ChangeOption o, Inventory inv){
		if(getAvailableSlots(p).size() == 0 && inv.getType() == p.getInventory().getType()){
			p.sendMessage(Message.USED_ALL_TRADING_SPACE.toString());
			return;
		}
		if(o == ChangeOption.ADD){
			p.getInventory().removeItem(i);
			getInventory().setItem(getAvailableSlots(p).get(0).intValue(), i);
		}else{
			p.getInventory().addItem(i);
			for(int x=0;x<getInventory().getSize();x++){
				if(getInventory().getItem(x)!=null&&getInventory().getItem(x).getType()!=Material.AIR){
					if(getInventory().getItem(x).equals(i) && contains(getTotalSlots(p), x)){
						getInventory().setItem(x, null);
						break;
					}
				}
			}
		}
		p.updateInventory();
	}
	
	public enum TradeOption{
		TRADE("Trade", ChatColor.GREEN, 0), CONFIRM("Confirm", ChatColor.GOLD, 1), CANCEL("Cancel", ChatColor.RED, -1), NULL("", ChatColor.BLACK, -1), FINISH("Waiting for partner..", ChatColor.DARK_AQUA, 2);
		
		private String name;
		private ChatColor color;
		private int step;
		
		private TradeOption(String name, ChatColor color, int step){
			this.name = name;
			this.color = color;
			this.step = step;
		}
		
		public String getName(){
			return color+name;
		}
		
		public int getStep(){
			return step;
		}
		
		public ChatColor getColor(){
			return color;
		}
		
		public DyeColor getDye(){
			switch(name){
			case "Trade": return DyeColor.LIME;
			case "Confirm": return DyeColor.ORANGE;
			case "Cancel": return DyeColor.RED;
			case "Waiting for partner..": return DyeColor.BLUE;
			default: return DyeColor.BLACK;
			}
		}
	}
}
