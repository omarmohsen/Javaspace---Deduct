
import java.rmi.RemoteException;

import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.lookup.ServiceMatches;
import net.jini.core.lookup.ServiceItem;

public class DiscoveryUtil {
    public static void dumpRegistrar(ServiceRegistrar aRegistrar) 
        throws RemoteException {

        String[] myGroups = aRegistrar.getGroups();

        System.out.println("Registrar ServiceID: " +
                           aRegistrar.getServiceID());
        System.out.print("Groups: ");
        for (int i = 0; i < myGroups.length; i++) {
            System.out.print(myGroups[i] + " ");
        }
        System.out.println();
        System.out.println("LookupLocator: " + aRegistrar.getLocator());
    }

    public static ServiceMatches
        findServicesOfType(Class aClass,
                           ServiceRegistrar aRegistrar) 
        throws RemoteException {

        ServiceTemplate myTemplate =
            new ServiceTemplate(null, new Class[] {aClass}, null);

        ServiceMatches myServices = aRegistrar.lookup(myTemplate, 255);

        return myServices;
    }

    public static void dump(ServiceMatches aMatches) {
        System.out.println("Found " + aMatches.totalMatches);

        for (int i = 0; i < aMatches.totalMatches; i++) {
            System.out.println("ServiceID: " +
                               aMatches.items[i].serviceID + ", " +
                               aMatches.items[i].service);
            System.out.print("Interfaces: ");
            dumpInterfaces(aMatches.items[i].service.getClass());
        }
    }

    public static void dump(ServiceItem anItem) {
            System.out.println("ServiceID: " +
                               anItem.serviceID + ", " + anItem.service);
            System.out.print("Interfaces: ");
            dumpInterfaces(anItem.service.getClass());
    }

    public static void dumpInterfaces(Class aClass) {

        Class[] myInterfaces = aClass.getInterfaces();
            
        for (int j = 0; j < myInterfaces.length; j++) {
            System.out.print(myInterfaces[j].getName() + " ");
        }

        if (aClass.getSuperclass() != null)
            dumpInterfaces(aClass.getSuperclass());
        else
            System.out.println();
    }
}
