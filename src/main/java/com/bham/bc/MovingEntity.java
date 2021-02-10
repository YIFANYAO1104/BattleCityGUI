package main.java.com.bham.bc;

/*the abstract parent class of all moving objects */
public abstract class MovingEntity extends BaseGameEntity{
    /**
     * the velocity of moving object( speed x in horizontal and speed y in vertical)
     */

    public int speedX;
    public int speedY;
    /**
     * the coordinate of the current position of the moving object
     */
    protected  int x,y;
    /**
     *  the length and width of the moving object
     */

    public static int width;
    public static int length;
    /**
     * the moving direction of the moving object
     */

    protected Direction direction;

    /**
     * the status to check if the object is alive or dead
     */
    protected boolean live = true;
    protected boolean good;

    /**
     * the constructor of this class, will generate a valid ID using parent class's generating ID method
     */
    protected MovingEntity() {
        super(GetNextValidID());
    }
    /**
     * a method to define the object's movement
     */
    protected abstract void move();
}
