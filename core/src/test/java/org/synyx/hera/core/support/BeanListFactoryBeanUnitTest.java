package org.synyx.hera.core.support;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;


/**
 * Unit test for {@link BeanListFactoryBean}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class BeanListFactoryBeanUnitTest {

    private BeanListFactoryBean<Ordered> factory;
    private ApplicationContext context;


    @Before
    public void setUp() {

        context = createNiceMock(ApplicationContext.class);

        factory = new BeanListFactoryBean<Ordered>();
        factory.setApplicationContext(context);
        factory.setType(Ordered.class);
    }


    @Test
    public void regardsOrderOfBeans() throws Exception {

        // They shall be switched in the result.
        Ordered first = getOrdered(5);
        Ordered second = getOrdered(0);

        Map<String, Ordered> beans = new HashMap<String, Ordered>();
        beans.put("first", first);
        beans.put("second", second);

        expect(context.getBeansOfType(Ordered.class)).andReturn(beans)
                .anyTimes();
        replay(context);

        Object result = factory.getObject();
        assertTrue(result instanceof List<?>);

        List<Ordered> members = type(result);

        assertEquals(0, members.indexOf(second));
        assertEquals(1, members.indexOf(first));
    }


    @Test
    public void returnsEmptyListIfNoBeansFound() throws Exception {

        expect(context.getBeansOfType(Ordered.class)).andReturn(
                new HashMap<String, Ordered>());
        replay(context);

        Object result = factory.getObject();
        assertTrue(result instanceof List<?>);

        List<Ordered> members = type(result);
        assertTrue(members.isEmpty());
    }


    @SuppressWarnings("unchecked")
    private <T> List<T> type(Object list) {

        return (List<T>) list;
    }


    /**
     * Returns an {@link Ordered} with the given order.
     * 
     * @param order
     * @return
     */
    public Ordered getOrdered(final int order) {

        return new Ordered() {

            public int getOrder() {

                return order;
            }
        };
    }
}
