package pkgCMEF;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

class CmeAudioPlayer {
  private Clip m_Clip = null;
  private String m_currentPath = null;
  private boolean m_bCanPlay = false;

  static float getGainFromPercent(int perc, float maxbias) {
    
    try {
      Clip clp = AudioSystem.getClip();
      FloatControl gc = (FloatControl) clp.getControl(
        FloatControl.Type.MASTER_GAIN);

      float max = gc.getMaximum() - maxbias;
      float min = Math.max(-40.0f, gc.getMinimum());

      float mag = max - min;
      mag *= ((float)perc)/100.0f;
      mag += min;

      return mag;
    }
    
    catch (LineUnavailableException e) {
      e.printStackTrace();
      System.err.println("Line Unavailable for audio file.");
      return 0.0f;
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Exception while loading audio file.");
      return 0.0f;
    }

  }

  public void ensureClipExists() throws Exception {
    if (m_Clip == null)
      m_Clip = AudioSystem.getClip();
  }

  private boolean _loadFile() {
    File file = new File(m_currentPath);
    
    if (!file.exists()) {
      System.err.println("Audio file does not exist.");
      return false;
    }
    
    try {
      ensureClipExists();

      AudioInputStream audioStream =
        AudioSystem.getAudioInputStream(file);

      m_Clip.close();
      m_Clip.open(audioStream);
    }
    
    catch (IOException e) {
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
    vol = getNormalizedVolume(vol);

    FloatControl gc = (FloatControl) m_Clip.getControl(
      FloatControl.Type.MASTER_GAIN);

    gc.setValue(vol);
  }

  public float getVolume() {
    if (m_Clip != null) {
      FloatControl gc = (FloatControl) m_Clip.getControl(
        FloatControl.Type.MASTER_GAIN);
      return gc.getValue();
    }

    return 0.0f;
  }

  public float getNormalizedVolume(float vol) {

    try {
      ensureClipExists();
    }
    
    catch (IOException e) {
      e.printStackTrace();
      System.err.println("IO Error for audio file.");
      return 0.0f;
    } catch (LineUnavailableException e) {
      e.printStackTrace();
      System.err.println("Line Unavailable for audio file.");
      return 0.0f;
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Exception while loading audio file.");
      return 0.0f;
    }


    FloatControl gc = (FloatControl) m_Clip.getControl(
      FloatControl.Type.MASTER_GAIN);

    vol = Math.min(vol, gc.getMaximum());
    vol = Math.max(vol, gc.getMinimum());

    return vol;
  }

  public void stop() {
    if (m_Clip.isRunning())
      m_Clip.stop();
  }
}