package Checkers_game;

import java.util.ArrayList;

public class Move {
    private int x;
    private int y;
    private int old_x;
    private int old_y;
    private ArrayList<Piece> piecesTaken;
    private Piece piece;

    public Move(Piece piece, int x, int y, int old_x, int old_y){
        this.piece = piece;
        this.x = x;
        this.y = y;
        this.old_x = old_x;
        this.old_y = old_y;
        piecesTaken = new ArrayList<>();
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getOldX() { return old_x; }
    public int getOldY() { return old_y; }

    public boolean equals(Move m) {
        return this.x == m.getX() && this.y == m.getY();
    }

    public void pieceTaken(Piece p){
        this.piecesTaken.add(p);
    }

    public ArrayList<Piece> getPiecesTaken() {
        return piecesTaken;
    }

    public Piece getPiece() {
        return piece;
    }
}
