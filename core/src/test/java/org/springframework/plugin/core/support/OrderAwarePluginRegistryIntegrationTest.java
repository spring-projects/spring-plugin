package org.springframework.plugin.core.support;

import java.util.List;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.plugin.core.OrderAwarePluginRegistry;
import org.springframework.plugin.core.Plugin;
import org.springframework.plugin.core.support.AbstractTypeAwareSupport.BeansOfTypeTargetSource;

//@RunWith(SpringJUnit4ClassRunner.class)
public class OrderAwarePluginRegistryIntegrationTest {

	@Autowired
	ApplicationContext context;

	// @Test
	public void considersJdkProxiedOrderedImplementation() {
		BeansOfTypeTargetSource targetSource = new AbstractTypeAwareSupport.BeansOfTypeTargetSource(context,
				TestPlugin.class, false);

		ProxyFactory factory = new ProxyFactory(List.class, targetSource);
		List<TestPlugin> beans = (List<TestPlugin>) factory.getProxy();
		OrderAwarePluginRegistry<TestPlugin, String> registry = OrderAwarePluginRegistry.create(beans);

		List<TestPlugin> plugins = registry.getPlugins();
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
