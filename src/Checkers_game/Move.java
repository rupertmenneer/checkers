package Checkers_game;

import java.util.ArrayList;

public class Move {
    private int x;
    private int y;
    private ArrayList<Piece> piecesTaken;

    public Move(int x, int y){
        this.x = x;
        this.y = y;
        piecesTaken = new ArrayList<>();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean equals(Move m) {
        return this.x == m.getX() && this.y == m.getY();
    }

    public void pieceTaken(Piece p){
        this.piecesTaken.add(p);
    }

    public ArrayList<Piece> getPiecesTaken() {
        return piecesTaken;
    }
}
