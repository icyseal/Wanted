package IcySeal.Wanted;

import java.io.*;
import java.util.Scanner;

import org.bukkit.ChatColor;
import org.bukkit.Location;




class reportLog{
	public Main plugin;
	public BufferedWriter out;
	public String dataFile = "plugins/Wanted/wanted.txt";
	
	reportLog(Main inst){
		plugin = inst;
	}

	public boolean loadData(){
		
		if(new File("plugins/Wanted/").mkdir()){

			plugin.outputConsole("Created wanted directory.");

		}
		boolean loadData = false;
		try {
			loadData = !(new File("plugins/Wanted/wanted.txt").createNewFile());
		} catch (IOException e) {
			plugin.outputConsole("Error upon creating data file...");
		}
		String line="";
		try {

			Scanner s = new Scanner(new File(dataFile));
			while(s.hasNextLine()){	

				line = s.nextLine();

				if(line.substring(0,1).equalsIgnoreCase("#")){
					String[] args = line.split("=");
					if(args[0].equalsIgnoreCase("#cool")){
						if(Integer.parseInt(args[1])==0){
							plugin.reportMgr.cool=false;
						}else{
							plugin.reportMgr.cool=true;
							plugin.reportMgr.coolT=Integer.parseInt(args[1]);
						}
					}else if(args[0].equalsIgnoreCase("#idCount")){
						if(Integer.parseInt(args[1])!=0){
							plugin.reportMgr.reportidCount=Integer.parseInt(args[1]);
						}
					}
				}else if(line.substring(0,1).equalsIgnoreCase("@")){
					String[] args = line.split(",");
					plugin.reportMgr.reports.put(Integer.parseInt(args[0]), new report(plugin.getServer().getPlayer(args[1]),Integer.parseInt(args[2]), plugin.getServer().getPlayer(args[3]),new Location(plugin.getServer().getWorld(args[4]),Double.parseDouble(args[5]),Double.parseDouble(args[6]),Double.parseDouble(args[7])), Integer.parseInt(args[0]),Boolean.parseBoolean(args[8])));
				}
			}
		} catch (FileNotFoundException e) {
			plugin.outputConsole("Error upon loading save data...");
		}


		return true;

	}

	public boolean saveData(){
		
		if(new File("plugins/Wanted/").mkdir()){

			plugin.outputConsole("Created wanted directory.");

		}
		boolean saveData = false;
		try {
			saveData = !(new File("plugins/Wanted/wanted.txt").createNewFile());
		} catch (IOException e) {
			plugin.outputConsole("Error upon creating data file...");
		}
		try {
			out = new BufferedWriter(new FileWriter(dataFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
		writeLine("#cool="+plugin.reportMgr.coolT);
		writeLine("#idCount="+plugin.reportMgr.reportidCount);
		String write="";
		for(Integer I : plugin.reportMgr.reports.keySet()){
			write ="@";
			write += I+",";
			write += plugin.reportMgr.reports.get(I).target.getName()+",";
			write += plugin.reportMgr.reports.get(I).reason+",";
			write += plugin.reportMgr.reports.get(I).caller.getName()+",";
			write += plugin.reportMgr.reports.get(I).caller.getWorld().getName()+",";
			write += plugin.reportMgr.reports.get(I).caller.getLocation().getBlockX()+",";
			write += plugin.reportMgr.reports.get(I).caller.getLocation().getBlockY()+",";
			write += plugin.reportMgr.reports.get(I).caller.getLocation().getBlockZ()+",";
			write += plugin.reportMgr.reports.get(I).solved;
			writeLine(write);
		}

		return true;
	}
	public boolean setup(){
		
		if(new File("plugins/Wanted/").mkdir()){

			plugin.outputConsole("Created wanted directory.");

		}
		boolean saveData = false;
		try {
			saveData = !(new File("plugins/Wanted/wanted.txt").createNewFile());
		} catch (IOException e) {
			plugin.outputConsole("Error upon creating data file...");
		}
		try {
			out = new BufferedWriter(new FileWriter(dataFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
		writeLine("#cool="+plugin.reportMgr.coolT);
		writeLine("#idCount="+plugin.reportMgr.reportidCount);
		return true;
	}
	public void writeLine(String str){

		try {

			out.write(str + "\r\n");

		} catch(Exception e){

			plugin.outputConsole("Error while writing line" + str);

		}

	}

}