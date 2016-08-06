package nl.dubehh.events;

import nl.dubehh.Message;
import nl.dubehh.Options;
import nl.dubehh.Trade;
import nl.dubehh.Trade.ChangeOption;
import nl.dubehh.Trade.TradeClose;
import nl.dubehh.TradeMisc;

import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

public class TradeEvents implements Listener{
	
	@EventHandler
	public void onClick(InventoryClickEvent e){
		if(!(e.getWhoClicked() instanceof Player)) return;
		Player p = (Player) e.getWhoClicked();
		if(Trade.isTrading(p)){
			e.setCancelled(true);
			if(e.getClickedInventory() != null && e.getCurrentItem() != null && e.getCurrentItem().getType() != org.bukkit.Material.AIR){
				Trade trade = Trade.getTrade(p);
				if(e.getClickedInventory().getType() == p.getInventory().getType()){
					if(!stopChangeItem(p, trade, e.getInventory())){
						trade.changeItem(p, e.getCurrentItem(), ChangeOption.ADD, e.getClickedInventory());
						p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 30, 2);
					}else{
						p.playSound(p.getLocation(), Sound.ENTITY_IRONGOLEM_HURT, 30, 2);
					}
				}
				else
				{
					if(e.getSlot() == 4) trade.endTrade(TradeClose.CANCEL);
					else if(trade.contains(trade.getTotalSlots(p), e.getSlot())){
						if(stopChangeItem(p, trade, e.getInventory())){
							p.playSound(p.getLocation(), Sound.ENTITY_IRONGOLEM_HURT, 30, 2);
						}else{
							p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 30, 2);
							trade.changeItem(p, e.getCurrentItem(), ChangeOption.REMOVE, e.getClickedInventory());
						}
					}
					else if (((e.getSlot() == 3 && p == trade.getLeftPlayer()) || (e.getSlot() == 5 && p != trade.getLeftPlayer())) && trade.canContinue(e.getCurrentItem(), p)) trade.nextOption(p);
					else p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 30, 2);
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	private boolean stopChangeItem(Player p , Trade t, Inventory inv){
		return (inv.getItem(3).getData().getData() == DyeColor.ORANGE.getData() && p == t.getLeftPlayer()) || (inv.getItem(5).getData().getData() == DyeColor.ORANGE.getData() && p != t.getLeftPlayer());
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e){
		if(!(e.getPlayer() instanceof Player)) return;
		Player p = (Player) e.getPlayer();
		if(Trade.isTrading(p)){
			Trade.getTrade(p).endTrade(TradeClose.CANCEL);
		}
	}
	
	@EventHandler
	public void onClick(PlayerInteractEntityEvent e){
		if(e.getRightClicked() instanceof Player){
			Player p = e.getPlayer();
			Player target = (Player) e.getRightClicked();
			FileConfiguration c = Options.getInstance().getConfig();
			if(p.isSneaking() && p.hasPermission("trade.sneaktrade") && c.getBoolean("rightclick-trading")){
				if((p.getGameMode() == GameMode.ADVENTURE && c.getBoolean("disable-adventure-trade")) || (p.getGameMode() == GameMode.CREATIVE && c.getBoolean("disable-creative-trade"))){
					p.sendMessage(Message.DONT_WANT_TRADE.toString(target.getName()));
					return;
				}
				if(TradeMisc.hasRequest(p, target)){
					new Trade(p, target);
					return;
				}
				if(TradeMisc.hasRequest(target, p)){
					p.sendMessage(Message.ALREADY_REQUEST_SEND.toString(target.getName()));
					return;
				}
				
				if(TradeMisc.isIgnoring(target)){
					p.sendMessage(Message.DONT_WANT_TRADE.toString(target.getName()));
					return;
				}
				p.sendMessage(Message.REQUEST_SENT.toString(target.getName()));
				target.sendMessage(Message.REQUEST_RECEIVED.toString(p.getName()));
				TradeMisc.requestTrade(p, target);
			}
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		TradeMisc.resetTrade(e.getPlayer());
		Player p = e.getPlayer();
		if(Trade.isTrading(p)){
			Trade.getTrade(p).endTrade(TradeClose.CANCEL);
		}
	}

}
