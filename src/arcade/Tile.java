
package arcade;

import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;


/**
 *
 * @author Felix
 */
public class Tile extends Rectangle {

    private Chip chip;
	private int x;
	private int y;
 
/**
 * Returns if the coordinate has a chip.
 *
 * @author Felix
 */ 
    public boolean hasChip(){
	return chip != null;
    } // hasChip

/**
 * Returns the chip.
 *
 * @author Felix
 */    
    public Chip getChip(){
	return chip;
    } // getChip

/**
 * Sets the chip.
 *
 * @author Felix
 * @param chip		the chip to be set.
 */    
    public void setChip(Chip chip){
	this.chip = chip;
    } //setChip
 
/**
 * Constructor for Tile.
 *
 * @author Felix
 * @param white		the color of the tile is white or black.
 * @param x 		the x-coordinate.
 * @param y 		the y-coordinate.
 */ 
    public Tile(boolean white, int x, int y){
		this.x = x;
		this.y = y;
	
		// Sets the tile sizes and positions them
		setWidth(Arcade.size);
		setHeight(Arcade.size);
		relocate(x*Arcade.size, y*Arcade.size);

		// Tests for coloring
		if(white == true){
			setFill(Color.valueOf("WHITE"));
		} // if
		else{
			setFill(Color.valueOf("BLACK"));
		} // else
    } // Constructor
    
} // Tile