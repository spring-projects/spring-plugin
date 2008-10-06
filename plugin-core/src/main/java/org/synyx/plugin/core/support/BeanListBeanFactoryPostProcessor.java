package org.synyx.plugin.core.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ListFactoryBean;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.util.StringUtils;
import org.synyx.plugin.core.PluginRegistry;


/**
 * @author Oliver Gierke - gierke@synyx.de
 */
public class BeanListBeanFactoryPostProcessor implements
        BeanFactoryPostProcessor {

    private static final Log log =
            LogFactory.getLog(BeanListBeanFactoryPostProcessor.class);

    private Map<String, Class<?>> lists = new HashMap<String, Class<?>>();


    /**
     * Setter to inject required plugin registry configuration. The map's keys
     * will be used as bean ids for the resulting {@link PluginRegistry}
     * instances. These registry instances will contain all beans having the
     * given type.
     * 
     * @param registryMap
     */
    public void setLists(Map<String, Class<?>> lists) {

        this.lists = lists;
    }


    /*
     * (non-Javadoc)
     * 
     * @seeorg.springframework.beans.factory.config.BeanFactoryPostProcessor#
     * postProcessBeanFactory
     * (org.springframework.beans.factory.config.ConfigurableListableBeanFactory
     * )
     */
    public void postProcessBeanFactory(
            ConfigurableListableBeanFactory beanFactory) throws BeansException {

        if (!(beanFactory instanceof BeanDefinitionRegistry)) {
            throw new IllegalArgumentException(
                    "Given beanFactory is not a BeanDefinitionRegistry!");
        }

        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;

        // Do for each required wrapper
        for (String wrapperName : lists.keySet()) {

            Class<?> requiredType = lists.get(wrapperName);

            // Find implementations of the component type
            List<RuntimeBeanReference> beans =
                    getBeanReferencesOfType(beanFactory, requiredType);

            registerListBeanDefinition(wrapperName, requiredType, beans,
                    registry);
        }
    }


    /**
     * Registers a {@link ListFactoryBean} containing the given beans under the
     * given id. Allows subclasses to modify or wrap the actually registered
     * bean definition by calling
     * {@link #wrapListBeanDefinition(BeanDefinition)} with the bean definition
     * of the {Ïlinl {@link ListFactoryBean}.
     * 
     * @param id
     * @param beans
     * @param registry
     */
    protected <T> void registerListBeanDefinition(String id, Class<T> type,
            List<RuntimeBeanReference> beans, BeanDefinitionRegistry registry) {

        // Create ListFactory bean definition
        BeanDefinitionBuilder listDefinitionBuilder =
                BeanDefinitionBuilder.rootBeanDefinition(ListFactoryBean.class);

        // Set the implementations as source for the wrapper
        listDefinitionBuilder.addPropertyValue("sourceList", beans);

        BeanDefinition beanDefinition =
                wrapListBeanDefinition(getSourcedBeanDefinition(listDefinitionBuilder));

        if (log.isDebugEnabled()) {
            log.debug(String.format(
                    "Registering bean '%s' of type %s containing %s!", id,
                    beanDefinition.getBeanClassName(), beans.toString()));
        }

        // Register wrapper under defined name
        registry.registerBeanDefinition(id, beanDefinition);
    }


    /**
     * Template method to allow subclasses to wrap the list bean definition into
     * another bean definition that will actually be registered. The default
     * implementation simply returns the given bean definition.
     * 
     * @param beanDefinition
     * @return
     */
    protected BeanDefinition wrapListBeanDefinition(
            BeanDefinition beanDefinition) {

        return beanDefinition;
    }


    /**
     * Returns a {@link BeanDefinitionBuilder}s {@link BeanDefinition} setting
     * the current {@link PluginRegistryBeanFactoryPostProcessor} as source.
     * 
     * @param builder
     * @return
     */
    protected BeanDefinition getSourcedBeanDefinition(
            BeanDefinitionBuilder builder) {

        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        beanDefinition.setSource(this);

        return beanDefinition;
    }


    /**
     * Returns bean references to beans with the given type.
     * 
     * @param beanFactory
     * @param type
     * @return
     */
    @SuppressWarnings("unchecked")
    protected List<RuntimeBeanReference> getBeanReferencesOfType(
            ConfigurableListableBeanFactory beanFactory, Class<?> type) {

        List<String> beanNames = getBeanCandidates(beanFactory, type);

        List<RuntimeBeanReference> beanReferences =
                new ManagedList(beanNames.size());

        for (String beanName : beanNames) {

            RuntimeBeanReference reference = new RuntimeBeanReference(beanName);
            reference.setSource(this);

            beanReferences.add(reference);
        }

        return (List<RuntimeBeanReference>) beanReferences;
    }


    /**
     * Looks up candidate beans for the given type from the {@link BeanFactory}.
     * Does <em>not</em> use Spring type lookup facilities as this initializes
     * {@link FactoryBean}s eagerly breaking possible registered
     * {@link BeanPostProcessor}s.
     * <p>
     * We traverse the entire {@link BeanFactory} hierarchy to lookup beans.
     * 
     * @param beanFactory
     * @param type
     * @return
     */
    private List<String> getBeanCandidates(
            ConfigurableListableBeanFactory beanFactory, Class<?> type) {

        List<String> candidates = new ArrayList<String>();

        // None given or most top bean factory reached?
        if (null == beanFactory) {
            return candidates;
        }

        String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();

        for (String beanName : beanDefinitionNames) {

            BeanDefinition beanDefinition =
                    beanFactory.getBeanDefinition(beanName);

            // No bean class available at all
            if (!StringUtils.hasText(beanDefinition.getBeanClassName())) {
                continue;
            }

            try {
                Class<?> beanClass =
                        Class.forName(beanDefinition.getBeanClassName());

                if (type.isAssignableFrom(beanClass)) {
                    candidates.add(beanName);
                }

            } catch (ClassNotFoundException e) {
                continue;
            }
        }

        // Traverse parent bean factories
        if (beanFactory.getParentBeanFactory() instanceof ConfigurableListableBeanFactory) {
            candidates.addAll(getBeanCandidates(
                    (ConfigurableListableBeanFactory) beanFactory
                            .getParentBeanFactory(), type));
        }

        return candidates;
    }
}
