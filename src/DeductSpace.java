


import java.rmi.RemoteException;

import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace;
public class DeductSpace  {
	Deduct it;
	JavaSpace space;


	
public Deduct MessageWrote(){		
	Deduct test = new Deduct("It doesn't matter");
    System.out.println("Searching for a JavaSpace...");
    Lookup finder = new Lookup(JavaSpace.class);
    try {
    	
    space = (JavaSpace) finder.getService();
    System.out.println("A JavaSpace has been discovered");
    }catch(Exception e){
 	   e.printStackTrace();
    }
	   System.out.println("Writing to JavaSpace");
	try {
		
		 space.write(test, null, Long.MAX_VALUE);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransactionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return test;
		// TODO Auto-generated method stub
	}



}
