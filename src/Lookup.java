
import java.io.IOException;

import java.rmi.RemoteException;
import java.rmi.RMISecurityManager;

import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.DiscoveryEvent;

import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceMatches;
import net.jini.core.lookup.ServiceTemplate;

import net.jini.space.JavaSpace;

/**
   This is an example of a simple lookup via multicast.  It gets of rid of
   the need to be aware of static lookup information because we can "discover"
   lookup services dynamically at startup.  This basic implementation has
   one fault which is that it only scans each LookupService it finds once
   for service matches (see the comments in DiscoveryListenerImpl).  We could
   go to the trouble of fixing this all ourselves but there's an easier way - 
   ServiceDiscoveryManager.
*/
public class Lookup implements DiscoveryListener {
    private ServiceTemplate theTemplate;
    private LookupDiscovery theDiscoverer;
    private Object theProxy;
    public ServiceRegistrar myRegistrar;


  public  Lookup(Class aServiceInterface) {
      System.setSecurityManager(new RMISecurityManager());
        Class[] myServiceTypes = new Class[] {aServiceInterface};
        theTemplate = new ServiceTemplate(null, myServiceTypes, null);
    }

    /**
     * Having created a Lookup (which means it now knows what type of service
     * you require), invoke this method to attempt to locate a service
     * of that type.  The result should be cast to the interface of the
     * service you originally specified to the constructor.
     *
     * @return proxy for the service type you requested - could be an rmi
     *         stub or an intelligent proxy.
     */
    public Object getService() {
        /*
          We're going to look for all ServiceRegistrar instances in all
          groups - everything we can find via a multicast
         */
        synchronized (this) {
            if (theDiscoverer == null) {
                try {
                    theDiscoverer = new LookupDiscovery(LookupDiscovery.ALL_GROUPS);
                    theDiscoverer.addDiscoveryListener(this);
                } catch (IOException anIOE) {
                    System.err.println("Couldn't setup LookupDiscovery - exiting");
                    anIOE.printStackTrace(System.err);
                    System.exit(-1);
                }
            }
        }

        return waitForProxy();
    }

    /**
     * Location of a service causes the creation of some threads.  Call this
     * method to shut those threads down either before exiting or after a
     * proxy has been returned from getService().
     */
    public void terminate() {
        synchronized (this) {
            if (theDiscoverer != null) {
                theDiscoverer.terminate();
            }
        }
    }

    /**
     * Caller of getService ends up here, blocked until we find a proxy.
     *
     * @return the newly downloaded proxy
     */
    private Object waitForProxy() {
        synchronized (this) {
            while (theProxy == null) {
                try {
                    wait();
                }
                catch (InterruptedException anIE) {
                }
            }

            return theProxy;
        }
    }

    /**
     * Invoked to inform a blocked client waiting in waitForProxy that
     * one is now available.
     *
     * @param aProxy the newly downloaded proxy
     */
    private void signalGotProxy(Object aProxy) {
        synchronized (this) {
            if (theProxy == null) {
                theProxy = aProxy;
                notify();
            }
        }
    }

    /**
     * Everytime a new ServiceRegistrar is found, we will be called back on
     * this interface with a reference to it.  We then ask it for a service
     * instance of the type specified in our constructor.
     */
    public void discovered(DiscoveryEvent anEvent) {
        synchronized (this) {
            if (theProxy != null) {
                return;
            }
        }

        ServiceRegistrar[] myRegs = anEvent.getRegistrars();

        for (int i = 0; i < myRegs.length; i++) {

            try {
                myRegistrar = myRegs[i];

                if (myRegistrar != null) {
                    System.out.println("Found registrar:");

                    DiscoveryUtil.dumpRegistrar(myRegistrar);

                    /*
                      HACK: If we find a registrar, it may have just
                      started and not have any services yet (they haven't
                      found it at this point).  So we wait a while before
                      doing the interrogation.  We could fix this by
                      registering a listener with the ServiceRegistar
                      via notify() but there's an easier way
                      - ServiceDiscoveryManager.
                    */

/*
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException anIE) {
                    }
*/

                    ServiceMatches myMatches = DiscoveryUtil.findServicesOfType(JavaSpace.class, myRegistrar);

                    if ((myMatches != null) && (myMatches.items.length > 0)) {
                        signalGotProxy(myMatches.items[0].service);
                        DiscoveryUtil.dump(myMatches);

                        break;
                    }
                }

            } catch (RemoteException aRE) {
                System.err.println("Couldn't talk to ServiceRegistrar");
                aRE.printStackTrace(System.err);
            } catch (IOException anIOE) {
                System.err.println("Whoops couldn't talk to ServiceRegistrar");
                anIOE.printStackTrace(System.err);
            }
        }
    }

    public void discarded(DiscoveryEvent anEvent) {
        // We don't care about these
    }

    public static void main(String args[]) {
   //     new Lookup().getService();

        /*
           The thing we need to remember about this kind of discovery is
           that things take *time*.  Locating a ServiceRegistrar doesn't happen
           immediately and is actually being done *asynchronously* (on another
           thread).  So, we wait a while or, in this case, forever.  Note
           that, depending on implementation, this might not be necessary.
           It may be enough to have an active LookupDiscovery instance if it
           uses normal threads.  However, we assume it uses all daemon threads.
         */
/*
        try {
            Object myLock = new Object();

            synchronized(myLock) {
                myLock.wait(0);
            }
        } catch (InterruptedException anIE) {
            System.err.println("Whoops main thread interrupted");
        }
*/
    }
}
