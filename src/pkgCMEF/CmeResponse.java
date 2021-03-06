package pkgCMEF;

public interface CmeResponse {
	/** Get the variable name associated with the response */
	public String getName();
	
	/** Get the variable group associated with the response */
	public String getGroup();

	/** Get the value associated with the response */ 
	public String getValue(); 

	/** Get the type of input field associated with this response */ 
	public int getType(); 
}
