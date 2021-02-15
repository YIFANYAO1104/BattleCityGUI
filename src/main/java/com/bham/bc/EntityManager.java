/**
 * Desc:   Singleton class to handle the  management of Entities.          
 * 
 * @author Petr (http://www.sallyx.org/)
 */
package com.bham.bc;

import com.bham.bc.common.BaseGameEntity;

import java.util.HashMap;
/**
 * Class to manage Entities
 */
public class EntityManager {
    /**
     * Create EntityManager to provide Easy Access
     */
    public static EntityManager EntityMgr = new EntityManager();

    /**
     * Private class that provides HashMap with <Integer,BaseGameEntity> as key-value Entry
     */
    private final class EntityMap extends HashMap<Integer, BaseGameEntity> {
    }
    /**
     * Providing quick and Low time Complexity LOOPUP
     * Retrieving BaseGameEntity Object by ID NUMBER
     */
    private EntityMap m_EntityMap = new EntityMap();

    public EntityManager() {
    }

    //copy ctor and assignment should be private
    private EntityManager(EntityManager cs) {
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cloning not allowed");
    }

//--------------------------- Instance ----------------------------------------
//
//   this class is a singleton
//-----------------------------------------------------------------------------
    public static EntityManager Instance() {
        return EntityMgr;
    }

    /**
     * Registering new Entity
     * By create new EntityID-EntityObject paired Entry and put into HashMap
     * @param NewEntity
     */
    public void RegisterEntity(BaseGameEntity NewEntity) {
        m_EntityMap.put(NewEntity.ID(), NewEntity);
        System.out.println("reigter id :"+NewEntity.ID()+" with type: "+ NewEntity.toString() );
    }

    /**
     * Retrieve Specific BaseGame Entity from HashMap by Entity ID
     * @param id
     * @return
     */
public BaseGameEntity GetEntityFromID(int id) {
        //find the entity
        BaseGameEntity ent = m_EntityMap.get(id);

        //assert that the entity is a member of the map
        assert (ent != null) : "<EntityManager::GetEntityFromID>: invalid ID";

        return ent;
    }

    /**
     * Remove the EntityID-EntityObject paired entry from HashMap
     * @param pEntity
     */
    public void RemoveEntity(BaseGameEntity pEntity) {
        //m_EntityMap.erase(m_EntityMap.find(pEntity.ID()));
        m_EntityMap.remove(pEntity.ID());
    }
}
