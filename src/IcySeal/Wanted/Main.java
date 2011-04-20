package IcySeal.Wanted;

import java.io.*;
import java.util.HashMap;
import java.util.Calendar;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.ArrayList;




import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{




	public static Permissions Permissions = null;
	public reportManager reportMgr = new reportManager(this);
	public reportLog rLog = new reportLog(this);



	public void sendM(Player p, String text){//errror message
		p.sendMessage(text);
	}

	public static void outputConsole(String output){
		System.out.println("[Wanted] "+output);
	}

	@Override
	public void onDisable() {
		rLog.saveData();
		outputConsole(" Reports saved.");

	}
	

	@Override
	public void onEnable() {
		if(getServer().getPluginManager().getPlugin("Permissions") == null){

			outputConsole("Permissions is not loaded, disabling.");
			getServer().getPluginManager().disablePlugin(this);

		} else {

			outputConsole("Permissions found, commencing load.");
			Permissions = (Permissions)getServer().getPluginManager().getPlugin("Permissions");

		}
		if(!new File(rLog.dataFile).exists()){
			//try { new File(rLog.dataFile).createNewFile(); }catch(Exception e){ outputConsole("Could not create save file."); }
			outputConsole("Running save file setup.");
			rLog.setup();
		}else{
			outputConsole("Found data file commencing load.");
			rLog.loadData();
		}



		PluginDescriptionFile pdfFile = this.getDescription();
		outputConsole("version [" + pdfFile.getVersion() + "] enabled." );

	}
	public boolean onCommand(CommandSender sender, Command cmd, String name,
			String[] args) {
		if(sender instanceof Player && args.length > 0){
			Player p = (Player)sender;
			if((cmd.getName().equalsIgnoreCase("wanted")||cmd.getName().equalsIgnoreCase("wa"))){
				
				
				if(Permissions.getHandler().has(p, "wanted.file") && (args[0].equalsIgnoreCase("file"))){
					if( args.length==3){
						reportMgr.fileReport(args[1],args[2],p.getName());

					}else{
						sendM(p,ChatColor.RED + "[Wanted]Improper amount of arguments.");
					}

				}
				
				
				
				if(Permissions.getHandler().has(p, "wanted.read") && (args[0].equalsIgnoreCase("read"))){
					if(reportMgr.reports.size()!=0){
						if(args.length==1){
							reportMgr.listAllReports(1, p);
						}else if(args.length>2){
							sendM(p,ChatColor.RED + "[Wanted]Too many arguements.");
						}else{
							reportMgr.listAllReports(Integer.parseInt(args[1]), p);
						}
					}else{
						sendM(p,ChatColor.RED + "[Wanted]No reports have been filed yet.");
					}
				}
				
				
				
				if(Permissions.getHandler().has(p, "wanted.read") && (args[0].equalsIgnoreCase("get"))){
					if(args.length==2){
						reportMgr.getPReports( args[1], p, 1);
					}else if(args.length>3){
						sendM(p,ChatColor.RED + "[Wanted]Too many arguements.");
					}else{
						reportMgr.getPReports( args[1], p, Integer.parseInt(args[2]));
					}
				}
				
				
				
				if(Permissions.getHandler().has(p, "wanted.manage") && (args[0].equalsIgnoreCase("drop"))){
					if(args.length!=2){
						if(reportMgr.reports.containsKey(Integer.parseInt(args[1]))){
							if(getServer().matchPlayer(reportMgr.reports.get(Integer.parseInt(args[1])).target).contains(reportMgr.reports.get(Integer.parseInt(args[1])).target)){
								sendM(getServer().getPlayer(reportMgr.reports.get(Integer.parseInt(args[1])).target),ChatColor.BLUE+"[Wanted]A report against you has been dropped.");
							}
							sendM(p,ChatColor.BLUE+"[Wanted]You dropped the report.");
							reportMgr.reports.remove(Integer.parseInt(args[1]));
						}else{
							sendM(p,ChatColor.RED+"[Wanted]No report exists with this ID.");
						}
					}else{
						sendM(p,ChatColor.RED+"[Wanted]Improper arguments used.");
					}

				}
				
				
				
				if(Permissions.getHandler().has(p, "wanted.manage") && (args[0].equalsIgnoreCase("resolve"))){
					if(args.length!=2){
						if(reportMgr.reports.containsKey(Integer.parseInt(args[1]))){
							if(!reportMgr.reports.get(Integer.parseInt(args[1])).solved){
								if(getServer().matchPlayer(reportMgr.reports.get(Integer.parseInt(args[1])).target).contains(reportMgr.reports.get(Integer.parseInt(args[1])).target)){
									sendM(getServer().getPlayer(reportMgr.reports.get(Integer.parseInt(args[1])).target),ChatColor.BLUE+"[Wanted]A report against you has been resolved.");
								}
								sendM(p,ChatColor.BLUE+"[Wanted]You resolved the report.");
								reportMgr.reports.get(Integer.parseInt(args[1])).solved=true;
							}
						}else{
							sendM(p,ChatColor.RED+"[Wanted]No report exists with this ID.");
						}
					}else{
						sendM(p,ChatColor.RED+"[Wanted]Improper arguments used.");
					}

				}
				
				
				
				if(Permissions.getHandler().has(p, "wanted.manage") && (args[0].equalsIgnoreCase("goto"))){
					if(args.length==2){
						if(reportMgr.reports.containsKey(Integer.parseInt(args[1]))){
							if(!reportMgr.gobackLocs.containsKey(p)){reportMgr.gobackLocs.put(p, p.getLocation());}
							p.teleport(reportMgr.reports.get(Integer.parseInt(args[1])).L);
						}
					}else{
						sendM(p,ChatColor.RED+"[Wanted]Improper arguments used.");
					}
				}
				
				
				
				if(Permissions.getHandler().has(p, "wanted.manage") && (args[0].equalsIgnoreCase("return"))){
					if(args.length==2){
						if(reportMgr.gobackLocs.containsKey(p)){
							p.teleport(reportMgr.gobackLocs.get(p));
							reportMgr.gobackLocs.remove(p);
						}else{
							sendM(p,ChatColor.RED+"[Wanted] You cannot use this command at this time.");
						}
					}else{
						sendM(p,ChatColor.RED+"[Wanted]Improper arguments used.");
					}
				}
				
				
				if( (Permissions.getHandler().has(p, "wanted.manage")) && (args[0].equalsIgnoreCase("ignore"))){
					if(!reportMgr.ignore.contains(p)){
						reportMgr.ignore.add(p.getName());
					}else{
						reportMgr.ignore.remove(p.getName());
					}
				}
				
				if( (Permissions.getHandler().has(p, "wanted.manage")) && (args[0].equalsIgnoreCase("save"))){
					rLog.saveData();
				}
				
				
				
				
			}
			if((cmd.getName().equalsIgnoreCase("respond") && (Permissions.getHandler().has(p, "wanted.manage")))){
				if(reportMgr.live!=null){
					if(!reportMgr.gobackLocs.containsKey(p)){reportMgr.gobackLocs.put(p, p.getLocation());}
					p.teleport(reportMgr.live.L);
				}else{
					sendM(p,ChatColor.RED+"[Wanted]There is no live report at the moment.");
				}
			}
		}
		return false;
	}


}
