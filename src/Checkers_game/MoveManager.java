package Checkers_game;

import java.util.ArrayList;
import java.util.Collections;

public class MoveManager {

    private Checkers game;
    private Piece p;
    private ArrayList<Move> valid_moves;

    public MoveManager(Checkers game, Piece p){
        this.game = game;
        this.p = p;
        this.valid_moves = findValidMoves(p.getBoardX(), p.getBoardY(), null);
    }

    private ArrayList<Move> findValidMoves(int x, int y, Piece taken){
        ArrayList<Move> move_list = new ArrayList<>();
        // assign direction
        int d = p.getPlayer().direction;
        // skip non capturing moves if piece has already captured
        if(taken == null) {
            // check non capture left (+ move within board)
            if (withinBoundary(x-1, y+d) && !game.getBoard()[x - 1][y + d].has_piece()) {
                move_list.add(new Move(x - 1, y + d));
            }
            // check non capture right (+ move within board)
            if (withinBoundary(x+1, y+d) && !game.getBoard()[x + 1][y + d].has_piece()) {
                move_list.add(new Move(x + 1, y + d));
            }
        }
        // check if there is a valid capture move left (enemy diagonal AND free space in line afterwards)
        if(captureCheck(x, y, 1, d)){
            // if capture is valid, add move and check if any more valid captures from new position
            Move m = new Move(x + 2, y + (2*d));
            // add captured piece to move's pieces taken
            Piece taken_piece = game.getBoard()[x+1][y+d].getPiece();
            if(taken != null){
                m.pieceTaken(taken);
            }
            m.pieceTaken(taken_piece);
            move_list.add(m);
            move_list.addAll(findValidMoves(x + 2, y + (2*d) , taken_piece));
        }
        // check if there is a valid capture move right (enemy diagonal AND free space in line afterwards)
        if(captureCheck(x, y, -1, d)){
            // if capture is valid, add move and check if any more valid captures from new position
            Move m = new Move(x - 2, y + (2*d));
            // add captured piece to move's pieces taken
            Piece taken_piece = game.getBoard()[x-1][y+d].getPiece();
            if(taken != null){
                m.pieceTaken(taken);
            }
            m.pieceTaken(taken_piece);
            move_list.add(m);
            move_list.addAll(findValidMoves(x - 2, y + (2*d) , taken_piece));
        }
        // return valid move list
        return move_list;
    }

    public ArrayList<Move> getValid_moves() {
        return valid_moves;
    }

    public void printValidMoves(){
        for (Move valid_move : valid_moves) {
            System.out.println("Valid move found at: " + valid_move.getX() + " " + valid_move.getY());
        }
    }

    private boolean withinBoundary(int x, int y){
        return x >= 0 && x <= 7 && y >= 0 && y <= 7;
    }

    private boolean captureCheck(int x, int y, int side_dir, int piece_dir){
        // horizontal check
        if (!withinBoundary(x+(2*side_dir), y+(2*piece_dir))){
            return false;
        }
        // capture check
        return game.getBoard()[x+side_dir][y+piece_dir].has_piece() &&
                game.getBoard()[x+side_dir][y+piece_dir].getPiece().getPlayer() != p.getPlayer() &&
                !(game.getBoard()[x+(2*side_dir)][y+(2*piece_dir)].has_piece());
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
