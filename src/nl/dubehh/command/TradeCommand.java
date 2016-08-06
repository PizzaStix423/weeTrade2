package nl.dubehh.command;

import nl.dubehh.Main;
import nl.dubehh.Message;
import nl.dubehh.Options;
import nl.dubehh.Trade;
import nl.dubehh.TradeMisc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class TradeCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if(!(sender instanceof Player)) return false;
		Player p = (Player) sender;
		if(args.length == 0){
			p.sendMessage(new String[]{
				ChatColor.GOLD+"weeTrade "+ChatColor.YELLOW+" command page:",
				ChatColor.GRAY+"/Trade <player> "+ChatColor.DARK_GRAY+"Send trade request",
				ChatColor.GRAY+"/Trade accept <player> "+ChatColor.DARK_GRAY+"Accept trade request",
				ChatColor.GRAY+"/Trade ignore "+ChatColor.DARK_GRAY+"Ignore incoming requests",
				ChatColor.GRAY+"/Trade reload "+ChatColor.DARK_GRAY+"Reload the configuration file",
			});
		}else{
			FileConfiguration c = Options.getInstance().getConfig();
			switch(args[0].toLowerCase()){
			case "reload":
				if(!p.hasPermission("trade.reload")){
					p.sendMessage(Message.NO_PERM.toString());
					return true;
				}
				Main.getInstance().reloadConfig();
				Options.getInstance().reloadConfig();
				Options.getInstance().saveConfig();
				Main.getInstance().saveConfig();
				p.sendMessage(Message.CONFIG_RELOADED.toString());
				return true;
			case "accept":
				if(!p.hasPermission("trade.accept")){
					p.sendMessage(Message.NO_PERM.toString());
					return true;
				}
				if(args.length==1){
					p.sendMessage(ChatColor.GRAY+"/Trade accept <player>");
				}else{
					Player target = Bukkit.getPlayer(args[1]);
					if(target == null){
						p.sendMessage(Message.PLAYER_NOT_ONLINE.toString(args[1]));
					}else{
						if((p.getGameMode() == GameMode.ADVENTURE && c.getBoolean("disable-adventure-trade")) || (p.getGameMode() == GameMode.CREATIVE && c.getBoolean("disable-creative-trade"))){
							p.sendMessage(Message.DONT_WANT_TRADE.toString(target.getName()));
							return true;
						}
						if(TradeMisc.hasRequest(p, target)){
							new Trade(p, target);
						}else{
							p.sendMessage(Message.NOT_SEND_REQUESTS.toString(target.getName()));
						}
					}
				}
				return true;
			case "ignore":
				if(!p.hasPermission("trade.ignore")){
					p.sendMessage(Message.NO_PERM.toString());
					return true;
				}
				TradeMisc.toggleIgnored(p);
				p.sendMessage(Message.IGNORING_STATE.toString()+ChatColor.GOLD+TradeMisc.isIgnoring(p));
				return true;
			default:
				if(!p.hasPermission("trade.trade")){
					p.sendMessage(Message.NO_PERM.toString());
					return true;
				}
				Player target = Bukkit.getPlayer(args[0]);
				if(target == null){
					p.sendMessage(Message.PLAYER_NOT_ONLINE.toString(args[0]));
				}else{
					if(p == target){
						p.sendMessage(Message.TRADE_WITH_SELF.toString());
						return true;
					}
					if(TradeMisc.hasRequest(target, p)){
						p.sendMessage(Message.ALREADY_REQUEST_SEND.toString(target.getName()));
						return true;
					}
					if(TradeMisc.isIgnoring(target) || (p.getGameMode() == GameMode.ADVENTURE && c.getBoolean("disable-adventure-trade")) || (p.getGameMode() == GameMode.CREATIVE && c.getBoolean("disable-creative-trade"))){
						p.sendMessage(Message.DONT_WANT_TRADE.toString(target.getName()));
						return true;
					}
					p.sendMessage(Message.REQUEST_SENT.toString(target.getName()));
					target.sendMessage(Message.REQUEST_RECEIVED.toString(p.getName()));
					TradeMisc.requestTrade(p, target);
				}
				return true;
			}
		}
		return true;
	}

}
