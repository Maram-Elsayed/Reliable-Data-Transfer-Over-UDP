
package selective_repeat;

import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.Scanner;
public class receiver{

    public static String [] client=new String [5];
   
    String packet;
    String []data;
    public static int[] ack;
    public static int i=0,sequence=0,serverport,window,count=0,clientport;
     public static float lost;
     int c=0;
    receiver(){}
    
     public static String readClientInfo() throws FileNotFoundException{
       
         Scanner input = new Scanner (new File("client.txt"));
          String text = new Scanner(new File("client.txt")).useDelimiter("\\A").next();
          String[] lines = text.split("\\r?\\n");
          int i=0;
        while (input.hasNext()){
           client[i]=lines[i];
            input.nextLine();
            i++;
       }
         serverport= Integer.parseInt(client[1]);
        window=Integer.parseInt(client[4]);
        clientport=Integer.parseInt(client[2]);
      return client[3];
      
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


    
    public void run()throws FileNotFoundException{ 
        String str=readClientInfo(); 
         
        try{
           DatagramSocket ds = new DatagramSocket(clientport);
        byte[] buf = new byte[516];
         byte[] buf1 =str.getBytes();
         InetAddress ia=InetAddress.getLocalHost();
        DatagramPacket dp1 = new DatagramPacket(buf1,buf1.length,ia,serverport);
         ds.send(dp1);
          DatagramPacket dp = new DatagramPacket(buf, buf.length);
             ds.receive(dp);
             float loss=Float.valueOf(data(buf).toString());  
          lost=1/Float.valueOf(data(buf).toString());  
           buf = new byte[516];
           dp = new DatagramPacket(buf, buf.length);
             ds.receive(dp); 
             int n=Integer.parseInt(data(buf).toString());    
             int count=0;
          BufferedWriter output = new BufferedWriter(new FileWriter("output.txt"));
             int [] buffer=new int [window];
             ack=new int[n];
             data=new String[n]; 
              for(int y=0;y<n;y++)
                 ack[y]=y;
              for(int y=0;y<window;y++){
                    buffer[y]=y;
                     
              }
            do{   buf = new byte[516];
                    buf1 =new byte[516];
                    
                try{ 
                    dp = new DatagramPacket(buf, buf.length);
                    ds.receive(dp);
                     int instancePort = dp.getPort();
                    packet=new String(buf);         
                    String[] p= packet.split("-", 2);    
                    if(p[0].equals("end")){
                        break;}
                    for(int y=0;y<window;y++){      
                    if(buffer[y]==Integer.valueOf(p[0])){ 
                         sequence=Integer.valueOf(p[0]);
                         data[sequence]=p[1];
                       
                         
                     
                    
                    if(loss!=0){
                    Random random = new Random( );
           int chance = random.nextInt( 100 );
           
                    if(chance%lost!=0){   
  System.out.println("\n\nReceive  >  "+"packet"+sequence+"   From Port: "+instancePort+"   Clientport: "+clientport);
                                          
                         byte[] b=String.valueOf(sequence).getBytes();
                         dp1 = new DatagramPacket(b,b.length,ia,instancePort);
                         ds.send(dp1); 
                          System.out.println("\n\nSend    >"+"ack"+sequence+" To Port: "+instancePort+"   Clientport: "+clientport);
                         count++;
                         
                    }
                   
                }
                    else{
                          System.out.println("\n\nReceive  >  "+"packet"+sequence+"   From Port: "+instancePort+"   Clientport: "+clientport);
                  
                      byte[] b=String.valueOf(sequence).getBytes();
                         dp1 = new DatagramPacket(b,b.length,ia,instancePort);
                         ds.send(dp1); 
                           System.out.println("\n\nSend    >"+"ack"+sequence+" To Port: "+instancePort+"   Clientport: "+clientport);
                     count++;
                    }
      }
           }
                   if(sequence>=i+window/2){  
                            for(int j=0;j<window;j++){  
                                 if(j==window-1){  
                                 buffer[j]=ack[i+window];
                               i++;
                            } else{
                                 buffer[j]=buffer[j+1]; }
                                        
                            }                         
                                     
                        }
                   
                }catch(Exception e){} 
            }while(true);
            for(int y=0;y<n;y++)
             output.write(data[y]);
     output.close();
     ds.close();
        }catch(Exception e){}
        finally{ 
            try{   
              
     
            }catch(Exception e){}
        }
       
    }
    public static void main(String args[]) throws FileNotFoundException{
        System.out.println("................ CLIENT ................\r\n");  
        receiver r=new receiver();
     
            r.run();
    }
}
