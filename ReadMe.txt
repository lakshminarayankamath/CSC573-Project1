Pre-requisites:
1. Super User Access in Linux OS
2. jdk 7u21 installed.

If running on eos, Linux Lab Machine (Realm RHEnterprise 6), install jdk using the following commands:

eos$add jdk
eos$add jdk7u21
----------------------------------------------------------------------------------------------------------------------------------------------------------------

STEP 1: Check the pre-requisites. Make sure that jdk 7 r higher is installed.

STEP 2: Create three new folders RegServer, PeerA and PeerB.

STEP 3: Copy the files RS.java and clientHandler.java into the folder RegServer.

STEP 4: Copy the files Peer2.java, PeerHandler.java, Client.java and Server.java into the folder PeerA and also into the folder PeerB.

STEP 5: Copy the files 7333.txt and 7334.txt into the folder PeerB. These are the two RFC's that PeerB has.

STEP 6: Copy the file RFCIndexA.txt into the folder PeerA. This is the initial RFCIndex for PeerA. The first line is only for reference.
        Follow the alignment of the text in the first line in case a new entry has to be made. If the entered field entry has a smaller
        length than the first line entry field, fill the empty spaces with x until the fields are properly aligned.

        Example: 0000 000.000.000.000 RFCName
                 1546 127.0.0.1xxxxxx IPv6

STEP 7: Rename the above copied file into PeerA as RFCIndex.txt

STEP 8: Copy the file RFCIndexB.txt into the folder PeerB. This is the initial RFCIndex for PeerB

STEP 9: Rename the above copied file into PeerB as RFCIndex.txt
        Follow the same procedure as latter part of STEP 6.

--------------------------------------------------------------------------------------------------------------------------------------------------------------

A. Registration Process

STEP 1: Open a terminal and change the path to RegServer using "cd /<pathname>/RegServer"

STEP 2: Open another terminal and change the path to PeerA using "cd /<pathname>/PeerA"

STEP 3: Compile and run the Peer program using "javac Peer2.java" and "java Peer2" respectively.

STEP 4: When the prgram runs, choose option 1. Then enter the port number on which the peer will listen. 

STEP 5: To Register, choose option 1 again.

STEP 6: Enter the IP of the registration server as 127.0.0.1 (if running on local host) or the IP address of the machine on which it is running. 

STEP 7: Repeat the procedure for PeerB.
--------------------------------------------------------------------------------------------------------------------------------------------------------------

B. Get Peer List

STEP 1: In the terminal Peer A, choose option 3. 

STEP 2: Enter the IP address of the Reg Server. 

STEP 3: Open the folder Peer A. This folder will now contain a file by name "PeerList.txt". This is the list of active Peers. 

---------------------------------------------------------------------------------------------------------------------------------------------------------------

C. Get RFC Index

STEP 1: In the terminal of PeerA, choose Option 4 to get the RFC Index of Peer B.

STEP 2: Enter the IP and port Number of Peer B (127.0.0.1 if running on same machine) or refer the file "PeerList.txt" for IP Address of PeerB.

STEP 3: Open the folder Peer A. This folder will now contain the file by name "RFCIndex.txt". This is the merged RFC Index of both Peer A and Peer B.

---------------------------------------------------------------------------------------------------------------------------------------------------------------

D. Get RFC

STEP 1: Go back to terminal Peer A and choose option 5.

STEP 2: Enter the IP and port Number of Peer B (127.0.0.1 if running on same machine).

STEP 3: Enter the RFC number that is required to be downloaded.[7328,7329] or refer the RFC Index for the RFC number. 

STEP 4: Open the folder Peer A. This will folder will now contain the downloaded RFC File. 

-----------------------------------------------------------------------------------------------------------------------------------------------------------------

E. De Registaration of Peers

For each of the Peers A and B follow the steps given below:

STEP 1: Choose option 2 from the respective terminals.

STEP 2: Program terminates. 
------------------------------------------------------------------------------------------------------------------------------------------------------------------

Note: For each of the above cases, the request message is displayed at the Servers [RS or Server of the Peer being requested] and the response message
is dispayed at the Peer Client. Each of the response is followed by a respective File Transfer.



