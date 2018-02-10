package fr.axicer.downloader;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

public class App {
	
	public static final int MAX_PAGE = 750;
	
	public static String term;
	public static String page;
	
	public static Logger logger = (Logger) LoggerFactory.getLogger(App.class.getSimpleName());
	
	public static void main(String[] args) {
		if(args.length > 2 || args.length == 0) {
			System.out.println("Usage: cmd <term> [page]");
		}else {
			if(args.length == 2) {
				term = args[0];
				page = args[1];
			}else if(args.length == 1) {
				term = args[0];
				page = "1";
			}
			
			if(Integer.valueOf(page) > MAX_PAGE)page = String.valueOf(MAX_PAGE);
			if(Integer.valueOf(page) < 1)page = "1";
			
			for(int i = 1 ; i <= Integer.valueOf(page) ; i++) {
				new PageDownloader(term, String.valueOf(i), "e621/").start();
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
