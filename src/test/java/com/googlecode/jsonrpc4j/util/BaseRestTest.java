package com.googlecode.jsonrpc4j.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.googlecode.jsonrpc4j.spring.rest.JsonRpcRestClient;

public abstract class BaseRestTest {
	
	@Rule
	final public ExpectedException expectedEx = ExpectedException.none();
	private JettyServer jettyServer;
	
	@Before
	public void setup() throws Exception {
		this.jettyServer = createServer();
	}
	
	private JettyServer createServer() throws Exception {
        Class<?> service = service();
		if (service == null) return null;
		JettyServer jettyServer = new JettyServer(service);
		jettyServer.startup();
		return jettyServer;
	}
	
    protected abstract Class<?> service();
	
	protected JsonRpcRestClient getClient() throws MalformedURLException {
		return getClient(JettyServer.SERVLET);
	}
	
	protected JsonRpcRestClient getClient(final String servlet) throws MalformedURLException {
		return new JsonRpcRestClient(new URL(jettyServer.getCustomServerUrlString(servlet)));
	}
	
	protected JsonRpcRestClient getClient(final String servlet, RestTemplate restTemplate) throws MalformedURLException {
		return new JsonRpcRestClient(new URL(jettyServer.getCustomServerUrlString(servlet)), restTemplate);
	}

	protected JsonRpcHttpClient getHttpClient(boolean gzipRequests, boolean acceptGzipResponses) throws MalformedURLException {
		Map<String, String> header = new HashMap<>();
		return new JsonRpcHttpClient(new ObjectMapper(), new URL(jettyServer.getCustomServerUrlString(JettyServer.SERVLET)), header, gzipRequests, acceptGzipResponses);
	}
	
	@After
	public void teardown() throws Exception {
		jettyServer.stop();
	}
	
}
