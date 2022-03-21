package sample;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

//position object
class Position{
    int x;
    int y;
    //constructor
    public Position(int x,int y){
        this.x=x;
        this.y=y;
    }
}

//size object
class Size{
    int width;
    int height;
    //constructor
    public Size(int width, int height){
        this.width = width;
        this.height = height;
    }
}

//color object
class ColorId{
    //table of colors
    Color[] color = {Color.WHITE,Color.RED,Color.GREEN,Color.BLUE};
    //constructor
    public ColorId(){}
    //getting color by id
    public Color GetColor(int id){return this.color[id];}
    //setting new color
    public void SetColor(Color color){
        //copping old array with 1 empty column
        Color[] colTab=new Color[this.color.length+1];
        System.arraycopy(this.color, 0, colTab, 0, this.color.length);
        //adding new color
        colTab[colTab.length-1]=color;
        //settings all colors
        this.color=colTab;
    }
}

//map object
class Map{
    //data
    Position pos;
    Block[][] grid;
    Size cellSize;
    Size size;
    int border;
    ColorId color=new ColorId();
    boolean running =false;
    //constructor
    public Map(Position pos,Size cellSize,int border){
        this.pos=pos;
        this.cellSize=cellSize;
        this.border=border;
    }
    //creating new grid
    public void setGrid(Size size){
        //new grid
        this.grid=new Block[size.width][size.height];
        //foreach all, grid add new block with random id from 1 to colors length - 1
        for (int i=size.width-1;i>=0;i--) {
            for (int j = size.height-1; j >=0 ; j--) {
                this.grid[i][j] = new Block(new Position(i, j), (int) Math.floor(Math.random() * (this.color.color.length-1)) + 1,this.color);
            }
        }
        //creating size of map
        this.size = new Size(
                this.grid.length * this.cellSize.width,
                this.grid[0].length * this.cellSize.height
        );
    }
    //drawing map
    public void Draw(GraphicsContext gtx){
        //set runing if on grid is block with id 0
        this.running = this.find(0);
        if(running){
            //foreach block
            for(int i=this.grid.length-1;i>=0;i--){
                for(int j=0;j<this.grid[0].length;j++){
                    //if block id is 0 and width -1 is greater or equals 0
                    if(this.grid[i][j].id==0&&j-1>=0){
                        //move higher block to lower position
                        this.grid[i][j].add(this.grid[i][j-1].id,this.color);
                        //delete old block
                        this.grid[i][j-1].delete(this.color);

                        //else if block id is 0 and height is 0
                    }else if(this.grid[i][j].id==0&&j==0){
                        //generate random block
                        this.grid[i][j].generate(this.color);
                    }
                }
            }
        }
        //foreach block
        for (Block[] gridW: grid) {
            for (Block gridH: gridW) {
                //draw each block
                gridH.draw(gtx,this.cellSize,this.pos);
            }
        }
    }
    //find block with id
    public boolean find(int id){
        //foreach block
        for (Block[] a:
             this.grid) {
            for (Block b:
                 a) {
                try{
                    if(b.id==id){
                        //if block with this id exists return true
                        return true;
                    }
                }catch(NullPointerException npe){

                }
            }
        }
        //otherwise return false
        return false;
    }
    //animate blocks loop
    public void animation(GraphicsContext gtx){
        while(true){
            //draw map
            this.Draw(gtx);
            try{
                //loop delay
                Thread.sleep(50);
            }catch (InterruptedException e){
                //e.printStackTrace();
            }
        }
    }
    //delete blocks positions
    //returning positions of all blocks adjacent to same block
    public Position[] BlocksToDelete(Position pos,int id){
        //creating nodes with opened and closed blocks
        Node openedNode=new Node();
        Node closedNode=new Node();
        //open first node
        openedNode.addNode(this.grid[pos.x][pos.y]);
        //foreach opened node
        for(int i=0;i<openedNode.getNodes().length;){
            //get first node
            Position o=openedNode.getNode(0);
            //if this node not exists in closed nodes
            if(!closedNode.FindNode(o)){
                //if this node is above this node
                if(o.x>0){
                    if(this.grid[o.x-1][o.y].id==id){
                        //add it to open nodes
                        openedNode.addNode(this.grid[o.x-1][o.y]);
                    }
                }
                //if this node is on the left side of this node
                if(o.y>0){
                    if(this.grid[o.x][o.y-1].id==id){
                        //add it to open nodes
                        openedNode.addNode(this.grid[o.x][o.y-1]);
                    }
                }
                //if this node is bellow this node
                if(o.x<this.grid.length-1){
                    if(this.grid[o.x+1][o.y].id==id){
                        //add it to open nodes
                        openedNode.addNode(this.grid[o.x+1][o.y]);
                    }
                }
                //if this node is on the right side of this node
                if(o.y<this.grid[0].length-1){
                    if(this.grid[o.x][o.y+1].id==id){
                        //add it to open nodes
                        openedNode.addNode(this.grid[o.x][o.y+1]);
                    }
                }
                //add current node to closed nodes
                closedNode.addNode(this.grid[o.x][o.y]);
            }
            //if none node are opened, break loop
            if(openedNode.deleteNode()){
                break;
            }
        }
        //return positions of all nodes
        return closedNode.getNodes();
    }
}

//block object
class Block{
    //data
    Position pos;
    int id;
    Color color;
    //constructor
    public Block(Position pos, int id,ColorId color){
        this.pos=pos;
        this.id=id;
        this.color=color.GetColor(id);
    }
    //draw method
    public void draw(GraphicsContext gtx, Size cellSize, Position pos){
        //drawing rectangle on block position
        gtx.setFill(this.color);
        gtx.fillRect(this.pos.x*cellSize.width+ pos.x,this.pos.y*cellSize.height+ pos.y,cellSize.width,cellSize.height);
        gtx.setFill(Color.WHITE);
    }
    //deleting id of block
    public void delete(ColorId color){
        this.id=0;
        this.color=color.GetColor(this.id);
    }
    //adding id to block
    public void add(int id,ColorId color){
        this.id=id;
        this.color=color.GetColor(this.id);
    }
    //generating random id from 1 to color length -1
    public void generate(ColorId color){
        this.id=(int)Math.floor(Math.random() * (color.color.length-1)) + 1;
        this.color=color.GetColor(this.id);
    }
}

//node object
class Node{
    //data
    private Position[] position=new Position[0];
    //constructor
    public Node(){}
    //adding method
    public void addNode(Block block){
        //copying old table
        Position[] position=new Position[this.position.length+1];
        System.arraycopy(this.position, 0, position, 0, this.position.length);
        //adding new node
        position[position.length-1]=block.pos;
        //setting nodes
        this.position=position;
    }
    //delete method
    public boolean deleteNode(){
        //copying old table without first element
        Position[] position=new Position[this.position.length-1];
        if (position.length >= 0) System.arraycopy(this.position, 1, position, 0, position.length);
        //setting nodes
        this.position=position;
        //return if nodes length is equal to 0
        return this.position.length == 0;
    }
    //method return all nodes
    public Position[] getNodes(){
        return position;
    }
    //method return node of int id
    public Position getNode(int id){
        return position[id];
    }
    //find method
    public boolean FindNode(Position pos){
        //foreach node
        for (Position p:
             this.position) {
            //if bode position is equal to pos, return true
            if(p.x==pos.x&&p.y==pos.y){
                return true;
            }
        }
        //otherwise return false
        return false;
    }
}

//player object
class Player{
    int moves=0;
    int points=0;
    //constructor
    public Player(){}
    //drawing points and moves
    public void drawPoints(Map map,GraphicsContext gtx){
        //clear old background
        gtx.clearRect((double)map.size.width+30,(double)map.size.height/2-20,100,50);
        //background
        gtx.setFill(Color.rgb(236,246,151));
        gtx.fillRect((double)map.size.width+30,(double)map.size.height/2-20,100,50);
        gtx.setFill(Color.rgb(151,246,236));
        gtx.strokeRect((double)map.size.width+30,(double)map.size.height/2-20,100,50);

        //set moves and points
        gtx.setFill(Color.BLACK);
        gtx.fillText("Moves "+this.moves,(double)map.size.width+50,(double)map.size.height/2);
        gtx.fillText("Points: "+this.points,(double)map.size.width+50,(double)map.size.height/2+20);
        gtx.setFill(Color.WHITE);
    }
}

//clock object
class clock{
    short sec=0;
    short min=0;
    short hours=0;
    //constructor
    public clock(int seconds){
        //splitting time to seconds, minutes and hours
        if(seconds>0){
            this.sec=(short)(seconds%60);
            if(seconds-this.sec>59){
                this.min=(short)((seconds-this.sec)/60%60);
                if(seconds-this.min*60-this.sec>59){
                    this.hours=(short)(((seconds-this.sec)/60-this.min)/60);
                }
            }
        }
    }
    //clock loop
    public void clock(){
        while(true){
            //add 1 sec
            sec++;
            //if seconds id greater or equal 60, subtract 60 seconds and add 1 minute
            if(sec>=60){
                sec-=60;
                min++;
            }
            //if minutes id greater or equal 60, subtract 60 minutes and add 1 hour
            if(min>=60){
                min-=60;
                hours++;
            }
            try {
                //timer delay
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
        }
    }
}