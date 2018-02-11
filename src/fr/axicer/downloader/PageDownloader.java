package fr.axicer.downloader;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class PageDownloader extends Thread{
	
	private String term, page;
	
	public PageDownloader(String term, String page) {
		this.term = term;
		this.page = page;
	}
	
	@Override
	public void run() {
		super.run();
		//create the JSON parser for this page
		JSONParser parser = new JSONParser();
		//setting thread name for logging
		String threadPrefix = "[Thread " + Thread.currentThread().getId() + " ] > ";
        try {       
        	//getting json data
            URL jsonURL = new URL("https://e621.net/post/index.json?tags="+term+"&page="+page); // each pages contains 75 images
            URLConnection JSONconnection = jsonURL.openConnection();
            JSONconnection.setRequestProperty("User-Agent", "Axicer/e621-downloader/0.2 (by axicer)");
            BufferedReader in = new BufferedReader(new InputStreamReader(JSONconnection.getInputStream()));
            
            //reading data
            String inputLine;
            int count = 0;
            while ((inputLine = in.readLine()) != null) {               
                JSONArray a = (JSONArray) parser.parse(inputLine);
                
                // Loop through each item
                for (Object o : a) {
                	JSONObject obj = (JSONObject)o;
                	
                	//logging this file
                	String id = Long.toString((long)obj.get("id"));
                	App.logger.info(threadPrefix + "Downloading image " + id + " by " + obj.get("author") + "(" + Long.toString((long)obj.get("creator_id")) + ")...");
                	
                	//downloading and copying file
                    URL website = new URL((String) obj.get("file_url"));
                    ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                    FileOutputStream fos = new FileOutputStream(App.folder+"/"+id+"."+(String)obj.get("file_ext"));
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                    fos.close();
                    
                    //more logging
                    count++;
                    App.logger.info(threadPrefix + "Downloaded.");
                }
            }
            //when all pages are downloaded then log copied datas
            App.logger.info(threadPrefix + "End of download, downloaded " + count + " images, (page = "+page+")");
            in.close();
            this.interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }   
	}
}
