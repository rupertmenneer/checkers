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

    public void pieceTaken(ArrayList<Piece> p){
        this.piecesTaken.addAll(p);
    }

    public ArrayList<Piece> getPiecesTaken() {
        return piecesTaken;
    }

    public Piece getPiece() {
        return piece;
    }

    public void printMove(){
        for (Piece taken : piecesTaken){
            System.out.println("Piece taken from: " + taken.getBoardX() + " " + taken.getBoardY());
        }
//        System.out.println("This move - from old X " + old_x + " old Y " + old_y + " to new X  " + x + " new Y " + y + " takes this many pieces: " + piecesTaken.size());
    }
}
