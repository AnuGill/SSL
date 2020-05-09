package ssl;

import java.math.BigInteger;
import java.net.Socket;

public class SimpleClient { 
	
	static BigInteger[] publicKey = new BigInteger[2];
	static BigInteger[] privateKey = new BigInteger[2];

   // network socket
   private Socket s;

   public SimpleClient(String host, int port) throws Exception {
       // open a connection to the server
       s = new Socket(host,port);
       BigInteger[] keys = RSA.generateKeys();
		publicKey[0] = keys[0]; //e
		privateKey[0] = keys[1]; //d
		publicKey[1] = privateKey[1]= keys[2]; //n
   }

   // data transfer
   public void execute() throws Exception {
      int c, k=0,i=0; char tmp;
      StringBuilder sb = new StringBuilder();
      String[] K = {"110001", "110001", "110010", "110010", "1100110", "110010", 
    		  "110011", "11111100", "110010", "110010", "110100", "110010", 
    		  "110001", "110010", "100100", "110100", "110010",
    		  "1100110", "110010", "110100", "11111100", "110010", "110100"};
      // read data from keyboard until end of file
      while((c = System.in.read()) != -1) {

    	  tmp = (char) c;
    	  sb.append(tmp);
    	  
         if ((char)c == '\n' || (char)c == '\r') {
        	 break;
         }
         ++k;
      }
      
      String[] packet = Hash.pack(sb.toString(), sb.length(), sb.length(), 7, 123, 1);
      
      String[] encodedPacket = OneTimeKey.applyOneTimeKey(packet, K);
      
      for(int l = 0; l < encodedPacket.length; l++) {
    	  c = Integer.parseInt(encodedPacket[l], 2);
    	  s.getOutputStream().write(c);
      }
      
      s.getOutputStream().flush();
 
      // read until end of file or same number of characters
      // read from server 
      while((c = s.getInputStream().read()) != -1) {
         System.out.write(c);
         if(i++ == k) break;
      }
      System.out.println();
      System.out.println("wrote " +i + " bytes");
      s.close();
   }

   
   public static void main(String[] argv) throws Exception {
      if (argv.length != 2) {
         System.out.println("java SimpleClient <host> <port>");
         System.exit(1);
      }

      String host = argv[0];
      int port = Integer.parseInt(argv[1]);

      new SimpleClient(host,port).execute();
   } 
}
