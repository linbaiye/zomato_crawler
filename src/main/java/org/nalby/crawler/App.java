package org.nalby.crawler;

import org.nalby.crawler.http.Crawler;
import org.nalby.crawler.http.MainPageCrawler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) {
    	ApplicationContext context = new ClassPathXmlApplicationContext("application.xml");
    	Crawler crawl = context.getBean(MainPageCrawler.class);
    	crawl.crawl();
    }
}
