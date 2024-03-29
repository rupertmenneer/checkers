package Checkers_game;

import java.util.ArrayList;

public class Move {
    private final int x;
    private final int y;
    private final int old_x;
    private final int old_y;
    private final ArrayList<Piece> piecesTaken;
    private final Piece piece;
    private boolean capturesKing;
    private final boolean alreadyKing;

    public Move(Piece piece, int x, int y, int old_x, int old_y){
        this.piece = piece;
        this.x = x;
        this.y = y;
        this.old_x = old_x;
        this.old_y = old_y;
        this.alreadyKing = piece.isKing();
        piecesTaken = new ArrayList<>();
    }

    public void setCapturesKing() {
        this.capturesKing = doesMoveCaptureKing();
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getOldX() { return old_x; }
    public int getOldY() { return old_y; }
    public boolean capturesKing(){return capturesKing; }
    public boolean isAlreadyKing(){return alreadyKing;}

    private boolean doesMoveCaptureKing(){
        for(Piece taken : piecesTaken){
            if (taken.isKing()){
                return true;
            }
        }
        return false;
    }


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
}
