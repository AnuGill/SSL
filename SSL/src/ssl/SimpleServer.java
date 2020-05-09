package ssl;

import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SimpleServer implements Runnable {
	
	static BigInteger[] publicKey = new BigInteger[2];
	static BigInteger[] privateKey = new BigInteger[2];

   // server's socket
   private ServerSocket s;

   // server's port
  // private int port;

   public SimpleServer(int p) throws Exception {

      // open server socket and start listening
      s = new ServerSocket(p);
      BigInteger[] keys = RSA.generateKeys();
		publicKey[0] = keys[0]; //e
		privateKey[0] = keys[1]; //d
		publicKey[1] = privateKey[1]= keys[2]; //n
   }

 //NOT COMPLETE
   public class RequestHandler implements Runnable {
      private Socket sock;

      private RequestHandler(java.net.Socket x) {
         sock = x;
      }

      public void run() {
         try {
            System.out.println("connect...");
            int c; int i = 0;
            List<String> list = new ArrayList<>();
            String[] K = {"110001", "110001", "110010", "110010", "1100110", "110010", 
          		  "110011", "11111100", "110010", "110010", "110100", "110010", 
          		  "110001", "110010", "100100", "110100", "110010",
          		  "1100110", "110010", "110100", "11111100", "110010", "110100"};
            // read the bytes from the socket
            // and convert the case 
			while ((c = Integer.parseInt(sock.getInputStream().read() + "")) != -1) {
				list.add(Integer.toBinaryString(c));
				i++;
				if (i == K.length)
					break;
			}
            String[] packet = new String[list.size()];
            for(int m = 0; m < packet.length; m++) {
            	packet[m] = list.get(m);
            }
            
            String[] decodedPacket = OneTimeKey.applyOneTimeKey(packet, K);
            
            String[] receivedPacket = Hash.disassemblePacket(decodedPacket, 7, 123, 1);
            
            for(int l = 1; l < decodedPacket.length-1; l++) {
            	c = Integer.parseInt(receivedPacket[l], 2);
            	if(c >= 97 && c <= 122) {
        			c -= 32;
        		} else if (c >= 65 && c <=90) {
        			c += 32;
        		}
            	sock.getOutputStream().write(c);
            }
            sock.getOutputStream().flush();
            sock.close();
            System.out.println("disconnect...");
         } catch (Exception e) {
            System.out.println("HANDLER: " + e);
         }
      } 
   }

   public void run() {
      while(true) {
         try {
            // accept a connection and run handler in a new thread
            new Thread(new RequestHandler(s.accept())).run();
         } catch(Exception e) {
            System.out.println("SERVER: " + e);
         }
      }
   } 


  public static void main(String[] argv) throws Exception {
     if (argv.length != 1) {
        System.out.println("java SimpleServer <port>");
        System.exit(1);
     }
     new SimpleServer(Integer.parseInt(argv[0])).run();
  }
   

}
