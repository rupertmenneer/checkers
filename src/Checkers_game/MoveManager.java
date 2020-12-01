package Checkers_game;

import java.util.ArrayList;
import java.util.Collections;

public class MoveManager {

    private Tile[][] board;
    private Piece p;
    private ArrayList<Move> valid_moves;

    public MoveManager(Tile[][] board, Piece p){
        this.board = board;
        this.p = p;
        this.valid_moves = new ArrayList<>();
    }

    private ArrayList<Move> findValidMoves(int x, int y, Move prevMove){
        ArrayList<Move> move_list = new ArrayList<>();
        // assign direction
        int d = p.getPlayer().direction;
        // skip non capturing moves if piece has already captured
        if(prevMove == null) {
            // check non capture left (+ move within board)
            if (withinBoundary(x-1, y+d) && !board[x - 1][y + d].has_piece()) {
                move_list.add(new Move(p,x - 1, y + d, p.getBoardX(), p.getBoardY()));
            }
            // KING check non capture left (+ move within board)
            if (p.isKing() && withinBoundary(x-1, y-d) && !board[x - 1][y - d].has_piece()) {
                move_list.add(new Move(p,x - 1, y - d, p.getBoardX(), p.getBoardY()));
            }
            // check non capture right (+ move within board)
            if (withinBoundary(x+1, y+d) && !board[x + 1][y + d].has_piece()) {
                move_list.add(new Move(p,x + 1, y + d, p.getBoardX(), p.getBoardY()));
            }
            // KING check non capture right (+ move within board)
            if (p.isKing() && withinBoundary(x+1, y-d) && !board[x + 1][y - d].has_piece()) {
                move_list.add(new Move(p,x + 1, y - d, p.getBoardX(), p.getBoardY()));
            }
        }
        // check if there is a valid capture move left (enemy diagonal AND free space in line afterwards)
        if(captureCheck(x, y, 1, d)){
            // if capture is valid, add move and check if any more valid captures from new position
            Move m = new Move(p,x + 2, y + (2*d), p.getBoardX(), p.getBoardY());
            // add previously captured piece to this move's pieces taken
            if(prevMove != null) {
                m.pieceTaken(prevMove.getPiecesTaken());
            }
                // add newly captured piece to pieces taken
            if (prevMove == null | !alreadyTaken(board[x + 1][y + d].getPiece(), m)) {
                ArrayList<Piece> taken_piece = new ArrayList<>();
                taken_piece.add(board[x + 1][y + d].getPiece());
                m.pieceTaken(taken_piece);
                move_list.add(m);
                move_list.addAll(findValidMoves(x + 2, y + (2 * d), m));
            }

        }
        // check if there is a valid capture move right (enemy diagonal AND free space in line afterwards)
        if(captureCheck(x, y, -1, d)){
            // if capture is valid, add move and check if any more valid captures from new position
            Move m = new Move(p,x - 2, y + (2*d), p.getBoardX(), p.getBoardY());
            // add previously captured piece to this move's pieces taken
            if(prevMove != null) {
                m.pieceTaken(prevMove.getPiecesTaken());
                // add newly captured piece to pieces taken
            }
            if (prevMove == null | !alreadyTaken(board[x - 1][y + d].getPiece(), m)) {
                ArrayList<Piece> taken_piece = new ArrayList<>();
                taken_piece.add(board[x - 1][y + d].getPiece());
                m.pieceTaken(taken_piece);
                move_list.add(m);
                move_list.addAll(findValidMoves(x - 2, y + (2 * d), m));
            }

        }
        // return valid move list
        return move_list;
    }

    private boolean alreadyTaken(Piece p, Move m){
        for (Piece taken : m.getPiecesTaken()){
            if(p == taken){
                return true;
            }
        }
        return false;
    }

    public ArrayList<Move> getValidMoves() {
        this.valid_moves.clear();
        this.valid_moves = findValidMoves(p.getBoardX(), p.getBoardY(), null);
        return valid_moves;
    }

//    public void printValidMoves(){
//        for (Move valid_move : valid_moves) {
//            System.out.println("Valid move found at: " + valid_move.getX() + " " + valid_move.getY() + " for Piece at " + p.getBoardY() + p.getBoardY());
//        }
//    }

    private boolean withinBoundary(int x, int y){
        return x >= 0 && x <= 7 && y >= 0 && y <= 7;
    }

    private boolean captureCheck(int x, int y, int side_dir, int piece_dir){
        // horizontal check
        if (!withinBoundary(x+(2*side_dir), y+(2*piece_dir))){
            return false;
        }
        // capture check
        return board[x+side_dir][y+piece_dir].has_piece() &&
                board[x+side_dir][y+piece_dir].getPiece().getPlayer() != p.getPlayer() &&
                !(board[x+(2*side_dir)][y+(2*piece_dir)].has_piece());
    }

    // iterate through valid moves list, if passed in move is equal (x1=x2, y1=y2) return true, otherwise false
    public Move getMove(Move m){
        for (Move valid_move : valid_moves) {
            if (valid_move.equals(m)) {
                return valid_move;
            }
        }
        return null;
    }


}
