
public class CANNodesIPClient implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String ipAddress;
	public int port;
	public String peerName;
	
	public CANNodesIPClient(){
		ipAddress = null;
		port = 0;
		peerName = null;
	}
	
}
