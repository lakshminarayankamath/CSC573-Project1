import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lakshminarayan
 */
public class Client extends Thread{
    String ipPeer;
    String ipRS;
    String destPort;
    String sourcePort;
    String requestMessage;
    String responseMessage;
    String message;
    String command;
    String code;
    String fileSent;
    
  
    public Client(int sPort)
    {
        sourcePort=String.valueOf(sPort);
    }
    @Override
    public void run()
    {
        BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
        for(;;)
        {
        try {
            System.out.println("\n 1: Register 2:Leave 3:GetPeerList 4:GetRFCIndex 5:GetRFC ");
            int choice=0;
            try {
                choice = Integer.parseInt(br.readLine());
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            switch(choice)
            {
                case 1:
                    requestMessage="REG "+"1"+sourcePort+" "+"************";
                    regUnreg(1,br);
                    break;
                    
                case 2:
                    requestMessage="REG "+"0"+sourcePort+" "+"************";
                    regUnreg(0,br);
                    System.exit(1);
                    break;
                    
                case 3:
                    requestMessage="GETP"+"F"+sourcePort+" "+"PeerList.txt";
                    getPeerList(br);
                    break;
                    
                case 4:
                    requestMessage="GETR"+"X"+sourcePort+" "+"RFCIndex.txt";
                    System.out.println("\nConnect to a Remote Peer. Enter IP of Remote Peer");
                    
                    try
                    {
                        ipPeer=br.readLine();
                        System.out.println("\nEnter the Port Number of the Remote Peer");
                        destPort=br.readLine();
                        
                        Socket client=new Socket(ipPeer,Integer.parseInt(destPort));
                        String fileName="RFCIndex1.txt";
                        
                        sendRequest(client,fileName);
                        receiveReply(client,fileName);
                        client.close();
                    }
                    catch (IOException ex)
                    {
                        System.out.println(ex.getMessage());
                    }
                    break;
                    
                case 5:
                    System.out.println("Enter the IP of the Remote Peer");
                    
                    try
                    {
                        ipPeer=br.readLine();
                        System.out.println("\nEnter the Port Number of the Remote Peer");
                        destPort=br.readLine();
                        System.out.println("Enter the RFC Number to Download");
                        
                        String fname;
                        fname = br.readLine();
                        requestMessage="GETF"+"X"+sourcePort+" "+fname+".txt";
                        
                        Socket client=new Socket(ipPeer,Integer.parseInt(destPort));
                        
                        sendRequest(client,fname);
                        receiveReply2(client,fname);
                        client.close();
                    }
                    catch (IOException ex)
                    {
                        System.out.println("Peer not online or does not exist. Try again");
                    }
                    break;
                    
                default:
                    System.out.println("Invalid Choice. Try again");
                    
            }
            
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }   
        
        }   
    }
    public void regUnreg(int status,BufferedReader br) throws IOException
    {
        Socket s=null;
        if(status==1)
        {
            System.out.println("\n Enter the IP of RS ");
        
            try 
            {
                ipRS=br.readLine();
            } 
            catch (IOException ex) 
            {
                System.out.println(ex.getMessage());
            }
        }
                try
                {
                    
                   s=new Socket(ipRS,65423);
                   System.out.println("Trying");
                               
                   DataOutputStream outToRS=new DataOutputStream(s.getOutputStream());
                   outToRS.writeUTF(requestMessage);
                   
                   DataInputStream inFromRS = new DataInputStream((s.getInputStream()));
                   responseMessage = inFromRS.readUTF();
                   displayMessage();   
                }
                catch(IOException ex)
                {
                   System.out.println(ex.getMessage());
                   DataInputStream inFromRS = new DataInputStream((s.getInputStream()));
                   responseMessage = inFromRS.readUTF();
                   displayMessage();
                }
                if(status==0)
                {
                try
                { 
                   s.close();
                }
                catch(IOException ex)
                {
                    System.out.println(ex.getMessage());
                }
                }
    }
    public void sendRequest(Socket client,String fileName)
    {
        try 
        {
            DataOutputStream outToPeer=new DataOutputStream(client.getOutputStream());
            outToPeer.writeUTF(requestMessage);
            System.out.println("Requesting Peer for "+fileName);
        } 
        catch (IOException ex) 
        {
          System.out.println(ex.getMessage());
        }
    }
    public void getPeerList(BufferedReader br)
    {
        System.out.println("Enter the IP address of the RS ");
        try 
        {
            ipRS=br.readLine();
            Socket s=new Socket(ipRS,65000);
            
            String fname="PeerList.txt";
            sendRequest(s,fname);
            receiveReply2(s,fname);
        } 
        catch (IOException ex) 
        {
            System.out.println(ex.getMessage());
        }
    }
    public void receiveReply(Socket client,String fileName)
    {
        try 
        {
            DataInputStream inFromPeer=new DataInputStream(client.getInputStream());
            responseMessage=inFromPeer.readUTF();
            displayMessage();
            
            //Receive RFCIndex sent by Remote Peer
            File file=new File(fileName);
            byte [] bytearray = new byte [100000];
            InputStream is = client.getInputStream();
            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            int bytesRead = is.read(bytearray,0,bytearray.length);
            int currentTot;
            currentTot = bytesRead;
            do {
                bytesRead = is.read(bytearray, currentTot, (bytearray.length-currentTot));
                if(bytesRead >= 0)
                    currentTot += bytesRead;
               } while(bytesRead > -1);
            bos.write(bytearray, 0 , currentTot);
            bos.flush();
            System.out.println(fileName+" Received \n");
            
            //Merge RFCIndex
            
            File file1=new File("RFCIndex.txt");
            File file2=new File("RFCIndex1.txt");
            BufferedReader br1=new BufferedReader(new FileReader(file1));
            BufferedReader br2=new BufferedReader(new FileReader(file2));
            BufferedWriter bw=new BufferedWriter(new FileWriter(file1,true));

            int count=0;
            int count2=0;
            String s=null;
            while((s=br1.readLine())!=null)
            {
                 count++;
            }
            while((s=br2.readLine())!=null)
            {
                 count2++;
            }
            br2=new BufferedReader(new FileReader(file2));
            String sentence=null;
            for(int i=1;i<=count2;i++)
            {
               bw.newLine();
               sentence=br2.readLine();
               bw.write(sentence);
            }
          bw.close();
        } 
        catch (IOException ex) 
        {
           System.out.println(ex.getMessage());
        }
    }
    public void receiveReply2(Socket client,String fname) throws IOException
    {
        //Receive response message sent by the Peer
        DataInputStream inFromServer=null;       
        try 
        {
            inFromServer = new DataInputStream(client.getInputStream());
            responseMessage=inFromServer.readUTF();
            displayMessage();
        } 
        catch (IOException ex) 
        {
            System.out.println(ex.getMessage());
        }
            
            if(code.matches("NF"))
            {
                System.out.println();
            }
            else
            {
            //Receive RFC File sent by the Requested Peer
            File file=new File(fname+".txt");
            byte [] bytearray = new byte [1000000];
            InputStream is;
            try 
            {
            is = client.getInputStream();
            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            int bytesRead = is.read(bytearray,0,bytearray.length);
            int currentTot;
            currentTot = bytesRead;
            do {
                bytesRead = is.read(bytearray, currentTot, (bytearray.length-currentTot));
                if(bytesRead >= 0)
                    currentTot += bytesRead;
               } while(bytesRead > -1);
            bos.write(bytearray, 0 , currentTot);
            bos.flush();
            System.out.println(fname+" Received \n");
            }
            catch (IOException ex) 
            {
            inFromServer = new DataInputStream(client.getInputStream());
            responseMessage=inFromServer.readUTF();
            displayMessage();
            System.out.println(ex.getMessage());
            }
         }
    }
    public void displayMessage()
    {
        command=responseMessage.substring(0,4);
        code=responseMessage.substring(4,6);
        fileSent=responseMessage.substring(6,18);
        message=responseMessage.substring(18,responseMessage.length());
        
        System.out.println();
        System.out.println("P2P Response Message:");                       
        System.out.println("Command: "+command);
        System.out.println("Flag: "+code);
        System.out.println("File Sent: "+fileSent);
        System.out.println("Message: "+message);
        System.out.println();
    }
  }

  
