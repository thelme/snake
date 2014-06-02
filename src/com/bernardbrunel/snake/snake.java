package com.bernardbrunel.snake;

import com.bernardbrunel.snake.controler.Controler;
import com.bernardbrunel.snake.vue.Vue;

public class snake {
	//--------------------------------[MAin]
	public static void main(String[] args) {
		Vue.getInstance().setVisible(true);
		Vue.getInstance().drawWelcom();
		Controler unControleur = new Controler();
	}
}
