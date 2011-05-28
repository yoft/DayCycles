package yoft.DayCycles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.config.Configuration;

public class DayCycles extends JavaPlugin{
	public PermissionHandler Permissions;
	public Configuration config;
	public List<World> noTime = new ArrayList<World>();
	public Map<String, Integer> cycles = new HashMap<String, Integer>();
	public Map<String, Integer> days = new HashMap<String, Integer>();
	
	
	@Override
	public void onDisable() {
		this.saveConfig();
		
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println("[" + pdfFile.getName() + "] has been disabled.");
	}
	
	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
	    System.out.println("[" + pdfFile.getName() + "] version [" + pdfFile.getVersion() + "] is enabled!");
	    
	    DayCycleCommandExecutor executor = new DayCycleCommandExecutor(this);
			this.getCommand("newday").setExecutor(executor);
			this.getCommand("newnight").setExecutor(executor);
			this.getCommand("setdays").setExecutor(executor);
		
		config = this.getConfiguration();
		this.loadConfig();
		
		this.setupPermissions();
		
		//20L = 1sec = 20 minecraft time
		//1L = 1 minecraft time
		//1000L = 500sec (8min 20sec) =  1000mc time
		
		this.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin) this, CheckTime, 20L, 1000L);
	    
	}
    
    Runnable CheckTime = new Runnable() {
		public void run() {
			
			for (World world : getServer().getWorlds()) {
				if (!worldHasTime(world))
					continue;
				
				long time = world.getTime();
				int cycle = cycles.get(world.getName());
				int day = days.get(world.getName());
				
				if ((time >= 12000 && time <= 13000) && (cycle < day)) {
					cycle++;
					world.setTime(0);
					System.out.println("skipped night " + cycle + " in \"" + world.getName() + "\".");
				}else if ((time > 12000 && time < 13000) && (cycle == day)){
					cycle++;
				}else if ((time < 1000) && cycle == day+1) {
					cycle=0;
					System.out.println("Started cycle 0" + " in \"" + world.getName() + "\".");
				}
				
				cycles.put(world.getName(), cycle);
			}
			saveConfig();
		}
	};
	
	public boolean worldHasTime(World world){
		if (world.getEnvironment() == Environment.NETHER){
			return false;
		}else{
			return !(noTime.contains(world));
		}
	}
	
    private void loadConfig() {
    	config.load();

		for (World world : getServer().getWorlds()) {
			days.put(world.getName(), config.getInt(world.getName() + "-days", 3));
			cycles.put(world.getName(), config.getInt(world.getName() + "-cycle", 0));
			if (!config.getBoolean(world.getName() + "-hasTime", worldHasTime(world))){
				noTime.add(world);
			}
		}
		
		this.saveConfig();
	}
    
    private void saveConfig() {

		for (World world : getServer().getWorlds()) {
			config.setProperty(world.getName() + "-days", days.get(world.getName()));
			config.setProperty(world.getName() + "-cycle", cycles.get(world.getName()));
			config.setProperty(world.getName() + "-hasTime", worldHasTime(world));
		}
		
		config.save();
	}
    
    public void setDays(World world, int days) {
    	
    	this.days.put(world.getName(), days);
    	
    	this.saveConfig();
    }
	
	private void setupPermissions() {
		Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");
		
		if (this.Permissions == null) {
			if (test != null) {
				this.Permissions = ((Permissions)test).getHandler();
			} else {
				System.out.println("Permission system not detected, defaulting to OP");
			}
		}
	}
	
}
