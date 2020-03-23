
package selective_repeat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
public class sender  {
    
    public static String [] server=new String [4];
     public static int serverport,window;
    public static String lost;
    private  DatagramSocket serverSocket;
    public static Queue <Integer> ports; 
   private Queue <thread> threads; 
	
   public static StringBuilder data(byte[] a)
    {
        if (a == null)
            return null;
        StringBuilder ret = new StringBuilder();
        int i = 0;
        while (a[i] != 0)
        {
            ret.append((char) a[i]);
            i++;
        }
        return ret;
    }
    
    
    public sender() throws SocketException {
		serverSocket = new DatagramSocket(serverport);
		threads = new LinkedList<thread>();
		ports = new LinkedList<>(Arrays.asList(1000, 1001, 1002, 1003, 1004));
	}
    
    public static void readServerInfo() throws FileNotFoundException{
         
         Scanner input = new Scanner (new File("server.txt"));
          String text = new Scanner(new File("server.txt")).useDelimiter("\\A").next();
          String[] lines = text.split("\\r?\\n");
          int i=0;
        while (input.hasNext()){
           server[i]=lines[i];
            input.nextLine();
            i++;
       }
        serverport= Integer.parseInt(server[0]);
        lost=server[3];
        window=Integer.parseInt(server[1]);
      
    }
 
    
     public static void main(String[] args) throws UnknownHostException, IOException,FileNotFoundException {
        readServerInfo();   
         sender sender=new sender();  
         
         System.out.println("................ SERVER ................\r\n");
         
		         
         while(true) {
             
         byte[] receive = new byte [516];
         DatagramPacket receivePacket = new DatagramPacket(receive, receive.length);
	sender.serverSocket.receive(receivePacket);
        
        System.out.println("\r\nNew client request received\n\n");
			
        
          String filename= data(receive).toString();    
        if(!ports.isEmpty()) {
				int port = ports.remove();                               
				thread thread = new thread(port, receivePacket, filename);
				sender.threads.add(thread);
				sender.threads.remove().start();	
			} else {
				System.out.println("No available port, try again later");
				
			}
         
         
         
         }
       
    }
}
