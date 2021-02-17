package com.bham.bc.entity;
/**
 * An Enum to classify and indicate the type of Entity
 */
public enum EntityNames {

    ent_tank(0),
    ent_map_element(1);

    final public int id;
    /**
     * Constructor of EntityNames,use id as parameter to determine Entity Type
     * @param id
     */
    EntityNames(int id) {
        this.id = id;
    }
    /**
     *
     * @param e
     * @return Entity's type
     */
    public static String GetNameOfEntity(EntityNames e) {
        return e.toString();
    }

    @Override
    public String toString() {
        return EntityNames.GetNameOfEntity(this.id);
    }

    static public String GetNameOfEntity(int n) {
        return "(have no idea how to distinguish the item)";
    }
}
