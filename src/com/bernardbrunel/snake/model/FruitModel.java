package com.bernardbrunel.snake.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Observable;

/*
 * Class qui defini un fruit sur le jardin.
 * Le fruit à une position et un age
 * Le fruit va prendre de l'age petit a petit, jusqu'a mourir sont age de mort
 */
public class FruitModel extends Observable {
	private Point coord;
	private int age;
	private int ageDeMort;

	private int typeDuFruit;
	private int lastTypeDuFruit;
	
	private static String[]  strByType = { "NORMAL"   	, "LAGGER"    	, "SPEEDER" , "SLOWER"		, "BOURRE"  , "FUCKER"};
	private   int[] propasByType = { 20			, 30			, 20		, 10			, 20		, 40 }; 
	private static Color[] colorsByType = { Color.green, Color.pink	, Color.yellow, Color.magenta	, Color.cyan, Color.black};
	private   int[] propasIncByType = new int[propasByType.length+1]; 
	
	private Thread uneVieUnFruit = new Thread(new Runnable() { 
		public void run() { 
	        Thread thisThread = Thread.currentThread();
	        
			while( uneVieUnFruit == thisThread ) {
				vielli();
				try {
					Thread.sleep(500);
				} catch (InterruptedException ex) {}
			}
		}
	});

	//-------------------------[CONSTRUCTEUR]
	/**
	 * 
	 * @param dim endroit ou peuvent apparaitre des fruits
	 */
	public FruitModel(Dimension dim){
		int couronne = 10*(1+(int)(Math.random()*2f/3f) );
		int wHeadMax = SnakeModel.getInstance().getHead().x+couronne;
		int wHeadMin = SnakeModel.getInstance().getHead().x-couronne;
		int hHeadMax = SnakeModel.getInstance().getHead().y+couronne;
		int hHeadMin = SnakeModel.getInstance().getHead().y-couronne;
		
		wHeadMax = (wHeadMax <= dim.width) ? wHeadMax : dim.width;
		wHeadMin = (wHeadMin >= 0) ? wHeadMin : 0;
		hHeadMax = (hHeadMax <= dim.height) ? hHeadMax : dim.height;
		hHeadMin = (hHeadMin >= 0) ? hHeadMin : 0;
		
		
		this.coord = new Point(	(int)((wHeadMax-wHeadMin)*Math.random())+wHeadMin, 
								(int)((hHeadMax-hHeadMin)*Math.random())+hHeadMin);
		
		if (SnakeModel.getInstance().containsPointOn(this.coord)){
			System.out.println("fuuu");
			this.coord = new Point(	(int)(dim.width*Math.random()), 
									(int)(dim.height*Math.random()));
			if (SnakeModel.getInstance().containsPointOn(this.coord)){
				System.out.println("Ho fuuu");
			}
		}
		/*this.coord = new Point(	(int)(dim.width*Math.random()), 
								(int)(dim.height*Math.random()));*/
		
		this.age = 0;
		this.ageDeMort = (int)(100*Math.random())+50;
		this.typeDuFruit  = 0;
		this.lastTypeDuFruit = 0;
		
		uneVieUnFruit.start();
		//System.out.println("Créateur de fruit depuis " + this.ageDeMort);
	}
	//-------------------------[DEATHTRUCTEUR]
	@SuppressWarnings("deprecation")
	public void finalize(){
		//System.out.println("Fruit: AAARRGGGggg...");
		stop();
	}

	public void stop(){
		uneVieUnFruit = null;
	}
	
	//-----------------------------[POSITION]
	public Point getPosition(){
		return coord;
	}
	
	public boolean isInPosition(Point p){
		return (p.x == coord.x)&&(p.y == coord.y);
	}

	//----------------------------------[TYPE]
	public int getType(){
		float sPropaByType = 0;
		for (int iPropaByType: propasByType){
			sPropaByType+=iPropaByType;
		}
		
		propasIncByType[0] = 0;
		for (int i = 0; i < propasByType.length; i++){
			propasIncByType[i+1] = propasByType[i]+propasIncByType[i];
		}

		for (int i = 1; i < propasIncByType.length; i++){
			if ( ( (propasIncByType[i-1]/sPropaByType)  <= getTauxAge()) &&  (getTauxAge() < (propasIncByType[i]/sPropaByType) )  ){
				this.typeDuFruit = i-1;
			}
		}
		return this.typeDuFruit;
	}
	
	public int getMiam(){
		return this.typeDuFruit;
	}
	
	public int getIdType(String name){
		int retour = 0;
		for (int i = 0; i < strByType.length; i++){
			if (strByType[i].equals(name)){
				retour = i;
			}
		}
		return retour;
	}
	
	public static String getNameType(int i){
		return strByType[i];
	}
	
	
	//----------------------------------[AGE]
	public int getAge(){
		return age;
	}

	public float getTauxAge(){
		return (float)age/(float)ageDeMort;
	}
	
	public Color getColor(){
		return getColor(typeDuFruit);
		//return (float)age/(float)ageDeMort;
	}
	
	public static Color getColor(int i){
		return colorsByType[i];
	}
	
	public static int nColor(){
		return colorsByType.length;
	}

	public void vielli(int coupDeVieux){
		this.age = (this.age+coupDeVieux < ageDeMort) ? this.age+coupDeVieux : ageDeMort;
		if (isDead()){
		    this.setChanged(); // Positionne son indicateur de changement
		    this.notifyObservers(); // (1) notification
		}
		if (lastTypeDuFruit != getType()){
			lastTypeDuFruit = getType();
		    this.setChanged(); // Positionne son indicateur de changement
		    this.notifyObservers(); // (1) notification
		}
	}
	
	public void vielli(){
		vielli(1);
	}
	
	public boolean isDead(){
		return (this.age == ageDeMort);
	}

	//-------------------------------------[TOSTRING]
	@Override
	/*public String toString() {
		return "FruitModel [coord=" + coord + ", age=" + age + ", taux=" + age/ageDeMort + "]";
	}*/
	public String toString() {
		return " Fruit (" + coord.x + ":" + coord.y + ")\t" + this.age + "ans " + strByType[getType()] + " ] ";
	}

	//-------------------------------------[EQUALS]
    @Override
    public boolean equals(Object object) {
        boolean sameSame1 = false;
        boolean sameSame2 = false;

        if (object != null && object instanceof FruitModel) {
            sameSame1 = isInPosition(((FruitModel) object).coord);
        }

        if (object != null && object instanceof Point) {
        	sameSame2 = isInPosition(((Point) object));
        }
        return sameSame1||sameSame2;
    }
}
