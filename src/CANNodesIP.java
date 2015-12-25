
public class CANNodesIP implements java.io.Serializable {
	
	
	public String ipAddress;
	public int port;
	public String peerName;
	
	public CANNodesIP(){
		ipAddress = null;
		port = 0;
		peerName = null;
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
	
}
