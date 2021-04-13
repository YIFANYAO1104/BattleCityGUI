package com.bham.bc;


import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.entity.physics.MapDivision;
import javafx.embed.swing.JFXPanel;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MapDivisionTest {

    @Test
    public void mapDivTest(){
        new JFXPanel();
        MapDivision<BaseGameEntity> m1 = new MapDivision<>(128,128,4,4,10);
        assertEquals (16,m1.sizeOfCells());
        assertEquals(32.0,m1.getCellWidth(),1.0);
        assertEquals(32.0,m1.getCellWidth(),1.0);
    }

//    @Test
//    public void indextest(){
//        new JFXPanel();
//        MapDivision<BaseGameEntity> m1 = new MapDivision<>(128,128,4,4,10);
//
//        DefaultEnemy en1 =  new DefaultEnemy(12,12);
//        DefaultEnemy en2 =  new DefaultEnemy(46,23);
//        DefaultEnemy en3 =  new DefaultEnemy(95,97);
//
//        m1.AddEntity(en1);
//        m1.AddEntity(en2);
//        m1.AddEntity(en3);
//
//        int i = m1.getRegister().get(en1);
//        assertEquals(0,i);
//
//        i = m1.getRegister().get(en2);
//        assertEquals(1,i);
//
//        i = m1.getRegister().get(en3);
//        assertEquals(14,i);
//
//        m1.UpdateEntity(en1);
//
//        en1.setPos(47,23);
//        m1.UpdateEntity(en1);
//        i = m1.getRegister().get(en1);
//        assertEquals(1,i);
//
//
//    }

//    @Test
//    public void surrorderTest(){
//        new JFXPanel();
//        MapDivision<BaseGameEntity> m1 = new MapDivision<>(128,128,4,4,10);
//
//        DefaultEnemy en1 =  new DefaultEnemy(20,20);
//        DefaultEnemy en2 =  new DefaultEnemy(46,23);
//        DefaultEnemy en3 =  new DefaultEnemy(95,97);
//
//        m1.AddEntity(en1);
//        m1.AddEntity(en2);
//        m1.AddEntity(en3);
//
//        m1.CalculateNeighbors(new Point2D(30,30),20);
//
//
//        assertEquals(en1,m1.start());
//        assertEquals(en2,m1.next());
//
//    }

}
