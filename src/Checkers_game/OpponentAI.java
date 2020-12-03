package Checkers_game;
import java.util.ArrayList;
import java.util.Random;

public class OpponentAI {

    private final Tile[][] board;
    private final int depth;
    private final Move best_move;
    // for testing
    private int static_evals;

    // player variables
    private final Piece_player player;
    private final Piece_player opposingPlayer;

    public OpponentAI(Tile[][] board, int depth, Piece_player player) {
        this.board = board;
        this.depth = depth;
        this.player = player;
        if(this.player == Piece_player.AI){
            this.opposingPlayer = Piece_player.Human;
        } else {
            this.opposingPlayer = Piece_player.AI;
        }

        this.best_move = bestMove();
    }

    // find all available moves - assign score to each and return move with best score
    private Move bestMove(){
        ArrayList<Move> moves = getPlayersAvailableMoves(player);
        // initalise move as random for easy difficulty
        Random r = new Random();
        int random = r.nextInt(moves.size());
        Move m = moves.get(random);
        // if difficulty is easy then just take first available move
        if (depth>Difficulty.Easy.difficulty) {
            int best_score = Integer.MIN_VALUE;
            for (Move move : moves) {
                makeMove(move);
                int min_max = minimax(depth - 1, opposingPlayer, Integer.MIN_VALUE, Integer.MAX_VALUE);
                if (min_max > best_score) {
                    best_score = min_max;
                    m = move;
                    System.out.println(best_score);
                }

                undoMove(move);
            }
        }
        return m;
    }

    public Move getBestMove(){
        return best_move;
    }

    // look for positions on the board where piecs can't be taken
    private int getDefensivePositions(){
        int defensive_positions = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if(board[i][j].has_piece()){
                    // if both spots behind it are occupied
                    if(withinBoundary(i-1,j-1) && withinBoundary(i+1,j-1) &&
                    board[i-1][j-1].has_piece() && board[i+1][j-1].has_piece() &&
                    board[i-1][j-1].getPiece().getPlayer()==player && board[i+1][j-1].getPiece().getPlayer()==player){
                        defensive_positions++;
                    }
                    // if both spots in front are occupied
                    if(withinBoundary(i-1,j+1) && withinBoundary(i+1,j+1) &&
                    board[i-1][j+1].has_piece() && board[i+1][j+1].has_piece() &&
                    board[i-1][j+1].getPiece().getPlayer()==player && board[i+1][j+1].getPiece().getPlayer()==player){
                        defensive_positions++;
                    }
                }
            }
        }
        return defensive_positions;
    }

    // get back row pieces for each player
    private int getBackRowPieces(){
        int backRow = 0;
        for (int y = 0; y < board.length; y++) {
            if(player == Piece_player.AI && board[0][y].has_piece() && board[0][y].getPiece().getPlayer() == player){
                    backRow++;
                }
            if(player == Piece_player.Human && board[7][y].has_piece() && board[7][y].getPiece().getPlayer() == player){
                backRow++;
                }
            }
        return backRow;
    }

    private boolean withinBoundary(int x, int y){
        return x >= 0 && x <= 7 && y >= 0 && y <= 7;
    }

    // get score from heuristics
    private int getScore(){
        int score = 0;
        // -200 points for losing piece
        score += (getNumberOfPlayerPieces(player, false) - getNumberOfPlayerPieces(opposingPlayer, false))*200;
        if(depth>Difficulty.Normal.difficulty) {
            // 300 points for gaining a king (-300 for losing)
            score += getNumberOfPlayerPieces(player, true) * 300;
            // -500 letting opponent king / +500 for taking their king
            score += getNumberOfPlayerPieces(opposingPlayer, true) * -500;
            // 25 for defensive spots
            score += getDefensivePositions() * 25;
            // 75 for keeping back row pieces (blocks kinging)
            score += getBackRowPieces() * 75;
        }
        return score;
    }

    private int minimax(int depth, Piece_player player_turn, int alpha, int beta){
        if (depth == 0 | gameOver(player_turn)){
            static_evals++;
            return getScore();
        } else {
            int max_eval;
            ArrayList<Move> moves;
            if (player_turn == player){
                // set initial val
                max_eval = Integer.MIN_VALUE;
                // get all available moves
                moves = getPlayersAvailableMoves(player);
                for (Move move : moves) {
                    makeMove(move);
                    int eval = minimax(depth - 1, opposingPlayer, alpha, beta);
                    undoMove(move);
                    max_eval = Math.max(eval, max_eval);
                    alpha = Math.max(alpha, eval);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
            else {
                // set initial val
                max_eval = Integer.MAX_VALUE;
                // get all available moves
                moves = getPlayersAvailableMoves(opposingPlayer);
                for (Move move : moves) {
                    makeMove(move);
                    int eval = minimax(depth - 1, player, alpha, beta);
                    undoMove(move);
                    max_eval = Math.min(eval, max_eval);
                    beta = Math.min(beta, eval);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
            return max_eval;
        }
    }


    // this method checks whether the game is over e.g. no available moves when it's the players turn
    // this is used in the minimax method
    private boolean gameOver(Piece_player p) {
        ArrayList<Move> moves;
        if (p == player){
            moves = getPlayersAvailableMoves(player);
        } else {
            moves = getPlayersAvailableMoves(opposingPlayer);
        }
        return moves.size() == 0;
    }

    // this method returns the number of standard or king pieces for a given player
    private int getNumberOfPlayerPieces(Piece_player player, boolean king) {
        int pieces = 0;
        for (Tile[] tiles : board) {
            for (int j = 0; j < board.length; j++) {
                if (tiles[j].has_piece() && tiles[j].getPiece().getPlayer() == player && tiles[j].getPiece().isKing() == king) {
                    pieces++;
                }
            }
        }
        return pieces;
    }

    // this method searches board for all of a players pieces on the board, it then
    // finds all valid moves for all of the players pieces
    private ArrayList<Move> getPlayersAvailableMoves(Piece_player player) {
        ArrayList<Piece> pieces = new ArrayList<>();
        for (Tile[] tiles : board) {
            for (int j = 0; j < board.length; j++) {
                if (tiles[j].has_piece() && tiles[j].getPiece().getPlayer() == player) {
                    pieces.add(tiles[j].getPiece());
                }
            }
        }
        ArrayList<Move> all_moves = new ArrayList<>();
        for (Piece piece : pieces) {
            all_moves.addAll(piece.getMoveManager().getValidMoves());
        }
        return all_moves;
    }

    // this method is used by the AI in its minimax algorithm, it makes the move on the board (but not the UI)
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
        // is now king?
        if(m.capturesKing()) {
            p.setKing(true);
        }
    }

    // this method is used by the AI in it's minimax method, it reverses the move given to it, this is used
    // to recursively restore the board to it's normal state as moves are explored in minimax.
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
        // set back piece's king status:
        p.setKing(m.isAlreadyKing());
    }

}