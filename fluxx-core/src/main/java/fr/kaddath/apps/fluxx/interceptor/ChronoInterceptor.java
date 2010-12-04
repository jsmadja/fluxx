package fr.kaddath.apps.fluxx.interceptor;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

public class ChronoInterceptor implements Serializable {

	private static final long serialVersionUID = -5649581046041044809L;

	private static final Logger LOG = Logger.getLogger(ChronoInterceptor.class.getName());

	private static final int DURATION_THRESHOLD_IN_MS = 100;

	@AroundInvoke
	public Object intercept(InvocationContext ctx) throws java.lang.Exception {
		long timeMillis = System.currentTimeMillis();
		Object o = ctx.proceed();
		long currentTimeMillis = System.currentTimeMillis();
		String methodName = ctx.getMethod().getDeclaringClass().getName() + "." + ctx.getMethod().getName();
		long duration = currentTimeMillis - timeMillis;
		if (duration >= DURATION_THRESHOLD_IN_MS) {
			String message = MessageFormat.format("{0} executed in {1} ms", new Object[] { methodName, duration });
			LOG.log(Level.INFO, message);
			// Logger.getLogger("1longrequest.fr.kaddath.apps.fluxx").severe("truc");
		}
		return o;
	}
}
