package Checkers_game;

import java.util.ArrayList;

public class OpponentAI {

    private Tile[][] board;
    private int depth;
    private Move best_move;

    public OpponentAI(Tile[][] board, int depth) {
        this.board = board;
        this.depth = depth;
        int best_move_index = minimax(board, depth, true, true);
        ArrayList<Piece> pieces = getPlayerPieces(board, Piece_player.AI);
        ArrayList<Move> moves = getAllAvailableMoves(pieces);
        System.out.print("constructor " + moves.size());
        best_move = moves.get(best_move_index);
    }

    public Move getBestMove(){
        return best_move;
    }

    private int getScore(Tile[][] b){
        return getNumberOfPlayerPieces(b, Piece_player.AI) - getNumberOfPlayerPieces(b, Piece_player.Human);
    }

    private int minimax(Tile[][] b, int depth, boolean max_player, boolean isRoot) {
        // set default move
        int move_index = 0;
        // return move
        if (depth == 0 | gameOver(b)) {
            return getScore(b);
        }

        else if (max_player) {
            int max_eval = Integer.MIN_VALUE;
            ArrayList<Piece> pieces = getPlayerPieces(b, Piece_player.AI);
            ArrayList<Move> moves = getAllAvailableMoves(pieces);
            for(int i = 0; i< moves.size(); i++){
                makeMove(moves.get(i), b);
                int eval = minimax(b, depth - 1, false, false);
               // undoMove
                undoMove(moves.get(i), b);
                // if root node and is better than current max - update move index
                if (isRoot){
                    if(eval > max_eval) {
                        System.out.print("minmax " + moves.size());
                        move_index = i;
                    }
                }
                else {
                    // return max
                    return Math.max(max_eval, eval);
                }
            }
        }
        else {
            int min_eval = Integer.MAX_VALUE;
            ArrayList<Piece> pieces = getPlayerPieces(b, Piece_player.Human);
            ArrayList<Move> moves = getAllAvailableMoves(pieces);
            for(int i = 0; i< moves.size(); i++){
                makeMove(moves.get(i), b);
                int eval = minimax(b, depth - 1, true, false);
                //undo move
                undoMove(moves.get(i), b);
                // return min
                return Math.min(min_eval, eval);
            }
        }
        System.out.println(move_index);

        return move_index;
    }

    private boolean gameOver(Tile[][] b) {
        int human_pieces = getNumberOfPlayerPieces(b, Piece_player.Human);
        int AI_pieces = getNumberOfPlayerPieces(b, Piece_player.AI);
        return human_pieces == 0 | AI_pieces == 0;
    }

    private int getNumberOfPlayerPieces(Tile[][] b, Piece_player player) {
        int pieces = 0;
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b.length; j++) {
                if (b[i][j].has_piece() && b[i][j].getPiece().getPlayer() == player) {
                    pieces++;
                }
            }
        }
        return pieces;
    }

    private ArrayList<Piece> getPlayerPieces(Tile[][] b, Piece_player player) {
        ArrayList<Piece> players_pieces = new ArrayList<>();
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b.length; j++) {
                if (b[i][j].has_piece() && b[i][j].getPiece().getPlayer() == player) {
                    players_pieces.add(b[i][j].getPiece());
                }
            }
        }
        return players_pieces;
    }

    private ArrayList<Move> getAllAvailableMoves(ArrayList<Piece> pieces){
        ArrayList<Move> all_moves = new ArrayList<>();
        for(int i = 0; i < pieces.size(); i++){
            pieces.get(i).clearAvailableMoves();
            pieces.get(i).setAvailableMoves(new MoveManager(board, pieces.get(i)));
            all_moves.addAll(pieces.get(i).getAvailableMoves().getValidMoves());
        }
        return all_moves;
    }

    private void makeMove(Move m, Tile[][] b) {
        // remove taken pieces from b
        ArrayList<Piece> taken_pieces = m.getPiecesTaken();
        Piece p = m.getPiece();
        for (Piece taken_piece : taken_pieces) {
            b[taken_piece.getBoardX()][taken_piece.getBoardY()].setPiece(null);
        }
        // set old b ref to null
        b[p.getBoardX()][p.getBoardY()].setPiece(null);
        // move piece to new position on b
        b[m.getX()][m.getY()].setPiece(p);
        // update pieces x and y co-ords
        p.setBoardX(m.getX());
        p.setBoardY(m.getY());
    }

    private void undoMove(Move m, Tile[][] b){
        Piece p = m.getPiece();
        // set new b ref to null
        b[m.getX()][m.getY()].setPiece(null);
        // move piece back to old position on b
        b[m.getOldX()][m.getOldY()].setPiece(p);
        // update pieces x and y co-ords
        p.setBoardX(m.getOldX());
        p.setBoardY(m.getOldY());
        // add taken pieces back to b
        ArrayList<Piece> taken_pieces = m.getPiecesTaken();
        for (Piece taken_piece : taken_pieces) {
            b[taken_piece.getBoardX()][taken_piece.getBoardY()].setPiece(taken_piece);
        }
    }

}