import net.jini.core.entry.Entry;

public class Deduct implements Entry {
   public String content;

   public Deduct() {
   }

   public Deduct(String content) {
     this.content = content;
   }

   public String toString() {
     return "MessageContent: " + content;
   }
}