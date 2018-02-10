package fr.axicer.downloader;

import java.io.File;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

public class App {
	
	public static final int MAX_PAGE = 750;
	public static final int MIN_PAGE = 0;
	
	public static String term;
	public static String page;
	public static String folder;
	
	public static Logger logger = (Logger) LoggerFactory.getLogger("E621-Downloader");
	
	public static void main(String[] args) {
		//checking for correct args
		if(args.length > 2 || args.length == 0) {
			System.out.println("Usage: java -jar e621-downloader.jar <searchTerm> [pages]");
		}else {
			//setting args depending on cmd args
			if(args.length == 2) {
				term = args[0];
				page = args[1];
			}else if(args.length == 1) {
				term = args[0];
				page = String.valueOf(MIN_PAGE);
			}
			//setting foldername
			folder = term.replaceAll("/", "-")+"-Download/";
			
			//verifying page number
			if(Integer.valueOf(page) > MAX_PAGE) {
				logger.error("Max pages is 750 ! setting pages argument to 750.");
				page = String.valueOf(MAX_PAGE);
			}
			if(Integer.valueOf(page) < MIN_PAGE) {
				logger.error("Min pages is 0 ! setting pages argument to 0.");
				page = String.valueOf(MIN_PAGE);
			}
			
			//creating folder
			try{
				File folderfile = new File(System.getProperty("user.dir")+File.separator+folder);
				if(!folderfile.exists())folderfile.mkdirs();
			}catch(Exception e) {
				logger.warn("Exception creating folder at: "+System.getProperty("user.dir")+File.separator+folder+".");
				return;
			}
			
			//starting threads
			for(int i = 0 ; i <= Integer.valueOf(page) ; i++) {
				//starting each threads
				new PageDownloader(term, String.valueOf(i)).start();
				//informing about next launch (each threads need to be spaced by 500ms in order to follow e621.net API docs)
				try {
					if(i < Integer.valueOf(page)){
						logger.info("Started page "+i+" next in 500ms.");
						Thread.sleep(500);
					}else {
						logger.info("Started page "+i+".");
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
