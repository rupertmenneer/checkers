package Checkers_game;

import java.util.ArrayList;
import java.util.Random;

public class OpponentAI {

    private Tile[][] board;
    private int depth;
    private Move best_move;
    private int static_evals;

    public OpponentAI(Tile[][] board, int depth) {
        this.board = board;
        this.depth = depth;
        ArrayList<Move> moves = getPlayersAvailableMoves(Piece_player.AI);
        int best_score = Integer.MIN_VALUE;
//        for (int i = 0; i < moves.size(); i++) {
//            makeMove(moves.get(i));
//            int min_max = minimax(depth, Piece_player.AI);
//            if (min_max > best_score) {
//                best_score = min_max;
//                best_move = moves.get(i);
//            }
//            undoMove(moves.get(i));
//        }
        Random r = new Random();
        best_move = moves.get(r.nextInt(moves.size()));
    }

    public void printBoardState(){
        for(int y = 0; y < Checkers.grid_size; y++){
            for(int x = 0; x < Checkers.grid_size; x++) {
                if (board[x][y].has_piece()) {
                    System.out.print(" | " + board[x][y].getPiece().getPlayer());
                } else{
                    System.out.print(" | empty");
                }
            }
            System.out.println(" ");
        }
    }

    public Move getBestMove(){
        return best_move;
    }

    private int getScore(Tile[][] b){
        return getNumberOfPlayerPieces(Piece_player.AI) - getNumberOfPlayerPieces(Piece_player.Human);
    }

    private int minimax(int depth, Piece_player player){
        if (depth == 0 | gameOver(player)){
            static_evals++;
            return getScore(board);
        } else {
            if (player == Piece_player.AI){
                // set initial val
                int max_eval = Integer.MIN_VALUE;
                // get all available moves
                ArrayList<Move> moves = getPlayersAvailableMoves(Piece_player.AI);
                for (int i = 0; i < moves.size(); i++) {
                    makeMove(moves.get(i));
                    int eval = minimax(depth - 1, Piece_player.Human);
                    undoMove(moves.get(i));
                    max_eval = Math.max(eval, max_eval);
                }
                return max_eval;
            }
            else {
                // set initial val
                int min_eval = Integer.MAX_VALUE;
                // get all available moves
                ArrayList<Move> moves = getPlayersAvailableMoves(Piece_player.Human);
                for (int i = 0; i < moves.size(); i++) {
                    makeMove(moves.get(i));
                    int eval = minimax(depth - 1, Piece_player.AI);
                    undoMove(moves.get(i));
                    min_eval = Math.min(eval, min_eval);
                }
                return min_eval;
            }
        }
    }



    private boolean gameOver(Piece_player player) {
        ArrayList<Move> moves;
        if (player == Piece_player.AI){
            moves = getPlayersAvailableMoves(Piece_player.AI);
        } else {
            moves = getPlayersAvailableMoves(Piece_player.Human);
        }
        return moves.size() == 0;
    }

    private int getNumberOfPlayerPieces(Piece_player player) {
        int pieces = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j].has_piece() && board[i][j].getPiece().getPlayer() == player) {
                    pieces++;
                }
            }
        }
        return pieces;
    }

    private ArrayList<Move> getPlayersAvailableMoves(Piece_player player) {
        ArrayList<Piece> pieces = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j].has_piece() && board[i][j].getPiece().getPlayer() == player) {
                    pieces.add(board[i][j].getPiece());
                }
            }
        }
        ArrayList<Move> all_moves = new ArrayList<>();
        for(int i = 0; i < pieces.size(); i++){
            all_moves.addAll(pieces.get(i).getMoveManager().getValidMoves());
        }
        return all_moves;
    }

    private void makeMove(Move m) {
        // remove taken pieces from board
        ArrayList<Piece> taken_pieces = m.getPiecesTaken();
        Piece p = m.getPiece();
        for (Piece taken_piece : taken_pieces) {
            board[taken_piece.getBoardX()][taken_piece.getBoardY()].setPiece(null);
        }
        // set old board ref to null
        board[m.getOldX()][m.getOldY()].setPiece(null);
        // move piece to new position on board
        board[m.getX()][m.getY()].setPiece(p);
        // update pieces x and y co-ords
        p.setBoardX(m.getX());
        p.setBoardY(m.getY());
    }

    private void undoMove(Move m) {
        // remove taken pieces from board
        ArrayList<Piece> taken_pieces = m.getPiecesTaken();
        Piece p = m.getPiece();
        for (Piece taken_piece : taken_pieces) {
            board[taken_piece.getBoardX()][taken_piece.getBoardY()].setPiece(taken_piece);
        }
        // set old board ref to null
        board[m.getOldX()][m.getOldY()].setPiece(p);
        // move piece to new position on board
        board[m.getX()][m.getY()].setPiece(null);
        // update pieces x and y co-ords
        p.setBoardX(m.getOldX());
        p.setBoardY(m.getOldY());
    }

}