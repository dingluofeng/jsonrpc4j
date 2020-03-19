package com.googlecode.jsonrpc4j.okhttp;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.remoting.support.UrlBasedRemoteAccessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.jsonrpc4j.ExceptionResolver;
import com.googlecode.jsonrpc4j.JsonRpcClient;
import com.googlecode.jsonrpc4j.JsonRpcClient.RequestListener;
import com.googlecode.jsonrpc4j.ReflectionUtil;

import okhttp3.OkHttpClient;

public class JsonOkhttpProxyFactoryBean extends UrlBasedRemoteAccessor
        implements MethodInterceptor, InitializingBean, FactoryBean<Object>, ApplicationContextAware {

    private Object proxyObject = null;
	private RequestListener requestListener = null;
	private ObjectMapper objectMapper = null;

    private OkHttpClient okHttpClient = null;

    private JsonRpcOkhttpClient jsonRpcClient = null;

	private Map<String, String> extraHttpHeaders = new HashMap<>();

	private ExceptionResolver exceptionResolver; 
	
	private ApplicationContext applicationContext;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();

		proxyObject = ProxyFactory.getProxy(getObjectType(), this);

        if (jsonRpcClient == null) {
			
			if (objectMapper == null && applicationContext != null && applicationContext.containsBean("objectMapper")) {
				objectMapper = (ObjectMapper) applicationContext.getBean("objectMapper");
			
			}
			if (objectMapper == null && applicationContext != null) {
				try {
					objectMapper = BeanFactoryUtils.beanOfTypeIncludingAncestors(applicationContext, ObjectMapper.class);
				} catch (Exception e) {
					logger.debug(e);
				}
			}
			
			if (objectMapper == null) {
				objectMapper = new ObjectMapper();
			}

			try {
                jsonRpcClient = new JsonRpcOkhttpClient(new URL(getServiceUrl()), objectMapper, okHttpClient,
                        new HashMap<String, String>());
                jsonRpcClient.setRequestListener(requestListener);
				
				if (exceptionResolver!=null) {
                    jsonRpcClient.setExceptionResolver(exceptionResolver);
				}
				
			} catch (MalformedURLException mue) {
				throw new RuntimeException(mue);
			}
			
		}

		ReflectionUtil.clearCache();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Method method = invocation.getMethod();
		if (method.getDeclaringClass() == Object.class && method.getName().equals("toString")) {
			return proxyObject.getClass().getName() + "@" + System.identityHashCode(proxyObject);
		}

		Type retType = (invocation.getMethod().getGenericReturnType() != null) ? invocation.getMethod().getGenericReturnType() : invocation.getMethod().getReturnType();
		Object arguments = ReflectionUtil.parseArguments(invocation.getMethod(), invocation.getArguments());

        return jsonRpcClient.invoke(invocation.getMethod().getName(), arguments, retType, extraHttpHeaders);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public Object getObject() {
		return proxyObject;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public Class<?> getObjectType() {
        return getServiceInterface();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSingleton() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * @param objectMapper the objectMapper to set
	 */
	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	/**
	 * @param extraHttpHeaders the extraHttpHeaders to set
	 */
	public void setExtraHttpHeaders(Map<String, String> extraHttpHeaders) {
		this.extraHttpHeaders = extraHttpHeaders;
	}

	/**
	 * @param requestListener the requestListener to set
	 */
	public void setRequestListener(JsonRpcClient.RequestListener requestListener) {
		this.requestListener = requestListener;
	}

	/**
     * @param okHttpClient
     *            external OkHttpClient
     */
    public void setOkHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
	}

    public void setJsonRpcOkhttpClient(JsonRpcOkhttpClient jsonRpcOkhttpClient) {
        this.jsonRpcClient = jsonRpcOkhttpClient;
	}

	public void setExceptionResolver(ExceptionResolver exceptionResolver) {
		this.exceptionResolver = exceptionResolver;
	}

	
	
}
