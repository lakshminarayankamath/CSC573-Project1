
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Lakshminarayan
 */
public class RS {

    public static void main(String args[]) throws Exception 
    {
        ServerSocket server = new ServerSocket(65423,5);
        server.setSoTimeout(1000000);
        Socket connectionSocket=null;

            try
            {
               while(true)
               {
                 connectionSocket = server.accept();
                 System.out.println("Connected to peer :"+connectionSocket.getInetAddress().toString());
                 new clientHandler(connectionSocket).start();
               }
            }
            catch(IOException ex)
            {
             System.out.println(ex.getMessage());
            }
   }
 }