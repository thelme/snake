package com.bernardbrunel.snake.vue;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.bernardbrunel.snake.controler.Controler;
import com.bernardbrunel.snake.model.FruitModel;
import com.bernardbrunel.snake.model.JardinModel;
import com.bernardbrunel.snake.model.SnakeModel;


public class Vue  extends JFrame {
	private JPanel aireDeJeux	= new JPanel();
	private JPanel panel		= new JPanel();
	
	private int sizeBox = 10;
	private Dimension dim;	
	private Dimension dimPanel;	
	private boolean backgroundSet = false;
	
	private Thread reDraw = new Thread(new Runnable() { 
		public void run() { 
			boolean continu = true;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
			drawBackground();
			drawBackgroundPanel();
			drawGrid();
			backgroundSet = true;
			
			while( continu ) {
				drawFruits();
				try {
					Thread.sleep(100);
				} catch (InterruptedException ex) {}
			}
		}
	});

	//--------------------------------[CONSTRUCTEUR]
	public Vue (){
		Dimension dimTp = JardinModel.getInstance().getSize();
		this.setTitle("Snacky");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);

		this.dim 	  = new Dimension(dimTp.width*sizeBox+1, dimTp.height*sizeBox+1);
		this.aireDeJeux.setPreferredSize(this.dim);
		this.aireDeJeux.setBackground(Color.black);

		this.dimPanel = new Dimension(dimTp.width*sizeBox+1, 7*sizeBox+1);
		this.panel.setPreferredSize(this.dimPanel);
		this.panel.setBackground(Color.black);
		
		//this.setLayout(new BorderLayout());

		Container orga_Container = this.getContentPane();
		this.setLayout( new BoxLayout(orga_Container, BoxLayout.Y_AXIS) );
		
		this.add(aireDeJeux, BorderLayout.CENTER);
		this.add(panel, BorderLayout.CENTER);
		this.pack();
		reDraw.start();
		
	    this.addKeyListener(new Controler());
	}
	
	//--------------------------------[SINGLETON]
	/** Donnée de classe contenant l'instance courante */
	private static Vue instance = new Vue();
	
	/** Singleton de la classe courante */ 
	public static Vue getInstance() { return instance; }

	//--------------------------------[DEATHTRUCTEUR]
	@SuppressWarnings("deprecation")
	public void finalize(){
		reDraw.stop();
	}

	//--------------------------------[DRAW]
	public void drawGrid(){
		Graphics crayon = aireDeJeux.getGraphics();
		crayon.setColor(Color.darkGray);
		
		for (int w = 0; w <= this.dim.width; w+=(sizeBox+0)){
			crayon.drawLine(w, 0, w, this.dim.height);
		}
		for (int h = 0; h <= this.dim.height; h+=(sizeBox+0)){
			crayon.drawLine(0, h,  this.dim.width, h);
		}
		crayon.dispose();
	}
	
	public void drawFruits(){
		Graphics crayon = aireDeJeux.getGraphics();
		ArrayList<FruitModel> tempFruits = JardinModel.getInstance().getFruits();
		for(FruitModel f: tempFruits ){
			Point p = f.getPosition();
			if (!f.isDead()){
				crayon.setColor(f.getColor());
			}
			crayon.fillRect(p.x*sizeBox+1, p.y*sizeBox+1, sizeBox-1, sizeBox-1);
			
			
		}
		crayon.dispose();
	}
	
	public void drawSnacke(){
		Graphics crayon = aireDeJeux.getGraphics();
		ArrayList<Point> tempCoords = SnakeModel.getInstance().getCoordonnees();
		float i = 0, size = (float)SnakeModel.getInstance().size();

		for (Point p : tempCoords){
			crayon.setColor(Color.getHSBColor(SnakeModel.getInstance().getColor_h(), 1f-i/(2f*size), 1));
			//crayon.setColor(SnakeModel.getInstance().getColor());
			crayon.fillRect(p.x*sizeBox+1, p.y*sizeBox+1, sizeBox-1, sizeBox-1);
			i++;
		}
		crayon.setColor(Color.getHSBColor(1f/3f, 1f, 2f/3f));
		crayon.drawRect(SnakeModel.getInstance().getHead().x*sizeBox+1, 
					    SnakeModel.getInstance().getHead().y*sizeBox+1, sizeBox-2, sizeBox-2);
		crayon.dispose();
	}
	
	public void drawBackground(){
		int beginHi = this.dim.height/(sizeBox*2);
		int   endHi = this.dim.height/(sizeBox*1);
		int beginWi = this.dim.width/(sizeBox*2);
		int   endWi = this.dim.width/(sizeBox*1);
		int wi1, wi2;
		int hi1, hi2;
		for (hi1 = beginHi, hi2 = beginHi; hi1 <= endHi; hi1+=1, hi2-=1){
		for (wi1 = beginWi, wi2 = beginWi; wi1 <= endWi; wi1+=1, wi2-=1){
			drawUpdateBackground(wi1, hi1);
			drawUpdateBackground(wi2, hi1);
			drawUpdateBackground(wi1, hi2);
			drawUpdateBackground(wi2, hi2);
		}
		}
	}

	//------------------------------[drawUpdateBackground]
	public void drawUpdateBackground(Point i){
		drawUpdateBackground(i.x, i.y);
	}
	
	public void drawUpdateBackground(int wi, int hi){
		drawCase(wi, hi, getColorBackground(wi, hi));
	}


	//------------------------------[drawCase]
	public void drawCase(Point i, Color color){
		drawCase(i.x, i.y, color);
	}

	public void drawCase(int wi, int hi, Color color){
		drawCase(wi, hi, color, aireDeJeux.getGraphics() );
	}

	public void drawCase(int wi, int hi, Color color, Graphics crayon){
		crayon.setColor(color);
		crayon.fillRect(wi*sizeBox+1, hi*sizeBox+1, sizeBox-1, sizeBox-1);
		crayon.dispose();
	}
	
	//------------------------------[getColorBackground]
	public Color getColorBackground(Point i){
		return getColorBackground(i.x, i.y);
	}
	
	public Color getColorBackground(int wi, int hi, float sp){
		wi *= 10;
		hi *= 10;
		float h = (2f/3f);
		float sWi = Math.abs(((((hi+wi)*4f)/(this.dim.width+this.dim.height) )%1)-0.5f);
		float s = sp*(1+sWi);
		return Color.getHSBColor(h, s, 1);	
	}
	
	public Color getColorBackground(int wi, int hi){
		//System.out.print("BACK- " + wi + ":" + hi + "  ");
		return getColorBackground(wi, hi, (1f/3f) );
	}

	//------------------------------[welCome]
	public void drawWelcom(){
		while(!backgroundSet){System.out.print("");}
		System.out.println("Welcome");
		Graphics crayon = aireDeJeux.getGraphics();
		crayon.setColor(Color.getHSBColor(2f/3f, 3f/8f, 1) );
		crayon.fillRect(this.dim.width/4, this.dim.height/2, this.dim.width*2/4, this.dim.height/3);
		crayon.setColor(Color.getHSBColor(2f/3f, 1, 6f/8f) );
		crayon.drawRect(this.dim.width/4, this.dim.height/2, this.dim.width*2/4, this.dim.height/3);
		crayon.drawRect(this.dim.width/4+1, this.dim.height/2+1, this.dim.width*2/4-2, this.dim.height/3-2);
		int begin_h = this.dim.height/2;
		int   end_h = (int)( this.dim.height*(1f/2f+1f/3f) );
		int pas_h = (end_h-begin_h)/(FruitModel.nColor()+1);
		int iC = 0;
		for (int h = begin_h+pas_h; h < end_h-pas_h; h+=pas_h){
			drawCase(this.dim.width*3/80, h/10, FruitModel.getColor(iC));
			crayon.setColor(Color.getHSBColor(2f/3f, 1, 6f/8f) );
			crayon.drawString(FruitModel.getNameType(iC), this.dim.width*4/10, h+10);
			iC++;
		}
		crayon.dispose();
	}
	
	public void drawBackgroundPanel(){
		int beginHi = 0; //this.dim.height;
		int   endHi = this.dim.height+this.dimPanel.height;
		int beginWi = this.dim.width/2;
		int   endWi = this.dim.width;
		int wi1, wi2;
		int hi;
		
		for (hi = beginHi-1; hi <= endHi; hi+=1){
		for (wi1 = beginWi, wi2 = beginWi; wi1 <= endWi; wi1+=1, wi2-=1){
			drawCase(wi1, hi-1, getColorBackground(wi1, hi, (1f/5f)), panel.getGraphics() );
			drawCase(wi2, hi-1, getColorBackground(wi2, hi, (1f/5f)), panel.getGraphics() );
		}
		}
		

		Graphics crayon = panel.getGraphics();
		crayon.setColor(Color.gray);
		
		for (int w = 0; w <= endWi; w+=(sizeBox+0)){
			crayon.drawLine(beginHi, w, endHi, w);
		}
		for (int h = beginHi; h <= endHi; h+=(sizeBox+0)){
			crayon.drawLine(h, 0, h,  endWi);
		}
		crayon.dispose();
		
		drawPanel();
	}
	
	public void drawPanel(){
		Graphics crayon = panel.getGraphics();
		int nLagg = SnakeModel.getInstance().getLagg();
		for (int iLagg = 0; iLagg < nLagg; iLagg++){
			int wi = (3+iLagg)*sizeBox+1;
			int hi = (this.dimPanel.height-sizeBox)/2+1;
			crayon.setColor(Color.pink);
			crayon.fillRect(wi, hi, sizeBox-1, sizeBox-1);
		} 
		for (int iLagg = nLagg; iLagg < 10; iLagg++){
			int wi = (3+iLagg)*sizeBox+1;
			int hi = (this.dimPanel.height-sizeBox)/2+1;
			crayon.setColor(getColorBackground(wi, hi+1, (1f/5f)));
			crayon.fillRect(wi, hi, sizeBox-1, sizeBox-1);
		} 
		crayon.dispose();
	}

}
