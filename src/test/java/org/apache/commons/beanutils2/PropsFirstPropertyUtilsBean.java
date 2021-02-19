/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.beanutils2;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * A PropertyUtilsBean which customises the behavior of the setNestedProperty
 * and getNestedProperty methods to look for simple properties in preference to
 * map entries.
 *
 */
public class PropsFirstPropertyUtilsBean {

	public static PropertyUtilsBean mockPropertyUtilsBean1()
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IllegalAccessException,
			NoSuchMethodException, NoSuchMethodException, InvocationTargetException, IllegalArgumentException {
		PropertyUtilsBean mockInstance = spy(PropertyUtilsBean.class);
		doAnswer((stubInvo) -> {
			Map<?, ?> bean = stubInvo.getArgument(0);
			String propertyName = stubInvo.getArgument(1);
			final PropertyDescriptor descriptor = mockInstance.getPropertyDescriptor(bean, propertyName);
			if (descriptor == null) {
				return bean.get(propertyName);
			}
			return mockInstance.getSimpleProperty(bean, propertyName);
		}).when(mockInstance).getPropertyOfMapBean(any(), any());
		doAnswer((stubInvo) -> {
			Map<String, Object> bean = stubInvo.getArgument(0);
			String propertyName = stubInvo.getArgument(1);
			Object value = stubInvo.getArgument(2);
			final PropertyDescriptor descriptor = mockInstance.getPropertyDescriptor(bean, propertyName);
			if (descriptor == null) {
				bean.put(propertyName, value);
			} else {
				mockInstance.setSimpleProperty(bean, propertyName, value);
			}
			return null;
		}).when(mockInstance).setPropertyOfMapBean(any(), any(), any());
		return mockInstance;
	}
}
