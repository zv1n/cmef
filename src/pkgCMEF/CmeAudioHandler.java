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
  private CmeApp m_App;

  // The only reason these are here and not in the AudioPlay is that they
  // may change per sequence and per item.  So every time play is called,
  // these should be reprocessed.
  public CmeAudioPlayer m_AudioPlayer = new CmeAudioPlayer();
  
  public CmeAudioHandler(CmeState state, CmeApp app) throws Exception {
    m_CurState = state;
    m_App = app;
  }

  public void playAudio() throws Exception {
    m_AudioPlayer.loadFile(getAudioPath());

    if (!m_AudioPlayer.canPlay())
      throw new Exception("Invalid file played: " + getAudioPath().toString());

    m_AudioPlayer.setVolume(getBiasVolume() + getAudioVolume());

    System.err.println("Volume:");
    System.err.println(m_AudioPlayer.getVolume());

    m_AudioPlayer.play();
  }

  private float getBiasVolume() throws Exception {
    return getFloat("BiasVolume", 0.0f);
  }

  private float getAudioVolume() throws Exception {
    return getFloat("AudioVolume", 0.0f);
  }

  private String getAudioPath() throws Exception {
    return m_CurState.translate(getString("AudioPath", "$Pair1B"));
  }

  public void setActualAudioProperty(String property) throws Exception {
    float vol = m_AudioPlayer.getNormalizedVolume(getBiasVolume() + getAudioVolume());
    System.err.println("Setting volume");
    m_CurState.setProperty(property, String.valueOf(vol));
  }

  private float getFloat(String prop, float df) throws Exception {
    String vol = getStringProperty(prop);

    if (vol == null)
      return df;

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
  
  
  private String getString(String prop, String df) throws Exception {
    String vol = getStringProperty(prop);
    if (vol == null)
      vol = df;
    vol = m_CurState.translate(vol);
    return vol;
  }

  private String getStringProperty(String prop) {
    String ret = m_CurState.getStringProperty(prop);

    if (ret == null)
      ret = (String) m_App.getProperty(prop);

    return ret;
  }
}
