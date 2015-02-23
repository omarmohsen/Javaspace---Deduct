import net.jini.space.JavaSpace;

public class DeductClient {
	static int total=-550-5-550-550-550+550-(550 *22)-5-5-5-5+5-5-5-5-5-5-5-5+5;
	static JavaSpace space;
	static int lecnum=4;
	static double avg;
   public static void main(String argv[]) {
	   System.out.println("Deducted marks statistics in distributed computing course"+"("+lecnum+" lectures)");
	   avgPerLecture();
      try {
         Deduct msg = new Deduct();
         msg.content = "Total number of points in all lectures:"+total+"%\nAverage marks deducted per lecture:"+avg+"%";
         
         System.out.println("Searching for a JavaSpace...");
          
                   Lookup finder = new Lookup(JavaSpace.class);
                   try {
          space = (JavaSpace) finder.getService();
                   }catch(Exception e){
                	   e.printStackTrace();
                   }
                  
         System.out.println("A JavaSpace has been discovered.");
         System.out.println("Writing a message into the space...");
         space.write(msg, null, 60*60*1000);
         DeductSpace sp = new DeductSpace();
         System.out.println("Reading a message from the space...");
         Deduct result = (Deduct) space.read(sp.MessageWrote(), null, Long.MAX_VALUE);
         System.out.println("The message wrote was:\n"+msg.content);
         System.out.println("The message read is: "+result.content);
      } catch(Exception e) {
         e.printStackTrace();
      }
   }
   static double avgPerLecture(){
	   avg=total/lecnum;
	   return avg;
   }
   
}
