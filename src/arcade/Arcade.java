/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arcade;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.util.Duration;

/**
 *
 * @author Habilou Sanwidi
 */
public class Arcade extends Application {

    /**
     * @param args the command line arguments
     */
    // Variables
    boolean forward = true;
    int shipHealth = 100;
    int shipScore = 0;
    int shipLevel = 1;
    HBox hBoxStats;
    Label lbLive;
    Label lbScore;
    Label lbLevel;
    Pane pane;
    ImageView imageShip;
    ArrayList<ImageView> enemyShip;
    ArrayList<Timeline> timelineArray = new ArrayList<>();
    int num = 15;
    
    public static int size = 80;
    public static int height = 8;
    public static int width = 8;

    private Tile[][] board = new Tile[width][height];

    private Group tileGroup = new Group();
    private Group chipGroup = new Group();

    private boolean playing = true;

    private int redChips = 12;
    private int blueChips = 12;

    @Override
    public void start(Stage primaryStage) throws Exception {

        //Menu bar
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        MenuItem menuItemExit = new MenuItem("Exit");
        menuItemExit.setOnAction(e ->{
            System.exit(0);
        });
        Menu menuOption = new Menu("Option");
        MenuItem menuItemHighScore = new MenuItem("High Score");
        menuItemHighScore.setOnAction(e ->{
            Label lbGameOver = new Label("Space Invaders 1125400");
            Button btOk = new Button("OK");
            VBox vBox = new VBox(10);
            vBox.setAlignment(Pos.CENTER);
            vBox.getChildren().addAll(lbGameOver, btOk);
            Scene scene = new Scene(vBox, 150, 100);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.sizeToScene();
            stage.show();
            btOk.setOnAction(e3 ->{
                stage.close();
            });
        });
        Menu menuHelp = new Menu("Help");
        MenuItem menuItemAbout = new MenuItem("About");

        //Images
        ImageView imageViewBackground = new ImageView(new Image("game-background.jpg"));
        ImageView imageViewSpaceInvaders = new ImageView(new Image("SpaceInvaders.gif"));
        ImageView imageViewCheckers = new ImageView(new Image("Checkers.gif"));

        //add Menu items to Menu
        menuFile.getItems().add(menuItemExit);
        menuOption.getItems().add(menuItemHighScore);
        menuHelp.getItems().add(menuItemAbout);
        menuBar.getMenus().addAll(menuFile, menuOption, menuHelp);

        //sizing
        imageViewSpaceInvaders.setFitHeight(80);
        imageViewSpaceInvaders.setFitWidth(80);
        imageViewSpaceInvaders.setLayoutX(235);
        imageViewSpaceInvaders.setLayoutY(70);
        imageViewCheckers.setFitHeight(80);
        imageViewCheckers.setFitWidth(80);
        imageViewCheckers.setLayoutX(100);
        imageViewCheckers.setLayoutY(70);

        //Scenes
        Pane menuStagePane = new Pane();
        menuStagePane.getChildren().add(imageViewBackground);
        menuStagePane.getChildren().add(menuBar);
        menuStagePane.getChildren().addAll(imageViewCheckers, imageViewSpaceInvaders);
        Scene MenuStageScene = new Scene(menuStagePane, 400, 200);
        
        //American Checkers 
        //New CodeSetCache
        // I like borderpanes since they're intuitive
        BorderPane border = new BorderPane();
        // Creating the pane to set the board to
        Pane pane1 = new Pane();
        pane1.setPrefSize(size * width, size * height);

        // Setting it up
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                // adding the tiles to the game
                Tile tile = new Tile((i + j) % 2 == 0, j, i);
                board[j][i] = tile;

                tileGroup.getChildren().add(tile);

                //adding the chips to the game
                Chip chip = null;
                if (i <= 2 && (i + j) % 2 != 0) {
                    chip = newChip(false, j, i);
                }
                // if
                if (i >= 5 && (i + j) % 2 != 0) {
                    chip = newChip(true, j, i);
                } // if
                if (chip != null) {
                    tile.setChip(chip);
                    chipGroup.getChildren().add(chip);
                } // if
            } // nested for
        } // for
        pane1.getChildren().addAll(tileGroup, chipGroup);
        border.setCenter(pane1);

        // Menubar creation
        MenuBar menubarChek = new MenuBar();
        final Menu file = new Menu("File");
        menubarChek.getMenus().addAll(file);
        border.setTop(menubarChek);

        MenuItem mainMenu = new MenuItem("Return to Menu");
        mainMenu.setOnAction(e -> {
            Platform.runLater(() -> {
                try {
                    primaryStage.close();
                    new Arcade().start(new Stage());
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            });
        });
        file.getItems().add(mainMenu);

        // Setting the Scene and Stage
        Scene scene = new Scene(border, 640, 670);

        //American Checkers
        imageViewCheckers.setOnMouseClicked(e -> {
             Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setHeaderText("Instructions");
            alert.setContentText("THINGS TO KNOW\n"
                    + "Drag pieces to move\n"
                    + "Kings can move foward and backwards\n"
                    + "Blue's kings turn Green\n"
                    + "Red's kings turn Orange\n"
                    + "The game tries to mimic real life...\n"
                    + "So don't take two turns!\n"
                    + "Game ends when no pieces are left on one team\n"
                    + "However, you have to make a victory lap\n"
                    + "(You have to move once more)\n"
                    + "HAVE FUN!\n");

            // The alert
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                primaryStage.setScene(scene);
                primaryStage.sizeToScene();
                primaryStage.show();
            } else {

            }
            
        }); // imageViewCheckers.setOnMouseClicked
        // instructions
        imageViewSpaceInvaders.setOnMouseClicked(e -> {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setHeaderText("Instructions");
            alert.setContentText("THINGS TO KNOW\n"
                    + "-Use Left/A to move left\n"
                    + "-Use Right/D to move right\n"
                    + "-Use Space to fire\n"
                    + "-Use P to pause\n"
                    + "Enemies increase with Level\n"
                    + "There are infinite levels\n"
                    + "Pixel are not original\n"
                    + "PLAY WITH SOUND\n"
                    + "HAVE FUN!\n");

            Optional<ButtonType> result = alert.showAndWait();

            //Space Invaders
            if (result.get() == ButtonType.OK) {

                pane = new Pane();
                ImageView image = new ImageView(new Image("Background.gif"));
                image.setFitHeight(600);
                image.setFitWidth(800);

                //Ship
                imageShip = new ImageView(new Image("ShipSkin.gif"));
                imageShip.setFitHeight(50);
                imageShip.setFitWidth(50);
                imageShip.setLayoutX(100);
                imageShip.setLayoutY(535);

                //stats bar
                hBoxStats = new HBox(300);
                lbLive = new Label("Lives: " + shipHealth);
                lbScore = new Label("Score: " + shipScore);
                lbLevel = new Label("Level: " + shipLevel);
                lbLive.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
                lbLive.setTextFill(Color.CRIMSON);
                lbScore.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
                lbScore.setTextFill(Color.CRIMSON);
                lbLevel.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
                lbLevel.setTextFill(Color.CRIMSON);
                hBoxStats.getChildren().addAll(lbLive, lbScore, lbLevel);

                //Pane
                pane.getChildren().add(image);
                pane.getChildren().add(imageShip);
                pane.getChildren().add(hBoxStats);
                Scene spaceInvadersScene = new Scene(pane);

                // Keyboard
                spaceInvadersScene.setOnKeyPressed(e1 -> {
                    keyboardManage(e1, imageShip, primaryStage, MenuStageScene);
                });
                primaryStage.setScene(spaceInvadersScene);
                primaryStage.sizeToScene();
                primaryStage.show();

                //enemy
                enemyShip = new ArrayList<ImageView>();
                getEnemy(primaryStage);

            } else {

            }
        }
        ); // imageViewSpaceInvaders.setOnMouseClicked

        primaryStage.setTitle("Space Invaders");
        primaryStage.setScene(MenuStageScene);
        primaryStage.sizeToScene();
        primaryStage.show();
    } //start

    /**
     * This method detects which key has been 
     * pressed
     *
     * @param ke triggering event
     * @param imageShip image of the player ship
     * @param primaryStage the primaryStage of the application
     * @param menuStageScene menu scene
     */
    public void keyboardManage(KeyEvent ke, ImageView imageShip, Stage primaryStage, Scene menuStageScene) {
        if (ke.getCode() == KeyCode.D || ke.getCode() == KeyCode.RIGHT) {
            // move right
            imageShip.setLayoutX(imageShip.getLayoutX() + 15);
        } else if (ke.getCode() == KeyCode.A || ke.getCode() == KeyCode.LEFT) {
            // move left
            imageShip.setLayoutX(imageShip.getLayoutX() - 15);
        } else if (ke.getCode() == KeyCode.SPACE) {
            // fire
            fire(primaryStage);
        } else if (ke.getCode() == KeyCode.P) {
            //Pause 
            for (int p = 0; p < timelineArray.size(); p++) {
                timelineArray.get(p).pause();
            }
            // display pause window
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Menu");
            alert.setHeaderText("CONTINUE?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                for (int p = 0; p < timelineArray.size(); p++) {
                    alert.close();
                    timelineArray.get(p).play();
                } // for
            } else {
                primaryStage.setScene(menuStageScene);
                primaryStage.show();
            } // else
        } // else if
    } // keyboardmanager

    /**
     * This method creates the enemy ship
     * <p>
     * Depending on the level, the method will
     * create new enemies and add them to the pane. This 
     * method also control how the enemies move.
     *
     * @param primaryStage the primaryStage of the application
     */
    public void getEnemy(Stage primaryStage) {
        //gets raid of previous enemies
        enemyShip.clear();
        ImageView firstAlien = new ImageView(new Image("AlienSkin1.gif"));
        firstAlien.setLayoutX(0);
        firstAlien.setLayoutY(50);
        enemyShip.add(firstAlien);
        pane.getChildren().add(firstAlien);
        int x = 60;
        int y = 50;
        for (int i = 0; i < num - 2; i++) {
            ImageView alienM = new ImageView(new Image("AlienSkin1.gif"));
            if (x <= 400) {
                alienM.setLayoutX(x);
                alienM.setLayoutY(y);
                x += 60;
            } else {
                x = 0;
                y += 50;
                alienM.setLayoutX(x);
                alienM.setLayoutY(y);
            } // else
            enemyShip.add(alienM);
            pane.getChildren().add(alienM);
        } // for
        ImageView lastAlien = new ImageView(new Image("AlienSkin1.gif"));
        lastAlien.setLayoutX(x);
        lastAlien.setLayoutY(y);
        enemyShip.add(lastAlien);
        pane.getChildren().add(lastAlien);

        EventHandler<ActionEvent> moveHandler = e1 -> {
            move(firstAlien, lastAlien, enemyShip, num);
        };
        KeyFrame moveKeyFrame = new KeyFrame(Duration.millis(25), moveHandler);
        Timeline timeline = new Timeline(moveKeyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        timelineArray.add(timeline);

        EventHandler<ActionEvent> firehandler = e2 -> {
            enemyFire(enemyShip, primaryStage);
        };
        KeyFrame firekeyFrame = new KeyFrame(Duration.millis(1000), firehandler);
        Timeline timeline2 = new Timeline(firekeyFrame);
        timeline2.setCycleCount(Timeline.INDEFINITE);
        timeline2.play();
        timelineArray.add(timeline2);
    } // getEnemy

    /**
     * This method controls how the enemies move
     * around.
     *
     * @param firstAlien the enemy on the far left
     * @param lastAlien the enemy on the far right
     * @param enemyShip list of enemies
     * @param num number of enemies
     */
    public void move(ImageView firstAlien, ImageView lastAlien, ArrayList<ImageView> enemyShip, int num) {
        if (forward) {
            for (int i = 0; i < num; i++) {
                enemyShip.get(i).setLayoutX(enemyShip.get(i).getLayoutX() + 5);
            } // for
            if (lastAlien.getLayoutX() > 735) {
                forward = false;
            } // for
        } else if (!forward) {
            for (int i = 0; i < num; i++) {
                enemyShip.get(i).setLayoutX(enemyShip.get(i).getLayoutX() - 5);
            } // else if
            if (firstAlien.getLayoutX() < 15) {
                forward = true;
            } // if
        } // else if
    } // move
    
    /**
     * This method controls how 
     * enemies fire.
     *
     * @param enemyShip list of enemies
     * @param primaryStage the enemy on the far right
     */
    public void enemyFire(ArrayList<ImageView> enemyShip, Stage primaryStage) {
        // determines how many enemies can fire at the same 
        // time at each level
        int fire = 0;
        if (enemyShip.size() <= 15) {
            fire = 3;
        } else if (enemyShip.size() <= 31) {
            fire = 6;
        } else if (enemyShip.size() <= 47) {
            fire = 9;
        } else if (enemyShip.size() <= 60) {
            fire = 12;
        } else if (enemyShip.size() <= 75) {
            fire = 15;
        } // else if
        // determine which enemy can fire
        for (int i = 0; i < fire; i++) {
            if (canFire(enemyShip)) {
                int random = (int) (Math.random() * getEnemyShipAlive().size());
                //creates the bullets
                Rectangle r = new Rectangle(getEnemyShipAlive().get(random).getLayoutX() + 20, getEnemyShipAlive().get(random).getLayoutY() - 15, 10, 10);
                r.setFill(Color.BLUEVIOLET);
                r.setArcHeight(10);
                r.setArcWidth(10);
                pane.getChildren().add(r);
                //Animation for the bullet
                Duration duration = new Duration(20);
                KeyFrame keyFrame = new KeyFrame(duration, e -> {
                    r.setY(r.getY() + 5);
                    // checks if there is any collision
                    if (r.getBoundsInParent().intersects(imageShip.getBoundsInParent())) {
                        shipHealth--;
                        updateStats();
                        // checks player's health
                        if (shipHealth < 1) {
                            r.setY(r.getY() + 200);
                            primaryStage.close();
                            for (int t = 0; t < timelineArray.size(); t++) {
                                timelineArray.get(t).stop();
                            }

                            // Back to menuStage if player has less than 0 health
                            Platform.runLater(() -> {
                                try {
                                    new Arcade().start(new Stage());
                                } catch (Exception ex) {
                                    Logger.getLogger(Arcade.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                
                                // informs player of lost
                                Label lbGameOver = new Label("Game Over");
                                Button btOk = new Button("OK");
                                VBox vBox = new VBox(10);
                                vBox.setAlignment(Pos.CENTER);
                                vBox.getChildren().addAll(lbGameOver, btOk);
                                Scene scene = new Scene(vBox, 150, 100);
                                Stage stage = new Stage();
                                stage.setScene(scene);
                                stage.initModality(Modality.APPLICATION_MODAL);
                                stage.sizeToScene();
                                stage.show();
                                btOk.setOnAction(handler -> {
                                    stage.close();
                                }); // btOk.setOnAction
                            }); // Platform.runLater

                        } // if
                    } // if
                });
                Timeline timeline = new Timeline(keyFrame);
                timeline.setCycleCount(Animation.INDEFINITE);
                timeline.play();
                timelineArray.add(timeline);
            } // if
        } // for
    } // enemyfire

    /**
     * This method checks if there 
     * are anymore enemies left to fire
     *
     * @param enemyShip list of enemies
     * @return true if there are enemies left
     */
    public boolean canFire(ArrayList<ImageView> enemyShip) {
        for (int i = 0; i < enemyShip.size(); i++) {
            if (enemyShip.get(i).isVisible()) {
                return true;
            } // if
        } // for
        return false;
    } // canFire

    /**
     * This method returns the list of 
     * enemies still alive
     * 
     * @return enemyShipAlive
     */
    public ArrayList<ImageView> getEnemyShipAlive() {
        ArrayList<ImageView> enemyShipAlive = new ArrayList<>();
        for (int i = 0; i < enemyShip.size(); i++) {
            if (enemyShip.get(i).isVisible()) {
                enemyShipAlive.add(enemyShip.get(i));
            } // if
        } // for
        return enemyShipAlive;
    } // getEnemyShipAlive

    /**
     * This method refreshes the statistic bar
     */
    public void updateStats() {
        pane.getChildren().remove(hBoxStats);
        hBoxStats = new HBox(300);
        lbLive = new Label("Lives: " + shipHealth);
        lbScore = new Label("Score: " + shipScore);
        lbLevel = new Label("Level: " + shipLevel);
        lbLive.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        lbLive.setTextFill(Color.CRIMSON);

        lbScore.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        lbScore.setTextFill(Color.CRIMSON);

        lbLevel.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        lbLevel.setTextFill(Color.CRIMSON);

        hBoxStats.getChildren().addAll(lbLive, lbScore, lbLevel);
        pane.getChildren().add(hBoxStats);
    } // updateStats

    /**
     * This method controls how the player
     * fires
     *
     * @param primaryStage primaryStage of the application
     */
    public void fire(Stage primaryStage) {
        // creates a bullet
        Rectangle r = new Rectangle(imageShip.getLayoutX() + 20, imageShip.getLayoutY() - 15, 10, 30);
        r.setFill(Color.RED);
        r.setArcHeight(10);
        r.setArcWidth(10);
        pane.getChildren().add(r);
        // animation
        Duration duration = new Duration(5);
        KeyFrame keyFrame = new KeyFrame(duration, e -> {
            r.setY(r.getY() - 5);
            for (int i = 0; i < enemyShip.size(); i++) {
                // checks for collision
                if (r.getBoundsInParent().intersects(enemyShip.get(i).getBoundsInParent())) {
                    if (enemyShip.get(i).isVisible()) {
                        r.setY(r.getY() - 200);
                        pane.getChildren().remove(r);
                        enemyShip.get(i).setVisible(false);
                        shipScore += 100;
                        updateStats();
                        // moves to to next level if all enemies are down
                        if (getEnemyShipAlive().isEmpty()) {
                            // pauses animations
                            for (int p = 0; p < timelineArray.size(); p++) {
                                timelineArray.get(p).stop();
                            } // for
                            // update stats
                            num += 15;
                            shipLevel++;
                            getEnemy(primaryStage);
                            // Level update  
                            Text text = new Text("Level: " + shipLevel);
                            text.setFont(Font.font("Verdana", FontWeight.BOLD, 60));
                            text.setFill(Color.RED);
                            text.setLayoutX(300);
                            text.setLayoutY(300);
                            pane.getChildren().add(text);
                            FadeTransition ft = new FadeTransition(Duration.millis(2000), text);
                            ft.setFromValue(1.0);
                            ft.setToValue(0);
                            ft.setCycleCount(1);
                            ft.setAutoReverse(true);
                            ft.play();

                        } // if
                    } // if
                } // if
            } // for
        }); // keyframes
        Timeline timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    } // fire
    public static void main(String[] args) {
        Application.launch(args);
    }
    /**
     * Creates the new chip to be placed.
     *
     * @author Felix
     * @param team	the team the chip is on
     * @param x	the x coordinate
     * @param y	the y coordinate
     */
    private Chip newChip(boolean team, int x, int y) {
        Chip chip = new Chip(team, x, y);
        chip.setOnMouseReleased(e -> {
            int newX = convert(chip.getLayoutX());
            int newY = convert(chip.getLayoutY());

            Turn result = attempt(chip, newX, newY);

            // End of the game
            if (redChips == 0 || blueChips == 0) {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setHeaderText("Instructions");
                alert.setContentText("FINISHED!!!!\n"
                        + "Press File -> Return to Menu\n"
                        + "...or you can stay to bask in victory.\n");
                Optional<ButtonType> reeeee = alert.showAndWait();
            }

            int x0 = convert(chip.getOldX());
            int y0 = convert(chip.getOldY());

            switch (result.getType()) {
                case NONE:
                    chip.nope();
                    break;
                case NORMAL:
                    chip.move(newX, newY);
                    board[x0][y0].setChip(null);
                    board[newX][newY].setChip(chip);
                    if (chip.team == true) {
                        if (newY == 0) {
                            chip.setKing(chip, true);
                        } // if
                    } // if
                    else if (chip.team == false) {
                        if (newY == 7) {
                            chip.setKing(chip, true);
                        } // if
                    } // else if
                    break;
                case KILL:
                    chip.move(newX, newY);
                    board[x0][y0].setChip(null);
                    board[newX][newY].setChip(chip);
                    Chip killed = result.getChip();
                    board[convert(killed.getOldX())][convert(killed.getOldY())].setChip(null);
                    if (killed.getTeam() == true) {
                        blueChips--;
                    }
                    if (killed.getTeam() == false) {
                        redChips--;
                    }
                    chipGroup.getChildren().remove(killed);
                    if (chip.team == true) {
                        if (newY == 0) {
                            chip.setKing(chip, true);
                        } // if
                    } // if
                    else if (chip.team == false) {
                        if (newY == 7) {
                            chip.setKing(chip, true);
                        } // if
                    } // else if
                    break;
            }
        });

        return chip;
    } // newChip

    /**
     * Check for the attempt to move the chip.
     *
     * @author Felix
     * @param chip	the chip that is being moved
     * @param x	the x coordinate
     * @param y	the y coordinate
     */
    private Turn attempt(Chip chip, int newX, int newY) {
        if (chip.isKing() == false) {
            if (board[newX][newY].hasChip() || (newX + newY) % 2 == 0) {
                if (chip.team == true) {
                    if (newY == 0) {
                        chip.setKing(chip, true);
                    } // if
                } // if
                else if (chip.team == false) {
                    if (newY == 7) {
                        chip.setKing(chip, true);
                    } // if
                } // else if	
                return new Turn(Turn.Movement.NONE);
            } // if

            int x0 = convert(chip.getOldX());
            int y0 = convert(chip.getOldY());

            if (Math.abs(newX - x0) == 1 && newY - y0 == chip.direction) {
                if (chip.team == true) {
                    if (newY == 0) {
                        chip.setKing(chip, true);
                    } // if
                } // if
                else if (chip.team == false) {
                    if (newY == 7) {
                        chip.setKing(chip, true);
                    } // if
                } // else if
                return new Turn(Turn.Movement.NORMAL);
            } // if
            else if (Math.abs(newX - x0) == 2 && newY - y0 == chip.direction * 2) {
                int x1 = x0 + (newX - x0) / 2;
                int y1 = y0 + (newY - y0) / 2;

                if (board[x1][y1].hasChip() && board[x1][y1].getChip().getTeam() != chip.getTeam()) {
                    if (chip.team == true) {
                        if (newY == 0) {
                            chip.setKing(chip, true);
                        } // if
                    } // if
                    else if (chip.team == false) {
                        if (newY == 7) {
                            chip.setKing(chip, true);
                        } // if
                    } // else if				
                    return new Turn(Turn.Movement.KILL, board[x1][y1].getChip());
                } // if
            } // else if
        } // if
        else {
            if (board[newX][newY].hasChip() || (newX + newY) % 2 == 0) {
                return new Turn(Turn.Movement.NONE);
            } // if

            int x0 = convert(chip.getOldX());
            int y0 = convert(chip.getOldY());

            if (Math.abs(newX - x0) == 1) {
                return new Turn(Turn.Movement.NORMAL);
            } // if
            else if (Math.abs(newX - x0) == 2) {
                int x1 = x0 + (newX - x0) / 2;
                int y1 = y0 + (newY - y0) / 2;

                if (board[x1][y1].hasChip() && board[x1][y1].getChip().getTeam() != chip.getTeam()) {
                    return new Turn(Turn.Movement.KILL, board[x1][y1].getChip());
                } // if
            } // else if
        } // else

        return new Turn(Turn.Movement.NONE);

    } // attempt

    /**
     * Converts pixels to the board size.
     *
     * @author Felix
     * @param px	the pixels to convert
     */
    private int convert(double px) {
        return (int) (px + size / 2) / size;
    } // convert
}
