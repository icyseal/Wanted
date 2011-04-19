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
			try { new File(rLog.dataFile).createNewFile(); }catch(Exception e){ outputConsole("Could not create save file."); }
			rLog.setup();
		}else{
			rLog.loadData();
		}
		
		

		PluginDescriptionFile pdfFile = this.getDescription();
		outputConsole( " version [" + pdfFile.getVersion() + "] enabled." );

	}
	public boolean onCommand(CommandSender sender, Command cmd, String name,
			String[] args) {
		if(sender instanceof Player && args.length > 0){
			Player p = (Player)sender;

			if(Permissions.getHandler().has(p, "wanted.file") && (args[0].equalsIgnoreCase("file"))){
				if( args.length==3){
					reportMgr.fileReport(args[1],args[2],p);

				}else{
					sendM(p,ChatColor.RED + "[Wanted]Improper amount of arguments.");
				}

			}
			if(Permissions.getHandler().has(p, "wanted.read") && (args[0].equalsIgnoreCase("read"))){
				if(args.length==2){
					reportMgr.listAllReports(1, p);
				}else if(args.length>2){
					sendM(p,ChatColor.RED + "[Wanted]Too many arguements.");
				}else{
					reportMgr.listAllReports(Integer.parseInt(args[1]), p);
				}

			}
			if(Permissions.getHandler().has(p, "wanted.read") && (args[0].equalsIgnoreCase("get"))){
				if (args.length==3){
					reportMgr.getPReports( args[1], p, Integer.parseInt(args[2]));
				}else{
					sendM(p,ChatColor.RED+"[Wanted]Improper arguments used.");
				}

			}
			if(Permissions.getHandler().has(p, "wanted.manage") && (args[0].equalsIgnoreCase("drop"))){
				if(args.length!=2){
					if(reportMgr.reports.containsKey(Integer.parseInt(args[1]))){
						sendM(reportMgr.reports.get(Integer.parseInt(args[1])).target,ChatColor.BLUE+"[Wanted]A report against you has been dropped.");
						sendM(p,ChatColor.BLUE+"[Wanted]You dropped the report.");
						reportMgr.reports.remove(Integer.parseInt(args[1]));
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
						p.teleport(reportMgr.reports.get(Integer.parseInt(args[1])).L);
					}
				}else{
					sendM(p,ChatColor.RED+"[Wanted]Improper arguments used.");
				}
			}
		}
		return false;
	}


}

class reportManager{

	Main plugin;
	public boolean cool;
	public int coolT;
	public ArrayList<Player> blocked = new ArrayList<Player>();
	public HashMap<Integer, report> reports = new HashMap<Integer, report>();

	public reportManager(Main instance){

		plugin = instance;

	}

	public int reportidCount=0;


	public void fileReport(String name, String reason, Player caller){
		if(reason.equalsIgnoreCase("grief") ||reason.equalsIgnoreCase("griefing") ||reason.equalsIgnoreCase("griefer")){

			reports.put(reportidCount, new report(plugin.getServer().getPlayer(name), 1, caller,caller.getLocation(), reportidCount));
			Main.outputConsole("Report filed. By:" + caller.getName()+"; Against:"+name+"; Reason:"+reason);caller.sendMessage(ChatColor.BLUE+"[Wanted] Report filed.");reportidCount++;
			plugin.sendM(plugin.getServer().getPlayer(name),ChatColor.RED + "A report has been filed by " + caller.getName()+" against you  because:"+reason);
			if(cool)
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new renew(caller,this), coolT);
		}else if(reason.equalsIgnoreCase("thief") ||reason.equalsIgnoreCase("stealing")){
			reports.put(reportidCount, new report(plugin.getServer().getPlayer(name), 2, caller,caller.getLocation(), reportidCount));
			Main.outputConsole("Report filed. By:" + caller.getName()+"; Against:"+name+"; Reason:"+reason);caller.sendMessage(ChatColor.BLUE+"[Wanted] Report filed.");reportidCount++;
			plugin.sendM(plugin.getServer().getPlayer(name),ChatColor.RED + "A report has been filed by " + caller.getName()+" against you  because:"+reason);
			if(cool)
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new renew(caller,this), coolT);
		}else if(reason.equalsIgnoreCase("assult") ||reason.equalsIgnoreCase("pvp") ||reason.equalsIgnoreCase("attack")){
			reports.put(reportidCount, new report(plugin.getServer().getPlayer(name), 3, caller,caller.getLocation(), reportidCount));
			Main.outputConsole("Report filed. By:" + caller.getName()+"; Against:"+name+"; Reason:"+reason);caller.sendMessage(ChatColor.BLUE+"[Wanted] Report filed.");reportidCount++;
			plugin.sendM(plugin.getServer().getPlayer(name),ChatColor.RED + "A report has been filed by " + caller.getName()+" against you  because:"+reason);
			if(cool)
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new renew(caller,this), coolT);
		}else if(reason.equalsIgnoreCase("trespassing")){
			reports.put(reportidCount, new report(plugin.getServer().getPlayer(name), 4, caller,caller.getLocation(), reportidCount));
			Main.outputConsole("Report filed. By:" + caller.getName()+"; Against:"+name+"; Reason:"+reason);caller.sendMessage(ChatColor.BLUE+"[Wanted] Report filed.");reportidCount++;
			plugin.sendM(plugin.getServer().getPlayer(name),ChatColor.RED + "A report has been filed by " + caller.getName()+" against you  because:"+reason);
			if(cool)
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new renew(caller,this), coolT);
		}else{
			plugin.sendM(caller,"No such offense.");
		}

	}

	public String prReport( int reportID){
		

		if(reports.containsKey(reportID)){
			report r = reports.get(reportID);

			String ret="";

			ret = "From: "+r.caller.getName()+";";
			ret = ret + "Against: "+r.target.getName()+";";
			ret = ret + "ID: " + reportID + ";";
			switch(r.reason){
			case 1: ret = ret + "Reason:Griefing;";break;
			case 2: ret = ret + "Reason:Stealing;";break;
			case 3: ret = ret + "Reason:Assult;";break;
			case 4: ret = ret + "Reason:Tresspassing;";break;
			default:ret = ret + "Reason:Other;";break;
			}
			return ret;

		}
		return null;
	}
	public void listAllReports( int page, Player p){

		if ( page-1 <= (reports.size()-1)/7 && page > 0){
			p.sendMessage(ChatColor.RED+"Reports list: Page "+ (page) + "/" + ((reports.size()-1)/7+1));
			int i =0;
			for(Integer I : reports.keySet()){

				if((page-1) * 7 <= i && i < (page) * 7){
					p.sendMessage(ChatColor.GREEN+prReport(I));
				}
				i++;
			}
		}
	}
	public void getPReports(String name, Player p, int page){
		int total=0;
		for(Integer I : reports.keySet()){
			if(reports.get(I).target.getName().equalsIgnoreCase(name)){
				total++;
			}
		}
		if ( page-1 <= (total-1)/7 && page > 0){
			if(total>7)
				p.sendMessage(ChatColor.RED+"Reports list: Page "+ (page) + "/" + ((reports.size()-1)/7+1));
			int i =0;
			for(Integer I : reports.keySet()){
				if(reports.get(I).target.getName().equalsIgnoreCase(name)){
					if((page-1) * 7 <= i && i < (page) * 7){
						p.sendMessage(ChatColor.GREEN+prReport(I));
					}
					i++;
				}
			}
		}
	}

}

class report {
	Player caller, target;
	Location L;
	int reason, idNum;
	boolean solved;
	public report(Player player, int i, Player caller2, Location location,
			int reportidCount) {
		caller = caller2; target = player;
		reason=i; L = location; idNum=reportidCount;
		solved = false;
	}
	public report(Player player, int i, Player caller2, Location location,
			int reportidCount, boolean slv) {
		caller = caller2; target = player;
		reason=i; L = location; idNum=reportidCount;
		solved = slv;
	}


}
class renew implements Runnable{

	reportManager rm;
	Player pl;
	public renew(Player p, reportManager rmt){
		pl=p;
		rm=rmt;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub

	}
}
