package pkgCMEF;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

class CmeAudioPlayer {
  private Clip m_Clip = null;
  private String m_currentPath = null;
  private boolean m_bCanPlay = false;

  private boolean _loadFile() {
    File file = new File(m_currentPath);
    
    if (!file.exists()) {
      System.err.println("Audio file does not exist.");
      return false;
    }
    
    try {
      if (m_Clip == null)
        m_Clip = AudioSystem.getClip();
      AudioInputStream audioStream =
        AudioSystem.getAudioInputStream(file);
      m_Clip.open(audioStream);
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println("IO Error for audio file.");
      return false;
    } catch (LineUnavailableException e) {
      e.printStackTrace();
      System.err.println("Line Unavailable for audio file.");
      return false;
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Exception while loading audio file.");
      return false;
    }

    return true;
  }

  public boolean canPlay() {
    return m_bCanPlay;
  }

  public boolean loadFile(String path) {
    m_currentPath = path;
    return (m_bCanPlay = _loadFile());
  }

  public void play() {
    if (m_bCanPlay)
      m_Clip.start();
  }

  public void setVolume(float vol) {
    if (m_Clip != null) {
      FloatControl gc = (FloatControl) m_Clip.getControl(
        FloatControl.Type.MASTER_GAIN);
      gc.setValue(vol);
    }
  }

  public void stop() {
    if (m_Clip.isRunning())
      m_Clip.stop();
  }
}