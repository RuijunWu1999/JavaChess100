/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chess;

import java.util.*;
import chess.Side.*;

import java.awt.Container;
import java.awt.GridLayout;
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

    public Board(Game g)
    {
        game = g;
        switch (game.LayoutType){
            case Game.STD_LAYOUT: totalRows = 8; break;
            case Game.XL_LAYOUT: totalRows = 10; break;
            case Game.XL_PLUS_CANNON: totalRows = 10; break;
            case Game.XL_PLUS_CANNON_8D: totalRows = 10; break;
            case Game.XL_PLUS_CANNON_4m8c: totalRows = 10; break;
            default: totalRows = 8;
        }

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
            case Game.XL_PLUS_CANNON: setSize(820, 820); break;
            case Game.XL_PLUS_CANNON_8D: setSize(820, 820); break;
            case Game.XL_PLUS_CANNON_4m8c: setSize(820, 820); break;
            default: setSize(700, 700);
        }

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
        game.showActiveSide();
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

        public String toString(){
            Piece p;
            String srcPieceName = "";
            String mvLocations =  "";
            if (sourceSquare != null && sourceSquare.piece != null) {
                p = sourceSquare.piece;
                srcPieceName = p.getSideName().concat(p.Name);
                mvLocations = " From : Rank(".concat(String.valueOf(p.square.rank));
                mvLocations = mvLocations.concat(") File(").concat(String.valueOf(p.square.file)).concat(") ");
            }
            if (targetSquare != null) {
                if (srcPieceName.equals("")) {
                    srcPieceName = targetSquare.piece.getSideName().concat(targetSquare.piece.Name);
                }
                mvLocations = mvLocations.concat(" To : Rank(".concat(String.valueOf(targetSquare.rank)));
                mvLocations = mvLocations.concat(") File(").concat(String.valueOf(targetSquare.file)).concat(").");
            }
            return srcPieceName.concat(mvLocations);
        }
    }

    public List<Piece> getCheckingPiecesList(Square square, Side attackingSide)
    {
        // Redesigned class
        return getAttackingPieces(square,attackingSide);
    }


    /**
     *
     * @param square
     * @param attackingSide
     * @return true if the square is under attack from the specified
     * attackingSide
     */
    public boolean isUnderAttack(Square square, Side attackingSide, Piece pShouldExcluded)
    {
        // King is not in any list, need check it also.
        return isUnderAttackFromNonKingPieces(square, attackingSide, pShouldExcluded)
                || isUnderAttackFromKing(square,attackingSide);
        //return false;
        //return isUnderAttackFromNonKingPieces(square, attackingSide, pinMatters)
        //        || isUnderAttackFromKing(square, attackingSide);
    }


    public boolean isUnderAttackFromNonKingPieces(Square square, Side attackingSide, Piece pShouldExcluded)
    {
        List<Piece> attackingPieces = getAttackingPieces(square, attackingSide);
        attackingPieces.remove(pShouldExcluded);
        // 去除将被吃掉的子的攻击力
        if (attackingPieces.size() > 0 ) return true;
        return false;
    }

    static List<Piece> getAttackingPieces(Square square, Side attackingSide) {
        List<Piece> attackingPieces = new ArrayList(6);
        for (List lstKinds: attackingSide.allKindsOfPieces ) {
            for (Object p : lstKinds) {
                if ( ((Piece) p).canAttackTo(square)) attackingPieces.add( ((Piece) p) );
            }
        }
        return attackingPieces;
    }


    private boolean isUnderAttackFromKing(Square square, Side attackingSide)
    {
        return (Math.abs(square.rank - attackingSide.king.square.rank) <= 1 && Math.abs(square.file - attackingSide.king.square.file) <= 1);
    }

    public boolean doesNotExposeKingToCheck(Move m)
    {
        Piece pAttacking = m.sourceSquare.piece;
        Piece pTarget = m.targetSquare.piece;
        // adeel原本的实现是按格子找可能攻击的子，但还是需要保留模拟的棋子移动
        Piece temp = m.sourceSquare.piece;
        Piece temp2 = m.targetSquare.piece;

        // make the move
        m.sourceSquare.piece = null;
        m.targetSquare.piece = temp;
        temp.square = m.targetSquare;
        // 这样多了一个子？？？

        // 改成各自的Piece内置method之后需要判断/排除正在被吃掉的子
        // check if it exposes king to check
        boolean result = !pAttacking.getSide().king.square.isUnderAttackFrom(pAttacking.getSide().opponent, pTarget);
        if (!result) {
            JOptionPane.showMessageDialog(null,"The selected move makes The King underAttack!\n Canceled.");
        }

        // revert
        m.sourceSquare.piece = temp;
        temp.square = m.sourceSquare;
        m.targetSquare.piece = temp2;

        return result;
    }
}
