package StopandWait;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException; 
import java.net.*;
import java.util.Random;
import java.util.Scanner;
//import networking.udpBaseClient;

public class receiver {
    public static String [] client=new String [5];
     public static int i=0,serverport,clientport;
     public static float lost;    
    public static String data="";
    public static  DatagramSocket ds;
       
    
   receiver(){}
    
     public static String readfile() throws FileNotFoundException{
       
         Scanner input = new Scanner (new File("client1.txt"));
          String text = new Scanner(new File("client1.txt")).useDelimiter("\\A").next();
          String[] lines = text.split("\\r?\\n");
          int i=0;
        while (input.hasNext()){
           client[i]=lines[i];
            input.nextLine();
            i++;
       }
        serverport= Integer.parseInt(client[1]);
        clientport=Integer.parseInt(client[2]);
      return client[3];
      
    }
       public static StringBuilder data(byte[] a)
    {   
        if (a == null)
            return null;
        StringBuilder ret = new StringBuilder();
        int j = 0;
        while (a[j] != 0)
        {
            ret.append((char) a[j]);
            j++;
        }
        return ret;
    }

     public static void run() throws SocketException, UnknownHostException, IOException,FileNotFoundException{
          String str=readfile();       
       ds = new DatagramSocket(clientport);
        byte[] buf = new byte[516];
         byte[] buf1 =str.getBytes();
         InetAddress ia=InetAddress.getLocalHost();
        DatagramPacket dp1 = new DatagramPacket(buf1,buf1.length,ia,serverport);
         ds.send(dp1);
          DatagramPacket dp = new DatagramPacket(buf, buf.length);
             ds.receive(dp);            
          lost=1/Float.valueOf(data(buf).toString()); 
           buf = new byte[516];
           dp = new DatagramPacket(buf, buf.length);
             ds.receive(dp); 
             int n=Integer.parseInt(data(buf).toString());  
             int count=0;
          BufferedWriter output = new BufferedWriter(new FileWriter("output.txt"));
           int sequence=0;
       do
        {
            
              buf = new byte[516];
        buf1 = new byte[516];
            dp = new DatagramPacket(buf, buf.length);
             ds.receive(dp);
               int instancePort = dp.getPort();
            String packet=new String(buf);  
                     
                    
            Random random = new Random( );
            int chance = random.nextInt( 100 ); 
             if(chance%lost!=0){
              if(Integer.valueOf(packet.substring(0,1))==sequence){
               
                   sequence=(sequence==0)?1:0;
                System.out.println("\n\nReceive  >  "+"packet"+sequence+"   From Port: "+instancePort+"   Clientport: "+clientport);
                        data+=packet.substring(1);
                       
                         count++;
                           buf1=String.valueOf(sequence).getBytes();
               dp1 = new DatagramPacket(buf1,buf1.length,ia,instancePort);
         ds.send(dp1);
                System.out.println("\n\nSend    >"+"ack"+sequence+" To Port: "+instancePort+"   Clientport: "+clientport);
              
             
               }
             
             }
           
           
             
        }while(count<n);
      
     output.write(data);
     output.close();
     ds.close();
     }
    
     public static void main(String[] args) throws UnknownHostException, IOException ,FileNotFoundException{
     receiver r=new receiver();
     System.out.println("................ CLIENT ................\r\n");  
      r.run();
      
    }
}

