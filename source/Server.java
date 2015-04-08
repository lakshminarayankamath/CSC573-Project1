import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lakshminarayan
 */
public class Server extends Thread
{
    static class RFCIndex
   {
    int rfcNumber;
    String hostname;
    String rfcName;
    }
   static LinkedList<RFCIndex> list=new LinkedList<>();
    int sourcePort;
    String command;
    String srcPort;
    String fileName;
    String code;
    String message;
    String requestMessage;
    String responseMessage;
    String ipPeer;
    public Server()
    {
        
    }
    public Server(int sourcePort)
    {
        this.sourcePort=sourcePort;
        
    }
    @Override
    public void run()
    {
        try {
            ServerSocket server=new ServerSocket(sourcePort);
            Socket clientSocket=null;
            
            while(true)
            {
                clientSocket=server.accept();
                ipPeer=clientSocket.getInetAddress().toString();
                System.out.println("Connected to"+ipPeer);
                
                receiveRequest(clientSocket);
                if(command.matches("GETR"))
                {
                    String fileName="RFCIndex.txt";
                    sendResponse(clientSocket,fileName);
                    clientSocket.close();
                }
                if(command.matches("GETF"))
                {
                    String fileName=this.fileName;
                    sendResponse2(clientSocket,fileName);
                    clientSocket.close();
                }
            }
        }
        catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    public void receiveRequest(Socket clientSocket)
    {
        try 
        {
            DataInputStream inFromPeer=new DataInputStream(clientSocket.getInputStream());
            requestMessage=inFromPeer.readUTF();
            
            displayMessage();
        } 
        catch (IOException ex)
        {
            System.out.println(ex.getMessage());
        }
        
    }
    public void sendResponse(Socket clientSocket,String fileName) throws IOException
    {
        //Convert RFCIndex from File to LinkedList
    	    responseMessage="RESR"+"OK"+"RFCIndex.txt"+"Sending RFCIndex";
    	    DataOutputStream outToPeer=new DataOutputStream(clientSocket.getOutputStream());
    	    outToPeer.writeUTF(responseMessage);
    	
            File f=new File("RFCIndex.txt");
            try 
            {
            BufferedReader br=new BufferedReader(new FileReader(f));
            String line=null;
            while((line=br.readLine())!=null)
            {
                String rfcNum=line.substring(0,4);
                String hostname=line.substring(4,20);
                hostname=hostname.trim();
                String rfcName=line.substring(20,line.length());
                rfcName=rfcName.trim();

                RFCIndex e=new RFCIndex();
                e.rfcNumber=Integer.parseInt(rfcNum);
                e.hostname=hostname;
                e.rfcName=rfcName;
                list.add(e); 
            }
            
            //Send RFCIndex file to Remote asking Peer
            File transferFile = new File ("RFCIndex.txt");
            byte [] bytearray = new byte [(int)transferFile.length()];
            FileInputStream fin = new FileInputStream(transferFile);
            BufferedInputStream bin = new BufferedInputStream(fin);
            bin.read(bytearray,0,bytearray.length);
            OutputStream os = clientSocket.getOutputStream();
            os.write(bytearray,0,bytearray.length);
            os.flush();
            os.close();
            System.out.println("RFC Index transferred.");  
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void sendResponse2(Socket client,String fname) throws IOException
    {
            
            try 
            { 
            File transferFile = new File (fname);
            if(transferFile.exists())
            {
            responseMessage="RESF"+"OK"+fname+" "+" "+" "+" "+"Sending RFC "+fname;
            DataOutputStream outToPeer = new DataOutputStream(client.getOutputStream());
            outToPeer.writeUTF(responseMessage);
            
            //Send Requested RFC File to Peer
            
            byte [] bytearray = new byte [(int)transferFile.length()];
            FileInputStream fin = new FileInputStream(transferFile);
            BufferedInputStream bin = new BufferedInputStream(fin);
            bin.read(bytearray,0,bytearray.length);
            OutputStream os = client.getOutputStream();
            os.write(bytearray,0,bytearray.length);
            os.flush();
            os.close();
            System.out.println("RFC"+fname+" transferred");
            
            }
            else
            {
            System.out.println("Requested File does not exist");
            responseMessage="RESF"+"NF"+"FILENOTFOUND"+"RFC NOT FOUND:"+fname;
            DataOutputStream outToPeer = new DataOutputStream(client.getOutputStream());
            outToPeer.writeUTF(responseMessage);
            }
            }
            catch (IOException ex) 
            {
            System.out.println("Requested File does not exist");
            responseMessage="RESF"+"NF"+"FILENOTFOUND"+"RFC NOT FOUND:"+fname;
            DataOutputStream outToPeer = new DataOutputStream(client.getOutputStream());
            outToPeer.writeUTF(responseMessage);
            }
    	    
    }
    public void displayMessage()
    {
        System.out.println(requestMessage);
        command=requestMessage.substring(0,4);
        code=requestMessage.substring(4,5);
        srcPort=requestMessage.substring(5,10);
        srcPort=srcPort.trim();
        fileName=requestMessage.substring(10,requestMessage.length());
        
        System.out.println();
        System.out.println("P2P Request Message:");
        System.out.println("Command: "+command);
        System.out.println("Flag: "+code);
        System.out.println("IP Address: "+ipPeer);
        System.out.println("Source Port: "+srcPort);
        System.out.println("File Requested: "+fileName);
        System.out.println();
    }
}
