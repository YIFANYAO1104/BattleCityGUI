package com.bham.bc.entity.physics;


import com.bham.bc.components.characters.agents.enemies.EnemyTestDemo;
import com.bham.bc.entity.BaseGameEntity;
import javafx.embed.swing.JFXPanel;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MapDivisionTest {

    @Test
    public void mapDivTest(){
        new JFXPanel();
        MapDivision<BaseGameEntity> m1 = new MapDivision<>(128,128,4,4);
        assertEquals (16,m1.sizeOfCells());
        assertEquals(32.0,m1.getCellWidth(),1.0);
        assertEquals(32.0,m1.getCellWidth(),1.0);
    }

    @Test
    public void test01(){
        new JFXPanel();
        MapDivision<BaseGameEntity> m1 = new MapDivision<>(128,128,4,4);

        EnemyTestDemo en1 =  new EnemyTestDemo(12.0,12.0);
        EnemyTestDemo en2 =  new EnemyTestDemo(46,23);
        EnemyTestDemo en3 =  new EnemyTestDemo(95,97);

        m1.addEntity(en1);
        m1.addEntity(en2);
        m1.addEntity(en3);

        List<BaseGameEntity> b1 = m1.getRelevantEntities(en1);
        List<BaseGameEntity> ans1 = new ArrayList<>();

        assertEquals(ans1, b1);


    }

    @Test
    public void test02(){
        new JFXPanel();
        MapDivision<BaseGameEntity> m1 = new MapDivision<>(128,128,4,4);

        EnemyTestDemo en1 =  new EnemyTestDemo(12.0,12.0);
        EnemyTestDemo en2 =  new EnemyTestDemo(12.0,40);
        EnemyTestDemo en3 =  new EnemyTestDemo(12.0,70);

        m1.addEntity(en1);
        m1.addEntity(en2);
        m1.addEntity(en3);

        List<BaseGameEntity> b1 = m1.getRelevantEntities(en1);
        List<BaseGameEntity> ans1 = new ArrayList<>();
        ans1.add(en2);
        HashSet<BaseGameEntity> ss1 = new HashSet<>(b1);
        HashSet<BaseGameEntity> ss2 = new HashSet<>(ans1);
        assertEquals(ss2, ss1);


    }



}
