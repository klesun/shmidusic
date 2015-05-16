package Main;

import Gui.ImageStorage;
import Stuff.Midi.DeviceEbun;

import java.io.*;
public class Main {

	public static MajesticWindow window = null;

	public static void main(String[] args){
		String OS_NAME = System.getProperty("os.name");
		try {
			System.out.println(OS_NAME);
			if (OS_NAME.equals("Windows XP") || OS_NAME.equals("Windows 7")) { // looks like windows 7 has different encoding
				PrintStream out = new PrintStream(System.out, true, "Cp866");
				System.setOut(out);
			}
			System.out.println("Отведай же ещё этих сочных французских булок");
		} catch (Exception e) {
			System.out.println("blablablabalall");
		}

		// TODO: encapsulate somewhere
		ImageStorage.inst().loadImagesFromDisk();
		ImageStorage.inst().refreshImageSizes();
		DeviceEbun.openMidiDevices();

		window = new MajesticWindow();
		window.setVisible(true);
	}
}