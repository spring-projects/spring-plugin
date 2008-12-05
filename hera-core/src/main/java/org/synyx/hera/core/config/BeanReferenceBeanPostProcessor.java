package org.synyx.hera.core.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.BeanReference;


/**
 * {@link BeanPostProcessor} to handle {@link BeanReference} beans and replacing
 * it by its reference target.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class BeanReferenceBeanPostProcessor implements BeanPostProcessor,
        BeanFactoryAware {

    private BeanFactory beanFactory;


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org
     * .springframework.beans.factory.BeanFactory)
     */
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {

        this.beanFactory = beanFactory;
    }


    /*
     * (non-Javadoc)
     * 
     * @seeorg.springframework.beans.factory.config.BeanPostProcessor#
     * postProcessAfterInitialization(java.lang.Object, java.lang.String)
     */
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {

        // Skip non RuntimeBeanReferences
        if (!(bean instanceof BeanReference)) {
            return bean;
        }

        BeanReference reference = (BeanReference) bean;

        return beanFactory.getBean(reference.getBeanName());
    }


    /*
     * (non-Javadoc)
     * 
     * @seeorg.springframework.beans.factory.config.BeanPostProcessor#
     * postProcessBeforeInitialization(java.lang.Object, java.lang.String)
     */
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {

        return bean;
    }
}