/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chess;

import chess.Board.*;
import chess.Side.*;

import javax.swing.*;
import java.awt.Color;

/**
 *
 * @author Adeel
 */
public class Square
{
    private final Board board;
    public Piece piece;
    public int file;
    public int rank;
    ChessLabel icon;
    // 本身是JButton，可以有Image ICON。

    public Square(Board b, int rank, int file)
    {
        board = b;
        piece = null;
        setCoordinates(rank, file);
        icon = new ChessLabel("", board, this);
        // 初始化格子的string
    }

    public void setIconDefaultBackground()
    {
        icon.set(this.rank,this.file);
        // Setup Square's Default ICON Background
    }

    public void setPiece(Piece targetPiece)
    {
        this.piece = targetPiece;
        if (targetPiece == null)
        {
            icon.setText("");
            // Add following to clear Image
            icon.setIcon(null);
        }
        else
        {
            icon.setText(targetPiece.icon);
            icon.setForeground((targetPiece.isWhite()) ? new Color(255,237,210) : new Color(50,50,50));
            // Add following to bind Piece's ICON with Square
            icon.setIcon(targetPiece.imgICON);
        }
    }

    private void setCoordinates(int rank, int file)
    {
        this.rank = rank;
        this.file = file;
    }

    public boolean isUnderAttackFrom(Side attackingSide, Piece pShouldExcluded)
    {
        boolean result = board.isUnderAttack(this, attackingSide, pShouldExcluded);
        /*
        if (result){
            JOptionPane.showMessageDialog(null,"This potential Move makes the King under attack!");
        }
         */
        return result;
    }
    
    public boolean isUnderAttackFromNonKingPieces(Side attackingSide, Piece pShouldExcluded)
    {
        return board.isUnderAttackFromNonKingPieces(this, attackingSide, pShouldExcluded);
    }

    public int getPiecesEnRoute(Square sqTarget) {
        int piecesEnRoute = 0;
        int rankInc = (int) Math.signum(sqTarget.rank - this.rank),
                fileInc = (int) Math.signum(sqTarget.file - this.file);
        // Direction by signed number to FOR Loop.
        for (int rank = this.rank + rankInc, file = this.file + fileInc;
             Math.abs(rank - sqTarget.rank) > 0 || Math.abs(file - sqTarget.file) > 0;
             rank += rankInc, file += fileInc)
        {
            if (board.get(rank, file).piece != null)
            {
                piecesEnRoute ++;
                // Tranditional Rook, Any Piece should not able to move over to an unoccupied Square.
            }
        }
        return piecesEnRoute;
    }
}
