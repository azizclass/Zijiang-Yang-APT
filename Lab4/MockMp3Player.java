import java.util.ArrayList;

public class MockMp3Player implements Mp3Player{
	
	private ArrayList playList;
	private boolean isPlaying = false;
	private double position = 0.0;
	private int songIndex = 0;
	
	 /** 
	   * Begin playing the filename at the top of the
	   * play list, or do nothing if playlist 
	   * is empty. 
	   */
	  public void play(){
		  if(playList == null || playList.size() == 0) 
			  return;
		  isPlaying = true;
		  position += 1.0;
	  }

	  /** 
	   * Pause playing. Play will resume at this spot. 
	   */
	  public void pause(){
		  isPlaying = false;
	  }

	  /** 
	   * Stop playing. The current song remains at the
	   * top of the playlist, but rewinds to the 
	   * beginning of the song.
	   */
	  public void stop(){
		  isPlaying = false;
		  position = 0.0;
	  }
	  
	  /** Returns the number of seconds into 
	   * the current song.
	   */
	  public double currentPosition(){
		  return position;
	  }


	  /**
	   * Returns the currently playing file name.
	   */
	  public String currentSong(){
		  if(playList == null || playList.size() == 0)
			  return null;
		  return (String)playList.get(songIndex);
	  }

	  /** 
	   * Advance to the next song in the playlist 
	   * and begin playing it.
	   */
	  public void next(){
		  if(playList == null || playList.size() == 0)
			  return;
		  songIndex ++;
		  if(songIndex >= playList.size())
			  songIndex = playList.size()-1;
		  position = 0.0;
		  play();
	  }

	  /**
	   * Go back to the previous song in the playlist
	   * and begin playing it.
	   */
	  public void prev(){
		  if(playList == null || playList.size() == 0)
			  return;
		  songIndex --;
		  if(songIndex < 0)
			  songIndex = 0;
		  position = 0.0;
		  play();
	  }

	  /** 
	   * Returns true if a song is currently 
	   * being played.
	   */
	  public boolean isPlaying(){
		  return isPlaying;
	  }

	  /**
	   * Load filenames into the playlist.
	   */
	  public void loadSongs(ArrayList names){
		  playList = names;
	  }
}