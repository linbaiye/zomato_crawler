package org.nalby.crawler.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainPageCrawler extends BasicHttp implements Crawler {
	private final String BASE_URL = "https://www.zomato.com/sydney";
	private final static Logger LOGGER = LoggerFactory.getLogger(MainPageCrawler.class);
	public MainPageCrawler() {
		super();
	}
	
	private List<String> parsePayload(String payload) {
        Document doc = Jsoup.parse(payload);
        Elements anchors = doc.select("div[class=\"ui segment row\"] a");
        if (anchors.size() <= 0) {
        	LOGGER.error("Failed to parse payload.");
        	return null;
        }
        List<String> list = new ArrayList<String>(200);
        for (int i = 0; i < anchors.size(); i++) {
        	Element element = anchors.get(i);
        	LOGGER.debug(element.attr("href"));
        	list.add(new String(element.attr("href")));
        }
        return list;
	}

	public boolean crawl() {
		HttpGet get = new HttpGet(BASE_URL);
		try {
			String payload = issueRequestAndSaveResponse(get);
			dumpPayloadToFile("/tmp/mainPage.html", payload);
			parsePayload(payload);
		} catch (IOException e) {
			LOGGER.error("Failed to crawl main page.");
		}
		return false;
	}
}
