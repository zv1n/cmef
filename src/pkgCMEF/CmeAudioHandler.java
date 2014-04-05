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
  
  public CmeAudioHandler(CmeState app) {
    m_CurState = app;
    
    String stop = m_CurState.getStringProperty("StopAudio");
    String volume = m_CurState.getStringProperty("BiasVolume");
    if (volume == null)
      volume = "0";

    String bvolume = m_CurState.getStringProperty("AudioVolume");
    if (bvolume == null)
      bvolume = "0";

    m_AudioPath = m_CurState.getStringProperty("AudioPath");
    if (m_AudioPath == null)
      m_AudioPath = "$Pair1B";

    String bvolf = getBiasVolume();
    float volf = getAudioVolume();

  }
}