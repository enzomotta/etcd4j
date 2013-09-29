package com.enzomotta.etcd4j;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class EtcdClient {


    private URI baseUri;

	public EtcdClient(URI baseUri) {
        String uri = baseUri.toString();
        if (!uri.endsWith("/")) {
            uri += "/";
            baseUri = URI.create(uri);
        }
        this.baseUri = baseUri;
    }

    /**
     * Retrieves a key. Returns null if not found.
     */
    public EtcdResponse get(String key) {
    	CloseableHttpClient httpclient = HttpClients.createDefault();
        URI uri = buildKeyUri("v1/keys", key, "");
        HttpGet httpget = new HttpGet(uri);
        CloseableHttpResponse response = null;
        try {
        	response = httpclient.execute(httpget);
        	System.out.println(response.getStatusLine().getStatusCode());
        	HttpEntity entity = response.getEntity();
            if (entity != null) {
                long len = entity.getContentLength();
                if (len != -1 && len < 2048) {
                    System.out.println(EntityUtils.toString(entity));
                } else {
                    // Stream content out
                	return null;
                }
            }
        } catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            try {
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        EtcdResponse result = null;
		return result;
    }

    /**
     * Deletes the given key
     */
    public EtcdResponse delete(String key) {
        URI uri = buildKeyUri("v1/keys", key, "");
        HttpDelete request = new HttpDelete(uri);

        return null;
    }

    /**
     * Sets a key to a new value
     */
    public EtcdResponse set(String key, String value) {
        return set(key, value, null);
    }

    /**
     * Sets a key to a new value with an (optional) ttl
     */

    public EtcdResponse set(String key, String value, Integer ttl) {
        return null;
    }

    /**
     * Sets a key to a new value, if the value is a specified value
     */
    public EtcdResponse cas(String key, String prevValue, String value) {
        return null;
    }

    /**
     * Watches the given subtree
     */
    public Future<EtcdResponse> watch(String key) {
        return watch(key, null);
    }

    /**
     * Watches the given subtree
     */
    public Future<EtcdResponse> watch(String key, Long index) {
        URI uri = buildKeyUri("v1/watch", key, "");

        HttpPost request = new HttpPost(uri);

        return null;
    }

    /**
     * Gets the etcd version
     */
    public String getVersion() {
        URI uri = baseUri.resolve("version");

        HttpGet request = new HttpGet(uri);

        return null;
    }

    private EtcdResponse set0(String key, List<BasicNameValuePair> data, int... expectedErrorCodes) {
        URI uri = buildKeyUri("v1/keys", key, "");

        HttpPost request = new HttpPost(uri);

        return null;
    }

    public List<EtcdResponse> listChildren(String key) {
        URI uri = buildKeyUri("v1/keys", key, "/");
        HttpGet request = new HttpGet(uri);

        return null;
    }

    

    private URI buildKeyUri(String prefix, String key, String suffix) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        if (key.startsWith("/")) {
            key = key.substring(1);
        }
        for (String token : key.split("/")) {
            sb.append("/");
            sb.append(urlEscape(token));
        }
        sb.append(suffix);

        URI uri = baseUri.resolve(sb.toString());
        return uri;
    }

    protected static String urlEscape(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException();
        }
    }
   
}
