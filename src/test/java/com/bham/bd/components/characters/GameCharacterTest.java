package com.bham.bd.components.characters;

import com.bham.bd.components.Controller;
import com.bham.bd.components.environment.MapType;
import javafx.embed.swing.JFXPanel;
import static org.junit.Assert.*;
import java.lang.reflect.Field;
import java.util.EnumSet;

import org.junit.Test;

public class GameCharacterTest {

	@Test
	public void healthTest() {
		new JFXPanel();		
		Controller.setMode(MapType.SMALL);
		Player x =  new Player(10,10);
		
		assertTrue(100==x.getHp());
		x.changeHp(100);
		assertTrue(100==x.getHp());
		x.changeHp(-100);
		// assertTrue(!x.exist()));		 destroy() method for player not completed yet
	}
	
	@Test
	public void speedTest() throws Exception {
		new JFXPanel();		
		Controller.setMode(MapType.SMALL);
		Player x =  new Player(10,10);
	
		assertTrue(x.getVelocity().magnitude() == 0);
		Field field = Player.class.getDeclaredField("DIRECTION_SET");
        field.setAccessible(true);
        field.set(x,EnumSet.of(Direction.U));
        for (int i = 0; i<17; i++) x.move();
        assertTrue(x.getVelocity().magnitude() <= x.getMaxSpeed());
        field.set(x,EnumSet.noneOf(Direction.class));
        for (int i = 0; i<7; i++) x.move();
        assertTrue(x.getVelocity().magnitude() == 0);	
	}
	
	@Test
	public void hitboxTest() {
		new JFXPanel();		
		Controller.setMode(MapType.SMALL);
		Player a =  new Player(0,0);
		Player b =  new Player(0,24.99);
		Player c =  new Player(0,25);
		
		assertTrue(a.intersects(b));
		assertFalse(a.intersects(c));
	}

}
