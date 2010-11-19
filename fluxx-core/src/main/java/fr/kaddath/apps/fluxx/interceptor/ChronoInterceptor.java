package fr.kaddath.apps.fluxx.interceptor;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

public class ChronoInterceptor {

	private static final Logger LOG = Logger.getLogger(ChronoInterceptor.class.getName());

	@AroundInvoke
	public Object intercept(InvocationContext ctx) throws java.lang.Exception {
		long timeMillis = System.currentTimeMillis();
		Object o = ctx.proceed();
		long currentTimeMillis = System.currentTimeMillis();
		String methodName = ctx.getClass().getName() + "." + ctx.getMethod().getName();
		LOG.log(Level.INFO, "{0} executed in {1} ms", new Object[] { methodName, currentTimeMillis - timeMillis });
		return o;
	}
}
