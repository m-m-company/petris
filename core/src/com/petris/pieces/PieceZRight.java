package com.petris.pieces;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class PieceZRight extends Piece {

	public PieceZRight() {
		// TODO Auto-generated constructor stub
	}

	public PieceZRight(Color c) {
		super(c);
		blocks[0] = new Rectangle(800/2+Piece.BLOCK_HEIGHT, 0, Piece.BLOCK_HEIGHT, Piece.BLOCK_HEIGHT);
		blocks[1] = new Rectangle(800/2,0,Piece.BLOCK_HEIGHT,Piece.BLOCK_HEIGHT);
		blocks[2] = new Rectangle(800/2,Piece.BLOCK_HEIGHT,Piece.BLOCK_HEIGHT,Piece.BLOCK_HEIGHT);
		blocks[3] = new Rectangle(800/2-Piece.BLOCK_HEIGHT,Piece.BLOCK_HEIGHT,Piece.BLOCK_HEIGHT,Piece.BLOCK_HEIGHT);

	}

	@Override
	public void rotate() {
		if(this.getState() == 0) {
			blocks[0].setY(blocks[0].getY()+2*Piece.BLOCK_HEIGHT);
			blocks[1].setY(blocks[1].getY()+Piece.BLOCK_HEIGHT);
			blocks[1].setX(blocks[1].getX()+Piece.BLOCK_HEIGHT);
			blocks[3].setX(blocks[3].getX()+Piece.BLOCK_HEIGHT);
			blocks[3].setY(blocks[3].getY()-Piece.BLOCK_HEIGHT);
			this.setState(1);
		}
		else if(this.getState() == 1) {
			blocks[0].setY(blocks[0].getY()-2*Piece.BLOCK_HEIGHT);
			blocks[1].setY(blocks[1].getY()-Piece.BLOCK_HEIGHT);
			blocks[1].setX(blocks[1].getX()-Piece.BLOCK_HEIGHT);
			blocks[3].setX(blocks[3].getX()-Piece.BLOCK_HEIGHT);
			blocks[3].setY(blocks[3].getY()+Piece.BLOCK_HEIGHT);
			this.setState(0);
		}
	}

	@Override
	protected boolean canRotate(Vector2[] v) {
		// TODO Auto-generated method stub
		return false;
	}
}
