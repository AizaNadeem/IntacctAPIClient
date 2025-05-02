package com.intacct.xtera.api;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.intacct.xtera.config.ApiConstants;

public class IntacctApiClient {

	public static String sendPostRequest(String xmlRequest) throws Exception {
		try (CloseableHttpClient client = HttpClients.createDefault()) {
			HttpPost post = new HttpPost(ApiConstants.API_URL);
			post.setHeader("Content-Type", "application/xml");
			post.setEntity(new StringEntity(xmlRequest));
			try (CloseableHttpResponse response = client.execute(post)) {
				return EntityUtils.toString(response.getEntity());
			}
		}
	}
}
