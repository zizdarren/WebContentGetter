package main;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;


public class WebContentGetter {
	
	public static void main(String args []) throws IOException, InterruptedException {
		//variables
		String page = "https://www.powerball.net/archive/";
		HashMap<Integer, Integer> ballHM = new HashMap<Integer, Integer>(69);
		HashMap<Integer, Integer> powerballHM = new HashMap<Integer, Integer>(26);
		int total = 0;
		final int beginYear = 2016, endYear = 2020;
		int year = beginYear;
		long time = System.currentTimeMillis();
		
		System.out.println("Initializing...");
		
		//initialize hash map
		for(int i= 1; i<=69; i++) {
			ballHM.put(i, 0);
			//if(i<=26) //this was intent to set the powerball to 26, but some year ago powerball had more than 26 number
			powerballHM.put(i, 0);
		}
		
		//initialize file writing
		File lottoStatistic = new File(System.getProperty("user.home"), "Desktop/lotto_statistic.txt");
		lottoStatistic.createNewFile();
		FileWriter fw = new FileWriter(lottoStatistic);
		
		//setting for getting web content
		HttpClient client = HttpClient.newHttpClient();
		
		
		System.out.println("Getting content please wait...");
		//BufferedReader br;
		while(year<=endYear) {
			//System.out.println("Year: "+year);
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(page+year)).GET().build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			
			String content = response.body();
			
			BufferedReader br = new BufferedReader(new StringReader(content));
			
			String line;
			
			//select ball and powerball
			while((line = br.readLine()) != null) {
				if(line.contains("<div class=\"ball\">")) {
					int ti = Integer.parseInt(getBallNum(line));
					ballHM.replace(ti, ballHM.get(ti)+1);
					//System.out.println("ball: "+ti+ "   time: " +ballHM.get(ti));
				}else if(line.contains("<div class=\"powerball\">")) {
					int ti = Integer.parseInt(getBallNum(line));
					powerballHM.replace(ti, powerballHM.get(ti)+1);
					//System.out.println("powerball: "+ti+ "   time: " +powerballHM.get(ti));
					total++;
				}
			}
			
			br.close();
			
			/*
			System.out.println("total draw: "+ total);
			printTable("ball", total, ballHM);
			printTable("ball", total, powerballHM);
			*/
			
			year++;
		}
		
		fw.write("total draw: "+ total+"\n");
		writeTable("ball", total, ballHM, fw);
		writeTable("powerball", total, powerballHM, fw);
		
		fw.close();
		time = System.currentTimeMillis() - time;
		System.out.println("Done, Time used: " + time/1000 +" sec");
	}
	
	public static String getBallNum(String s) {
		char[] c = s.toCharArray();
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<c.length; i++) {
			if(c[i]>=48 && c[i]<=57) {
				sb.append(c[i]);
			}
		}
		return sb.toString();
	}
	
	public static void printTable(String s, int total, HashMap<Integer, Integer> hs) {
		System.out.println(s+" table: ");
		for(int i=1; i<hs.size()+1; i++){
			System.out.println("ball: "+i+"   appear times: "+hs.get(i)+"   percentage: "+(float)(hs.get(i)/(float)(total)));
		}
	}
	
	public static void writeTable(String s, int total, HashMap<Integer, Integer> hs, FileWriter fw) throws IOException {
		fw.write(s+" table: \n");
		for(int i=1; i<hs.size()+1; i++){
			fw.write("ball: "+i+"   appear times: "+hs.get(i)+"   percentage: "+(float)(hs.get(i)/(float)(total))+"\n");
		}
	}
}
