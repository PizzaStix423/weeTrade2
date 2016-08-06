package nl.dubehh;

import nl.dubehh.command.TradeCommand;
import nl.dubehh.events.TradeEvents;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
	
	private static Main _instance;
	public void onEnable(){
		_instance = this;
		initEvents();
		getCommand("Trade").setExecutor(new TradeCommand());
		generate();
		addDef();
	}
	
	public static Main getInstance(){
		return _instance;
	}
	private void initEvents(){
		Bukkit.getPluginManager().registerEvents(new TradeEvents(), this);
	}
	
	private void generate(){
		if(!getConfig().contains("prefix")){
			for(Message m : Message.values()){
				getConfig().set(m.getPath(), m.getDefault());
			}
		}
		saveConfig();
	}
	
	private void addDef(){
		FileConfiguration config = Options.getInstance().getConfig();
		for(DefaultConfig def : DefaultConfig.values()){
			if(!config.contains(def.toString())){
				config.set(def.toString(), false);
			}
		}
		Options.getInstance().saveConfig();
	}
	
	public enum DefaultConfig{
		RIGHT_CLICK_TRADING("rightclick-trading"),
		DISABLE_CREATIVE_TRADE("disable-creative-trade"),
		DISABLE_ADVENTURE_TRADE("disable-adventure-trade");
		
		private String path;
		
		private DefaultConfig(String path){
			this.path = path;
		}
		
		public String toString(){
			return path;
		}
	}
}
