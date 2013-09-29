package com.enzomotta.etcd4j;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class EtcdClient {

    private URI baseUri;

    private EtcdResponseParser parser = new EtcdResponseParser();

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
	URI uri = buildKeyUri("v1/keys", key, "");
	HttpGet httpget = new HttpGet(uri);
	return sendRequest(httpget);
    }

    /**
     * Deletes the given key
     */
    public EtcdResponse delete(String key) {
	URI uri = buildKeyUri("v1/keys", key, "");
	HttpDelete request = new HttpDelete(uri);
	return sendRequest(request);
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
	return testAndSet(key, null, value, ttl);

    }

    /**
     * Sets a key to a new value, if the value is a specified value
     */
    public EtcdResponse testAndSet(String key, String prevValue, String value, Integer ttl) {
	try {
	    URI uri = buildKeyUri("v1/keys", key, "");

	    List<BasicNameValuePair> data = new ArrayList<BasicNameValuePair>();
	    data.add(new BasicNameValuePair("value", value));
	    if (ttl != null) {
		data.add(new BasicNameValuePair("ttl", Integer.toString(ttl)));
	    }

	    if (prevValue != null) {
		data.add(new BasicNameValuePair("prevValue", prevValue));
	    }

	    HttpPost request = new HttpPost(uri);

	    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(data, "UTF_8");

	    request.setEntity(entity);

	    return sendRequest(request);
	} catch (Exception e) {
	    // TODO throw an exception
	    return null;
	}
    }

    /**
     * Lists a dir
     */
    @SuppressWarnings("unchecked")
    public List<EtcdResponse> listDir(String key) {
	URI uri = buildKeyUri("v1/keys", key, "/");
	HttpGet request = new HttpGet(uri);
	return (List<EtcdResponse>) sendRequest(request, true);
    }

    /**
     * Watches the given dir
     */
    public EtcdResponse watch(String key) {
	return watch(key, null);
    }

    /**
     * Watches the given dir
     */
    public EtcdResponse watch(String key, Long index) {

	URI uri = buildKeyUri("v1/watch", key, "");
	HttpPost request = new HttpPost(uri);
	
	try {
	    List<BasicNameValuePair> data = new ArrayList<BasicNameValuePair>();
	    data.add(new BasicNameValuePair("index", Long.toString(index)));
	    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(data, "UTF_8");
	    request.setEntity(entity);
	    return sendRequest(request);
	} catch (ParseException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return null;
    }

    private EtcdResponse sendRequest(HttpUriRequest httpRequest) {
	return (EtcdResponse) sendRequest(httpRequest, false);
    }

    private Object sendRequest(HttpUriRequest httpRequest, boolean isDir) {
	CloseableHttpClient httpclient = HttpClients.createDefault();
	CloseableHttpResponse response = null;
	try {
	    response = httpclient.execute(httpRequest);
	    if (response.getStatusLine().getStatusCode() == 200) {
		HttpEntity entity = response.getEntity();
		if (entity != null) {
		    if (isDir) {
			return parser.parseJSONArray(EntityUtils.toString(entity));
		    } else {
			return parser.parseJSON(EntityUtils.toString(entity));
		    }
		}
	    } else {
		// TODO some http error occured, throw an exception
	    }
	} catch (Exception e) {
	    // TODO throw an exception
	    return null;
	} finally {
	    try {
		response.close();
	    } catch (IOException e) {
	    }
	}
	return response;
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

    private static String urlEscape(String s) {
	try {
	    return URLEncoder.encode(s, "UTF-8");
	} catch (UnsupportedEncodingException e) {
	    throw new IllegalStateException();
	}
    }

}
