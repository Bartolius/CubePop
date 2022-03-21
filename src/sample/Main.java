package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class Main extends Application {

    public Thread gameLoop;
    public Thread Timer;

    public void settings(Stage primaryStage){
        primaryStage.setTitle("CubePop");
        /*try{
            primaryStage.getIcons().add(new Image("images/icon.png"));
        }catch (Exception e){
            System.out.println("No icon");
        }*/
        primaryStage.setResizable(false);
        primaryStage.setWidth(960);
        primaryStage.setHeight(540);
    }
    @Override
    public void start(Stage primaryStage){// throws Exception{
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        //settings
        settings(primaryStage);
        //elements
        AnchorPane anchorPane = new AnchorPane();
        Scene scene = new Scene(anchorPane, primaryStage.getWidth(), primaryStage.getHeight(), Color.WHITE);
        final Canvas canvas = new Canvas(primaryStage.getWidth(), primaryStage.getHeight());
        //position of canvas
        AnchorPane.setTopAnchor(canvas, 0.d);
        AnchorPane.setBottomAnchor(canvas, 0.d);
        AnchorPane.setLeftAnchor(canvas, 0.d);
        AnchorPane.setRightAnchor(canvas, 0.d);
        //canvas context
        GraphicsContext gtx = canvas.getGraphicsContext2D();


        //#####################################canvas code#########################################

        //objects
        Map map = new Map(new Position(10, 10), new Size(40, 40), 0);
        map.setGrid(new Size(10, 10));
        Player player = new Player();
        player.drawPoints(map, gtx);
        clock clock=new clock(0);

        //OnMouseClick event
        EventHandler<MouseEvent> eventHandler = e -> {
            if (
                    //if map isn't running and mouse position is on the grid
                    !map.running&&
                    e.getX() >= map.pos.x &&
                            e.getX() <= map.pos.x + map.cellSize.width * map.grid.length &&
                            e.getY() >= map.pos.y &&
                            e.getY() <= map.pos.y + map.cellSize.height * map.grid[0].length
            ) {
                //save position
                Position p = new Position((int) Math.floor((e.getX() - map.pos.x) / map.cellSize.width), (int) Math.floor((e.getY() - map.pos.y) / map.cellSize.height));
                //find all blocks adherent to clicked block
                Position[] D = map.BlocksToDelete(p, map.grid[p.x][p.y].id);
                //if blocks are more than 2
                if (D.length > 2) {
                    //save length
                    int a = D.length;
                    //foreach all blocks
                    for (Position pos :
                            D) {
                        //delete each block from grid
                        map.grid[pos.x][pos.y].delete(map.color);
                    }
                    //draw map
                    map.Draw(gtx);
                    //add 1 move to player
                    player.moves++;
                    //add a+(a-3) points
                    player.points += a + (a - 3);
                    //if points is bigger than color length - 3 and color length -4 taken to power
                    if(player.points>100*(Math.pow(map.color.color.length-3,2)-Math.pow(map.color.color.length-4,2))){
                        //create new color
                        map.color.SetColor(Color.rgb((int)Math.floor(Math.random()*256),(int)Math.floor(Math.random()*256),(int)Math.floor(Math.random()*256)));
                    }
                    //graw player points
                    player.drawPoints(map, gtx);
                    //if modulo 20 from player points is equal to 0
                    if(player.moves%20==0){
                        //print points,moves and time to console
                        System.out.println("You got "+player.points+" points in "+player.moves+" moves. Time: "+clock.hours+"h:"+clock.min+"min:"+clock.sec+"s.");
                    }
                }
            }else if(
                    //else if mouse position is on points and moves
                    e.getX()>map.size.width+30&&
                    e.getX()<=map.size.width+30+100&&
                    e.getY()>map.size.height/2-20&&
                    e.getY()<=map.size.height/2-20+50
            ){
                //replace old grid, new random grid
                map.setGrid(new Size(map.grid.length,map.grid[0].length));
                //add 1 move to player
                player.moves++;
                //draw points and moves
                player.drawPoints(map, gtx);
                //if modulo 20 from player points is equal to 0
                if(player.moves%20==0){
                    //print points,moves and time to console
                    System.out.println("You got "+player.points+" points in "+player.moves+" moves. Time: "+clock.hours+"h:"+clock.min+"min:"+clock.sec+"s.");
                }
            }
            gtx.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
        };
        canvas.addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler);

        //timer and gameloop
        gameLoop = new Thread(() -> map.animation(gtx));
        Timer = new Thread(clock::clock);


        //#####################################canvas code#########################################


        //application show
        anchorPane.getChildren().setAll(canvas);
        primaryStage.setScene(scene);
        primaryStage.show();
        gameLoop.start();
        Timer.start();
    }

    @Override
    //at end of program
    public void stop(){
        //close all threads
        gameLoop.stop();
        Timer.stop();
        //print text to console
        System.out.println("Have a nice day ;D");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
