package Midi;

import Musica.NotnyStan;
import Tools.KeyEventHandler;

import	javax.sound.midi.MidiMessage;
import	javax.sound.midi.ShortMessage;
import	javax.sound.midi.Receiver;



/**	Displays the file format information of a MIDI file.
 */
public class DumpReceiver implements Receiver {

	public static long seByteCount = 0;
	public static long smByteCount = 0;
	public static long seCount = 0;
	public static long smCount = 0;
	
	private KeyEventHandler eventHandler;
	
	public DumpReceiver(KeyEventHandler eventHandler) {
	    this.eventHandler = eventHandler;
	}
	
	public void close() {}
	long sPrev = 0;
	static boolean pitch=false;

	public void send(MidiMessage message, long timestamp) {
		timestamp /= 1000; // from microseconds to milliseconds as i can judge
	    int elapsed = (int)(timestamp - sPrev);
	    sPrev = timestamp;
	
	    int tune = ((ShortMessage) message).getData1();
	    int forca = ((ShortMessage)message).getData2();
	
	    if (tune <= 32 || tune >= 100) {
	    	// Handle instrument change/pitch-bend/tune/etc // Actually, useless
	    	return;
	    }
	    this.eventHandler.handleMidi( tune, forca, elapsed, timestamp );
	}
}