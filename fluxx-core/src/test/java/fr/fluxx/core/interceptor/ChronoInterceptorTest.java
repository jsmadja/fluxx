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
