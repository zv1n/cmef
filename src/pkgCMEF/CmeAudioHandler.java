package pkgCMEF;

//====================================================================
/**
 * CmeAudioHandler
 * Handles the Audio functions for a given state.
 * 
 * @author Terry Meacham
 * @version 2.0 Date: May, 2011
 */
// ===================================================================
class CmeAudioHandler {
  private CmeState m_CurState;
  
  // The only reason these are here and not in the AudioPlay is that they
  // may change per sequence and per item.  So every time play is called,
  // these should be reprocessed.
  public String m_BiasVolume;
  public String m_AudioVolume;
  public String m_AudioPath;
  public CmeAudioPlayer m_AudioPlayer = new CmeAudioPlayer();
  
  public CmeAudioHandler(CmeState state) throws Exception {
    m_CurState = state;
    
    String play = m_CurState.getStringProperty("PlayAudio");
    if (play == null)
      return;

    String stop = m_CurState.getStringProperty("StopAudio");
    
    m_BiasVolume = m_CurState.getStringProperty("BiasVolume");
    if (m_BiasVolume == null)
      m_BiasVolume = "0";

    m_AudioVolume = m_CurState.getStringProperty("AudioVolume");
    if (m_AudioVolume == null)
      m_AudioVolume = "0";

    m_AudioPath = m_CurState.getStringProperty("AudioPath");
    if (m_AudioPath == null)
      m_AudioPath = "$Pair1B";
  }

  public void playAudio() throws Exception {
    System.err.println("Load file");
    m_AudioPlayer.loadFile(getAudioPath());

    if (!m_AudioPlayer.canPlay())
      throw new Exception("Invalid file played: " + getAudioPath().toString());

    m_AudioPlayer.setVolume(getBiasVolume() + getAudioVolume());

    m_AudioPlayer.play();
  }

  private float getBiasVolume() throws Exception {
    return getFloat(m_BiasVolume, 0.5f);
  }

  private float getAudioVolume() throws Exception {
    return getFloat(m_AudioVolume, 0.0f);
  }
  
  private float getFloat(String vol, float df) throws Exception {
    vol = m_CurState.translate(vol);
    if (vol != null) {
      try {
        df = Float.parseFloat(vol);
      } catch(Exception ex) {
        throw new Exception("Invalid Decimal provided!" +
          "Must be a decimal 0.0 to 1.0.");
      }
    }
    
    return df;
  }

  private String getAudioPath() {
    return m_CurState.translate(m_AudioPath);
  }
}
