package fr.fluxx.core.interceptor;

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;

import fr.fluxx.core.interceptor.ChronoInterceptor;


public class ChronoInterceptorTest {

	@Test
	public void test() throws SecurityException, NoSuchMethodException {
		String methodName = "toString";
		long duration = 5;
		
		ChronoInterceptor interceptor = new ChronoInterceptor();
		Method method = String.class.getMethod(methodName);
		String message = interceptor.buildChronoMessage(method, duration);
		Assert.assertEquals("java.lang.String.toString executed in "+duration+" ms", message);
	}
}
