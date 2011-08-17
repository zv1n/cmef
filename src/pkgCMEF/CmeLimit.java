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
	
	public boolean start(int item);
	public boolean reset();
	public boolean stop(int item);
	public boolean isComplete();
	public boolean willComplete(int item);
	public boolean setEventResponse(CmeEventResponse response);
	
}
