/**
 * Copyright (C) 2010 Julien SMADJA <julien.smadja@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.kaddath.apps.fluxx.interceptor;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            String message = MessageFormat.format("{0} executed in {1} ms", new Object[]{methodName, duration});
            LOG.log(Level.INFO, message);
        }
        return o;
    }
}
