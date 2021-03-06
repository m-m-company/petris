package com.petris;

import java.util.Random;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Queue;
import com.managers.graphics.GraphicManager;
import com.managers.record.RecordManager;
import com.managers.sound.SoundManager;
import com.map.Map;
import com.petris.pieces.Piece;
import com.petris.pieces.PieceI;
import com.petris.pieces.PieceLLeft;
import com.petris.pieces.PieceLRight;
import com.petris.pieces.PieceS;
import com.petris.pieces.PieceT;
import com.petris.pieces.PieceZLeft;
import com.petris.pieces.PieceZRight;

public class Petris extends ApplicationAdapter {
	
	/*
	 * This is the main class
	 * At the start the create() method is called
	 * Every frame the render() method is called
	 * At the end the dispose() method is called
	 */
	
	public final static int PLAY_STATUS = 1;
	public final static int QUIT_STATUS = 2;
	
    GraphicManager graphicManager;
    SoundManager soundManager;
    RecordManager recordManager;
    Piece actual;
    Piece hold;
    Queue<Piece> nextPieces;
    Map map;
    int stage;
    int status;
    float difficulty;
    float delay;
    float endDelay;
    float blink;
    boolean started;
    boolean pause;
    boolean menu;
    boolean canSwap;
    Integer points;

    @Override
    public void create() {
        graphicManager = new GraphicManager();
        soundManager = new SoundManager();
        recordManager = new RecordManager();
        nextPieces = new Queue<Piece>();
        reset();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if(menu)
        	menuManager();
        else {
        	if (!pause) {
                play();
            } else {
                if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                    soundManager.playPause();
                    soundManager.playMusic();
                    pause = false;
                }
            }
            if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            	soundManager.stopMusic();
            	reset();
            }
            recordManager.changeHighScore(points);
            graphicManager.drawBackground();
            graphicManager.drawBorders(map);
            graphicManager.drawPoints(points);
            graphicManager.drawRecord(recordManager.getHighUser(), recordManager.getPoints(Integer.toString(points)));
            graphicManager.drawPieces(actual, hold, nextPieces, endDelay);
            graphicManager.drawMap(map, blink);
            graphicManager.restoreTransparency();
        }
    }
    
    //Reset the game
    private void reset() {
    	map = new Map();
    	createPiece();
    	hold = null;
        stage = 1;
        status = Petris.PLAY_STATUS;
        difficulty = 0.45f;
        delay = 0;
        points = 0;
        endDelay = 0;
        blink = 0;
        pause = false;
        menu = true;
        canSwap = true;
    }
    
    private void menuManager() {
    	graphicManager.drawMenu(status);
    	if(status == Petris.PLAY_STATUS && (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)))
			status = Petris.QUIT_STATUS;
		else if(status == Petris.QUIT_STATUS && (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)))
			status = Petris.PLAY_STATUS;
		if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
			Gdx.app.exit();
		if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
			if(status == Petris.PLAY_STATUS) {
				soundManager.playMusic();
				menu = false;
			}
			else if(status == Petris.QUIT_STATUS)
				Gdx.app.exit();
		}
    }

    //Change the piece with the piece in hold if present, otherwhise it just go to the next
    public void swapWithHold() {
        if (hold == null) {
            hold = actual;
            hold.goInHold();
            createPiece();
        } else {
            Piece tmp = actual;
            actual = hold;
            hold = tmp;
            hold.goInHold();
            actual.goToStart();
        }
        canSwap = false;
    }

    public void play() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) && !map.leftCollision(actual))
            actual.moveLeft();
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) && !map.rightCollision(actual))
            actual.moveRight();
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && !map.isAtTheEnd(actual)) {
            if (map.canRotate(actual))
                soundManager.playRotate();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.V) && canSwap) {
            swapWithHold();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            soundManager.stopMusic();
            soundManager.playPause();
            pause = true;
        }

        points += map.petrisControl();
        if (!map.rowsToDelete.isEmpty()) {
            blink += Gdx.graphics.getDeltaTime();
        }
        if (blink >= 1.0) {
            map.rowsToDelete.clear();
            blink = 0;
        }
        
        delay += Gdx.graphics.getDeltaTime();
        if (delay > difficulty && !map.isAtTheEnd(actual)) {
            actual.move();
            delay = 0;
            started = false;
        }
        
        //Died
        if (map.isAtTheEnd(actual) && started) {
        	recordManager.update(Integer.toString(points));
            soundManager.stopMusic();
            soundManager.playGameover();
            reset();
        }

        if (map.isAtTheEnd(actual)) {
            endDelay += Gdx.graphics.getDeltaTime();
            if (endDelay > 0.6) {
                map.addPiece(actual);
                points += 100;
                createPiece();
                endDelay = 0;
                graphicManager.restoreTransparency();
            }
        } else {
            endDelay = 0;
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && !started) {
                actual.move();
                points += 10;
            }
        }
        
        this.setDifficulty();
    }

    private Piece chooseAPiece() {
        Random r = new Random();
        int tmp = r.nextInt(7);
        switch (tmp) {
            case 0:
                return new PieceI("blue.png");
            case 1:
                return new PieceLLeft("green.png");
            case 2:
                return new PieceLRight("grey.png");
            case 3:
                return new PieceS("yellow.png");
            case 4:
                return new PieceT("red.png");
            case 5:
                return (new PieceZLeft("pink.png"));
            case 6:
                return (new PieceZRight("violet.png"));
        }
        return new PieceI("blue.png");
    }

    private void createPiece() {
        started = true;
        nextPieces.addLast(chooseAPiece());
        actual = nextPieces.first();
        nextPieces.removeFirst();
        canSwap = true;
        while (nextPieces.size < 4)
            nextPieces.addLast(chooseAPiece());
    }
    
    private void setDifficulty() {
    	if(points > 5000*stage) {
    		++stage;
    		difficulty -= 0.1f;
    	}
    }

    @Override
    public void dispose() {
        soundManager.dispose();
        graphicManager.dispose();
    }
}
