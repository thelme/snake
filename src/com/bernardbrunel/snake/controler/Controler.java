package com.bernardbrunel.snake.controler;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ConcurrentModificationException;
import java.util.Observable;
import java.util.Observer;

import com.bernardbrunel.snake.model.FruitModel;
import com.bernardbrunel.snake.model.JardinModel;
import com.bernardbrunel.snake.model.SnakeModel;
import com.bernardbrunel.snake.vue.Vue;

public class Controler implements Observer, KeyListener {
	final char TOUCH[]  = {'O', 'N', 'E', 'S'}; // Ouest = 36 / Nord = 38 / ...
	final char TOUCHB[] = {'E', 'S', 'N', 'O'}; // hey ! Chui bourré !
	
	//--------------------------------[CONSTRUCTEUR]
	public Controler(){
		JardinModel.getInstance().addObserver(this);
		SnakeModel.getInstance().addObserver(this);
	}

	//--------------------------------[SINGLETON]
	/** Donnée de classe contenant l'instance courante */
	private static Controler instance = new Controler();
	
	/** Singleton de la classe courante */ 
	public static Controler getInstance() { return instance; }
	
	//--------------------------------[OBSERVER]
	@Override
	public void update(Observable arg0, Object arg1) {
		boolean perdu = false;
		//System.out.println(arg0 + "\t" + arg1);
		if ( arg0 instanceof SnakeModel ) {
			
			if (arg1 == null){
				perdu = true;
			} else {
				Point pArg1 = ((Point)(arg1));
				
				//Si le point est dans le snacke: c'est une update de tête
				if ( SnakeModel.getInstance().isTheHead(pArg1) ){
					FruitModel leMiam = JardinModel.getInstance().containsFruitOn( pArg1 );
					if (leMiam != null){
						SnakeModel.getInstance().mange(leMiam);
					}
					
					perdu |= SnakeModel.getInstance().containsPointOn(pArg1);
					perdu |= JardinModel.getInstance().isInThePlace(pArg1);
				} else { //sinon c'est une update de queue
					Vue.getInstance().drawUpdateBackground( pArg1 );
				}
				Vue.getInstance().drawSnacke();
			}
		}
		if ( arg0 instanceof JardinModel ) {
			if ( ((FruitModel)arg1).isDead() ){
				Vue.getInstance().drawUpdateBackground( ((FruitModel)arg1).getPosition());
		    }
			try {
				Vue.getInstance().drawFruits();
				Vue.getInstance().drawPanel();
			} catch (ConcurrentModificationException e ){} 
		}
		
		if (perdu){
			System.out.println("NOOOOOB  ! Report !!!");
			JardinModel.getInstance().stop();
			SnakeModel.getInstance().stop();
			Vue.getInstance().drawWelcom();
		}
	}

	//--------------------------------[KEYBOARD]
	@Override
	public void keyPressed(KeyEvent arg0) { 
		if ( (arg0.getKeyCode() >= 37) && (arg0.getKeyCode() <= 40)){
			if (SnakeModel.getInstance().isDrunk()){
				SnakeModel.getInstance().setNewDirection(TOUCHB[arg0.getKeyCode()-37]);
			} else {
				SnakeModel.getInstance().setNewDirection(TOUCH[arg0.getKeyCode()-37]);
			}
		}
		if (arg0.getKeyCode() == 10){
			Vue.getInstance().drawBackground();
			Vue.getInstance().drawGrid();
			SnakeModel.getInstance().start();
			JardinModel.getInstance().start();
		}
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) { }

	@Override
	public void keyTyped(KeyEvent arg0) { }

}
