package com.intacct.xtera.api;

import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.intacct.xtera.config.ApiConstants;

import org.apache.http.entity.StringEntity;

import java.io.IOException;

public class IntacctApiClient {

    public static String sendPostRequest(String xmlRequest) throws IOException {
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
