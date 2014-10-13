package Pointiki;

import java.awt.image.BufferedImage;

import Musica.NotnyStan;

public abstract class Pointerable {
	public boolean underPtr = false;
	
	public int gsize = 1; // Сколько условных единиц эта хрень занимает на стане 
	
	public int cislic = 16;
	public int znamen = NotnyStan.DEFAULT_ZNAM;
	
	public Pointerable next;
	public Pointerable prev;
    public Pointerable retrieve;
	
	abstract public BufferedImage getImage();
    public Pointerable getNext() { return next; }
    public void setNext( Pointerable elem ) { next = elem; }
    
    public String slog = "";
    
    public Pointerable() {
    }
	
	public static int round(double n) {
    			if (n >= 96 + 16) return 128; // 
    	else	if (n >= 96 - 16) return 96; // 
    	else 	if (n >= 48 + 8) return 64; // 
    	else 	if (n >= 48 - 8) return 48; // 1/2 
    	else	if (n >= 24 + 4) return 32; 
    	else 	if (n >= 24 - 4) return 24; 
    	else 	if (n >= 12 + 2) return 16;
    	else	if (n >= 12 - 2) return 12; 
    	else 	if (n >= 6 + 1) return 8; 
    	else 	if (n >= 6 - 1) return 6;
    	else 					 return 4;
    }
	
	public abstract void changeDur(int i, boolean b);

    public boolean isTriol = false;
}