package fr.axicer.downloader;

import java.io.BufferedReader;
import java.io.File;
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
	
	private String term, page, folder;
	
	public PageDownloader(String term, String page, String folder) {
		this.term = term;
		this.page = page;
		this.folder = folder;
	}
	
	@Override
	public void run() {
		super.run();
		JSONParser parser = new JSONParser();
		File folderfile = new File(System.getProperty("user.dir")+File.separator+folder);
		if(!folderfile.exists())folderfile.mkdirs();
        try {         
            URL oracle = new URL("https://e621.net/post/index.json?tags="+term+"&page="+page); // each pages contains 75 images
            URLConnection yc = oracle.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            
            String inputLine;
            int count = 0;
            while ((inputLine = in.readLine()) != null) {               
                JSONArray a = (JSONArray) parser.parse(inputLine);
                
                // Loop through each item
                for (Object o : a) {
                	JSONObject obj = (JSONObject)o;
                	
                	String id = Long.toString((long)obj.get("id"));
                	App.logger.info("[Thread "
                						+ Thread.currentThread().getId()
                						+ " ] > Downloading image "
                						+ id
                						+ " by "
                						+ Long.toString((long)obj.get("creator_id"))
                						+ "("
                						+ obj.get("author")
                						+ ")...");
                	
                    URL website = new URL((String) obj.get("file_url"));
                    ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                    FileOutputStream fos = new FileOutputStream(folder+"/"+id+"."+(String)obj.get("file_ext"));
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                    fos.close();
                    
                    count++;
                    App.logger.info("[Thread "
    						+ Thread.currentThread().getId()
    						+ " ] > Downloaded.");
                }
            }
            App.logger.info("[Thread "
                				+ Thread.currentThread().getId()
                				+ " ] > End of download, downloaded "
                				+ count
                				+ " images, (page = "+page+")");
            in.close();
            this.interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }   
	}
}
