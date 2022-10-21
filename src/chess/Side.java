/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chess;

import chess.Board.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Adeel
 */
public class Side
{
    public static Scanner in = new Scanner(System.in);
    private final Game game;
    private final Side self;
    private final Board board;

    public boolean isWhite;
    boolean inCheck;
    public Side opponent;
    public King king;
    public List<Queen> queens;
    public List<Rook> rooks;
    public List<Bishop> bishops;
    public List<Knight> knights;
    public List<Pawn> pawns;
    public List<ChineseTrebuchet> chinesetrebuchets;

    public Side(boolean isWhite, Game g)
    {
        game = g;
        this.isWhite = isWhite;
        this.board = g.board;
        inCheck = false;
        
        queens = new ArrayList(1);
        rooks = new ArrayList(2);
        bishops = new ArrayList(2);
        knights = new ArrayList(2);
        //pawns = new ArrayList(8);
        // 初始化存储list for ALL Layout
        pawns = new ArrayList(g.board.totalRows);

        chinesetrebuchets = new ArrayList(0);
        // Initialized with 0 for STD Layout and XL Layout which not using it.

        self = this;
        setupPieces();        
    }

    private void setupPieces()
    {
        // 初始化存储list
        int totalRows;
        totalRows = this.board.totalRows;
        // 生成棋子并移动到指定位置
        if (this.game.LayoutType == Game.STD_LAYOUT) {
            // Original STD Layout setup
            setupSTDLayoutPieces();
        }
        else if (this.game.LayoutType == Game.XL_LAYOUT) {
            // XL Layout setup
            setupXLLayoutPieces(totalRows);
        } else if (this.game.LayoutType == Game.XL_PLUS_CHINESE_TREBUCHET) {
            // XL + Chinese Trebuchet setup
            setupXL_ChineseTrebuchet_Pieces(totalRows);
        }

    }

    private void setupXL_ChineseTrebuchet_Pieces(int totalRows) {
        king = new King(((isWhite) ? 0 : (totalRows -1)), 5, null);
        queens.add(new Queen(((isWhite) ? 0 : (totalRows -1)), 4, null));

        rooks.add(new Rook(((isWhite) ? 0 : (totalRows -1)), 0, null));
        rooks.add(new Rook(((isWhite) ? 0 : (totalRows -1)), 9, null));
        // rooks on end position

        knights.add(new Knight(((isWhite) ? 0 : (totalRows -1)), 2,
                ((isWhite) ? "" : "img/knight_b_sq_90.ico")));
        knights.add(new Knight(((isWhite) ? 0 : (totalRows -1)), 7,
                ((isWhite) ? null : "img/knight_b_sq_90.ico")));

        bishops.add(new Bishop(((isWhite) ? 0 : (totalRows -1)), 3, null));
        bishops.add(new Bishop(((isWhite) ? 0 : (totalRows -1)), 6, null));

        chinesetrebuchets.add(new ChineseTrebuchet(((isWhite) ? 0 : (totalRows -1)), 1,
                ((isWhite) ? "img/trebuchet_w_sq.ico" : "img/trebuchet_b_sq.ico")));
        chinesetrebuchets.add(new ChineseTrebuchet(((isWhite) ? 0 : (totalRows -1)), 8,
                ((isWhite) ? "img/trebuchet_w_sq.ico" : "img/trebuchet_b_sq.ico")));
        //ChineseTrebuchet
        setupPawns();
    }

    private void setupXLLayoutPieces(int totalRows) {
        king = new King(((isWhite) ? 0 : (totalRows -1)), 5, null);
        queens.add(new Queen(((isWhite) ? 0 : (totalRows -1)), 4, null));

        rooks.add(new Rook(((isWhite) ? 0 : (totalRows -1)), 1, null));
        rooks.add(new Rook(((isWhite) ? 0 : (totalRows -1)), 8, null));

        knights.add(new Knight(((isWhite) ? 0 : (totalRows -1)), 2,
                ((isWhite) ? "" : "img/knight_b_sq_90.ico")));
        knights.add(new Knight(((isWhite) ? 0 : (totalRows -1)), 7,
                ((isWhite) ? null : "img/knight_b_sq_90.ico")));

        bishops.add(new Bishop(((isWhite) ? 0 : (totalRows -1)), 3, null));
        bishops.add(new Bishop(((isWhite) ? 0 : (totalRows -1)), 6, null));

        setupPawns();
    }

    private void setupSTDLayoutPieces() {
        king = new King(((isWhite) ? 0 : 7), 4, null);
        queens.add(new Queen(((isWhite) ? 0 : 7), 3, null));

        rooks.add(new Rook(((isWhite) ? 0 : 7), 0, null));
        rooks.add(new Rook(((isWhite) ? 0 : 7), 7, null));

        knights.add(new Knight(((isWhite) ? 0 : 7), 1, null));
        knights.add(new Knight(((isWhite) ? 0 : 7), 6, null));

        bishops.add(new Bishop(((isWhite) ? 0 : 7), 2,null));
        bishops.add(new Bishop(((isWhite) ? 0 : 7), 5, null));

        setupPawns();
    }

    private void setupPawns()
    {
        if (this.game.LayoutType == Game.STD_LAYOUT) {
            // 初始化存储 for STD Layout
            for (int file = 0, pawnRow = (isWhite) ? 1 : 6; file < 8; file++)
            {
                pawns.add(new Pawn(pawnRow, file,null));
            }
        }
        else if (this.game.LayoutType == Game.XL_LAYOUT) {
            for (int file = 0, pawnRow = (isWhite) ? 2 : (this.board.totalRows-3); file < this.board.totalRows; file++)
            {
                pawns.add(new Pawn(pawnRow, file, null));
            }
        } else if (this.game.LayoutType == Game.XL_PLUS_CHINESE_TREBUCHET) {
            for (int file = 0, pawnRow = (isWhite) ? 2 : (this.board.totalRows-3); file < this.board.totalRows; file++)
            {
                pawns.add(new Pawn(pawnRow, file, null));
            }
        }
    }

    public void makeMove(Move m)
    {
        m.sourceSquare.piece.makeMove(m);
        // Try To Show progress Here.

    }

    public boolean isLegal(Move move)
    {
        return move.sourceSquare.piece != null
            && !move.sourceSquare.piece.equals(move.targetSquare.piece)
            && move.sourceSquare.piece.getSide().equals(this)
            && move.sourceSquare.piece.isLegal(move)
            && (move.sourceSquare.piece.equals(king) || board.doesNotExposeKingToCheck(move));
    }

    public abstract class Piece
    {
        public Square square;
        public String icon;
        // String ONLY.

        public ImageIcon imgICON = null;
        // Add this to hold Image with pieces.

        public Side getSide()
        {
            return self;
        }
        
        public boolean isWhite()
        {
            return self.isWhite;
        }

        abstract boolean isLegal(Board.Move move);

        abstract public void makeMove(Move m);

        public void setPieceICONwithImage(String imgFileName)
        {
            if ( !(imgFileName == null) && !(imgFileName == "") )
            {
                // 传入文件名并创建
                this.imgICON = new ImageIcon(imgFileName);
            }
        }

        public void moveTo(Square targetSquare)
        {
            if (this.square != null)
            {
                // 设置出发格为空
                this.square.setPiece(null);
            }

            if (targetSquare.piece != null)
            {
                // 目标格非空，设置吃子
                game.markAsCaptured(targetSquare.piece);
            }

            // 设置本子所占据的格子
            this.square = targetSquare;
            targetSquare.setPiece(this);
        }                
    }

    public class ChineseTrebuchet extends Piece
    {
        boolean notMoved;

        public ChineseTrebuchet(int rank, int file, String iconFileName)
        {
            //icon = "砲";
            icon = "\u7832";
            // String ONLY.
            setPieceICONwithImage(iconFileName);

            moveTo(board.get(rank, file));
            notMoved = true;
        }

        @Override
        public boolean isLegal(Board.Move move)
        {
            if (move.targetSquare.piece == null)
            {
                // Target is null, let's see is there any Pieces in route
                if ((move.sourceSquare.file == move.targetSquare.file
                                && move.sourceSquare.rank != move.targetSquare.rank)
                        || (move.sourceSquare.rank == move.targetSquare.rank
                                && move.sourceSquare.file != move.targetSquare.file))
                // 同行或同列
                {
                    int piecesEnRoute = getPiecesEnRoute(move);
                    if (piecesEnRoute > 0) return false;
                    else return true;
                    // Yes, can go to the unoccupied Square.
                }
                else return false;
                // 即不同行又不同列，不可移动；
            }
            else if (isCapturablePiece(move.targetSquare.piece))
            {
                // Target is not KING and not same side
                // MUST update KING's Checking!!!
                // Check is it legal to Capture with only 1 Piece as Launcher
                int piecesEnRoute = getPiecesEnRoute(move);
                if (piecesEnRoute == 1) return true;
                else return false;
            }
            return false;
        }

        private boolean isCapturablePiece(Piece piece){
            //return (piece!= null && !piece.getClass().equals(King.class) && piece.getSide().equals(opponent));
            return (piece!= null && piece.getSide().equals(opponent));
            // 取消KING不可吃的检查判断
        }

        //private int getPiecesEnRoute(Move move)
        int getPiecesEnRoute(Move move) {
            int piecesEnRoute = 0;
            int rankInc = (int) Math.signum(move.targetSquare.rank - move.sourceSquare.rank),
                    fileInc = (int) Math.signum(move.targetSquare.file - move.sourceSquare.file);
            // Direction by signed number to FOR Loop.
            for (int rank = move.sourceSquare.rank + rankInc, file = move.sourceSquare.file + fileInc;
                 Math.abs(rank - move.targetSquare.rank) > 0 || Math.abs(file - move.targetSquare.file) > 0;
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

        @Override
        public void makeMove(Move m)
        {
            notMoved = false;
            moveTo(m.targetSquare);
        }
    }

    public class King extends Piece
    {
        private List<Piece> checkingPiecesList;
        private boolean castlingAllowed;
        private boolean castlingInProgress;

        public King(int rank, int file, String iconFileName)
        {
            icon = "\u265A";
            // TEXT直接使用unicode符号
            setPieceICONwithImage(iconFileName);

            moveTo(board.get(rank, file));
            castlingAllowed = true;
            castlingInProgress = false;
        }

        public boolean isMated()
        {
            boolean b1 = inCheck;
            boolean b2 = noEscape();
            boolean b3 = noIntercepts();
            boolean b4 = checkingPieceNotCapturable();
            // With ChineseTrebuchet, b2/b3/b4 all are false.
            return inCheck && noEscape() && noIntercepts() && checkingPieceNotCapturable();
        }

        public boolean inCheck()
        {
            checkingPiecesList = board.getCheckingPiecesList(square, opponent);
            // 查看对方所有的处于将军的子力
            inCheck = checkingPiecesList.size() > 0;

            return inCheck;
        }

        @Override
        boolean isLegal(Move move)
        {
            int rankDiff = Math.abs(move.targetSquare.rank - move.sourceSquare.rank);
            int fileDiff = Math.abs(move.targetSquare.file - move.sourceSquare.file);

            return ((rankDiff >= 0 && rankDiff <= 1 && fileDiff >= 0 && fileDiff <= 1)
                    && (move.targetSquare.piece == null || isCapturablePiece(move.targetSquare.piece))
                    && !move.targetSquare.isUnderAttackFrom(opponent, false))
                    || isLegalCasling(move);
        }

        private boolean isLegalCasling(Move move)
        {
            int rankDiff = move.targetSquare.rank - move.sourceSquare.rank;
            int fileDiff = move.targetSquare.file - move.sourceSquare.file;

            if (castlingAllowed && !inCheck)
            {
                if (rankDiff == 0)
                {
                    if (fileDiff == 2)
                    {
                        if (rooks.get(1).notMoved)
                        {
                            castlingInProgress = !(board.get(square.rank, 5).isUnderAttackFrom(opponent, false)
                                                 && board.get(square.rank, 6).isUnderAttackFrom(opponent, false));
                            return castlingInProgress;
                        }
                    }
                    if (fileDiff == -2)
                    {
                        if (rooks.get(0).notMoved)
                        {
                            castlingInProgress = !(board.get(square.rank, 2).isUnderAttackFrom(opponent, false)
                                                 && board.get(square.rank, 3).isUnderAttackFrom(opponent, false));
                            return castlingInProgress;
                        }
                    }
                }
            }
            return false;
        }

        @Override
        public void makeMove(Move m)
        {
            castlingAllowed = false;
            inCheck = false;
            if (castlingInProgress)
            {
                if (m.targetSquare.file == 2)
                {
                    rooks.get(0).makeMove(new Move(rooks.get(0).square, board.get(m.sourceSquare.rank, 3)));
                }
                if (m.targetSquare.file == 6)
                {
                    rooks.get(1).makeMove(new Move(rooks.get(0).square, board.get(m.sourceSquare.rank, 5)));
                }
                castlingInProgress = false;
            }
            moveTo(m.targetSquare);
        }

        private boolean noEscape()
        {
            Square sq;
            for (int rankInc = -1, rank, file; rankInc <= 1; rankInc++)
            {
                rank = square.rank + rankInc;
                //for (int fileInc = -1;(rank <= 7 && rank >= 0) && fileInc <= 1; fileInc++)
                for (int fileInc = -1;(rank <= (board.totalRows-1) && rank >= 0) && fileInc <= 1; fileInc++)
                {
                    file = square.file + fileInc;
                    // 3 X 3 = 9 个格子遍历一遍

                    //if ((file <= 7 && file >= 0))
                    if ((file <= (board.totalRows-1) && file >= 0))
                    {
                        sq = board.get(rank, file);
                        if (sq.piece == null || isCapturablePiece(sq.piece))
                        // 无子 或 可被KING吃掉
                        {
                            if (!sq.isUnderAttackFrom(opponent, false))
                            // 新的格子上是否被攻击
                            {
                                return false;
                            }
                        }
                    }
                }
            }
            return true;
        }

        private boolean noIntercepts()
        // NEED add the rule for Chinese Trebuchet!!!
        {
            if (checkingPiecesList.size() == 1)
            {
                if (!(checkingPiecesList.get(0).getClass().equals(Knight.class) || checkingPiecesList.get(0).getClass().equals(Pawn.class)))
                {
                    int kRank = square.rank,
                        kFile = square.file;

                    int rankInc = (int) Math.signum(checkingPiecesList.get(0).square.rank - kRank),
                        fileInc = (int) Math.signum(checkingPiecesList.get(0).square.file - kFile);

                    int r = kRank + rankInc, f = kFile + fileInc;
                    while (Math.abs(checkingPiecesList.get(0).square.rank - r) >= 1
                            || Math.abs(checkingPiecesList.get(0).square.file - f) >= 1)
                    {
                        if (board.get(r, f).isUnderAttackFromNonKingPieces(self, true))
                        {
                            return false;
                        }
                        r += rankInc;
                        f += fileInc;
                    }
                }
            }
            return true;
        }

        private boolean checkingPieceNotCapturable()
        {
            return (checkingPiecesList.size() == 1)
                 && checkingPiecesList.get(0).square.isUnderAttackFrom(opponent, true)
                 && checkingPiecesList.get(0).square.isUnderAttackFrom(self, true);
        }

    }

    public class Queen extends Piece
    {
        public Queen(int rank, int file, String iconFileName)
        {
            icon = "\u265B";
            setPieceICONwithImage(iconFileName);

            moveTo(board.get(rank, file));
        }

        @Override
        public boolean isLegal(Board.Move move)
        {
            if (move.targetSquare.piece == null || isCapturablePiece(move.targetSquare.piece))
            {
                if (move.sourceSquare.file == move.targetSquare.file
                    || move.sourceSquare.rank == move.targetSquare.rank
                    || (Math.abs(move.sourceSquare.rank - move.targetSquare.rank)
                        == Math.abs(move.sourceSquare.file - move.targetSquare.file)))
                {
                    int rankInc = (int) Math.signum(move.targetSquare.rank - move.sourceSquare.rank),
                        fileInc = (int) Math.signum(move.targetSquare.file - move.sourceSquare.file);
                    for (int rank = move.sourceSquare.rank + rankInc, file = move.sourceSquare.file + fileInc;
                            Math.abs(rank - move.targetSquare.rank) > 0 || Math.abs(file - move.targetSquare.file) > 0;
                            rank += rankInc, file += fileInc)
                    {
                        if (board.get(rank, file).piece != null)
                        {
                            return false;
                        }
                    }
                    return true;
                }
            }
            return false;
        }

        @Override
        public void makeMove(Move m)
        {
            moveTo(m.targetSquare);
        }
    }

    public class Rook extends Piece
    {
        boolean notMoved;

        public Rook(int rank, int file, String iconFileName)
        {
            // Next try ICON by Image
            //if (file == 0 && rank == totalRows-1)
            //{
            //    sq_ChessLabel.setIcon(new ImageIcon("black_rook.png"));
            // 可以设置JButton为ImageICON类型的图显示
            //}

            icon = "\u265C";
            // String ONLY.
            setPieceICONwithImage(iconFileName);

            moveTo(board.get(rank, file));
            notMoved = true;
        }

        @Override
        public boolean isLegal(Board.Move move)
        {
            if (move.targetSquare.piece == null || isCapturablePiece(move.targetSquare.piece))
            {
                if ((move.sourceSquare.file == move.targetSquare.file && move.sourceSquare.rank != move.targetSquare.rank)
                        || (move.sourceSquare.rank == move.targetSquare.rank && move.sourceSquare.file != move.targetSquare.file))
                {
                    int rankInc = (int) Math.signum(move.targetSquare.rank - move.sourceSquare.rank),
                        fileInc = (int) Math.signum(move.targetSquare.file - move.sourceSquare.file);
                    for (int rank = move.sourceSquare.rank + rankInc, file = move.sourceSquare.file + fileInc;
                            Math.abs(rank - move.targetSquare.rank) > 0 || Math.abs(file - move.targetSquare.file) > 0;
                            rank += rankInc, file += fileInc)
                    {
                        if (board.get(rank, file).piece != null)
                        {
                            return false;
                        }
                    }
                    return true;
                }
            }
            return false;
        }

        @Override
        public void makeMove(Move m)
        {
            notMoved = false;
            moveTo(m.targetSquare);
        }        
    }

    public class Bishop extends Piece
    {
        public Bishop(int rank, int file, String iconFileName)
        {
            icon = "\u265D";
            setPieceICONwithImage(iconFileName);

            moveTo(board.get(rank, file));
        }

        @Override
        public boolean isLegal(Board.Move move)
        {
            if (move.targetSquare.piece == null || isCapturablePiece(move.targetSquare.piece))
            {
                if ((Math.abs(move.sourceSquare.rank - move.targetSquare.rank)
                        == Math.abs(move.sourceSquare.file - move.targetSquare.file)))
                {
                    int rankInc = (int) Math.signum(move.targetSquare.rank - move.sourceSquare.rank),
                        fileInc = (int) Math.signum(move.targetSquare.file - move.sourceSquare.file);
                    for (int rank = move.sourceSquare.rank + rankInc, file = move.sourceSquare.file + fileInc;
                            Math.abs(rank - move.targetSquare.rank) > 0 || Math.abs(file - move.targetSquare.file) > 0;
                            rank += rankInc, file += fileInc)
                    {
                        if (board.get(rank, file).piece != null)
                        {
                            return false;
                        }
                    }
                    return true;
                }
            }
            return false;
        }

        @Override
        public void makeMove(Move m)
        {
            moveTo(m.targetSquare);
        }
    }

    public class Knight extends Piece
    {
        public Knight(int rank, int file, String iconFileName)
        {
            icon = "\u265E";
            setPieceICONwithImage(iconFileName);
            //if (!isWhite) {
                // Should PUT before MOVE!!!
                //setPieceICONwithImage(iconFileName);
                // .ico图片可以透出背景，不遮挡背景
                // bind with square移动后不能被转移. Thus BIND with Piece.
            //}
            moveTo(board.get(rank, file));
        }

        @Override
        boolean isLegal(Board.Move move)
        {
            if (move.targetSquare.piece == null || isCapturablePiece(move.targetSquare.piece))
            {
                int rankDiff = Math.abs(move.targetSquare.rank - move.sourceSquare.rank);
                int fileDiff = Math.abs(move.targetSquare.file - move.sourceSquare.file);

                if ((rankDiff == 2 && fileDiff == 1) || (fileDiff == 2 && rankDiff == 1))
                {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void makeMove(Move m)
        {
            moveTo(m.targetSquare);
        }
    }

    public class Pawn extends Piece
    {
        boolean enPassantInProgress;
        public int initRow;

        public Pawn(int rank, int file, String iconFileName)
        {
            initRow = rank;
            // 记录初始位置，通用化STD和XL布局
            icon = "\u265F";
            setPieceICONwithImage(iconFileName);

            moveTo(board.get(rank, file));
            enPassantInProgress = false;
        }

        @Override
        boolean isLegal(Board.Move move)
        {
            int rankDiff = (move.targetSquare.rank - move.sourceSquare.rank) * ((isWhite) ? 1 : -1);
            int fileDiff = Math.abs(move.targetSquare.file - move.sourceSquare.file);
            boolean result = false;
            if ((rankDiff == 1) && (fileDiff == 1)) // capture
            {
                if (isCapturablePiece(move.targetSquare.piece))
                {
                    return true;
                }
                else
                {
                    enPassantInProgress = game.isLegalEnPassant(move);
                    return enPassantInProgress;
                }
            }
            if ((rankDiff == 1) && (fileDiff == 0)) // 1-square-forward move
            {
                return (move.targetSquare.piece == null);
            }
            if ((rankDiff == 2) && (fileDiff == 0)) // 2-squares-forward move
            {
                // 改成与记录的初始位置比较
                return ((move.sourceSquare.rank == initRow) && move.targetSquare.piece == null);
                // 第一步的判断，需要改进
                //return ((move.sourceSquare.rank == ((isWhite) ? 1 : 6)) && move.targetSquare.piece == null);
            }

            return false;
        }

        @Override
        public void makeMove(Move m)
        {
            // remove captured pawn
            if (enPassantInProgress)
            {
                game.markAsCaptured(board.get(m.sourceSquare.rank, m.targetSquare.file).piece);
                board.get(m.sourceSquare.rank, m.targetSquare.file).setPiece(null);
            }

            moveTo(m.targetSquare);


            //if (m.targetSquare.rank == ((isWhite) ? 7 : 0))
            if (m.targetSquare.rank == ((isWhite) ? (board.totalRows-1) : 0))
            {
                promote(this);
            }

            enPassantInProgress = false;
        }

        private void promote(Pawn p)
        {
            System.out.print("Promote to (q, r, b, n): ");
            String iconFileName=null;
            if ( game.LayoutType == Game.XL_LAYOUT ) {
                iconFileName = "";
                // Wait for detailed settings
                // OR put into cases with immediately ?
            }
            switch(in.next().charAt(0))
            {
                case 'q': queens.add(new Queen(p.square.rank, p.square.file, null)); break;
                case 'r': rooks.add(new Rook(p.square.rank, p.square.file, null)); break;
                case 'b': bishops.add(new Bishop(p.square.rank, p.square.file, null)); break;
                case 'n': knights.add(new Knight(p.square.rank, p.square.file, null)); break;
            }            
            game.markAsCaptured(p);
        }


    }

    private boolean isCapturablePiece(Piece piece)
    // private boolean isCapturablePiece(Piece piece)
    // 如果使用了 private 声明，则子类是无法重写的。
    {
        return (piece!= null && !piece.getClass().equals(King.class) && piece.getSide().equals(opponent));
        // King is not capturable but in CHECK state.
        // NEED CHANGE ???
    }
}
