# The smallest plugin system ever

## Preface

### Introduction

Building extensible architectures nowadays is a core principle to create maintainable applications. This is why fully fledged plugin environments like *OSGi* are so popular these days. Unfortunately the introduction of *OSGi* introduces a lot of complexity to projects.

Spring Plugin provides a more pragmatic approach to plugin development by providing the core flexibility of having plugin implementations extending a core system's functionality but of course not delivering core OSGi features like dynamic class loading or runtime installation and deployment of plugins. Although Spring Plugin thus is not nearly as powerful as OSGi, it serves a poor man's requirements to build a modular
extensible application.

### Context

-   You want to build an extensible architecture minimizing overhead as much as possible
-   You cannot use OSGi as fully fledged plugin architecture for whatever reasons
-   You want to express extensibility by providing dedicated plugin interfaces
-   You want to extend the core system by simply providing an implementation of the plugin interface bundled in a JAR file and available in the classpath
-   (You use Spring in your application)

The last point actually is not essential although Spring Plugin gains a
lot of momentum in collaborative use with Spring.

### Technologies

#### Spring

Spring is the de-facto standard application framework for Java applications. Its consistent programming model, easy configuration and wide support for all kinds of third party libraries makes it the first class citizen of application frameworks. Spring Plugin tightly integrates into Spring's component model and extends the core container
with some custom functionality.

## Core

### Introduction

Host system provides a plugin interface providers have to implement.
Core system is build to hold a container of instances of this interface
and works with them.

**Example 1.1. Basic example of plugin interface and host**

```java
/**
 * Interface contract for the providers to be implemented.
 */
public interface MyPluginInterface {
  public void bar();
}


/**
 * A host application class working with instances of the plugin
 * interface.
 */
public class HostImpl implements Host {

  private final List<MyPluginInterface> plugins;

  public HostImpl(List<MyPluginInterface> plugins) {
    Assert.notNull(plugins);
    this.plugins = plugins;
  }

  /**
   * Some business method actually working with the given plugins.
   */
  public void someBusinessMethod() {
    for (MyPluginInterface plugin : plugins) {
      plugin.bar();
    }
  }
}
```

This is the way you would typically construct a host component in general. Leveraging dependency injection via setters allows flexible usage in a variety of environments. Thus you could easily provide a factory class that is able to lookup `MyPluginInterface`
implementations from the classpath, instantiate them and inject them into `HostImpl`.

Using Spring as component container you could configure something like this:

**Example 1.2. Configuring HostImpl with Spring**

```xml
<bean id="host" class="com.acme.HostImpl">
  <property name="plugins">
    <list>
      <bean class="MyPluginImplementation" />
    </list>
  </property>
</bean>
```

This is pretty much well known to Spring developers and let's us face the wall that this is rather static. Everytime you want to add a new plugin implementation instance you have to modify configuration of the core. Let's see how we can get this dance a little more.

### Collecting Spring beans dynamically

With the `BeanListBeanFactory` Spring Plugin provides a Spring container extension, that allows to lookup beans of a given type in the current `ApplicationContext` and register them as list under a given name. Take a look at the configuration now:

**Example 1.3. Host and plugin configuration with Spring Plugin
support**

```xml
<import resource="classpath*:com/acme/**/plugins.xml" />

<bean id="host" class="com.acme.HostImpl">
  <property name="plugins" ref="plugins" />
</bean>

<bean class="org.springframework.plugin.support.BeanListBeanFactory">
  <property name="lists">
    <map>
      <entry key="plugins" value="org.acme.MyPluginInterface" />
    </map>
  </property>
</bean>
```

```xml
<!-- In a file called plugins.xml in the plugin project -->
<bean class="MyPluginImplementation" />
```

You can see that we include a wildcarded configuration file that allows plugin projects to easily contribute plugin implementations by declaring them as beans in configuration files matching the wildcarded path. If you use Spring 2.5 component scanning you don't have to use the import trick at all as Spring would detect the implementation automatically as long as it is annotated with `@Component`, `@Service` a.s.o.

The `BeanListBeanFactory` in turn allows registering a map of lists to be created, where the maps entry key is the id under which the list will be registered and the entry's value is the type to be looked up.

> #### Note
>
> The design of the `BeanListBeanFactory` might seem a little confusing at first
> (especially to set a map on a property named lists). This is due to the possibility to
> register more than one list to be looked up. We think about dropping this
> functionality for the sake of simplicity in future versions.

### A whole lotta XML - namespace to help!

Actually this already serves a lot of requirements we listed in [Section “Context”](#context). Nevertheless the amount of XML to be written is quite large. Furthermore it's rather not intuitive to configure a bean id as key, and a type as value. We can heavily shrink the XML required to a single line by providing a Spring namespace boiling configuration down to this:

**Example 1.4. Host configuration using the plugin namespace**

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
  xmlns:plugin="http://www.springframework.org/schema/plugin"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.springframework.org/schema/beans https://www.springframework.org/schema/beans/spring-beans.xsd
    http://www.springframework.org/schema/plugin https://www.springframework.org/schema/plugin/spring-plugin.xsd">

  <import resource="classpath*:com/acme/**/plugins.xml" />

  <bean id="host" class="com.acme.HostImpl">
    <property name="plugins" ref="plugins" />
  </bean>

  <plugin:list id="plugins" class="org.acme.MyPluginInterface" />
</beans>
```

Assuming you have added the namespace XSD into Eclipse and installed Spring IDE, you should get code completion on filling the class attribute.

### Using inner beans

The listing above features an indirection for the `plugin` bean definition. Defining the plugin list as top level bean can have advantages: you easily could place all plugin lists in a dedicated configuration file, presenting all application extension points in one single place. Nevertheless you also might choose to define the list directly in the property declaration:

**Example 1.5. Using inner bean definition**

```xml
<import resource="classpath*:com/acme/**/plugins.xml" />

<bean id="host" class="com.acme.HostImpl">
  <property name="plugins">
    <plugin:list class="org.acme.MyPluginInterface" />
  </property>
</bean>
```

This way you have a more compact configuration, paying the prica of tangling all extention points though possibly various config files.

#### Plugin beans

Using plain interfaces and `BeanListBeanFactory` offers an easy way to dynamically lookup beans in Spring environments. Nevertheless, very often you face the situation that you want to have dedicated access to a subset of all plugins, choose plugins by a given criteria or use a decent default plugin or the like. Thus we need a basic infrastructure interface for plugin interfaces to extend and a more sophisticated plugin container.

#### Plugin

Hera's central infrastructure interfacte is `Plugin<S>`, where `S` defines the delimiter type you want to let implementations decide on, whether they shall be invoked or not. Thus the plugin implementation have to implement `supports(S delimiter)` to come to the decision. Consider the following example:

**Example 1.6. Usage of Plugin interface**

```java
public enum ProductType {
  SOFTWARE, HARDWARE;
}

public interface ProductProcessor extends Plugin<ProductType> {
  public void process(Product product);
}
```

This design would allow plugin providers to implement `supports(ProductType productType)` to decide which product types they want to process and provide actual processing logic in `process(Product product)`.

#### PluginRegistry

Using a `List` as plugin container as well as the `Plugin` interface you can now select plugins supporting the given delimiter. To not reimplement the lookup logic for common
cases Spring Plugin provides a `PluginRegistry<T extends Plugin<S>, S>` interface that provides sophisticated methods to access certain plugins:

**Example 1.7. Usage of the PluginRegistry**

```java
PluginRegistry<ProductProcessor, ProductType> registry = SimplePluginRegistry.of(new FooImplementation());

// Returns the first plugin supporting SOFTWARE if available
Optional<ProductProcessor> plugin = registry.getPluginFor(ProductType.SOFTWARE);
// Returns the first plugin supporting SOFTWARE, or DefaultPlugin if none found
ProductProcessor plugin = registry.getPluginOrDefaultFor(ProductType.SOFTWARE, () -> new DefaultPlugin());
// Returns all plugins supporting HARDWARE, throwing the given exception if none found
List<ProductProcessor> plugin = registry.getPluginsFor(ProductType.HARDWARE, () -> new MyException("Damn!");
```

#### Configuration, XML namespace and @EnablePluginRegistries

Similar to the `BeanListBeanFactory` described in [Collecting Spring beans
dynamically](#core.beans-dynamically) Spring Plugin provides a `PluginRegistryBeanFactory` to automatically lookup beans of a dedicated type to be aggregated in a `PluginRegistry`. Note that the type has to be assignable to `Plugin` to let the registry work as expected.

Furthermore there is also an element in the namespace to shrink down configuration XML:

**Example 1.8. Using the XML namespace to configure a registry**

```xml
<plugin:registry id="plugins" class="com.acme.MyPluginInterface" />
```

As of version 0.8 creating a `PluginRegistry` can also be achieved using the `@EnablePluginRegistries` annotation:

```java
@Configuration
@EnablePluginRegistries(MyPluginInterface.class)
class ApplicationConfiguration { … }
```

This configuration snippet will register a `OrderAwarePluginRegistry` for `MyPluginInterface` within the `ApplicationContext` and thus make it available for injection into client beans. The registered bean will be named `myPluginInterfaceRegistry` so that it can be explicitly referenced on the client side using the `@Qualifier` annotation if necessary. The bean name can be customized using `@Qualifier` on the plugin interface definition.

### Ordering plugins

Declaring plugin beans sometimes it is necessary to preserve a certain order of plugins. Suppose you have a plugin host that already defines one plugin that shall always be executed after all plugins declared by extensions. Actually the Spring container typically returns beans in the order they were declared, so that you could import you wildcarded config files right before declaring the default plugin. Unfortunately the order of the beans is not contracted to be preserved for the Spring container. Thus we need a different solution.

Spring provides two ways to order beans. First, you can implement `Ordered` interface and implement `getOrder` to place a plugin at a certain point in the list. Secondly you can user the `@Order` annotation. For more information on ordering capabilities of Spring see the [section on this topic in the Spring reference documentation](https://docs.spring.io/spring/docs/3.1.x/javadoc-api/org/springframework/core/Ordered.html).

Using the Spring Plugin namespace you will get a `PluginRegistry` instance that is capable of preserving the order defined by the mentioned means. Using Spring Plugin
programmatically use `OrderAwarePluginRegistry`.

## Metadata

For plugin architectures it is essential to capture metadata information about plugin instances. A very core set of metadata (name, version) also serves as identifier of a plugin and thus can be used. The Spring Plugin metadata module provides support to capture metadata.

### Core concepts

The metadata module actually builds around two core interfaces, `PluginMetadata` and `MetadataProvider`:

**Example 2.1. Core concepts**

```java
public interface PluginMetadata {
  String getName();
  String getVersion();
}

public interface MetadataProvider {
  PluginMetadata getMetadata();
}
```

The `PluginMetadata` interface captures the required properties to define an identifiable plugin. This means, that implementations should ensure uniqueness through these two properties. With `SimplePluginMetadata` Spring Plugin provides a Java bean style class to capture metadata. Of course applications can and should provide extended metadata information according to their needs. The very narrow interface is only targeted at integrating the metadata concept with the `PluginRegistry` (see [the section called “PluginRegistry”](#core.plugin-registry)) without bothering developers with too much information required.

The `MetadataProvider` interface is to be used in application plugin interfaces to indicate that they can provide metadata. To ease plugin implementation we provide
`AbstractMetadataBasedPlugin` that uses the internal metadata to implement `supports(…)` method of `Plugin`. Extending this base class plugins with metadata as selection criteria can easily be build. This way you could store the metadata in user specific configuration files and use this to select a distinct plugin specific to a given user.

## Glossary


### O

OSGi

  * Open Services Gateway Initiative - a fully fledged plugin runtime environment on top of the Java VM - [https://en.wikipedia.org/wiki/OSGi](https://en.wikipedia.org/wiki/OSGi).

### X

XML

  * eXtensible Markup Language

XSD

  * Xml Schema Definition
