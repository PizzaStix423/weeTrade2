package nl.dubehh;

import java.util.ArrayList;
import java.util.List;

import nl.dubehh.Trade.TradeOption;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TradeInventory{
	
	/**
	 * @author Dubehh 
	 */
	
	private Inventory inventory;
	private Player pLeft;
	private final static int inventorySize = 36;
	
	public TradeInventory(Player p1, Player p2){
		this.inventory = getDefaultInventory();
		p1.openInventory(getInventory());
		p2.openInventory(getInventory());
		pLeft = p1;
	}
	
	/* Get slots that are empty */
	public List<Integer> getAvailableSlots(Player p){
		List<Integer> openSlots = new ArrayList<Integer>();
		for(int i : getTotalSlots(p)){
			if(inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR){
				openSlots.add(i);
			}
		}
		return openSlots;
	}
	/* Get Total of slots a player can use*/
	public int[] getTotalSlots(Player p){
		int[] slots = new int[((inventory.getSize()/9) *4)-1];
		int row = 0;
		for(int y = 0; y < inventory.getSize(); y+=9){
			for(int x = 0; x < 9; x++){
				if((x == 3 || x == 5) && y == 0) continue;
				boolean condition = p == pLeft ? x < 4 : x > 4 && x < 9;
				if(condition){
					slots[row] = y+x;
					row++;
				}
			}
		}
		return slots;
	}
	/**
	 * @return Inventory object containing the itemstacks
	 */
	public Inventory getInventory(){
		return inventory;
	}
	
	/** 
	 * @return Player object who posses left trading area
	 */
	public Player getLeftPlayer(){
		return pLeft;
	}
	
	@SuppressWarnings("deprecation")
	public void nextOption(Player p){
		if(!Trade.isTrading(p)) return;
		int slot = p == pLeft ? 3 : 5;
		DyeColor c = DyeColor.getByData(inventory.getItem(slot).getData().getData());
		inventory.setItem(slot, getPane(next(c)));
		p.updateInventory();
	}
	
	/**
	 * @param current Current color of the panes
	 * @return New tradeoption pane
	 */
	private TradeOption next(DyeColor current){
		if(current == DyeColor.LIME) return TradeOption.CONFIRM;
		if(current == DyeColor.ORANGE) return TradeOption.FINISH;
		return TradeOption.NULL;
	}
		
	/**
	 * Return default template inventory
	 * @return Inventory object
	 */
	private static final Inventory getDefaultInventory(){
		Inventory inventory = Bukkit.createInventory(null, inventorySize, getName());
		for(int i = 0; i < inventory.getSize(); i++){
			if((i+1) % 9 == 0){
				inventory.setItem((i-4), getPane(TradeOption.NULL));
			}
			else if(i == 3 || i == 5){
				inventory.setItem(i, getPane(TradeOption.TRADE));
			}
		}
		inventory.setItem(4, getPane(TradeOption.CANCEL));
		return inventory;
	}
	
	public static final String getName(){
		return ChatColor.GOLD+"Trading Gui";
	}
	/**
	 * Get colored glass pane with different option
	 * @param o Tradeoption (color)
	 * @return The itemstack
	 */
	@SuppressWarnings("deprecation")
	private static final ItemStack getPane(TradeOption o){
		ItemStack i = new ItemStack(Material.STAINED_GLASS_PANE, 1, o.getDye().getData());
		ItemMeta im = i.getItemMeta();
		im.setDisplayName(o.getName());i.setItemMeta(im);
		return i;
	}
}
