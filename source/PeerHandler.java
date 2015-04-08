/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lakshminarayan
 */
public class PeerHandler extends Thread {
    
    public void PeerHandler()
    {
        
    }
    @Override
    public void run()
    {  
        BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
        System.out.println("\n Enter THIS PEERS listening port ");
        try {
            int sourcePort=Integer.parseInt(br.readLine());
            Server s=new Server(sourcePort);
            s.start();
            
            Client c=new Client(sourcePort);
            c.start();
        } catch (IOException ex) {
            Logger.getLogger(PeerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
