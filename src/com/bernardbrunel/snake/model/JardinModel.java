package com.bernardbrunel.snake.model;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

public class JardinModel extends Observable implements Observer{
	private Dimension airDeJeux;
	private volatile ArrayList<FruitModel> mesFruits = new ArrayList<FruitModel>();
	private int nFruitMax;
	private Thread generateurDeFruit = new Thread(new Runnable() { 
		public void run() { 
	        Thread thisThread = Thread.currentThread();
	        
			while( generateurDeFruit == thisThread ) {
				generationDunFruit();
				try {
					Thread.sleep(200);
				} catch (InterruptedException ex) {}
			}
		}
	});
	
	//-------------------------[CONSTRUCTEUR]
	/** Constructeur privé interdisant l'instanciation en dehors de cette classe */
	private JardinModel(Dimension p_dim, int p_nFruitMax){
		System.out.println("Constructeur de jardin depuis 1823");
		this.airDeJeux = p_dim; 
		this.nFruitMax = p_nFruitMax;
		this.mesFruits.clear();
	}

	public void start(){
		this.generateurDeFruit.start();
	}
	public void stop(){
		for (FruitModel f: mesFruits)
			f.stop();

		this.mesFruits.clear();
		this.generateurDeFruit = null;
		instance = new JardinModel(new Dimension(64, 64), 4);
	}
	//-------------------------[DEATHTRUCTEUR]
	@SuppressWarnings("deprecation")
	public void finalize(){
		mesFruits.clear();
		generateurDeFruit.stop();
	}
	
	//--------------------------------[SINGLETON]
	private static JardinModel instance = new JardinModel(new Dimension(64, 64), 4);
	/** Donnée de classe contenant l'instance courante */
	
	/** Singleton de la classe courante */ 
	public static JardinModel getInstance() { return instance; }

	//--------------------------------[OBSERVER]
	/** update lorsqu'un fruit est mort */
	@Override
	public void update(Observable o, Object arg) {
	    if ( ((FruitModel)o).isDead() ){
	    	this.mesFruits.remove(o);
		    this.setChanged();     //| Notification pour 
		    this.notifyObservers(o);//| l'effacement du fruit pourri
	    	((FruitModel)o).stop();
	    	o = null;//.deleteObserver(this);
	    } else {
		    this.setChanged();     
		    this.notifyObservers(o);
	    }
	}

	//--------------------------------[isInThePlace]
	public boolean isInThePlace(Point ps){
		return isInThePlace(ps.x, ps.y);
	}

	public boolean isInThePlace(int wi, int hi){
		boolean perdu = false;
		perdu |= (wi >= this.airDeJeux.width);
		perdu |= (hi >= this.airDeJeux.height);
		perdu |= (wi < 0);
		perdu |= (hi < 0);
		return perdu;
	}
	
	//--------------------------------[FRUIT]
	private void generationDunFruit(){
		if (this.mesFruits.size() < this.nFruitMax){
			FruitModel f = new FruitModel(this.airDeJeux);
			this.mesFruits.add(f);
			f.addObserver(this);
		    this.setChanged();   
		    this.notifyObservers(f);
		}
	}
	
	public ArrayList<FruitModel> getFruits(){
		return this.mesFruits;
	}
	
	public FruitModel containsFruitOn(Object obEq){
		FruitModel isContains = null;
		for (FruitModel f: this.mesFruits){
			if (f.equals(obEq)){
				isContains = f;
			}
		}
		return isContains;
	}
	
	public int mangeFruit(FruitModel unFruit){
		this.mesFruits.remove(unFruit);
		return unFruit.getMiam();
	}
	
	//--------------------------------[METHODE]
	public Dimension getSize(){
		return this.airDeJeux;
	}
	
	@Override
	public String toString() {
		return mesFruits.size() + "\t" + this.mesFruits.toString();
	}



	//==================================================================
	/*public static void main(String[] args) {
		JardinModel ma_fenetre = new JardinModel(new Dimension(100, 100), 5);
		for (int i = 0; i < 65535; i++){
			System.out.println(ma_fenetre);
		}
	}*/
	

}
