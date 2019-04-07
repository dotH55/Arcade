
package arcade;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


/**
 *
 * @author Felix
 */
public class Chip extends StackPane {

	public boolean king;		// king == true if the chip is a king
	public boolean team;		// team == true is blue team, bottom side
	public int direction;		// -1 if blue team, 1 if red team, 0 if a king
	private double cursorX, cursorY;		// where the cursor is
	private double oldCursorX, oldCursorY;		// where the cursor was
	private Circle circle;
	
/**
 * Returns if the chip is a king.
 *
 * @author Felix
 */
	public boolean isKing(){
		return king;
	} // isKing

/**
 * Returns the chip's team.
 *
 * @author Felix
 */	
	public boolean getTeam(){
		return team;
	} // getTeam

/**
 * Returns the chip's direction.
 *
 * @author Felix
 */	
	public int getDirection(){
		return direction;
	} // getDirection

/**
 * Returns the old x-coordinate.
 *
 * @author Felix
 */	
	public double getOldX(){
		return oldCursorX;
	} // getOldX

/**
 * Returns the old y-coordinate.
 *
 * @author Felix
 */	
	public double getOldY(){
		return oldCursorY;
	} // getOldY

/**
 * Sets the chip to be a king.
 *
 * @author Felix
 * @param chip		the chip to be a king
 * @param king		whether it is a king or not
 */	
	public void setKing(Chip chip, boolean king){
		this.king = king;
		direction = 0;
		setColor(chip);
	} // setKing

/**
 * Returns the circle shape of the chip, for coloring.
 *
 * @author Felix
 */	
	public Circle getCircle(){
		return circle;
	}

/**
 * Sets the color of the chip.
 *
 * @author Felix
 * @param chip		the chip to be colored
 */	
	public void setColor(Chip chip){
		if(team == true)
			getCircle().setFill(Color.valueOf("GREEN"));
		if(team == false)
			getCircle().setFill(Color.valueOf("ORANGE"));
	} // setColor
	
/**
 * Constructor of Chip.
 *
 * @author Felix
 * @param team		what team the chip is on
 * @param x 		the x-coordinate of the chip
 * @param y 		the y-coordinate of the chip
 */	
	public Chip(boolean team, int x, int y){
		this.team = team;		// sets the team of the chip, red or blue
		move(x, y);				// moves the chips to the spots
		
		Circle chip = new Circle(Arcade.size*.30);
		if(team == true){
			chip.setFill(Color.valueOf("BLUE"));
			direction = -1;
		} // if
		else{
			chip.setFill(Color.valueOf("RED"));
			direction = 1;	
		} // else{
		chip.setStroke(Color.valueOf("GREY"));
		chip.setStrokeWidth(Arcade.size*.05);
		
		chip.setTranslateX((Arcade.size - Arcade.size*.30 * 2)/2);
		chip.setTranslateY((Arcade.size - Arcade.size*.30 * 2)/2);
		getChildren().add(chip);
		this.circle = chip;
		
		setOnMousePressed(e -> {
			cursorX = e.getSceneX();
			cursorY = e.getSceneY();
		}); // setOnMousePressed
		
		setOnMouseDragged(e -> {
			relocate(e.getSceneX()-cursorX+oldCursorX, e.getSceneY()-cursorY+oldCursorY);
		}); // setOnMouseDragged
	} // chip

/**
 * Moves the chip.
 *
 * @author Felix
 * @param x 		the x-coordinate of the chip
 * @param y 		the y-coordinate of the chip
 */	
	public void move(int x,int y){
		oldCursorX = x * Arcade.size;
		oldCursorY = y * Arcade.size;
		relocate(oldCursorX,oldCursorY);
	} // move
/**
 * Makes it so you can't move the chip to the spot.
 *
 * @author Felix
 */	
	public void nope(){
		relocate(oldCursorX, oldCursorY);
	} // nope

} // Chip