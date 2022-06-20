package GameState;

import Entity.Enemies.Slugger;
import Entity.Enemy;
import Entity.Explosion;
import Entity.HUD;
import Entity.Player;
import Main.GamePanel;
import TileMap.Background;
import TileMap.TileMap;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Level1State extends GameState {

    private TileMap tileMap;
    private Background bg;

    private HUD hud;

    private Player player;
    private ArrayList<Enemy> enemies;
    private ArrayList<Explosion> explosions;
    private GameStateManager gsm;
    private boolean inGame = false;

    public Level1State(GameStateManager gsm) {
        this.gsm = gsm;
        init();
    }

    public void init(){

        tileMap = new TileMap(30);
        tileMap.loadTiles("/Tilesets/grasstileset.gif");
        tileMap.loadMap("/Maps/level1-1.map");
        tileMap.setPosition(0,0);
        tileMap.setTween(1);



        bg = new Background("/Backgrounds/grassbg1.gif", 0.1);

        player = new Player(tileMap);
        player.setPosition(100,100);

        populateEnemies();

        explosions = new ArrayList<>();

        hud = new HUD(player);
        inGame = true;


    }

    private void populateEnemies(){
        enemies = new ArrayList<Enemy>();

        Slugger s;
        Point[] point = new Point[]{
                new Point(860,200),
                new Point(1525,200),
                new Point(1680, 200),
                new Point(1800,200)
        };
        for(int i = 0; i < point.length;i++){
            s = new Slugger(tileMap);
            s.setPosition(point[i].x,point[i].y);
            enemies.add(s);
        }

       // s.setPosition(860,200);

    }




    public void keyPressed(int k) {
        if(k == KeyEvent.VK_LEFT)player.setLeft(true);
        if(k == KeyEvent.VK_RIGHT)player.setRight(true);
        if(k == KeyEvent.VK_UP)player.setUp(true);
        if(k == KeyEvent.VK_DOWN)player.setDown(true);
        if(k == KeyEvent.VK_W)player.setJumping(true);
        if(k == KeyEvent.VK_E)player.setGliding(true);
        if(k == KeyEvent.VK_R)player.setScratching();
        if(k == KeyEvent.VK_F)player.setFiring();
    }


    public void keyReleased(int k) {
        if(k == KeyEvent.VK_LEFT)player.setLeft(false);
        if(k == KeyEvent.VK_RIGHT)player.setRight(false);
        if(k == KeyEvent.VK_UP)player.setUp(false);
        if(k == KeyEvent.VK_DOWN)player.setDown(false);
        if(k == KeyEvent.VK_W)player.setJumping(false);
        if(k == KeyEvent.VK_E)player.setGliding(false);
        if(k == KeyEvent.VK_F)player.setFiring();
       // if(k == KeyEvent.VK_R)player.setScratchingFalse();
       // if(k == KeyEvent.VK_F)player.setFiringFalse();

    }

    public void update() {
        player.update();
        tileMap.setPosition(
                GamePanel.WIDTH / 2 - player.getx(),
                GamePanel.HEIGHT / 2 - player.gety());

        //set background
        bg.setPosition(tileMap.getx(), tileMap.gety());

        //attack enemies
        player.checkAttack(enemies);

        //update all enemies
        for(int i = 0 ; i < enemies.size(); i++){
            Enemy e = enemies.get(i);
            enemies.get(i).update();
            if(enemies.get(i).isDead()){
                enemies.remove(i);
                i--;
                explosions.add(
                        new Explosion(e.getx(),e.gety()));
            }
        }

        //update explosions
        for(int i = 0; i < explosions.size();i++){
            explosions.get(i).update();
            if(explosions.get(i).shouldRemove()){
                explosions.remove(i);
                i--;
            }
        }

        //check if dead
        if(player.isDead() && inGame == true){
            System.out.println("Im dead");
            gsm.setState(GameStateManager.MENUSTATE);

            update();
        }

        if(player.gety() > 220 && inGame == true){
            gsm.setState(GameStateManager.MENUSTATE);
            inGame = false;
            update();

        }




    }

    public void draw(Graphics2D g) {

        //clear screen
        bg.draw(g);

        //draw map
        tileMap.draw(g);

        //draw player
        player.draw(g);

        //draw enemies
        for(int i = 0; i < enemies.size(); i++){
            enemies.get(i).draw(g);
        }

        //draw explosions
        for(int i = 0; i < explosions.size();i++){
            explosions.get(i).setMapPosition((int)tileMap.getx(), (int)tileMap.gety());
            explosions.get(i).draw(g);

        }

        //draw hud
        hud.draw(g);
    }
}
