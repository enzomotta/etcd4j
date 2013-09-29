package com.enzomotta.etcd4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

public class EtcdResponseParser {

    JsonFactory f = new JsonFactory();

    public EtcdResponse parseJSON(String json) {

	EtcdResponse response = null;
	try {
	    JsonParser jp = f.createJsonParser(json);
	    response = parseEtcdResponse(jp);
	    jp.close(); // ensure resources get cleaned up timely and properly
	} catch (Exception e) {
	    throw new IllegalStateException("Error parsing response from etcd", e);
	}

	return response;
    }

    public List<EtcdResponse> parseJSONArray(String json) {

	List<EtcdResponse> list = null;
	try {
	    JsonParser jp = f.createJsonParser(json);
	    list = new ArrayList<EtcdResponse>();
	    if (jp.nextToken() == JsonToken.START_ARRAY) {
		boolean iterate = true;
		do {
		    EtcdResponse response = parseEtcdResponse(jp);
		    if (response != null) {
			list.add(response);
		    } else {
			iterate = false;
		    }
		} while (iterate);

	    } else {
		list.add(parseEtcdResponse(jp));
	    }
	    jp.close(); // ensure resources get cleaned up timely and properly
	} catch (Exception e) {
	    throw new IllegalStateException("Error parsing response from etcd", e);
	}

	return list;
    }

    private EtcdResponse parseEtcdResponse(JsonParser jp) throws IOException, JsonParseException {
	EtcdResponse response = null;
	if (jp.nextToken() == JsonToken.START_OBJECT) {
	    response = new EtcdResponse();
	    while (jp.nextToken() != JsonToken.END_OBJECT) {
		String fieldname = jp.getCurrentName();
		jp.nextToken(); // move to value, or START_OBJECT/START_ARRAY
		if ("action".equals(fieldname)) { // contains an object
		    response.action = jp.getText();
		} else if ("key".equals(fieldname)) {
		    response.key = jp.getText();
		} else if ("value".equals(fieldname)) {
		    response.value = jp.getText();
		} else if ("prevValue".equals(fieldname)) {
		    response.prevValue = jp.getText();
		} else if ("newKey".equals(fieldname)) {
		    response.newKey = jp.getCurrentToken() == JsonToken.VALUE_TRUE;
		} else if ("index".equals(fieldname)) {
		    response.index = jp.getLongValue();
		} else if ("expiration".equals(fieldname)) {
		    response.expiration = jp.getText();
		} else if ("ttl".equals(fieldname)) {
		    response.ttl = jp.getIntValue();
		} else if ("errorCode".equals(fieldname)) {
		    response.errorCode = jp.getIntValue();
		} else if ("message".equals(fieldname)) {
		    response.message = jp.getText();
		} else if ("cause".equals(fieldname)) {
		    response.cause = jp.getText();
		} else if ("dir".equals(fieldname)) {
		    response.dir = jp.getCurrentToken() == JsonToken.VALUE_TRUE;
		} else {
		    throw new IllegalStateException("Unrecognized field '" + fieldname + "'!");
		}
	    }
	}
	return response;
    }

}
