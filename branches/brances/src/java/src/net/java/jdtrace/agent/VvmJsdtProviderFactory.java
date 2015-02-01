package net.java.jdtrace.agent;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import com.sun.tracing.ProviderFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author root
 */
public class VvmJsdtProviderFactory {

    private static ProviderFactory factory;
    public static VvmJsdtProvider provider = null;
    private static final Logger logger = Logger.getLogger(VvmJsdtProviderFactory.class.getName());

    static {
        try {
            //logger.info("in factory static initializer, was loaded by " + VvmJsdtProviderFactory.class.getClassLoader());
            init();
        } catch (Throwable t) {
            //Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, t);
        }
        //System.out.println(provider.getClass().getName() + " class loaded is: " + provider.getClass().getClassLoader().toString());
    }
    /*
     static {
     try {
     factory = ProviderFactory.getDefaultFactory();
     //DummyProvider dprovider = factory.createProvider(DummyProvider.class);
     System.out.println("MyProviderFactory initializing");
     provider = factory.createProvider(MyProvider.class);
     } catch (Exception e) {
     e.printStackTrace();
     }
     //provider = factory.createProvider(MyProvider.class);
     }
     
     */

    public static synchronized void init() {
        //if (provider != null) return;
        try {
            factory = ProviderFactory.getDefaultFactory();
            //DummyProvider dprovider = factory.createProvider(DummyProvider.class);
            provider = (VvmJsdtProvider) factory.createProvider(VvmJsdtProvider.class);
            // debug test provider
            //Thread.sleep(30000);
            //provider.method_entry(17, "aa", 2, "bb", 2, "cc", 2);
            //dummy = new DummyProvider();
            //dummy.methodEntry(17, "aa", 2, "bb", 2, "cc", 2);
        } catch (Exception e) {
            logger.log(Level.SEVERE, null, e);
        }
    }
}
