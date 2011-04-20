package IcySeal.Wanted;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

class reportManager{

	Main plugin;
	public boolean cool;
	public int coolT = 0;
	public report live;

	public String[] offenses = {"Griefing","Assault","Trespassing"};

	public ArrayList<String> blocked = new ArrayList<String>();
	public ArrayList<String> ignore = new ArrayList<String>();

	public HashMap<Integer, report> reports = new HashMap<Integer, report>();
	public HashMap<Player, Location> gobackLocs = new HashMap<Player, Location>();


	public reportManager(Main instance){

		plugin = instance;

	}

	public int reportidCount=0;


	public void fileReport(String name, String offense, String caller){
		if(!blocked.contains(caller)){
			boolean correct = false;
			for(String s: offenses){
				if(s.equalsIgnoreCase(offense)){
					reports.put(reportidCount, new report(name, offense, caller,plugin.getServer().getPlayer(caller).getLocation(), reportidCount));
					Main.outputConsole("Report filed. By:" + caller+"; Against:"+name+"; Reason:"+offense);plugin.getServer().getPlayer(caller).sendMessage(ChatColor.BLUE+"[Wanted] Report filed.");reportidCount++;
					plugin.sendM(plugin.getServer().getPlayer(name),ChatColor.RED + "[Wanted]A report has been filed by " + caller+" against you  because:"+offense);
					correct = true;
				}
			}
			
				

			if(correct){
				live = reports.get(reportidCount-1);
				Player[] L = plugin.getServer().getOnlinePlayers();
				for(Player p : L){
					if((plugin.Permissions.getHandler().has(p, "wanted.manage")) && (!ignore.contains(p))){
						plugin.sendM(p, ChatColor.RED + "[Wanted]A report has been filed by " + caller +" against "+name+". /respond ?");
					}
				}
				if(cool){
					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new renew(caller,this), coolT);
					blocked.add(caller);
				}
			}else{
				plugin.sendM(plugin.getServer().getPlayer(caller),ChatColor.RED +"[Wanted]No such offense.");
			}
		}
	}
	public String prReport( int reportID){


		if(reports.containsKey(reportID)){
			report r = reports.get(reportID);

			String ret="";

			ret = "From: "+r.caller+";";
			ret = ret + "Against: "+r.target+";";
			ret = ret + "ID: " + reportID + ";";
			ret = ret + "Reason: "+r.offense+";";
			return ret;

		}
		return null;
	}
	public void listAllReports( int page, Player p){

		if ( page-1 <= (reports.size()-1)/7 && page > 0){
			p.sendMessage(ChatColor.YELLOW+"[Wanted]Reports list: Page "+ (page) + "/" + ((reports.size()-1)/7+1));
			int i =0;
			for(Integer I : reports.keySet()){

				if((page-1) * 7 <= i && i < (page) * 7){
					if(reports.get(I).solved){
						p.sendMessage(ChatColor.GREEN+prReport(I));
					}else{
						p.sendMessage(ChatColor.RED+prReport(I));
					}
				}
				i++;
			}
		}
	}
	public void getPReports(String name, Player p, int page){
		int total=0;
		for(Integer I : reports.keySet()){
			if(reports.get(I).target.equalsIgnoreCase(name)){
				total++;
			}
		}
		if(total != 0){
			if ( page-1 <= (total-1)/7 && page > 0){
				if(total>7)
					p.sendMessage(ChatColor.YELLOW+"[Wanted]Reports list: Page "+ (page) + "/" + ((reports.size()-1)/7+1));
				int i =0;
				for(Integer I : reports.keySet()){
					if(reports.get(I).target.equalsIgnoreCase(name)){
						if((page-1) * 7 <= i && i < (page) * 7){
							if(reports.get(I).solved){
								p.sendMessage(ChatColor.GREEN+prReport(I));
							}else{
								p.sendMessage(ChatColor.RED+prReport(I));
							}
						}
						i++;
					}
				}
			}
		}else{
			plugin.sendM(p, ChatColor.RED+"[Wanted]"+name+" has not been reported.");
		}
	}

}
class report {
	String caller, target, offense;
	Location L;
	int idNum;
	boolean solved;
	public report(String player, String i, String caller2, Location location,
			int reportidCount) {
		caller = caller2; target = player;
		offense=i; L = location; idNum=reportidCount;
		solved = false;
	}
	public report(String player, String i, String caller2, Location location,
			int reportidCount, boolean slv) {
		caller = caller2; target = player;
		offense=i; L = location; idNum=reportidCount;
		solved = slv;
	}


}
class renew implements Runnable{

	reportManager rm;
	String pl;
	public renew(String caller, reportManager rmt){
		pl=caller;
		rm=rmt;
	}
	@Override
	public void run() {
		rm.blocked.remove(pl);

	}
}
