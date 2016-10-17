package org.nalby.crawler.http;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public abstract class BasicHttp {
	private HttpClientContext httpClientContext;
	private CookieStore cookieStore;
	private final Map<String, String> requestHeader;
	private CloseableHttpClient httpClient;
	

	protected BasicHttp() {
		cookieStore = new BasicCookieStore();
		requestHeader = new HashMap<String, String>();
		RequestConfig.Builder builder = RequestConfig.custom();
		builder.setConnectTimeout(5000).setSocketTimeout(15000).setConnectionRequestTimeout(15000);
		httpClient = HttpClients.createDefault();
		httpClientContext = new HttpClientContext();
		httpClientContext.setCookieStore(cookieStore);
		httpClientContext.setRequestConfig(builder.build());
		requestHeader.put("Accept", "*/*");
		requestHeader.put("User-Agent",
				"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/49.0.2623.108 Chrome/49.0.2623.108 Safari/537.36");
	}

	protected String issueRequestAndSaveResponse(HttpUriRequest request) throws IOException {
		for (String k : requestHeader.keySet()) {
			request.setHeader(k, requestHeader.get(k));
		}
		try {
			HttpResponse currentResponse = httpClient.execute(request, httpClientContext);
			int statusCode = currentResponse.getStatusLine().getStatusCode();
			if (statusCode >= 200 && statusCode < 300) {
				HttpEntity entity = currentResponse.getEntity();
				String payload = entity != null ? EntityUtils.toString(entity) : "";
				return payload;
			}
		} catch (IOException e) {
			throw e;
		} finally {
			if (request instanceof HttpPost) {
				((HttpPost) request).releaseConnection();
				((HttpPost) request).completed();
			} else {
				((HttpGet) request).releaseConnection();
				((HttpGet) request).completed();
			}
		}
		return null;
	}

	protected void dumpPayloadToFile(String path, String payload) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(path);
			out.write(payload.getBytes());
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
			}
		}
	}
}
