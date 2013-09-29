package com.enzomotta.etcd4j;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class EtcdResponseParserTest {

    @Test
    public void testSet() {
	EtcdResponseParser parser = new EtcdResponseParser();
	String json = "{\"action\":\"SET\",\"key\":\"/message\",\"value\":\"Hello world\",\"newKey\":true,\"index\":3}";
	EtcdResponse response = parser.parseJSON(json);
	assertEquals("action", response.action, "SET");
	assertEquals("key", response.key, "/message");
	assertEquals("value", response.value, "Hello world");
	assertEquals("newKey", response.newKey, true);
	assertEquals("index", response.index, 3);
    }

    @Test
    public void testGet() {
	EtcdResponseParser parser = new EtcdResponseParser();
	String json = "{\"action\":\"GET\",\"key\":\"/message\",\"value\":\"Hello world\",\"index\":3}";
	EtcdResponse response = parser.parseJSON(json);
	assertEquals("action", response.action, "GET");
	assertEquals("key", response.key, "/message");
	assertEquals("value", response.value, "Hello world");
	assertEquals("index", response.index, 3);
    }

    @Test
    public void testTestAndSet() {
	EtcdResponseParser parser = new EtcdResponseParser();
	String json = "{\"action\":\"SET\",\"key\":\"/message\",\"prevValue\":\"Hello world\",\"value\":\"Hello etcd\",\"index\":4}";
	EtcdResponse response = parser.parseJSON(json);
	assertEquals("action", response.action, "SET");
	assertEquals("key", response.key, "/message");
	assertEquals("prevValue", response.prevValue, "Hello world");
	assertEquals("value", response.value, "Hello etcd");
	assertEquals("index", response.index, 4);
    }

    @Test
    public void testDelete() {
	EtcdResponseParser parser = new EtcdResponseParser();
	String json = "{\"action\":\"DELETE\",\"key\":\"/message\",\"prevValue\":\"Hello etcd\",\"index\":5}";
	EtcdResponse response = parser.parseJSON(json);
	assertEquals("action", response.action, "DELETE");
	assertEquals("key", response.key, "/message");
	assertEquals("prevValue", response.prevValue, "Hello etcd");
	assertEquals("index", response.index, 5);
    }

    @Test
    public void testSetWithTTL() {
	EtcdResponseParser parser = new EtcdResponseParser();
	String json = "{\"action\":\"SET\",\"key\":\"/foo\",\"value\":\"bar\",\"newKey\":true,\"expiration\":\"2013-07-11T20:31:12.156146039-07:00\",\"ttl\":4,\"index\":6}";
	EtcdResponse response = parser.parseJSON(json);
	assertEquals("action", response.action, "SET");
	assertEquals("key", response.key, "/foo");
	assertEquals("value", response.value, "bar");
	assertEquals("newKey", response.newKey, true);
	assertEquals("expiration", response.expiration, "2013-07-11T20:31:12.156146039-07:00");
	assertEquals("ttl", response.ttl, (Integer) 4);
	assertEquals("index", response.index, 6);
    }

    @Test
    public void testError() {
	EtcdResponseParser parser = new EtcdResponseParser();
	String json = "{\"errorCode\":101,\"message\":\"The given PrevValue is not equal to the value of the key\",\"cause\":\"TestAndSet: one!=two\"}";
	EtcdResponse response = parser.parseJSON(json);
	assertEquals("errorCode", response.errorCode, (Integer) 101);
	assertEquals("message", response.message, "The given PrevValue is not equal to the value of the key");
	assertEquals("cause", response.cause, "TestAndSet: one!=two");
    }

    @Test
    public void testListDir() {
	EtcdResponseParser parser = new EtcdResponseParser();
	String json = "[{\"action\":\"GET\",\"key\":\"/foo/foo\",\"value\":\"barbar\",\"index\":10},{\"action\":\"GET\",\"key\":\"/foo/foo_dir\",\"dir\":true,\"index\":10}]";
	List<EtcdResponse> list = parser.parseJSONArray(json);
	EtcdResponse response = list.get(0);
	assertEquals("action", response.action, "GET");
	assertEquals("key", response.key, "/foo/foo");
	assertEquals("value", response.value, "barbar");
	assertEquals("index", response.index, 10);
	response = list.get(1);
	assertEquals("action", response.action, "GET");
	assertEquals("key", response.key, "/foo/foo_dir");
	assertEquals("dir", response.dir, true);
	assertEquals("index", response.index, 10);
    }

}
