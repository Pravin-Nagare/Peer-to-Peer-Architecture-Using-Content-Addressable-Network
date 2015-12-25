
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;


public class Driver {
	
	public static void main(String[] args) throws UnknownHostException{
		BootstrapInt c = null;
		RemoteInterface rInt = null;
		
		ClientNode client=null;
		try {
			client = new ClientNode();
		} catch (RemoteException e2) {
			e2.printStackTrace();
		}
		CANNodesIP node = null;
		//client.peerName = args[2];
		//client.port = Integer.parseInt(args[3]);
		Scanner sc = new Scanner(System.in);
		String choice;
		int choice1;
		String bootIP = null;
		int bootPort = 0;
		//System.out.println("IP Address of PC: " + InetAddress.getLocalHost().getHostAddress() + " " + InetAddress.getLocalHost().getHostName());
		
		while(true){
			try{		
			System.out.println("Select Operation: join  view  insert  search  leave  Quit");
			
			//choice = sc.nextInt();
			choice=sc.next();
			switch(choice){
				case "join":
							System.out.println("1. Get Random node from bootsrtap to request for join");
							System.out.println("2. Send join request to random node \nEnter 1 or 2\n>");
							choice1=sc.nextInt();
							switch(choice1){
							case 1: 
									System.out.println("Enter <Bootstrap IP> <Port of Bootstrap> <PeerName> <Port>");
									bootIP = sc.next();
									bootPort = sc.nextInt();
									client.peerName = sc.next();
									client.port = sc.nextInt();
									client.ipAddress = InetAddress.getLocalHost().getHostAddress();
									
									try{
										Registry reg = LocateRegistry.createRegistry(client.port);
										reg.rebind("myboot", client);
										System.out.println(client.peerName + " node is ready on port: " + client.port);
									}catch (RemoteException e1){
										e1.printStackTrace();
									}		
								
									try{			
										Registry myReg = LocateRegistry.getRegistry(bootIP, bootPort);
										c = (BootstrapInt) myReg.lookup("myboot");
										node = c.getNodeIPAddresses();
										client.sendJoinRequest(node, c);
										c.setNodeInBootstrap(client.ipAddress, client.port, client.peerName);
										client.printNeighborList();
										client.printAllFiles();
									}catch (Exception e) {
										e.printStackTrace();
									}
									client.printInformation();
									break;
							case 2:	
									System.out.println("Enter <Node IP> <Port of Node> <PeerName> <Port>\n>");
									String nodeIP = sc.next();
									int nodePort = sc.nextInt();
									client.peerName = sc.next();
									client.port = sc.nextInt();
									client.ipAddress = InetAddress.getLocalHost().getHostAddress();
									
									try{
										Registry reg = LocateRegistry.createRegistry(client.port);
										reg.rebind("myboot", client);
										//System.out.println(client.peerName + " node is ready on port: " + client.port);
									}catch (RemoteException e1){
										e1.printStackTrace();
									}		
									
									try{			
										Registry myReg = LocateRegistry.getRegistry(nodeIP, nodePort);
										rInt = (RemoteInterface) myReg.lookup("myboot");
										//client.sendJoinRequest(node, c);
										//c.setNodeInBootstrap("127.0.0.1", client.port, client.peerName);
										rInt.join(6, 7, client.ipAddress, client.peerName, client.port);
										client.printNeighborList();
									}catch (Exception e) {
										e.printStackTrace();
									}
									client.printInformation();
									break;
							}
							//System.out.println("Enter peername port");
//							client.peerName = sc.next();
//							client.port = sc.nextInt();
//							client.ipAddress = InetAddress.getLocalHost().toString();
							
								
							/*try{			
								Registry myReg = LocateRegistry.getRegistry("127.0.0.1", 1099);
								c = (BootstrapInt) myReg.lookup("myboot");
								node = c.getNodeIPAddresses();
								client.sendJoinRequest(node, c);
								c.setNodeInBootstrap("127.0.0.1", client.port, client.peerName);
								client.printNeighborList();
							}catch (Exception e) {
								e.printStackTrace();
							}*/
							//client.setNodeInfo();
							break;
							
				case "view":
							System.out.println("1. For peer name \n2. View All Nodes");
							int ch = sc.nextInt();
							if(ch==1){
								System.out.println("Enter peer name:");
								String peerName = sc.next();
								if(!client.viewByPeerName(peerName)){
									System.out.println("Node " + peerName + " Not Found!!");
								}
							}
							else{
								client.viewAllNodesInfo();
							}
							break;
				case "insert":
							System.out.println("Enter file name: ");
							String fileName = sc.next();
							client.insertFileToNode(fileName);
							break;
				case "search":
							System.out.println("Enter file name: ");
							fileName = sc.next();
							client.searchFileInCAN(fileName);
							break;
				case "leave": 
							client.leaveNetwork();
							if(bootIP!=null){
							try{			
									Registry myReg = LocateRegistry.getRegistry(bootIP, bootPort);
									c = (BootstrapInt) myReg.lookup("myboot");
									c.removeFromPool(client.getPeerName());
									client.printNeighborList();
							}catch (Exception e) {
									e.printStackTrace();
							}
							}
							System.exit(0);
							break;
				case "quit":System.out.println("Exiting...");
							sc.close();
							System.exit(0);
	 		}
			}catch(NumberFormatException nm){
				System.err.println("Invalid Input Type");
				 System.exit(1);
			}catch(InputMismatchException ie){
				System.err.println("Invalid Input. Select correct option");
				System.exit(1);
			} catch (RemoteException e) {
				e.printStackTrace();
				System.exit(1);
			}		
		}
		
	}
}
