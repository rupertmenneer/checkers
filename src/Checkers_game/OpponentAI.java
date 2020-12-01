package Checkers_game;

import java.util.ArrayList;
import java.util.Random;

public class OpponentAI {

    private Tile[][] board;
    private int depth;
    private Move best_move;
    private int static_evals;
    private int AI_pieces;
    private int AI_pieces_king;
    private int human_pieces;
    private int human_pieces_king;

    public OpponentAI(Tile[][] board, int depth) {
        this.board = board;
        this.depth = depth;
        initaliseBoardVariables();
        this.best_move = bestMove();
    }

    private void initaliseBoardVariables(){
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if(this.board[i][j].has_piece()){
                    if (this.board[i][j].getPiece().getPlayer() == Piece_player.AI) {
                        AI_pieces++;
                    }
                    else if (this.board[i][j].getPiece().getPlayer() == Piece_player.AI && this.board[i][j].getPiece().isKing()) {
                        AI_pieces_king++;
                    }
                    else if (this.board[i][j].getPiece().getPlayer() == Piece_player.Human) {
                        human_pieces++;
                    }
                    else if (this.board[i][j].getPiece().getPlayer() == Piece_player.Human && this.board[i][j].getPiece().isKing()) {
                        human_pieces_king++;
                    }
                }

            }
        }
    }

    private Move bestMove(){
        ArrayList<Move> moves = getPlayersAvailableMoves(Piece_player.AI);
        // initalise move
        Move m = moves.get(0);
        int best_score = Integer.MIN_VALUE;
        for (int i = 0; i < moves.size(); i++) {
            makeMove(moves.get(i));
            int min_max = minimax(depth - 1, Piece_player.Human, Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (min_max > best_score) {
                best_score = min_max;
                m = moves.get(i);
            }
            undoMove(moves.get(i));
        }
        System.out.println(static_evals);
        return m;
    }

    public Move getBestMove(){

        return best_move;
    }

    private int getScore(Tile[][] b){
        // 100 points for taking piece
        int taking_pieces_score = (human_pieces - getNumberOfPlayerPieces(Piece_player.Human, false))*100;
        // -110 points for losing piece
        int losing_pieces_score = -(AI_pieces - getNumberOfPlayerPieces(Piece_player.AI, false))*100;
        // 200 points for taking a king piece / -200 for conceding
        int taking_kings_score = (human_pieces_king - getNumberOfPlayerPieces(Piece_player.Human, true))*200;
        // -210 points for losing a king piece / 210 for gaining
        int losing_kings_score = -(AI_pieces_king - getNumberOfPlayerPieces(Piece_player.AI, true))*200;
        return (taking_pieces_score + losing_pieces_score + taking_kings_score + losing_kings_score);
    }

    private int minimax(int depth, Piece_player player, int alpha, int beta){
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
                    int eval = minimax(depth - 1, Piece_player.Human, alpha, beta);
                    undoMove(moves.get(i));
                    max_eval = Math.max(eval, max_eval);
                    alpha = Math.max( alpha, max_eval);
                    if (beta <= alpha) {
                        break;
                    }
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
                    int eval = minimax(depth - 1, Piece_player.AI, alpha, beta);
                    undoMove(moves.get(i));
                    min_eval = Math.min(eval, min_eval);
                    beta = Math.min( alpha, min_eval);
                    if (beta <= alpha) {
                        break;
                    }
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

    private int getNumberOfPlayerPieces(Piece_player player, boolean king) {
        int pieces = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j].has_piece() && board[i][j].getPiece().getPlayer() == player && board[i][j].getPiece().isKing() == king) {
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
        board[m.getX()][m.getY()].setPiece(null);
        // move piece to new position on board
        board[m.getOldX()][m.getOldY()].setPiece(p);
        // update pieces x and y co-ords
        p.setBoardX(m.getOldX());
        p.setBoardY(m.getOldY());

    }

}