package nl.dubehh;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


public class Options{
	
	private FileConfiguration config = null;
	private File file = null;
	private static Options instance = null;
	
	private Options(){
		/* Override default constructor */
	}
	
	public static Options getInstance(){
		if(instance == null) instance = new Options();
		return instance;
	}
	
	public void reloadConfig(){
		if(file == null){
			file = new File(Main.getInstance().getDataFolder(), "options.yml");
		}
		config = YamlConfiguration.loadConfiguration(file);
		
		InputStream defConfigStream = Main.getInstance().getResource("options.yml");
		if(defConfigStream != null){
			@SuppressWarnings("deprecation")
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        config.setDefaults(defConfig);
		}
	}
	
	public FileConfiguration getConfig(){
		if(config == null){
			reloadConfig();
		}
		return config;
	}
	
    public void saveConfig() {
        if (config == null || file == null) {
            return;
        }
        try {
            getConfig().save(file);
            reloadConfig();
        } catch (IOException ex) {
            return;
        } 
    }
}
