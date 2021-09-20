package org.springframework.plugin.core.support;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Integration test for {@link OrderAwarePluginRegistry}.
 *
 * @author Oliver Gierke
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration
class OrderAwarePluginRegistryIntegrationTest {

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

	@Autowired ApplicationContext context;

	@Autowired FirstImplementation first;
	@Autowired SecondImplementation second;
	@Autowired ThirdImplementation third;

	@Autowired OrderAwarePluginRegistry<TestPlugin, String> registry;

	@Test
	void considersJdkProxiedOrderedImplementation() {

		List<TestPlugin> plugins = registry.getPlugins();

		assertThat(plugins).hasSize(3);
		assertThat(plugins.get(0)).isEqualTo(second);
		assertThat(plugins.get(1)).isEqualTo(third);
		assertThat(plugins.get(2)).isEqualTo(first);

		plugins = registry.reverse().getPlugins();

		assertThat(plugins.get(2)).isEqualTo(second);
		assertThat(plugins.get(1)).isEqualTo(third);
		assertThat(plugins.get(0)).isEqualTo(first);
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
