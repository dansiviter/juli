/*
 * Copyright 2021 Daniel Siviter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.dansiviter.juli.cdi;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;

import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test for {@link LogExtension}.
 */
@EnableAutoWeld
@AddExtensions(LogExtension.class)
@ExtendWith(MockitoExtension.class)
class LogExtensionTest {
	@Inject
	Event<TestEvent> event;
	@Inject
	CdiLog log;

	@Test
	void log(@Mock Handler handler) {
		Logger.getLogger("").addHandler(handler);

		this.log.doLog();

		var recordCaptor = ArgumentCaptor.forClass(LogRecord.class);
		verify(handler).publish(recordCaptor.capture());

		var record = recordCaptor.getValue();
		assertEquals(java.util.logging.Level.INFO, record.getLevel());
		assertEquals("Hello", record.getMessage());
		assertEquals("uk.dansiviter.juli.cdi.LogExtensionTest", record.getLoggerName());
		assertEquals("uk.dansiviter.juli.cdi.LogExtensionTest", record.getSourceClassName());
		assertEquals("log", record.getSourceMethodName());
	}

	@Test
	void log_injectConstructor(TestBean bean) {
		assertThat(bean.getConstructor(), notNullValue());
	}

	@Test
	void log_injectField(TestBean bean) {
		assertThat(bean.getField(), notNullValue());
		assertThat(bean.getField(), sameInstance(bean.getConstructor()));
	}

	@Test
	void log_injectMethod(TestBean bean) {
		event.fire(new TestEvent());
		assertThat(bean.getMethod(), notNullValue());
		assertThat(bean.getMethod(), sameInstance(bean.getConstructor()));
	}

	void log_cdi() {
		CdiLog log = CDI.current().select(CdiLog.class).get();
		assertThat(log, notNullValue());
	}

	@ApplicationScoped
	static class TestBean {
		private final CdiLog constructor;
		@Inject
		private CdiLog field;
		private CdiLog method;

		@Inject
		TestBean(CdiLog log) {
			this.constructor = log;
		}

		void log(@Observes TestEvent e, CdiLog log) {
			this.method = log;
		}

		CdiLog getConstructor() {
			return this.constructor;
		}

		CdiLog getField() {
			return this.field;
		}

		CdiLog getMethod() {
			return this.method;
		}
	}

	static class TestEvent { }
}
