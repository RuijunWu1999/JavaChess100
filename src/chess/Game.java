/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chess;

import chess.Board.*;
import chess.Side.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TimerTask;

/**
 *  A game of chess consists of a board, two sides and a list of moves.
 *
 *  The Game class represents a game of chess.
 *  It contains (references to) a board, two sides (white and black), a move-list
 *  and some info about the status of the game.
 *  @author Adeel
 */
public class Game
{
    final public static int STD_LAYOUT = 1;
    final public static int XL_LAYOUT = 10;
    final public static int XL_PLUS_CANNON = 20;
    public static final ImageIcon BATTLING_ICON = new ImageIcon("img/Battling.gif");
    // CONSTANTS

    public static Scanner in = new Scanner(System.in);
    Board board;
    Side White;
    Side Black;
    MoveList moveList;
    GameStatus gameStatus;
    int LayoutType ;

    public Game(int initLayoutType)
    {
        LayoutType = initLayoutType;
        // 设置布局类型
        initializeGame();
        // 初始化各种数据
        board.display();
        // 棋盘显示
    }
    private void initializeGame()
    {
        board = new Board(this);
        // 只生成大棋盘 8 X 8 或 10 X 10
        switch (LayoutType){
            case Game.STD_LAYOUT: {
                White = new Side(true, this);
                // 生成并配置白方棋子
                Black = new Side(false, this);
                // 生成并配置黑方棋子
                break;
            }
            case Game.XL_LAYOUT : {
                White = new Side_XL(true, this);
                // 生成并配置白方棋子
                Black = new Side_XL(false, this);
                // 生成并配置黑方棋子
                break;
            }
            case Game.XL_PLUS_CANNON : {
                White = new Side_XL_Cannons(true, this);
                // 生成并配置白方棋子
                Black = new Side_XL_Cannons(false, this);
                // 生成并配置黑方棋子
                break;
            }
            default : {
                White = new Side(true, this);
                // 生成并配置白方棋子
                Black = new Side(false, this);
                // 生成并配置黑方棋子
            }
        }
        White.opponent = Black;
        Black.opponent = White;
        moveList = new MoveList();
        gameStatus = new GameStatus(this);
        // 生成新的状态记录
        gameStatus.readyForMove = true;
    }
    
    public void processMove(Move m)
    {
        if (gameActive())
        {
            if(gameStatus.readyForMove)
            {
                if (gameStatus.sideToMove.isLegal(m))
                {
                    gameStatus.readyForMove = false;
                    makeMove(m);
                    updateGameStatus();
                }
            }
        }
        board.discardMove();
    }
    
//    private void play()
//    {
//        do
//        {
//            makeMove(getNextMove());
//            updateGameStatus();
//        }
//        while(gameActive());
//    }
    
//    private Move getNextMove()
//    {
//        String s;
//        Move move;
//        do
//        {
//            System.out.println("next move:");
//            s = in.next();
//            move = new Move(board.get(s.charAt(1) - '1', s.charAt(0) - 'a'), board.get(s.charAt(3) - '1', s.charAt(2)- 'a'));
////            board.sq1 = null;
////            board.sq2 = null;
////            move = board.getMove();
//        }
//        while(!gameStatus.sideToMove.isLegal(move));
//        
//        return move;
//    }

    private void makeMove(Move move)
    {
        System.out.println("moving...".concat(move.toString()));

        if (move.targetSquare.piece != null && move.targetSquare.piece.getClass().equals(King.class))
        {
            // KING is captured !!!
            Side winningSide = move.targetSquare.piece.getSide().opponent;
            winAndStop(winningSide);
            return;
            // Try return to stop app running.
            // Looks it worked.
        }
        // Repaint Background
        move.targetSquare.setIconDefaultBackground();
        move.sourceSquare.setIconDefaultBackground();
        move.sourceSquare.icon.setIcon(null);
        // Try to clear Source Square's Piece ICON

        gameStatus.sideToMove.makeMove(move);
        moveList.addMove(move);
    }

    private void winAndStop(Side winningSide) {
        String winningMsg = (winningSide.isWhite ? "WHITE" : "BLACK ").concat(" WIN !");
        new JOptionPane().showMessageDialog(null, winningMsg, "", JOptionPane.WARNING_MESSAGE);
        gameStatus.readyForMove = false;
        // HOW TO skip NExt chained returns!!!???
        board.removeAll();
        // KEEP the board display but no more movement.
        throw new RuntimeException(winningMsg);
        // STOPPED ALL !!!
    }

    private boolean gameActive()
    {
        return gameStatus.gameActive;
    }

    private void updateGameStatus()
    {
        gameStatus.update();
    }

    public void showActiveSide() {
        gameStatus.showActiveSideINTurn(true);
    }

    private class GameStatus
    {
        Game game;
        boolean gameActive;
        boolean readyForMove;
        Side sideToMove;
        boolean enPassantActive;
        int enPassantFile;
        List<Piece> capturedPieces;

        public GameStatus(Game g)
        {
            game = g;
            sideToMove = White;
            gameActive = true;
            enPassantActive = false;

            capturedPieces = new ArrayList<>();
            // Not iniatialized will throw null pointer for capturation with the very 1st move
        }
        
        private void update()
        {
            // Reset Active Side's King's Background
            //sideToMove.king.square.setIconDefaultBackground();
            showActiveSideINTurn(false);

            sideToMove = sideToMove.opponent;
            removeCapturedPieces(capturedPieces);
            updateCheckStatus();
            
            if (!gameActive)
            {
                System.out.println("Checkmate! " + ((sideToMove.opponent.isWhite) ? "White wins!":"Black wins!"));
                winAndStop(gameStatus.sideToMove.opponent);
                return;
            }
            if (sideToMove.inCheck && (board.getComponentCount() > 0) ){
                // (board.getComponentCount() > 0) is used to avoid "CHECK!" appeared again when WIN of on side.
                System.out.println("Check!");

                new JOptionPane().showMessageDialog(null, "Check!", "Check!", JOptionPane.WARNING_MESSAGE);
            }

            updateEnPassantStatus();
            capturedPieces = new ArrayList();
            readyForMove = true;

            // Try Hint Which Side to Move
            //sideToMove.king.square.icon.setBackground(Color.PINK);
            showActiveSideINTurn(true);
        }

        private void updateCheckStatus()
        {
            if (sideToMove.king.inCheck())
                gameActive = !gameStatus.sideToMove.king.isMated();
        }

        private boolean getEnPassantStatus()
        {
            Move lastMove = moveList.lastMove();
            return lastMove.targetSquare.piece.getClass().equals(Pawn.class)
               && (Math.abs(lastMove.targetSquare.rank - lastMove.sourceSquare.rank) == 2);
        }

        private void updateEnPassantStatus()
        {
            enPassantActive = getEnPassantStatus();
            enPassantFile = ( (enPassantActive) ? moveList.lastMove().targetSquare.file : -1 );
        }

        private void removeCapturedPieces(List<Piece> pieceList)
        {
            if (pieceList != null)
            {
                for (Piece p : pieceList)
                {
                    showBattle(p.square);
                    // Show a progress .
                    p.square = null;
                    getPieceList(p).remove(p);
                }
                capturedPieces.clear();
            }
        }

        private void showBattle(Square sqBattling) {
            // Try Show a battle progress.
            final ChessLabel tt = sqBattling.icon;
            Icon bb = tt.getIcon();
            if (bb == BATTLING_ICON) {
                return;
            }
            tt.setIcon(BATTLING_ICON);
            tt.setContentAreaFilled(true);
            tt.repaint();
            java.util.Timer delayer = new java.util.Timer();
            delayer.schedule(new TimerTask() {
                @Override
                public void run() {
                    tt.setIcon(bb);
                }
            },1500);
            // After 1500ms change icon back to default.
        }

        private List<? extends Piece> getPieceList(Piece p)
        {
            // already加入炮的LIST
            if (p.getClass().equals(Pawn.class))
                return p.getSide().pawns;
            if (p.getClass().equals(Bishop.class))
                return p.getSide().bishops;
            if (p.getClass().equals(Knight.class))
                return p.getSide().knights;
            if (p.getClass().equals(Rook.class))
                return p.getSide().rooks;
            if (p.getClass().equals(Cannon.class))
                return p.getSide().cannons;
            else
                return p.getSide().queens;
        }

        public void showActiveSideINTurn(boolean blShow) {
            if (blShow) {
                sideToMove.king.square.icon.setBackground(Color.PINK);
                //
                // Hint the Side in Turn to MOVE.
            }
            else {
                sideToMove.king.square.setIconDefaultBackground();
            }
        }
    }

    /**
     *  A class representing the list of moves in a game.
     */
    private class MoveList
    {
        // list of moves
        private List<Move> moveList;

        // constructor
        public MoveList()
        {
            moveList = new ArrayList();
        }

        /**
         * Appends the move to the move-list
         * @param move
         */
        public void addMove(Move move)
        {
            moveList.add(move);
        }

        /**
         * Returns the last move in the move-list
         * @return
         */
        public Move lastMove()
        {
            return moveList.get(moveList.size() - 1);
        }
    }

    /**
     * Tells whether the move is a valid En Passant or not.
     * See: https://en.wikipedia.org/wiki/En_passant#The_rule
     *
     * @param move
     * @return true if the move is a valid En Passant. False otherwise.
     */
    public boolean isLegalEnPassant(Move move)
    {
        /*
        boolean bl_Orig_Result = gameStatus.enPassantActive
                && move.sourceSquare.rank == ((gameStatus.sideToMove == White) ? 4 : 3)
                // 需要调整以适合XL Layout,initRow + 2的位置
                && gameStatus.enPassantFile == move.targetSquare.file;
         */

        int pawn1stMoveRank = 0;
        // for XL layout.
        Piece p = move.sourceSquare.piece;
        if (p.getClass().equals(Pawn.class)) {
            //p = (Pawn) move.sourceSquare.piece;
            //tmp = (Pawn)p;
            pawn1stMoveRank = ((Pawn) p).initRow;
            pawn1stMoveRank = (gameStatus.sideToMove.isWhite) ? (pawn1stMoveRank+1 +2) : (pawn1stMoveRank-1 -2);
        }
        boolean bl_New_Result = gameStatus.enPassantActive
                && move.sourceSquare.rank == pawn1stMoveRank
                && gameStatus.enPassantFile == move.targetSquare.file;

        return bl_New_Result;
        /*
        吃过路兵：横吃对方的前进了2步的兵，并前进一步；
        What pieces can en passant?
        Only pawns can en passant.

        Official Rules/Conditions for an En Passant
            The pawn that captures must be on its fifth rank.
                The pawn that will be captured must be adjacent to the capturer.
            The pawn that will be captured must have just moved two squares in a single move.
                The capture can only be made on the move immediately after the enemy pawn makes the double-step move; otherwise, the right to capture it en passant is lost.
            How many times can you en passant?
                The en passant move can only be made on the move immediately after the enemy pawn makes the double-step move; otherwise, the right to capture it en passant is lost.
         */
    }

    public void markAsCaptured(Piece p)
    {
        gameStatus.capturedPieces.add(p);

    }
}
