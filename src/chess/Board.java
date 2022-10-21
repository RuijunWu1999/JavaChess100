/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chess;

import java.util.*;
import chess.Side.*;
import chess.Square.*;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import javax.swing.*;

/**
 *
 * @author Adeel
 */
public class Board extends JFrame
{
    int totalRows;
    // 设定棋盘的大小，统一；

    private static Square[][] board;
    Game game;
    public Square sq1 = null, sq2 = null;
//    boolean f = false;
//    public Move getMove()
//    {
//        do {} while ((sq1 == null || sq2 == null));
//        System.out.println("42");
//        sq1.icon.setBackground(Color.red);
//        sq2.icon.setBackground(Color.red);
//        return new Move(sq1, sq2);
//    }
    
//    public void getClick(MouseEvent e)
//    {
//        if (sq1 == null)
//            sq1 = e.getComponent;
//        else if (board.sq2 == null)
//            sq2 = square;
//    }

    
    public Board(Game g)
    {
        game = g;
        switch (game.LayoutType){
            case Game.STD_LAYOUT: totalRows = 8; break;
            case Game.XL_LAYOUT: totalRows = 10; break;
            case Game.XL_PLUS_CHINESE_TREBUCHET: totalRows = 10; break;
            default: totalRows = 8;
        }

        /*
        if (game.LayoutType == Game.STD_LAYOUT)        {            totalRows = 8;        }
        else if (game.LayoutType == Game.XL_LAYOUT)        {            totalRows = 10;        }
        else if (game.LayoutType == Game.XL_PLUS_CHINESE_TREBUCHET)        {            totalRows = 10;        }
        else        {            totalRows = 8;                    }
                    // FallBack Settings.
         */
        board = new Square[totalRows][totalRows];
        //board = new Square[8][8];
        initializeBoard();
    }

    private void initializeBoard()
    {
        // 初始化 totalRows X totalRows 个格子
        for (int rank = 0; rank < totalRows; rank++)
        {
            for (int file = 0; file < totalRows; file++)
            {
                board[totalRows-1 - rank][file] = new Square(this, rank, file);
            }
        }

        /*
        for (int rank = 0; rank < 8; rank++)
        {
            for (int file = 0; file < 8; file++)
            {
                board[7 - rank][file] = new Square(this, rank, file);
            }
        }
        Original STD Layout
         */
    }

    public Square get(int rank, int file)
    {
        return board[totalRows-1 - rank][file];
        //return board[7 - rank][file];
        // 调取格子
    }

//    public void displayBoard()
//    {
//        for (int rank = 0; rank < 8; rank++)
//        {
//            for (int file = 0; file < 8; file++)
//            {
//                get(rank, file).icon.set(rank, file);
//            }
//        }
//    }

    public void display()
    {
        setTitle("Chess");

        //setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // Original
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Change to EXIT for 2 JFrames operation.

        Container contentPane = getContentPane();
        GridLayout gridLayout = new GridLayout(totalRows, totalRows);
        //GridLayout gridLayout = new GridLayout(8, 8);

        contentPane.setLayout(gridLayout);

        for (int rank = totalRows-1; rank >= 0; rank--)
        {
            for (int file = 0; file < totalRows; file++)
            {
                ChessLabel sq_ChessLabel = get(rank, file).icon;
                sq_ChessLabel.set(rank, file);
                // 设置浅深背景色

                contentPane.add(sq_ChessLabel);
                // 添加的是ChessLabel,顺序添加 ；
                // 可以改Square的ICON为image icon类型。
            }
        }

        switch (game.LayoutType){
            case Game.STD_LAYOUT: setSize(700, 700); break;
            case Game.XL_LAYOUT: setSize(820, 820); break;
            case Game.XL_PLUS_CHINESE_TREBUCHET: setSize(820, 820); break;
            default: setSize(700, 700);
        }

        /*
        if (game.LayoutType == Game.STD_LAYOUT){
            setSize(700, 700);
            // 700 ==> 8 X 8 ；
        }
        else if (game.LayoutType == Game.XL_LAYOUT || game.LayoutType == Game.XL_PLUS_CHINESE_TREBUCHET) {
            setSize(820, 820);
            // 900 ==> 10 X 10 ；
        }
        else {
            setSize(700, 700);
            // 700 ==> 8 X 8 ；
        }
         */

        game.showActiveSide();
        // Hint Which Side to MOVE

        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    public void processMove(Move m)
    {
        game.processMove(m);
    }
    
    public void discardMove()
    {
        
        sq1.icon.setBorderPainted(false);
        sq2.icon.setBorderPainted(false);
        // Repaint background
        sq1.setIconDefaultBackground();
        sq2.setIconDefaultBackground();
        sq1 = null;
        sq2 = null;
    }

    /**
     *
     *
     */
    public static class Move
    {
        Square sourceSquare;
        Square targetSquare;

        public Move(Square source, Square target)
        {
            sourceSquare = source;
            targetSquare = target;
        }
    }

    public List<Piece> getCheckingPiecesList(Square square, Side attackingSide)
    {
        // Should update to include rule of Chinese Trebuchet.
        List<Piece> list;
        list = piecesAttackingOnFile(square, attackingSide);
        list.addAll(piecesAttackingOnRank(square, attackingSide));
        list.addAll(piecesAttackingOnDiagonals(square, attackingSide));
        list.addAll(knightsAttacking(square, attackingSide));
        list.addAll(pawnsAttacking(square, attackingSide));
        list.addAll(chinesetrebuchetsAttacking(square, attackingSide));
        return list;
    }

    private List<Piece> chinesetrebuchetsAttacking(Square target_square, Side attackingSide) {
        List<Piece> list = new ArrayList();
        for (int i = 0, fileDiff, rankDiff; i < attackingSide.chinesetrebuchets.size(); i++)
        {
            Square attacking_square = attackingSide.chinesetrebuchets.get(i).square;
            fileDiff = Math.abs(attacking_square.file - target_square.file);
            rankDiff = Math.abs(attacking_square.rank - target_square.rank);

            if ((rankDiff > 0 && fileDiff == 0) || (fileDiff > 0 && rankDiff == 0))
            // In same rank OR in same file, not both
            {
                Move virtualMove = new Move(attacking_square, target_square);
                if ( ((ChineseTrebuchet) attacking_square.piece).getPiecesEnRoute(virtualMove) == 1 ) {
                    list.add(attackingSide.chinesetrebuchets.get(i));
                };
            }
        }
        return list;
    }

    private List<Piece> piecesAttackingOnRank(Square square, Side attackingSide)
    {
        List<Piece> list;
        list = attackingPieceChecker(square, attackingSide, Rook.class, Queen.class, 0, -1); // look left
        list.addAll(attackingPieceChecker(square, attackingSide, Rook.class, Queen.class, 0, 1)); // look right
        
        return list;
    }

    private List<Piece> piecesAttackingOnFile(Square square, Side attackingSide)
    {
        List<Piece> list;
        list = (attackingPieceChecker(square, attackingSide, Rook.class, Queen.class, 1, 0));     // look up
        list.addAll(attackingPieceChecker(square, attackingSide, Rook.class, Queen.class, -1, 0));    // look below
        
        return list;
    }

    private List<Piece> piecesAttackingOnDiagonals(Square square, Side attackingSide)
    {
        List<Piece> list;
        list = attackingPieceChecker(square, attackingSide, Bishop.class, Queen.class, 1, -1);
        list.addAll(attackingPieceChecker(square, attackingSide, Bishop.class, Queen.class, 1, 1));
        list.addAll(attackingPieceChecker(square, attackingSide, Bishop.class, Queen.class, -1, -1));
        list.addAll(attackingPieceChecker(square, attackingSide, Bishop.class, Queen.class, -1, 1));
        return list;
    }

    private List<Piece> knightsAttacking(Square square, Side attackingSide)
    {
        List<Piece> list = new ArrayList();
        for (int i = 0, fileDiff, rankDiff; i < attackingSide.knights.size(); i++)
        {
            fileDiff = Math.abs(attackingSide.knights.get(i).square.file - square.file);
            rankDiff = Math.abs(attackingSide.knights.get(i).square.rank - square.rank);

            if ((rankDiff == 2 && fileDiff == 1) || (fileDiff == 2 && rankDiff == 1))
            {
                list.add(attackingSide.knights.get(i));
            }
        }
        return list;
    }

    private List<Piece> pawnsAttacking(Square square, Side attackingSide)
    {
        List<Piece> list = new ArrayList();

        Piece pieceLeft = get(square.rank + ((!attackingSide.isWhite) ? 1 : -1), (square.file - 1)).piece;
        Piece pieceRight = get(square.rank + ((!attackingSide.isWhite) ? 1 : -1), (square.file + 1)).piece;

        if (pieceLeft != null && pieceLeft.getSide().equals(attackingSide) && pieceLeft.getClass().equals(Pawn.class))
        {
            list.add(pieceLeft);
        }
        if (pieceRight != null && pieceRight.getSide().equals(attackingSide) && pieceRight.getClass().equals(Pawn.class))
        {
            list.add(pieceRight);
        }
        return list;
    }

    private List<Piece> attackingPieceChecker(Square square, Side attackingSide, Class pieceType1, Class pieceType2, int rankIncrement, int fileIncrement)
    {
        List<Piece> list = new ArrayList();
        Piece piece;
        for (int rank = square.rank + rankIncrement, file = square.file + fileIncrement; withinRange(rank, rankIncrement) && withinRange(file, fileIncrement); rank += rankIncrement, file += fileIncrement)
        {
            piece = get(rank, file).piece;
            if (piece != null)
            {
                if (piece.getSide().equals(attackingSide))
                {
                    if (piece.getClass().equals(pieceType1) || piece.getClass().equals(pieceType2))
                    {
                        list.add(piece);
                    }
                }
                break;
            }
        }
        return list;
    }

    private boolean withinRange(int axisVal, int axisIncrement)
    {
        return (((totalRows-1) * ((1 + axisIncrement) / 2) + (-1 * axisIncrement) * axisVal) >= 0);
        // ？？？
        //return ((7 * ((1 + axisIncrement) / 2) + (-1 * axisIncrement) * axisVal) >= 0);
    }

    /**
     *
     * @param square
     * @param attackingSide
     * @param pinMatters
     * @return true if the square is under attack from the specified
     * attackingSide
     */
    public boolean isUnderAttack(Square square, Side attackingSide, boolean pinMatters)
    {
        return isUnderAttackFromNonKingPieces(square, attackingSide, pinMatters)
            || isUnderAttackFromKing(square, attackingSide);
    }
    
    public boolean isUnderAttackFromNonKingPieces(Square square, Side attackingSide, boolean pinMatters)
    // NEED add Chinese Trebuchet RULE!!!
    {
        return isUnderAttackOnFile(square, attackingSide, pinMatters)
            || isUnderAttackOnRank(square, attackingSide, pinMatters)
            || isUnderAttackOnDiagonals(square, attackingSide, pinMatters)
            || isUnderAttackFromKnights(square, attackingSide, pinMatters)
            || ((pinMatters) ? isReachableByPawns(square, attackingSide, pinMatters) : isUnderAttackFromPawns(square, attackingSide, pinMatters));
    }

    private boolean isUnderAttackOnRank(Square square, Side attackingSide, boolean pinMatters)
    {
        return attackChecker(square, attackingSide, Rook.class, Queen.class, 0, -1, pinMatters) // look left
            || attackChecker(square, attackingSide, Rook.class, Queen.class, 0, 1, pinMatters); // look right
    }

    private boolean isUnderAttackOnFile(Square square, Side attackingSide, boolean pinMatters)
    {
        return attackChecker(square, attackingSide, Rook.class, Queen.class, 1, 0, pinMatters)      // look up
            || attackChecker(square, attackingSide, Rook.class, Queen.class, -1, 0, pinMatters);    // look below
    }

    private boolean isUnderAttackOnDiagonals(Square square, Side attackingSide, boolean pinMatters)
    {
        return attackChecker(square, attackingSide, Bishop.class, Queen.class, 1, -1, pinMatters) //look above-left
            || attackChecker(square, attackingSide, Bishop.class, Queen.class, 1, 1, pinMatters) // look above-right
            || attackChecker(square, attackingSide, Bishop.class, Queen.class, -1, -1, pinMatters) // look below-left
            || attackChecker(square, attackingSide, Bishop.class, Queen.class, -1, 1, pinMatters);    // look below-right
    }

    private boolean isUnderAttackFromKnights(Square square, Side attackingSide, boolean pinMatters)
    {
        for (int i = 0, fileDiff, rankDiff; i < attackingSide.knights.size(); i++)
        {
            fileDiff = Math.abs(attackingSide.knights.get(i).square.file - square.file);
            rankDiff = Math.abs(attackingSide.knights.get(i).square.rank - square.rank);

            if ((rankDiff == 2 && fileDiff == 1) || (fileDiff == 2 && rankDiff == 1))
            {
                return true;
            }
        }
        return false;
    }

    private boolean isUnderAttackFromPawns(Square square, Side attackingSide, boolean pinMatters)
    {
        boolean result = true;
        int r = square.rank + ((!attackingSide.isWhite) ? 1 : -1);
        int f1 = (square.file - 1);
        int f2 = (square.file + 1);
        
        if (!(r <= totalRows-1 && r >= 0))
            return false;
        if ((f1 <= totalRows-1 && f1 >= 0))
        {
            Square left = get(r, f1);
            Piece pieceLeft = left.piece;
            result = result && (pieceLeft != null 
                            && pieceLeft.getSide().equals(attackingSide) 
                            && pieceLeft.getClass().equals(Pawn.class)
                            && (!pinMatters || doesNotExposeKingToCheck(new Move(pieceLeft.square, square))));

        }
        if ((f2 <= totalRows-1 && f2 >= 0))
        {
            Square right = get(r, f2);
            Piece pieceRight = right.piece;
            result = result || ((pieceRight != null 
                            && pieceRight.getSide().equals(attackingSide) 
                            && pieceRight.getClass().equals(Pawn.class)
                            && (!pinMatters || doesNotExposeKingToCheck(new Move(pieceRight.square, square)))));

        }
        return result;
    }
    private boolean isReachableByPawns(Square square, Side attackingSide, boolean pinMatters)
    {        
        for (Pawn p : attackingSide.pawns)
        {
            if (p.square.file == square.file)
                return p.isLegal(new Move(square, p.square));
        }
        return false;
    }

    private boolean attackChecker(Square square, Side attackingSide, Class pieceType1, Class pieceType2, int rankIncrement, int fileIncrement, boolean pinMatters)
    {
        Piece piece;
        for (int rank = square.rank + rankIncrement, file = square.file + fileIncrement; withinRange(rank, rankIncrement) && withinRange(file, fileIncrement); rank += rankIncrement, file += fileIncrement)
        {
            piece = get(rank, file).piece;
            if (piece != null)
            {
                if (piece.getSide().equals(attackingSide))
                {
                    if (piece.getClass().equals(pieceType1) || piece.getClass().equals(pieceType2))
                    {
                        return !pinMatters || doesNotExposeKingToCheck(new Move(piece.square, square));
                    }
                }
                break;
            }
        }
        return false;
    }

    private boolean isUnderAttackFromKing(Square square, Side attackingSide)
    {
        return (Math.abs(square.rank - attackingSide.king.square.rank) <= 1 && Math.abs(square.file - attackingSide.king.square.file) <= 1);
    }

    public boolean doesNotExposeKingToCheck(Move m)
    {
        Piece temp = m.sourceSquare.piece;
        Piece temp2 = m.targetSquare.piece;

        // make the move
        m.sourceSquare.piece = null;
        m.targetSquare.piece = temp;

        // check if it exposes king to check
        boolean result = !temp.getSide().king.square.isUnderAttackFrom(temp.getSide().opponent, false);

        // revert
        m.sourceSquare.piece = temp;
        m.targetSquare.piece = temp2;

        return result;
    }
}
