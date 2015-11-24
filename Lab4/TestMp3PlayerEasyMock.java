/**
 * Excerpted from the book, "Pragmatic Unit Testing"
 * ISBN 0-9745140-1-2
 * Copyright 2003 The Pragmatic Programmers, LLC.  All Rights Reserved.
 * Visit www.PragmaticProgrammer.com
 */
import static org.easymock.EasyMock.*;

public class TestMp3PlayerEasyMock extends TestMp3Player {

  public void setUp() {
	  super.setUp();
	  mp3 = createMock(Mp3Player.class);
  }

  public void testPlay() {
	  mp3.loadSongs(list);
	  expectLastCall();
	  expect(mp3.isPlaying()).andReturn(false);
	  mp3.play();
	  expectLastCall();
	  expect(mp3.isPlaying()).andReturn(true);
	  expect(mp3.currentPosition()).andReturn(1.0);
	  mp3.pause();
	  expectLastCall();
	  expect(mp3.currentPosition()).andReturn(1.0);
	  mp3.stop();
	  expectLastCall();
	  expect(mp3.currentPosition()).andReturn(0.0);
	  replay(mp3);
	  super.testPlay();
  }

  public void testPlayNoList() {
	  expect(mp3.isPlaying()).andReturn(false);
	  mp3.play();
	  expectLastCall();
	  expect(mp3.isPlaying()).andReturn(false);
	  expect(mp3.currentPosition()).andReturn(0.0);
	  mp3.pause();
	  expectLastCall();
	  expect(mp3.currentPosition()).andReturn(0.0);
	  expect(mp3.isPlaying()).andReturn(false);
	  mp3.stop();
	  expectLastCall();
	  expect(mp3.currentPosition()).andReturn(0.0);
	  expect(mp3.isPlaying()).andReturn(false);
	  replay(mp3);
	  super.testPlayNoList();
  }

  public void testAdvance() {
	  mp3.loadSongs(list);
	  expectLastCall();
	  mp3.play();
	  expectLastCall();
	  expect(mp3.isPlaying()).andReturn(true);
	  mp3.prev();
	  expectLastCall();
	  expect(mp3.currentSong()).andReturn((String)list.get(0));
	  expect(mp3.isPlaying()).andReturn(true);
	  mp3.next();
	  expectLastCall();
	  expect(mp3.currentSong()).andReturn((String)list.get(1));
	  mp3.next();
	  expectLastCall();
	  expect(mp3.currentSong()).andReturn((String)list.get(2));
	  mp3.prev();
	  expectLastCall();
	  expect(mp3.currentSong()).andReturn((String)list.get(1));
	  mp3.next();
	  expectLastCall();
	  expect(mp3.currentSong()).andReturn((String)list.get(2));
	  mp3.next();
	  expectLastCall();
	  expect(mp3.currentSong()).andReturn((String)list.get(3));
	  mp3.next();
	  expectLastCall();
	  expect(mp3.currentSong()).andReturn((String)list.get(3));
	  expect(mp3.isPlaying()).andReturn(true);
	  replay(mp3);
	  super.testAdvance();
  }

}
