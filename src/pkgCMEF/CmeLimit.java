/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkgCMEF;

/**
 *
 * @author terrymeacham
 */
public interface CmeLimit {
	
	public boolean start(String item);
	public boolean reset();
	public int stop();
	public boolean isComplete();
	public boolean willComplete(int item);
	public String  getElement();
	public boolean setEventResponse(CmeEventResponse response);
	
}
