package org.springframework.plugin.core.support;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.plugin.core.OrderAwarePluginRegistry;
import org.springframework.plugin.core.Plugin;
import org.springframework.plugin.core.config.EnablePluginRegistries;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Integration test for {@link OrderAwarePluginRegistry}.
 * 
 * @author Oliver Gierke
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class OrderAwarePluginRegistryIntegrationTest {

	@Configuration
	@EnablePluginRegistries(TestPlugin.class)
	static class Config {

		@Bean
		public FirstImplementation firstImplementation() {
			return new FirstImplementation();
		}

		@Bean
		public SecondImplementation secondImplementation() {
			return new SecondImplementation();
		}

		@Bean
		public ThirdImplementation thirdImplementation() {
			return new ThirdImplementation();
		}
	}

	@Autowired
	ApplicationContext context;

	@Autowired
	FirstImplementation first;
	@Autowired
	SecondImplementation second;
	@Autowired
	ThirdImplementation third;

	@Autowired
	OrderAwarePluginRegistry<TestPlugin, String> registry;

	@Test
	public void considersJdkProxiedOrderedImplementation() {

		List<TestPlugin> plugins = registry.getPlugins();

		assertThat(plugins, Matchers.hasSize(3));
		assertThat(plugins.get(0), is((TestPlugin) second));
		assertThat(plugins.get(1), is((TestPlugin) third));
		assertThat(plugins.get(2), is((TestPlugin) first));

		plugins = registry.reverse().getPlugins();

		assertThat(plugins.get(2), is((TestPlugin) second));
		assertThat(plugins.get(1), is((TestPlugin) third));
		assertThat(plugins.get(0), is((TestPlugin) first));
	}

	private static interface TestPlugin extends Plugin<String> {

	}

	@Order(5)
	private static class FirstImplementation implements TestPlugin {

		@Override
		public boolean supports(String delimiter) {

			return true;
		}
	}

	@Order(1)
	private static class SecondImplementation implements TestPlugin {

		@Override
		public boolean supports(String delimiter) {

			return true;
		}
	}

	private static class ThirdImplementation implements TestPlugin, Ordered {

		@Override
		public int getOrder() {
			return 3;
		}

		@Override
		public boolean supports(String delimiter) {
			return true;
		}
	}
}
