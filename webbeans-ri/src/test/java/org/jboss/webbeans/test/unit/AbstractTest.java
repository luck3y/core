package org.jboss.webbeans.test.unit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import javax.webbeans.Production;
import javax.webbeans.Standard;

import org.jboss.webbeans.ManagerImpl;
import org.jboss.webbeans.bean.AbstractClassBean;
import org.jboss.webbeans.bean.EnterpriseBean;
import org.jboss.webbeans.bean.NewEnterpriseBean;
import org.jboss.webbeans.bean.NewSimpleBean;
import org.jboss.webbeans.bean.ProducerFieldBean;
import org.jboss.webbeans.bean.ProducerMethodBean;
import org.jboss.webbeans.bean.SimpleBean;
import org.jboss.webbeans.context.DependentContext;
import org.jboss.webbeans.test.mock.MockBootstrap;
import org.jboss.webbeans.test.mock.MockEjbDescriptor;
import org.jboss.webbeans.test.mock.MockWebBeanDiscovery;
import org.testng.annotations.BeforeMethod;

public class AbstractTest
{
   
   protected abstract static class RunInDependentContext 
   {
      
      protected void setup()
      {
         AbstractTest.activateDependentContext();
      }
      
      protected void cleanup()
      {
         AbstractTest.deactivateDependentContext();
      }
      
      public final void run() throws Exception
      {
         try
         {
            setup();
            execute();
         }
         finally
         {
            cleanup();
         }
      }
      
      protected abstract void execute() throws Exception;
      
   }
   
   protected static final int BUILT_IN_BEANS = 3;
   
   protected ManagerImpl manager;
   protected MockBootstrap webBeansBootstrap;

   public static boolean visited = false;

   @BeforeMethod
   public void before() throws Exception
   {
      webBeansBootstrap = new MockBootstrap();
      manager = webBeansBootstrap.getManager();
      manager.setEnabledDeploymentTypes(getEnabledDeploymentTypes());
   }

   protected List<Class<? extends Annotation>> getEnabledDeploymentTypes()
   {
      return getDefaultDeploymentTypes();
   }
   
   @SuppressWarnings("unchecked")
   protected final List<Class<? extends Annotation>> getDefaultDeploymentTypes()
   {
      return Arrays.asList(Standard.class, Production.class);
   }
   
   protected <T> EnterpriseBean<T> createEnterpriseBean(Class<T> clazz)
   {
      manager.getEjbDescriptorCache().add(MockEjbDescriptor.of(clazz));
      return EnterpriseBean.of(clazz, manager);
   }
   
   protected <T> NewEnterpriseBean<T> createNewEnterpriseBean(Class<T> clazz)
   {
      manager.getEjbDescriptorCache().add(MockEjbDescriptor.of(clazz));
      return NewEnterpriseBean.of(clazz, manager);
   }
   
   protected <T> NewSimpleBean<T> createNewSimpleBean(Class<T> clazz)
   {
      return NewSimpleBean.of(clazz, manager);
   }

   protected <T> SimpleBean<T> createSimpleBean(Class<T> clazz)
   {
      return SimpleBean.of(clazz, manager);
   }
   
   protected <T> ProducerMethodBean<T> createProducerMethod(Method method, AbstractClassBean<?> declaringBean)
   {
      return ProducerMethodBean.of(method, declaringBean, manager);
   }
   
   protected <T> ProducerFieldBean<T> createProducerField(Field field, AbstractClassBean<?> declaringBean)
   {
      return ProducerFieldBean.of(field, declaringBean, manager);
   }
   
   private static void activateDependentContext()
   {
      DependentContext.INSTANCE.setActive(true);
   }
   
   private static void deactivateDependentContext()
   {
      DependentContext.INSTANCE.setActive(false);
   }
   
   protected void deployBeans(Class<?>... classes)
   {
      MockBootstrap bootstrap = new MockBootstrap();
      bootstrap.setWebBeanDiscovery(new MockWebBeanDiscovery(classes));
      bootstrap.boot();
      manager = bootstrap.getManager();
   }
}
