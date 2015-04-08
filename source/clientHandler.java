/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

/**
 *
 * @author Lakshminarayan
 */
class clientHandler extends Thread {
	static class List{
		String hostname;
		int cookie;
		boolean flag;
		String lastRegd;
		static int noOfRegd;
		//int TTL=7200;
		int sourcePort;	
	}
	static int cookie=21;
	static LinkedList<List> list=new LinkedList<List>();
    Socket sock;
    String writeSentence;
    String requestMessage;
    String responseMessage;
    public clientHandler(Socket connectionSocket) 
    {
        sock=connectionSocket;
    }
    @Override
    public void run()
    {
    	int index=0;
        int exists=0;
    	DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date dateobj = new Date();
        try
        {
            DataInputStream inFromClient = new DataInputStream(sock.getInputStream());
            requestMessage = inFromClient.readUTF();
            
            String command=requestMessage.substring(0,4);
            command=command.trim();
            
            String flag=requestMessage.substring(4,5);
            String sourcePort=requestMessage.substring(5,10);
            sourcePort=sourcePort.trim();
            String fileRequested=requestMessage.substring(10,requestMessage.length());
            String ipAddress=sock.getInetAddress().toString();
            int sp=Integer.parseInt(sourcePort);
            
            System.out.println();
            System.out.println("P2P Request Message:");
            System.out.println("Command: "+command);
            System.out.println("Flag: "+flag);
            System.out.println("IP Address: "+ipAddress);
            System.out.println("Remote Peer Source Port: "+sourcePort);
            System.out.println("File Requested: "+fileRequested);
            List e=new List();
            e.hostname=ipAddress;
            e.sourcePort=sp;
      
       
            
            if(command.matches("REG"))
            {
            	for(int i=0;i<list.size();i++)
            	{
            	if(list.get(i).hostname.matches(ipAddress))
            	{   
            		index=i;
            		exists=1;
            		e.flag=true;
            		e.cookie=list.get(index).cookie;
            		list.set(index,e);
            		break;
            	}
            	}
            	
            	if(exists==0)
            	{
            		++cookie;
            		e.cookie=cookie;
            		e.flag=true;
            		list.add(e);
            	}
            	
                if(flag.matches("1"))
                {
                   e.lastRegd=df.format(dateobj).toString();
                   e.noOfRegd=(list.get(index).noOfRegd)+1;
                   list.set(index,e);
                   
                   responseMessage="RRG "+"OK"+"************"+"Registered";
                   DataOutputStream outToClient=new DataOutputStream(sock.getOutputStream());
                   outToClient.writeUTF(responseMessage); 
                }
                else if(flag.matches("0"))
                {
                   e.cookie=list.get(index).cookie;
                   e.lastRegd=list.get(index).lastRegd;
                   e.noOfRegd=list.get(index).noOfRegd;
                   e.flag=false;
                   list.set(index,e);
                   
                   responseMessage="RRG "+"EX"+"************"+"Exit";
                   DataOutputStream outToClient=new DataOutputStream(sock.getOutputStream());
                   outToClient.writeUTF(responseMessage);
                   sock.close();
                }
                
                //Display RFCList
                System.out.println("\n Peer List: ");
                for(int i=0;i<list.size();i++)
            	{
            		List l=list.get(i);
            		System.out.println(l.hostname+" "+l.cookie+" "+l.sourcePort+" "+l.flag+" "+l.lastRegd+" "+l.noOfRegd);
            	}
            }
            else if(command.matches("GETP"))
            {
            	//Transfer contents of LinkedList into a File
            	File peerList=new File("PeerList.txt");
            	BufferedWriter output = new BufferedWriter(new FileWriter(peerList));
                output.write("IP Address \tCookie \tPort Number \tActiveStatus \t\tLast Regd. \tTimes Regd");
                output.newLine();
            	
                for(int i=0;i<list.size();i++)
                {
                	List l=list.get(i);
                    writeSentence=l.hostname+"\t"+l.cookie+"\t"+l.sourcePort+"\t\t"+l.flag+"\t\t"+l.lastRegd+"\t\t"+l.noOfRegd;
                    output.write(writeSentence);
                    output.newLine();
                }
                output.close();
                
                //Send Response Message to the requesting Peer
                responseMessage="RGTP"+"OK"+"PeerList.txt"+"Peer List sent as File";
                DataOutputStream outToPeer=new DataOutputStream(sock.getOutputStream());
                outToPeer.writeUTF(responseMessage);
                
                //Send PeerList to the requesting Peer
                peerList = new File ("PeerList.txt"); 
                byte [] bytearray = new byte [(int)peerList.length()];
                FileInputStream fin = new FileInputStream(peerList);
                BufferedInputStream bin = new BufferedInputStream(fin);
                bin.read(bytearray,0,bytearray.length);
                OutputStream os = sock.getOutputStream(); 
                os.write(bytearray,0,bytearray.length);
                os.flush();  
                os.close();
                System.out.println("PeerList.txt sent to Peer");
            }
        }
        catch(IOException ex)
        {
            System.out.println(ex.getMessage());
             try 
             {
              responseMessage="RERR"+"RE"+"Unable to read Requests from Peer";
              DataOutputStream outToPeer=new DataOutputStream(sock.getOutputStream());
              outToPeer.writeUTF(responseMessage);
             } 
             catch (IOException ex1)
             {
                    System.out.println(ex.getMessage());
             }
        }
        
    }
    
}
