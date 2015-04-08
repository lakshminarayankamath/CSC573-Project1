/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author Lakshminarayan
 */
public class Peer2{
    public static void main(String[] args) throws IOException
    {
       
       BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
       System.out.println("1: Create Peer");
       int choice=(Integer.parseInt(br.readLine()));
       int sourcePort=0;
       String IP=null;
       switch(choice)
       {
           case 1:
               new PeerHandler().start();
               break;
           
           default:
               System.out.println("Wrong Choice. Restart Program");     
       }
    }
}
