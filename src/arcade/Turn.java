
package arcade;


/**
 *
 * @author Felix
 */
public class Turn {
	
	public enum Movement {NONE, NORMAL, KILL, KING}; // Movement enums
    private Movement type;
	private Chip chip;

/**
 * Returns the type of the movement.
 *
 * @author Felix
 */	
    public Movement getType() {
        return type;
    } // getType

/**
 * Returns the chip.
 *
 * @author Felix
 */
    public Chip getChip() {
        return chip;
    } // getChip

/**
 * Constructor of Turn.
 *
 * @author Felix
 * @param type		the type of movement
 */
    public Turn(Movement type) {
        this(type, null);
    } // Turn

/**
 * Overloaded constructor.
 *
 * @author Felix
 * @param type 		the type of movement
 * @param chip		the chip to be moved
 */
    public Turn(Movement type, Chip chip) {
        this.type = type;
        this.chip = chip;
    } // Turn
} // Turn