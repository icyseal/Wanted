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
			loadData = !(new File(dataFile).createNewFile());
		} catch (IOException e) {
			plugin.outputConsole("Error upon creating data file...");
		}
		String line="";
		try {

			int max = 0;
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
					}else if(args[0].equalsIgnoreCase("#offenses")){
						plugin.reportMgr.offenses = args[1].split(",");
					}
				}else if(line.substring(0,1).equalsIgnoreCase("@")){
					String[] args = line.split(",");
					plugin.reportMgr.reportidCount = Math.max(plugin.reportMgr.reportidCount, Integer.parseInt(args[0].substring(1))+1);
					plugin.reportMgr.reports.put(Integer.parseInt(args[0].substring(1)), new report(args[1],args[2], args[3],new Location(plugin.getServer().getWorld(args[4]),Double.parseDouble(args[5]),Double.parseDouble(args[6]),Double.parseDouble(args[7])), Integer.parseInt(args[0].substring(1)),Boolean.parseBoolean(args[8])));
				}
			}
			if(plugin.reportMgr.reportidCount!=0){
				plugin.outputConsole("Currently "+plugin.reportMgr.reports.size()+" reports.");
			}else{
				plugin.outputConsole("There are no reports.");
			}
			s.close();
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
			saveData = !(new File(dataFile).createNewFile());
		} catch (IOException e) {
			plugin.outputConsole("Error upon creating data file...");
		}
		try {
			out = new BufferedWriter(new FileWriter(dataFile));
			writeLine("#cool="+plugin.reportMgr.coolT);
			String write="#offenses=";
			for(String s: plugin.reportMgr.offenses){
				write = write + s + ",";
			}
			writeLine(write.substring(0, write.length()-1));
			
			write="";
			for(report r : plugin.reportMgr.reports.values()){
				
				write ="@";
				write += r.idNum+",";
				write = write + r.target+",";
				write = write + r.offense+",";
				write = write + r.caller+",";
				write = write + r.L.getWorld().getName()+",";
				write = write + r.L.getBlockX()+",";
				write = write + r.L.getBlockY()+",";
				write = write + r.L.getBlockZ()+",";
				write = write + r.solved;
				writeLine(write);
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		

		return true;
	}
	public boolean setup(){
		
		if(new File("plugins/Wanted/").mkdir()){

			plugin.outputConsole("Created wanted directory.");

		}
		boolean saveData = false;
		try {
			saveData = !(new File(dataFile).createNewFile());
		} catch (IOException e) {
			plugin.outputConsole("Error upon creating data file...");
		}
		try {
			out = new BufferedWriter(new FileWriter(dataFile));
			writeLine("#cool="+plugin.reportMgr.coolT);
			writeLine("#offenses=Griefing,Assault,Trespassing");
			
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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