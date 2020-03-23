
package selective_repeat;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import static selective_repeat.sender.lost;
import static selective_repeat.sender.server;
import static selective_repeat.sender.ports;
import static selective_repeat.sender.window;


public class thread  implements Runnable{
 
     
    public int n;
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
    private int i=0,r=0;
    
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
            
     
       try{
          byte[] str="ack".getBytes();
        byte[] buf1 = new byte[packetsize]; 
          packet= new Scanner(new File(filename)).useDelimiter("\\A").next();
          byte[] source = Files.readAllBytes(Paths.get(filename));
          List<byte[]> result = divideArray(source);
          
          n=result.size();   
             
            buf=lost.getBytes();         
       
            loss=1/Float.valueOf(lost); 
            DatagramPacket DpSend =new DatagramPacket(buf, buf.length, address, clientport);
              ds.send(DpSend);               
              buf=String.valueOf(n).getBytes(); 
              DpSend =new DatagramPacket(buf, buf.length, address, clientport);
              ds.send(DpSend);  
                            
             int[] sequence=new int[n];
             for(int y=0;y<n;y++)
                 sequence[y]=y;
            
           String [] buffer=new String [window];
            
           for(int y=0;y<window;y++){
                    buffer[y]=new String(result.get(r));
                    }
           i=0;
           int z=0,a=i;
            while(a<n && z<buffer.length){   
                        if (str.equals(new String(result.get(a)))){
                        a++;z++;continue;}
                        msg=String.valueOf(sequence[a]);
                        msg=msg.concat("-");
                       msg=msg.concat(new String(result.get(a)));
                         buf = msg.getBytes();
                     DpSend =new DatagramPacket(buf, buf.length, address, clientport);
              ds.send(DpSend);
                          System.out.println("Send Data> "+"packet"+sequence[a]+"  To port: "+serverport+"  Clientport: "+clientport);
                        z++;a++;
                    
                       
                } a=0;
            do{  buf = new byte[packetsize];
                    buf1 = new byte[packetsize];
                    boolean ackPacketReceived = false;
                try{
                    
                    
                    z=0;i=a; 
                    if(a==n){   
                        msg="end-0";
                         buf = msg.getBytes();
                       DpSend =new DatagramPacket(buf, buf.length, address, clientport);
              ds.send(DpSend);
                        break;
                    }
                   else if(i!=0 && a!=n){    
                    while(a<n && z<window){ 
                        if (result.get(a).equals(str)){  
                        a++;
                       
                        continue;}
                     
                        z++;a++;
                    
                       
                }
                    } 
                     
                    a=i; 
               
                      while (a<n) {  ack=null;
                      
                  buf1 = new byte[packetsize];  int flag=1;
                       DatagramPacket dp = new DatagramPacket(buf1, buf1.length);
                    try {   
                           ds.setSoTimeout(50);
                            ds.receive(dp); 
                             String c= data(buf1).toString();
                              ack=c;  
                              int q=0;
                        while(str.equals(result.get(a))){
                        a++;
                            r++; 
                        for(int y=0;y<window;y++){  
                       if(y+1==window)
                        buffer[y]=  new String(result.get(r+window));
                       else
                       buffer[y]=buffer[y+1];
                       }                      
                     msg=String.valueOf(sequence[a+window-1]);
                        msg=msg.concat("-");
                        msg=msg.concat(new String(result.get(a))); 
                          buf = msg.getBytes();
                      DpSend =new DatagramPacket(buf, buf.length, address, clientport);
                       ds.send(DpSend); 
                         System.out.println("Send Data> "+"packet"+sequence[a+window-1]+"  To port: "+serverport+"  Clientport: "+clientport);
                     
                     }
                         System.out.println("Receive    > "+"ack"+ack+"  Clientport: "+clientport+"\n\n"); 
                           
                             
                       if(ack.equals(String.valueOf(sequence[a]))){                 
                      buffer[0]="ack";
                        result.set(a, str);                      
                      a++;   
                      r++;  
                        for(int y=0;y<window;y++){  
                       if(y+1==window)
                        buffer[y]=  new String(result.get(r+window));
                       else
                       buffer[y]=buffer[y+1];
                       } int d=a+window;
                        
                        if(a+window-1<=n){   
                         msg=String.valueOf(sequence[a+window-1]);
                        msg=msg.concat("-");
                        msg=msg.concat(new String(result.get(a))); 
                          buf = msg.getBytes();
                      DpSend =new DatagramPacket(buf, buf.length, address, clientport);
                       ds.send(DpSend); 
                         System.out.println("Send Data> "+"packet"+sequence[a+window-1]+"  To port: "+serverport+"  Clientport: "+clientport);
                        }
                        else if(d==n-1){
                            msg=String.valueOf(sequence[d]);
                        msg=msg.concat("-");
                        msg=msg.concat(new String(result.get(a))); 
                          buf = msg.getBytes();
                      DpSend =new DatagramPacket(buf, buf.length, address, clientport);
                       ds.send(DpSend); 
                         System.out.println("Send Data> "+"packet"+sequence[d]+"  To port: "+serverport+"  Clientport: "+clientport);
                            
                        }
                             }
                         else{  
                    for(int y=1;y<window;y++){      
                   if(ack.equals(String.valueOf(sequence[a+y]))){
                        buffer[y]="ack";
                        result.set(y+a, str);
                      
                    }
              }  
           }
                           
                         
                        if(a==n)
                            ackPacketReceived = true;
                      
                     
                        } catch (SocketTimeoutException e) {
                            ackPacketReceived = false;
                            flag=0;
                               System.out.println("....Time out resending data....\n\n");
                             
                            
                        }
                     if (ackPacketReceived) {
                      
                      break;
                     }
                     else{
                       
                     if(flag==0){  
                        
                      msg=String.valueOf(sequence[a]);
                        msg=msg.concat("-");
                        msg=msg.concat(new String(result.get(a))); 
                          buf = msg.getBytes();
                      DpSend =new DatagramPacket(buf, buf.length, address, clientport);
                       ds.send(DpSend); 
                         System.out.println("Send Data> "+"packet"+sequence[a]+"  To port: "+serverport+"  Clientport: "+clientport);
                        
                     } 
                      
                     }
                  
                     buf1 = new byte[packetsize];
             }
                  
                 while(buffer[0].equals("ack")){     
                       r++; 
                        for(int y=0;y<window;y++){  
                       if(y+1==window)
                        buffer[y]=  new String(result.get(r+window));
                       else
                       buffer[y]=buffer[y+1];
                       }
                     
                       }    
                
              
          
                }catch(Exception e){}
            }while(!msg.equals("end"));
          
        }catch(Exception e){}   
        
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
