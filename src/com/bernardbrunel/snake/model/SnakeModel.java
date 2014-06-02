package com.bernardbrunel.snake.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Observable;


/**
 * 
 * @author henetmal
 *
 *  .- - - - -> y
 *  |      N
 *  |     O+E
 *  |      S
 * \|/
 *  x
 */
public class SnakeModel extends Observable {
	private Dimension airDeJeux;
	private char dir;
	private int size;
	private ArrayList<Character> listDir =  new ArrayList<Character>();
	private volatile int timeNewCoord;
	
	private int lagg = 0;
	
	private int iSpeed = 0;
	private boolean speed = false;
	private int iBourre = 0;
	private boolean bourre = false;
	
	private ArrayList<Point> coords = new ArrayList<Point>();
	
	private boolean stopThread = false;
	private Thread deplacement = new Thread(new Runnable() { 
		public void run() {
	        Thread thisThread = Thread.currentThread();
	        
			while( deplacement == thisThread ) {
				setNewCoordonne();
				setNewDirection();

				updateSpeed();
				updateBourre();
				
				//System.out.println("\t\t\tAlive");

				try {
					if (speed){
						Thread.sleep(timeNewCoord*6/10);
					} else {
						Thread.sleep(timeNewCoord);
					}
				} catch (InterruptedException ex) {}
				
			}
		}
	});

	//-------------------------[CONSTRUCTEUR]
	private SnakeModel(Dimension dim){
		System.out.println("Constructeur de snake depuis 1923");
		this.airDeJeux = dim;
		this.size = 3;
		this.dir = 'N';
		this.listDir.add('N');
		this.timeNewCoord = 150;
		coords.add(new Point( (int)(dim.width/2), (int)(dim.height/2) ) ); 
	}
	
	//-------------------------[DEATHTRUCTEUR]
	public void finalize(){
	}

	@SuppressWarnings("deprecation")
	public synchronized void stop(){
		System.out.println("STOP snake");
		this.coords.clear();
		this.deplacement = null;
		instance = new SnakeModel(JardinModel.getInstance().getSize() );
	}
	public void start(){
		System.out.println("START snake ");
		deplacement.start();
	
	}
	
	//--------------------------------[SINGLETON]
	/** Donnée de classe contenant l'instance courante */
	private static SnakeModel instance = new SnakeModel(JardinModel.getInstance().getSize() );
	
	/** Singleton de la classe courante */ 
	public static SnakeModel getInstance() { return instance; }
	
	//--------------------------[COORDONNEES]
	public boolean setNewDirection(char p_dir){
		char justAvant = this.listDir.get(this.listDir.size()-1);
		if ( (dir == 'N') || (dir == 'S') || (dir == 'O') || (dir == 'E') ){
			if ( (justAvant == 'S')&&(p_dir == 'N')){ return false; }
			if ( (justAvant == 'N')&&(p_dir == 'S')){ return false; }
			if ( (justAvant == 'O')&&(p_dir == 'E')){ return false; }
			if ( (justAvant == 'E')&&(p_dir == 'O')){ return false; }
			for (int iLagg = 0; iLagg <lagg; iLagg++){
				this.listDir.add(p_dir);
			}
			this.listDir.add(p_dir);
			return true;
		} else {
			return false;
		}
	}
	
	public void setNewDirection(){
		this.dir = this.listDir.get(0);
		if (this.listDir.size() > 1){
			this.listDir.remove(0);
		}
	}
	
	public void setNewCoordonne(){
		Point lastFirstCoord = new Point(getHead());
		if (this.dir == 'N') { lastFirstCoord.y-= 1; }
		if (this.dir == 'S') { lastFirstCoord.y+= 1; }
		if (this.dir == 'O') { lastFirstCoord.x-= 1; }
		if (this.dir == 'E') { lastFirstCoord.x+= 1; }
		coords.add(lastFirstCoord);
		this.setChanged();
		this.notifyObservers(lastFirstCoord);
		supprimeAncienCoordonnee();
	}
	
	public void supprimeAncienCoordonnee(){
		if (coords.size() > size){
			Point pRm = new Point (coords.get(0));
			coords.remove(pRm);
			this.setChanged();
			this.notifyObservers(pRm);
		}
	}
	
	public Point getHead(){
		return coords.get(coords.size()-1);
	}

	public float getColor_h(){
		float colorReturn = 1f/3f;
		if (speed){
			colorReturn = 1f/8f;
		}
		if (bourre) {
			colorReturn = 2f/3f;
		}
		return colorReturn;
	}
	public Color getColor(){
		Color colorReturn = Color.green;
		if (speed){
			colorReturn = Color.yellow;
		}
		if (bourre) {
			colorReturn = Color.cyan;
		}
		return colorReturn;
	}
	
	public boolean isTheHead(Point suisJeIci){
		return getHead().equals(suisJeIci);
	}

	public boolean containsPointOn(Point suisJeIci){
		boolean isContains = false;
		int max = (coords.size()-2 > 0) ? coords.size()-2 : 0;
		for (Point ici: coords.subList(0, max)){
			if (ici.equals(suisJeIci)){
				isContains = true;
			}
		}
		return isContains;
	}

	
	private void updateSpeed(){
		if (speed){
			iSpeed++;
			size++;
		}
		if (iSpeed > 10){
			this.timeNewCoord = this.timeNewCoord*7/8;
			speed = false; 
			iSpeed = 0;
		}
	}
	
	private void updateBourre(){
		if (bourre){
			iBourre++;
		}
		if (iBourre > 50){
			bourre = false; 
			iBourre = 0;
		}
	}

	public int getLagg(){
		return this.lagg;
	}
	public int getISpeed(){
		return this.iSpeed;
	}
	
	public void mange(FruitModel unFruit){
		JardinModel.getInstance().mangeFruit(unFruit);
		
		timeNewCoord = (timeNewCoord-1 > 10) ? timeNewCoord-1 : timeNewCoord;
		
		switch (unFruit.getType()){
		case 0: //NORMAL
			this.lagg = 0;
			break;
		case 1: //LAGGER
			this.lagg++;
			break;
		case 2: //SPEEDER
			this.speed = true;
			break;
		case 3: //SLOWER
			this.timeNewCoord = this.timeNewCoord*3/2;
			break;
		case 4: //BOURRE
			this.bourre = true;
			break;
		case 5: //FUCKER
			this.setChanged();
			this.notifyObservers();
			break;
		}
		
		//System.out.println("TIN TIN TIN ! " + timeNewCoord);
		System.out.println("MiAm : " + unFruit);
		this.size++;
	}
	
	public int getTimeBetweenPas(){
		return this.timeNewCoord;
	}
	
	public boolean isDrunk(){
		return this.bourre;
	}
	
	public ArrayList<Point> getCoordonnees(){
		return coords;
	}

	public int size(){
		return coords.size();
	}

}
