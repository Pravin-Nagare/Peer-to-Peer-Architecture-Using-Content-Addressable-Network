
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;




public class ClientNode extends UnicastRemoteObject implements RemoteInterface, Serializable{
	
	public ZoneCoordinates zone = null;
	public List<NeighborNodes> neighborList = null;
	NeighborNodes nodeInfo = null;
	public int xCoordinate;
	public int yCoordinate;
	public String ipAddress;
	public int port;
	public String peerName;
	RemoteInterface r = null;
	RemoteInterface rInt = null;
	Map<String, Boolean> fileMap = null;
	
	public ClientNode() throws RemoteException{
		xCoordinate = 0;
		yCoordinate = 0;
		ipAddress = null;
		port = 0;
		peerName = null;
		zone = new ZoneCoordinates();
		neighborList = new ArrayList<NeighborNodes>();	
		nodeInfo = new NeighborNodes();
		fileMap = new HashMap<String, Boolean>();
	}
	
		
	public Map<String, Boolean> getFileMap() throws RemoteException {
		return fileMap;
	}

	public void setFileMap(Map<String, Boolean> fileMap) throws RemoteException{
		this.fileMap = fileMap;
	}
	
	public void addInFileMap(String file){
		this.fileMap.put(file, true);
	}

	public NeighborNodes getNodeInfo(){
		NeighborNodes nodeInfo = new NeighborNodes();
		nodeInfo.ipAddress = this.getIpAddress();
		nodeInfo.peerName = this.getPeerName();
		nodeInfo.port = this.port;
		nodeInfo.zone = this.getZone();
		return nodeInfo;
	}
	
	public ZoneCoordinates getZone() {
		return zone;
	}

	public void setZone(ZoneCoordinates zone) {
		this.zone = zone;
	}

	public List<NeighborNodes> getNeighbor() {
		return neighborList;
	}

	public void setNeighbor(List<NeighborNodes> neighbor) {
		this.neighborList = neighbor;
		this.nodeInfo.setListOfNeighbors(this.neighborList);
	}

	public void addNeighborNode(NeighborNodes tempNodeOld) throws RemoteException{
		boolean copy = false;
		if(this.getPeerName().equals(tempNodeOld.getPeerName()))
			return;
		for(NeighborNodes node: this.getNeighbor()){
			if(node.getPeerName().equals(tempNodeOld.getPeerName())){
				copy=true;
			}
		}
		if(!copy){
			this.neighborList.add(tempNodeOld);
			this.nodeInfo.setListOfNeighbors(this.neighborList);
		}
	}
	
	public int getxCoordinate() {
		return xCoordinate;
	}

	public void setxCoordinate(int xCoordinate) {
		this.xCoordinate = xCoordinate;
	}

	public int getyCoordinate() {
		return yCoordinate;
	}

	public void setyCoordinate(int yCoordinate) {
		this.yCoordinate = yCoordinate;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getPeerName() {
		return peerName;
	}

	public void setPeerName(String peerName) {
		this.peerName = peerName;
	}

	public RemoteInterface getR() {
		return r;
	}

	public void setR(RemoteInterface r) {
		this.r = r;
	}

	public void addInHashMap(String key) throws RemoteException{
		this.getFileMap().put(key, true);
	}
	
	public void initializeZone() {
		this.zone.x1 = this.zone.x4 = 0;
		this.zone.x2 = this.zone.x3 = 10;
		this.zone.y1 = this.zone.y2 = 0;
		this.zone.y3 = this.zone.y4 = 10;		
	}

	public void removeNode(String peerName) throws RemoteException{
		for(int i=0; i<this.neighborList.size(); i++){
			if(this.neighborList.get(i).peerName.equals(peerName)){
				this.neighborList.remove(i);break;
			}
		}
	}

	public void updateZoneInNeighbrList(NeighborNodes tempNode) throws RemoteException{
		List<NeighborNodes> list = this.getNeighbor();
		RemoteInterface rInt = null;
		for(NeighborNodes node : list){
			try{			
				Registry myReg = LocateRegistry.getRegistry(node.getIpAddress(), node.port);
				rInt = (RemoteInterface) myReg.lookup("myboot");
				rInt.removeNode(peerName);
				rInt.addNeighborNode(tempNode);
			}catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	
	public void sendJoinRequest(CANNodesIP node, BootstrapInt c) {
		if(node==null){
			initializeZone();
			//System.out.println("First node initialized");	
		}
		else{
			try{			
				Registry myReg = LocateRegistry.getRegistry(node.getIpAddress(), node.port);
				r = (RemoteInterface) myReg.lookup("myboot");
				//Generate random number here
				Random rand = new Random();
				int x = rand.nextInt(10);
				int y = rand.nextInt(10);
				System.out.println("X: " + x + "Y: " + y);
				r.join(x, y, this.ipAddress, this.peerName, this.port);	
				printZoneInfo();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void printZoneInfo() {
		System.out.println("Zone of " + this.peerName + ": " + this.zone.x1 + " " + this.zone.y1 + " "+ this.zone.x2 + " " + this.zone.y2);
		System.out.println("Zone of " + this.peerName + ": " + this.zone.x3 + " " + this.zone.y3 + " "+ this.zone.x4 + " " + this.zone.y4);
	}

	public void divideZone(int x, int y, RemoteInterface rInt) throws RemoteException{
		ZoneCoordinates zoneRemoteNode = rInt.getZone();
		if((this.zone.x2-this.zone.x1) == (this.zone.y3 - this.zone.y2)){	//zone is square
			//System.out.println("Splitting square zone of " + this.peerName);
			zoneRemoteNode.x1 = zoneRemoteNode.x4 = (this.zone.x1 + this.zone.x2) / 2;
			zoneRemoteNode.y1 = zoneRemoteNode.y2 = this.zone.y2;
			zoneRemoteNode.x2 = zoneRemoteNode.x3 = this.zone.x2;
			zoneRemoteNode.y3 = zoneRemoteNode.y4 = this.zone.y3;
			this.zone.x2 = zoneRemoteNode.x1;
			this.zone.y2 = zoneRemoteNode.y1;
			this.zone.x3 = zoneRemoteNode.x4;
			this.zone.y3 = zoneRemoteNode.y4;
		}
		else{		//Zone is Rectangle
			//System.out.println("Splitting rectangle zone of " + this.peerName);
			zoneRemoteNode.x1 = zoneRemoteNode.x4 = this.zone.x1;
			zoneRemoteNode.y1 = zoneRemoteNode.y2 = (this.zone.y1 + this.zone.y4) / 2;
			zoneRemoteNode.x2 = zoneRemoteNode.x3 = this.zone.x2;
			zoneRemoteNode.y3 = zoneRemoteNode.y4 = this.zone.y3;
			this.zone.x3 = zoneRemoteNode.x2;
			this.zone.y3 = zoneRemoteNode.y2;
			this.zone.x4 = zoneRemoteNode.x1;
			this.zone.y4 = zoneRemoteNode.y1;
		}
		rInt.setZone(zoneRemoteNode);
	}
	
	public void updateNeighborsList(RemoteInterface rInt) throws RemoteException{
		ZoneCoordinates zoneRemoteNode = rInt.getZone();
		List<NeighborNodes> tempNighborList = new ArrayList<NeighborNodes>();
		if(!this.neighborList.isEmpty()){
			for(NeighborNodes node : this.neighborList)
				tempNighborList.add(node);
			this.neighborList.clear();
		}
		
		//Add new node in neighborList of current node and viseversa
			NeighborNodes tempNodeNew = new NeighborNodes();
			tempNodeNew.setZone(zoneRemoteNode);
			tempNodeNew.setIpAddress(rInt.getIpAddress());
			tempNodeNew.setPeerName(rInt.getPeerName());					
			tempNodeNew.setPort(rInt.getPort());
			this.neighborList.add(tempNodeNew);
			this.setNeighbor(this.neighborList);
			//System.out.println(rInt.getPeerName() + "Added as neighbor to node " + this.getPeerName());
			
			NeighborNodes tempNodeOld = new NeighborNodes();
			tempNodeOld.setZone(this.getZone());
			tempNodeOld.setIpAddress(this.getIpAddress());
			tempNodeOld.setPeerName(this.peerName);					
			tempNodeOld.setPort(this.port);
			List<NeighborNodes> newNodeNeighborList = rInt.getNeighbor();
			newNodeNeighborList.add(tempNodeOld);
			rInt.setNeighbor(newNodeNeighborList);
			//System.out.println(this.getPeerName() + " Added as neighbor to node " + rInt.getPeerName());
			
			divideHashMap(this, rInt);

			
		//update neighbors of created zone. If new node is neighbor of nodes in neighborList, add this node in neighborList of neighbor's nodes
		if(!tempNighborList.isEmpty()){
			RemoteInterface neighborNode = null;
			for(NeighborNodes tempnode : tempNighborList){
				if(checkIfNeighbors(zoneRemoteNode, tempnode.getZone())){
						//System.out.println("New node " + rInt.getPeerName()  +" is neighbor to " + tempnode.peerName);
					newNodeNeighborList.add(tempnode);
					rInt.setNeighbor(newNodeNeighborList);
					//System.out.println(tempnode.peerName + " Added as neighbor to node" + rInt.getPeerName());

					//add this current node as neighbor in neighbor list
					try {
						Registry myReg = LocateRegistry.getRegistry(tempnode.getIpAddress(), tempnode.getPort());
						neighborNode = (RemoteInterface) myReg.lookup("myboot");
						List<NeighborNodes> nodeNeighborList = neighborNode.getNeighbor();
						nodeNeighborList.add(tempNodeNew);
						neighborNode.setNeighbor(nodeNeighborList);
						neighborNode.printNeighborList();
						//System.out.println(tempNodeNew.peerName + " Added as neighbor to node" + neighborNode.getPeerName());
					} catch (NotBoundException e) {
						e.printStackTrace();
					}
				}
			}
		}
	
		//check if existing neighbors are still neighbors, remove those are not
		if(!tempNighborList.isEmpty()){
			for(NeighborNodes tempnode : tempNighborList){
				
				RemoteInterface neighborNode = null;
				List<NeighborNodes> nodeNeighborList = null;
				Registry myReg = LocateRegistry.getRegistry(tempnode.ipAddress, tempnode.port);
				try {
					neighborNode = (RemoteInterface) myReg.lookup("myboot");
					nodeNeighborList = neighborNode.getNeighbor();
					neighborNode.removeNode(this.getPeerName());
				} catch (NotBoundException e) {
					e.printStackTrace();
				}
				
				//System.out.println("checking neighbor for " + tempnode.peerName);
				if(checkIfNeighbors(this.getZone(), tempnode.getZone())){
					this.neighborList.add(tempnode);
					this.setNeighbor(this.neighborList);
					//send notification for change zone
					neighborNode.addNeighborNode(tempNodeOld);
					//System.out.println(tempnode.getPeerName()  +" Added to neighborList of " + this.peerName);
				}
				else{
					//System.out.println(tempnode.peerName +" not neighbor of " + this.peerName);
					this.printZoneInfo();
						for(NeighborNodes n : nodeNeighborList)
							//System.out.println("Neighbor of " + neighborNode.getPeerName() + " is " + n.getPeerName() +" after removel of " + this.peerName);
						neighborNode.printNeighborList();
						//System.out.println(this.peerName +" Removed from neighborList of " + neighborNode.getPeerName());
				}
			}
		}
	}
	
	public void divideHashMap(ClientNode clientNode, RemoteInterface rInt) throws RemoteException{
		for(Map.Entry<String, Boolean> entry : this.fileMap.entrySet()){
			
			String key = entry.getKey();
			int x = this.getXCoordinateOfFile(key);
			int y = this.getYCoordinateOfFile(key);
			System.out.println("checking for file " + key + " x,y: " + x + y);
			
			if(rInt.checkInZone(x, y, rInt.getZone())){
				//File in zone of new node, copy into hash map of new node and transfer
				rInt.addInHashMap(key);
				//System.out.println("In zone of new node " + rInt.getPeerName());
				rInt.printZoneInfo();
				
				File fs = new File(key);
				try {
					FileInputStream fin = new FileInputStream(fs);
					byte [] fileData = new byte[(int) fs.length()];
					int fileLen = fin.read(fileData);
					rInt.getData(fs.getName(), fileData, fileLen);
				} catch (FileNotFoundException e) {
					System.out.println("File Not found!!!");
					e.printStackTrace();
				} catch (IOException e) {
					System.out.println("Input output exception");
					e.printStackTrace();
				}
				this.fileMap.remove(key);
				//System.out.println(key + "removed from " + this.getPeerName());
			}
		}
	}

	public boolean checkIfNeighbors(ZoneCoordinates newZone, ZoneCoordinates neighborZone) {
		if(checkUpperLowerSide(newZone.getX4(), newZone.getY4(), newZone.getX3(), newZone.getY3(), 
				neighborZone.getX1(), neighborZone.getY1(), neighborZone.getX2(), neighborZone.getY2()) 
			|| checkUpperLowerSide(newZone.getX1(), newZone.getY1(), newZone.getX2(), newZone.getY2(), 
					neighborZone.getX4(), neighborZone.getY4(), neighborZone.getX3(), neighborZone.getY3()) 
			||	checkSideNodes(newZone.getX2(), newZone.getY2(), newZone.getX3(), newZone.getY3(), 
							neighborZone.getX1(), neighborZone.getY1(), neighborZone.getX4(), neighborZone.getY4())	
			||	checkSideNodes(newZone.getX1(), newZone.getY1(), newZone.getX4(), newZone.getY4(), 
					neighborZone.getX2(), neighborZone.getY2(), neighborZone.getX3(), neighborZone.getY3())){
			return true;
		}
		return false;
	}

	public boolean checkSideNodes(float n1x1, float n1y1, float n1x2, float n1y2,
			float n2x1, float n2y1, float n2x2, float n2y2) {
		
		
		
		if(n1x1 == n2x1){
			if((n2y1 >= n1y2) || (n2y2 <= n1y1))
				return false;
			return true;
		}

				return false;
	}
	
	public boolean checkUpperLowerSide(float n1x1, float n1y1, float n1x2, float n1y2,
			float n2x1, float n2y1, float n2x2, float n2y2) {

		
		
		if(n1y1 == n2y1){
			if((n1x1 >= n2x2) || (n1x2 <= n2x1))
				return false;
			return true;
		}
		
		return false;
	}

	public boolean join(int x, int y, String ipAddress, String peerName, int port) throws RemoteException{
		//System.out.println("Join request for node " + peerName + " " + port + "at x,y:" + x +"," + y + " on node " + this.getPeerName());
		if(checkInZone(x,y, this.getZone())){
			//System.out.println("In zone");
			try {
				Registry myReg = LocateRegistry.getRegistry(ipAddress, port);
				rInt = (RemoteInterface) myReg.lookup("myboot");
				this.divideZone(x,y, rInt);
				this.updateNeighborsList(rInt);
				this.printZoneInfo();
				this.printNeighborList();
			} catch (NotBoundException e) {
				e.printStackTrace();
			}
			return true;
		}
		//Forward to shortest neighbor
		else{
			NeighborNodes inNeighbourNode = this.checkInNeighbvorZone(x,y);
			if(inNeighbourNode!=null){
				try {
					Registry myReg = LocateRegistry.getRegistry(inNeighbourNode.getIpAddress(), inNeighbourNode.getPort());
					rInt = (RemoteInterface) myReg.lookup("myboot");
					//System.out.println("In Zone of neighbor so Calling join of node " + inNeighbourNode.getPeerName() +  " from node" + this.peerName + " for " + peerName);
					rInt.join(x, y, ipAddress, peerName, port);
				} catch (NotBoundException e) {
					e.printStackTrace();
				}
			}
			else{
				inNeighbourNode = this.findShortestNeighborNode(x,y);
				//System.out.println("Forwarding join request to shortest node " + inNeighbourNode.peerName +  " from node" + this.peerName + " for " + peerName);
				try {
					Registry myReg = LocateRegistry.getRegistry(inNeighbourNode.getIpAddress(), inNeighbourNode.getPort());
					rInt = (RemoteInterface) myReg.lookup("myboot");
					rInt.join(x, y, ipAddress, peerName, port);
				} catch (NotBoundException e) {
					e.printStackTrace();
				}
			}
		return true;
		}
	}

	public NeighborNodes findShortestNeighborNode(int x, int y) {
		NeighborNodes shortestNode = null;
		double min = 100;
		double distance=0;
		//Find shortest node along along X-axis
		for(NeighborNodes node : neighborList){
			distance = Math.sqrt(Math.abs(Math.pow(node.getZone().x1, 2) - Math.pow(x, 2)) + Math.abs(Math.pow(node.getZone().y1, 2) - Math.pow(y, 2)));
			if(distance < min){
				shortestNode = node;
				min = distance;
			}
		}
		return shortestNode;
	}
	
	public NeighborNodes checkInNeighbvorZone(int x, int y) {
		List<NeighborNodes> nodeList = this.getNeighbor();
		for(NeighborNodes node : nodeList){
			if(x <= node.getZone().x2 && x >= node.getZone().x1 && y <= node.getZone().y3 && y >= node.getZone().y2){
				return node;
			}
		}
		return null;
	}

	public boolean checkInZone(int x, int y, ZoneCoordinates zoneCoordinates) throws RemoteException{
		if(x <= zoneCoordinates.x2 && x >= zoneCoordinates.x1 && y <= zoneCoordinates.y3 && y >= zoneCoordinates.y2)
			return true;
		return false;
	}

	public void printNeighborList() {
		if(!this.neighborList.isEmpty()){
			System.out.println("NeighborList of " + this.peerName);
			for(NeighborNodes node : this.neighborList)
				System.out.print(" " + node.peerName + "\n");
		}
		else{
			System.out.println("NeighborList of " + this.peerName + " is empty");
		}
	}
	
	public void displayNodeInfo(NeighborNodes node, List<NeighborNodes> list2) throws RemoteException{
		RemoteInterface c = null;
		try{			
			Registry myReg = LocateRegistry.getRegistry(node.ipAddress, node.port);
			c = (RemoteInterface) myReg.lookup("myboot");
		}catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Peer Name: " + node.getPeerName());
		System.out.println("IP Address: " + node.getIpAddress());
		System.out.println("Node Name: " + node.getPeerName() + " port: " + node.getPort());
		System.out.println("Neighbors are: ");
		for(NeighborNodes node1 : list2){
			System.out.println(node1.getPeerName() + " ");
		}
		Map<String, Boolean> map = c.getFileMap();
		
		System.out.println("Stored Files: ");
		for(Map.Entry<String, Boolean> entry: map.entrySet()){
			String key = entry.getKey();
			System.out.println( key);
		}
	}
	
	public boolean viewByPeerName(String peerName) throws RemoteException{
		Queue<NeighborNodes> queue = new LinkedList<NeighborNodes>();
		Map<String, Boolean> nodeMap = new HashMap<String, Boolean>();
		NeighborNodes nodeInfo = this.getNodeInfo();
		if(this.peerName.equals(peerName)){
			this.displayNodeInfo(this.getNodeInfo(), this.getNeighbor());
			//this.printInformation(this);
			return true;
		}
		nodeMap.put(nodeInfo.getPeerName(), true);
		queue.add(nodeInfo);
		while(!queue.isEmpty()){
			NeighborNodes node = queue.remove();
			RemoteInterface c = null;
			try{			
				Registry myReg = LocateRegistry.getRegistry(node.ipAddress, node.port);
				c = (RemoteInterface) myReg.lookup("myboot");
			}catch (Exception e) {
				e.printStackTrace();
			}
			List<NeighborNodes> list = c.getNeighbor();
			for(NeighborNodes node1 : list){
				if(nodeMap.get(node1.getPeerName())==null){
					nodeMap.put(node1.getPeerName(), true);
					queue.add(node1);
					if(node1.peerName.equals(peerName)){
						RemoteInterface c1 = null;
						try{			
							Registry myReg = LocateRegistry.getRegistry(node1.ipAddress, node1.port);
							c1 = (RemoteInterface) myReg.lookup("myboot");
						}catch (Exception e) {
							e.printStackTrace();
						}
						this.displayNodeInfo(node1, c1.getNeighbor());
						//this.printInformation(c1);
						return true;
					}
				}
			}
		}
		return false;
	}

	public void viewAllNodesInfo() throws RemoteException {
		Queue<NeighborNodes> queue = new LinkedList<NeighborNodes>();
		Map<String, Boolean> nodeMap = new HashMap<String, Boolean>();
		NeighborNodes nodeInfo = this.getNodeInfo();
		
		nodeMap.put(nodeInfo.getPeerName(), true);
		queue.add(nodeInfo);
		this.displayNodeInfo(this.getNodeInfo(), this.getNeighbor());
		while(!queue.isEmpty()){
			NeighborNodes node = queue.remove();
			RemoteInterface c = null;
			try{			
				Registry myReg = LocateRegistry.getRegistry(node.ipAddress, node.port);
				c = (RemoteInterface) myReg.lookup("myboot");
			}catch (Exception e) {
				e.printStackTrace();
			}
			List<NeighborNodes> list = c.getNeighbor();
			for(NeighborNodes node1 : list){
				if(nodeMap.get(node1.getPeerName())==null || (nodeMap.get(node1.getPeerName()) == false)){
					nodeMap.put(node1.getPeerName(), true);
					queue.add(node1);
						RemoteInterface c1 = null;
						try{			
							Registry myReg = LocateRegistry.getRegistry(node1.ipAddress, node1.port);
							c1 = (RemoteInterface) myReg.lookup("myboot");
						}catch (Exception e) {
							e.printStackTrace();
						}
						this.displayNodeInfo(node1, c1.getNeighbor());
				}
			}
		}
	}

	public void sendMeFile(String fileName, List<NeighborNodes> list) throws RemoteException{
		System.out.println("Node found. Route for file: ");
		for(NeighborNodes node : list)
			System.out.print(node.getPeerName());
		RemoteInterface c1 = null;
		int listIndex = list.size()-1;
		try{			
			Registry myReg = LocateRegistry.getRegistry(list.get(listIndex).getIpAddress(), list.get(listIndex).getPort());
			c1 = (RemoteInterface) myReg.lookup("myboot");
		}catch (Exception e) {
			e.printStackTrace();
		}

		File fs = new File("/Users/pravin/Documents/workspace/Client/"+fileName);
		try {
			FileInputStream fin = new FileInputStream(fs);
			byte [] fileData = new byte[(int) fs.length()];
			int fileLen = fin.read(fileData);
			//while(fileLen > 0){
				c1.getData(fs.getName(), fileData, fileLen);
				//fileLen=fin.read(fileData);
			//}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(fileName  + " file created on " + c1.getPeerName());
	}
	
	public boolean getData(String fName, byte[] fileData, int fileLen) throws RemoteException{
		//File f = new File(fName);
		File f = new File(fName);
		try {
			f.createNewFile();
			FileOutputStream out = new FileOutputStream(f, true);
			out.write(fileData);
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(fName  + " file copied to " + this.getPeerName());
		this.fileMap.put(fName, true);
		return true;
	}
	
	public void serachInNextNode(int x, int y, String fileName, List<NeighborNodes> list) throws RemoteException{
		list.add(this.getNodeInfo());
		if(this.checkInZone(x, y, this.zone)){
			//insert in this node get first peer name
			NeighborNodes node = list.get(0);
			RemoteInterface c1 = null;
			try{			
				Registry myReg = LocateRegistry.getRegistry(node.ipAddress, node.port);
				c1 = (RemoteInterface) myReg.lookup("myboot");
			}catch (Exception e) {
				e.printStackTrace();
			}
			c1.sendMeFile(fileName, list);
			return;
		}
		
		NeighborNodes nodeNeighbor = this.checkInNeighbvorZone(x, y);
		if(nodeNeighbor!=null){
			//send to this node searchInNextZone
			RemoteInterface c1 = null;
			try{			
				Registry myReg = LocateRegistry.getRegistry(nodeNeighbor.ipAddress, nodeNeighbor.port);
				c1 = (RemoteInterface) myReg.lookup("myboot");
			}catch (Exception e) {
				e.printStackTrace();
			}
			c1.serachInNextNode(x, y, fileName, list);
			return;
		}
		
		NeighborNodes shortNode = this.findShortestNeighborNode(x, y);
		
		RemoteInterface c2 = null;
		try{			
			Registry myReg = LocateRegistry.getRegistry(shortNode.ipAddress, shortNode.port);
			c2 = (RemoteInterface) myReg.lookup("myboot");
		}catch (Exception e) {
			e.printStackTrace();
		}
		c2.serachInNextNode(x, y, fileName, list);
		return;
	}
	
	public void insertFileToNode(String fileName) throws RemoteException{
		//List<String> list = new ArrayList<String>();
		//list.add(this.peerName);
		int x = this.getXCoordinateOfFile(fileName);
		int y = this.getYCoordinateOfFile(fileName);
		System.out.println("File X: " + x + "File Y: " + y);
		this.serachInNextNode(x,y,fileName, new ArrayList<NeighborNodes>());
	}

	public void displayFileInformation(String fileName, List<NeighborNodes> list, boolean found) throws RemoteException{
		
		int lastIndex = list.size()-1;
		if(!found){
			System.out.println(fileName + " File Not found!!");
			return;
		}
		System.out.println("File found on " + list.get(lastIndex).getPeerName());
		System.out.println("Route: ");
		for(NeighborNodes node : list)
			System.out.print(" " + node.getPeerName());
	}
		
	public void searchFileInNeighbor(int x, int y, String fileName, List<NeighborNodes> list) throws RemoteException{
		list.add(this.getNodeInfo());
		boolean acknowledge = true;
		if(this.checkInZone(x, y, this.zone)){
			//insert in this node get first peer name
			NeighborNodes node = list.get(0);
			RemoteInterface c1 = null;
			try{			
				Registry myReg = LocateRegistry.getRegistry(node.ipAddress, node.port);
				c1 = (RemoteInterface) myReg.lookup("myboot");
			}catch (Exception e) {
				e.printStackTrace();
			}
			if(!this.getFileMap().containsKey(fileName)){
				//System.out.println("File " + fileName + " not present in HashMap of node " + this.getPeerName());
				//int listIndex = list.size()-1;
				//list.add(listIndex, null);
				acknowledge = false;
			}
			c1.displayFileInformation(fileName, list, acknowledge);
			return;
		}
		
		NeighborNodes nodeNeighbor = this.checkInNeighbvorZone(x, y);
		if(nodeNeighbor!=null){
			//send to this node searchInNextZone
			RemoteInterface c1 = null;
			try{			
				Registry myReg = LocateRegistry.getRegistry(nodeNeighbor.ipAddress, nodeNeighbor.port);
				c1 = (RemoteInterface) myReg.lookup("myboot");
			}catch (Exception e) {
				e.printStackTrace();
			}
			c1.searchFileInNeighbor(x, y, fileName, list);
			return;
		}
		
		NeighborNodes shortNode = this.findShortestNeighborNode(x, y);
		
		RemoteInterface c2 = null;
		try{			
			Registry myReg = LocateRegistry.getRegistry(shortNode.ipAddress, shortNode.port);
			c2 = (RemoteInterface) myReg.lookup("myboot");
		}catch (Exception e) {
			e.printStackTrace();
		}
		c2.searchFileInNeighbor(x, y, fileName, list);
		return;
	
	}
	
	public void searchFileInCAN(String fileName) throws RemoteException{
		int x = this.getXCoordinateOfFile(fileName);
		int y = this.getYCoordinateOfFile(fileName);
		System.out.println("File X: " + x + "File Y: " + y);
		this.searchFileInNeighbor(x, y, fileName, new ArrayList<NeighborNodes>());
		
	}
	public int getYCoordinateOfFile(String fileName) {
		int sum = 0;
		for(int i=1; i<fileName.length(); i+=2){
			sum += fileName.charAt(i);
		}
		return sum%10;
	}

	public int getXCoordinateOfFile(String fileName) {
		int sum = 0;
		for(int i=0; i<fileName.length(); i+=2){
			sum += fileName.charAt(i);
		}
		return sum%10;
	}
	
	public String checkSideOfNode(NeighborNodes node){
		
		ZoneCoordinates zonenode = node.getZone();
		ZoneCoordinates zone = this.getZone();
		
		if(zonenode.getX1() == zone.getX4() && zonenode.getX2() == zone.getX3()){
			return "upper";
		}
		if(zonenode.getX4() == zone.getX1() && zonenode.getX3() == zone.getX2()){
			return "lower";
		}
		if(zonenode.getY2() == zone.getY1() && zonenode.getY3() == zone.getY4()){
			return "left";
		}
		if(zonenode.getY1() == zone.getY2() && zonenode.getY4() == zone.getY3()){
			return "right";
		}
		return null;
	}
	
	public void mergeZone(String side, NeighborNodes node) throws RemoteException{
		RemoteInterface c1 = null;
		ZoneCoordinates zoneNode = null;
		try{			
			Registry myReg = LocateRegistry.getRegistry(node.ipAddress, node.port);
			c1 = (RemoteInterface) myReg.lookup("myboot");
			zoneNode = c1.getZone();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		if(side.equals("upper")){
			zoneNode.setY1(this.zone.getY1());
			zoneNode.setY2(this.zone.getY2());
		}
		if(side.equals("lower")){
			zoneNode.setY4(this.zone.getY4());
			zoneNode.setY3(this.zone.getY3());
		}
		if(side.equals("left")){
			zoneNode.setX2(this.zone.getX2());
			zoneNode.setX3(this.zone.getX3());
		}
		if(side.equals("right")){
			zoneNode.setX1(this.zone.getX1());
			zoneNode.setX4(this.zone.getX4());
		}
		c1.setZone(zoneNode);
		NeighborNodes tempNode = new NeighborNodes();
		tempNode.ipAddress = c1.getIpAddress();
		tempNode.peerName = c1.getPeerName();
		tempNode.port = c1.getPort();
		tempNode.zone = c1.getZone();
		tempNode.listOfNeighbors = c1.getNeighbor();
		
		//c1.updateZoneInNeighbrList(tempNode);
		//System.out.println("New Zone of " + node.getPeerName());
		c1.printZoneInfo();
	}
	
	public void transferNeighborList(NeighborNodes node, List<NeighborNodes> list) throws RemoteException{
		RemoteInterface c1 = null;
		try{			
			Registry myReg = LocateRegistry.getRegistry(node.ipAddress, node.port);
			c1 = (RemoteInterface) myReg.lookup("myboot");
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		List<NeighborNodes> list1 = c1.getNeighbor();
		String removePeer = this.getPeerName();
		for(NeighborNodes node1 : list){
			RemoteInterface c2 = null;
			try{			
				Registry myReg = LocateRegistry.getRegistry(node1.ipAddress, node1.port);
				c2 = (RemoteInterface) myReg.lookup("myboot");
			}catch (Exception e) {
				e.printStackTrace();
			}
			c2.removeNode(this.getPeerName());
			c1.addNeighborNode(node1);
		}
	}
	
	public void copyAllFilesToMerger(NeighborNodes node) throws RemoteException{
		RemoteInterface rInt = null;
		try{			
			Registry myReg = LocateRegistry.getRegistry(node.ipAddress, node.port);
			rInt = (RemoteInterface) myReg.lookup("myboot");
		}catch (Exception e) {
			e.printStackTrace();
		}
		for(Map.Entry<String, Boolean> entry : this.fileMap.entrySet()){
			String key = entry.getKey();
			System.out.println("Copying " + key + " to " + rInt.getPeerName());
				//File in zone of new node, copy into hash map of new node and transfer
				rInt.addInHashMap(key);
				File fs = new File("/Users/pravin/Documents/workspace/Client/"+key);
				try {
					FileInputStream fin = new FileInputStream(fs);
					byte [] fileData = new byte[(int) fs.length()];
					int fileLen = fin.read(fileData);
					rInt.getData(fs.getName(), fileData, fileLen);
				} catch (FileNotFoundException e) {
					System.out.println("File Not found!!!");
					e.printStackTrace();
				} catch (IOException e) {
					System.out.println("Input output exception");
					e.printStackTrace();
				}
				this.fileMap.remove(key);
				//System.out.println(key + "removed from " + this.getPeerName());
		}
	}
	
	public void leaveNetwork() throws RemoteException{
		List<NeighborNodes> list = this.getNeighbor();
		boolean sideFound = false;
		NeighborNodes node = null;
		String side = null;
		for(NeighborNodes node1 : list){
			side = checkSideOfNode(node1);
			if(side!=null){
				//System.out.println("Side:" + side);
				node = node1;
				break;
			}
		}
		if(node!=null){
			System.out.println("Merging with " + node.getPeerName());
			this.copyAllFilesToMerger(node);
			this.mergeZone(side, node);
			this.transferNeighborList(node, list);
		}
		else{
			//System.out.println("No node is aligned");
		}
	}

	public void printAllFiles() throws RemoteException{
		System.out.println("Stored Files: ");
		for(Map.Entry<String, Boolean> entry: fileMap.entrySet()){
			String key = entry.getKey();
			System.out.println( key);
		}
	}
	
	public void printInformation() throws RemoteException{
		System.out.println("Node Identifier: " + this.getPeerName());
		System.out.println("Node IP: " + this.getIpAddress());
		ZoneCoordinates zone = this.getZone();
		System.out.println("Corordinates: "+ zone.x1 + " " + zone.y1 + " "+ zone.x2 + " " + zone.y2  + " " + zone.x3 + " " + zone.y3 + " "+ zone.x4 + " " + zone.y4);
//		List<NeighborNodes> list = this.getNeighbor();
//		
//		if(!list.isEmpty()){
//			System.out.println("Neighbor Nodes: ");
//			for(NeighborNodes node : this.getNeighbor())
//				System.out.print(" " + node.peerName);
//		}
//		else{
//			System.out.println("NeighborList is empty!");
//		}
//		Map<String, Boolean> map = this.getFileMap();
//		if(map!=null){
//			System.out.println("Stored Files: ");
//			for(Map.Entry<String, Boolean> entry: map.entrySet()){
//				String key = entry.getKey();
//				System.out.println( key);
//			}
//		}
//		else{
//			System.out.println("No file to display!");
//		}
	}
}
