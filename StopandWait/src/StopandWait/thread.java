
package StopandWait;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import static StopandWait.sender.lost;
import static StopandWait.sender.server;
import static StopandWait.sender.ports;


public class thread  implements Runnable{
 
     
    public int n,window;
    public float loss;   
    public String ack;
    public byte buf[] = new byte[516];
    public String packet,msg="";
    private  DatagramSocket ds;
    private final DatagramPacket receivePacket;
    private InetAddress address;
    private final int packetsize=516;
    private final String filename;
    private Thread thread;
    private int serverport,clientport;
    private int i=0,sequence=0;
    
    public thread(int serverPort, DatagramPacket receivePacket, String filename) throws SocketException {
		
		this.serverport = serverPort;
		this.ds = new DatagramSocket(serverPort);
		this.receivePacket = receivePacket;
		this.address = receivePacket.getAddress();
		this.clientport = receivePacket.getPort();
		this.filename = filename;
               
		
	}
    
    public void start() {
		
		thread = new Thread((Runnable) this);
		thread.start();
		
	}
    
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
     
     public List<byte[]> divideArray(byte[] source) throws IOException {
		
		int start = 0;
                List<byte[]> result = new ArrayList<byte[]>();
                  while (start < source.length) {
		   int end = Math.min(source.length, start + packetsize);
        result.add(Arrays.copyOfRange(source, start, end));
        start += packetsize;
    }

    return result;
		
	}
    
     private void packetProcessor() throws  UnknownHostException, IOException,FileNotFoundException{
            
    
        byte[] buf1 = new byte[packetsize]; 
          packet= new Scanner(new File(filename)).useDelimiter("\\A").next();
          byte[] source = Files.readAllBytes(Paths.get(filename));
          List<byte[]> result = divideArray(source);
          
          n=result.size();   
             
            buf=lost.getBytes();         
              DatagramPacket DpSend =new DatagramPacket(buf, buf.length, address, clientport);
              ds.send(DpSend);               
              buf=String.valueOf(n).getBytes(); 
              DpSend =new DatagramPacket(buf, buf.length, address, clientport);
              ds.send(DpSend);  
      do
        {   boolean ackPacketReceived = false;
            ack=null;
              
           if(i<n){   
                     
               msg=String.valueOf(sequence);
                        msg=msg.concat(new String(result.remove(0)));
                    }
                   
             buf = msg.getBytes();
              DpSend =new DatagramPacket(buf, buf.length, address, clientport);
              ds.send(DpSend);
              sequence=(sequence==0)?1:0;
               System.out.println("Send Data> "+"packet"+sequence+"  To port: "+serverport+"  Clientport: "+clientport);
               
               while (!ackPacketReceived) {  ack=null;   
                  DatagramPacket dp = new DatagramPacket(buf1, buf1.length);
                    try {
                            ds.setSoTimeout(50);
                            ds.receive(dp); 
                             ack= data(buf1).toString();    
                            if(ack.equals(String.valueOf(sequence))){                                  
                     System.out.println("Receive    > "+"ack"+sequence+"  Clientport: "+clientport+"\n\n");
                            ackPacketReceived = true;
                            }
                        } catch (SocketTimeoutException e) {
                            ackPacketReceived = false;
                         
                        }
                     if (ackPacketReceived) {
                      i++;                    
                     break;}
                     else{
                       System.out.println("....Time out resending data....\n\n");
                 sequence=(sequence==0)?1:0;
                 DpSend =new DatagramPacket(buf, buf.length, address, clientport);
              ds.send(DpSend);
              sequence=(sequence==0)?1:0;
               System.out.println("data sent>"+"packet"+sequence+"  To port: "+serverport+"  Clientport: "+clientport);
                     } 
                     buf1 = new byte[packetsize];
               }
            
        buf1 = new byte[packetsize];
        }while(!result.isEmpty());
      System.out.println("\r\nThe file was sent successfully"+" PORT: "+serverport);
      
     sender.ports.add(serverport);
      ds.close();
     }
     
    public void run() {
		
		try {
                   
			packetProcessor();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
}
