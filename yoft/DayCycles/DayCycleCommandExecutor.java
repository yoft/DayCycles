package yoft.DayCycles;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DayCycleCommandExecutor implements CommandExecutor {
	
	private DayCycles plugin;
	
	public DayCycleCommandExecutor(DayCycles plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		World world;
		
		if (sender instanceof Player){
			world = ((Player) sender).getWorld();
		}else{
			world = plugin.getServer().getWorld(args[0]);
			if (world == null){
				sender.sendMessage("\"" + args[0] + "\" is not a valid world.");
				return true;
			}
		}
		
		if (commandLabel.equalsIgnoreCase("newday"))  {
			if (sender instanceof Player){
				if (!plugin.Permissions.has((Player) sender, "daycycles.time")) {
					sender.sendMessage(ChatColor.RED + "You don't have permission to use that command.");
					return true;
				}
			}
			
			if (!(plugin.worldHasTime(world))){
				sender.sendMessage("Time is supported in \"" + world.getName() + "\".");
				return true;
			}
				
			
			plugin.cycles.put(world.getName(), 0);
			world.setTime(0);
			
			for (Player player : world.getPlayers())
				player.sendMessage(ChatColor.GOLD + "Skipped to the next day cycle.");
			
			if (sender instanceof Player){
				System.out.println(((Player) sender).getDisplayName() + " skipped \"" + world.getName() + "\" to the next day cycle.");
			}else{
				System.out.println("Skipped \"" + world.getName() + "\" to the next day cycle.");
			}
			
		}
		
		if (commandLabel.equalsIgnoreCase("newnight"))  {
			if (sender instanceof Player){
				if (!plugin.Permissions.has((Player) sender, "daycycles.time")) {
					sender.sendMessage(ChatColor.RED + "You don't have permission to use that command.");
					return true;
				}
			}
			
			if (!(plugin.worldHasTime(world))){
				sender.sendMessage("Time is supported in \"" + world.getName() + "\".");
				return true;
			}
			
			plugin.cycles.put(world.getName(), plugin.days.get(world.getName())+1);
			world.setTime(13000);
			
			for (Player player : world.getPlayers())
				player.sendMessage(ChatColor.GOLD + "Skipped to the next night cycle.");
			
			if (sender instanceof Player){
				System.out.println(((Player) sender).getDisplayName() + " skipped \"" + world.getName() + "\" to the next night cycle.");
			}else{
				System.out.println("Skipped \"" + world.getName() + "\" to the next night cycle.");
			}
			
		}
		
		if (commandLabel.equalsIgnoreCase("setdays") && (sender instanceof Player))  {
			if (!plugin.Permissions.has((Player) sender, "daycycles.days")) {
				sender.sendMessage(ChatColor.RED + "You don't have permission to use that command.");
				return true;
			}
			
			if (args.length != 1){
				sender.sendMessage(ChatColor.RED + "Wrong number of arguments.");
				return true;
			}
			
			int newDays;
			
			try{
    			newDays = Integer.parseInt(args[0]);
    		}catch(Exception e){
    			sender.sendMessage(ChatColor.RED + args[0] + " is not a valid integer.");
    			return true;
    		}
			
			plugin.setDays(world, newDays);
			
			plugin.cycles.put(world.getName(), 0);
			world.setTime(0);
			
			for (Player player : world.getPlayers())
				player.sendMessage(ChatColor.GOLD + "There are now " + plugin.days.get(world.getName()) + " days before a night.");
			
			System.out.println("There are now " + plugin.days.get(world.getName()) + " in \"" + world.getName() + "\" before a night.");
			
		}else if (commandLabel.equalsIgnoreCase("setdays") && (!(sender instanceof Player)))  {
			if (args.length != 2){
				sender.sendMessage(ChatColor.RED + "Wrong number of arguments.");
				return true;
			}
			
			int newDays;
			
			try{
    			newDays = Integer.parseInt(args[1]);
    		}catch(Exception e){
    			sender.sendMessage(ChatColor.RED + args[1] + " is not a valid integer.");
    			return true;
    		}
			
			plugin.setDays(world, newDays);
			
			plugin.cycles.put(world.getName(), 0);
			world.setTime(0);
			
			for (Player player : world.getPlayers())
				player.sendMessage(ChatColor.GOLD + "There are now " + plugin.days.get(world.getName()) + " days before a night.");
			
			System.out.println("There are now " + plugin.days.get(world.getName()) + " in \"" + world.getName() + "\" before a night.");
		}
		
		return true;
	}

}
