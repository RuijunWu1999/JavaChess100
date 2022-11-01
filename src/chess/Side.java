/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chess;

import chess.Board.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Adeel
 */
public class Side
{
    //public static Scanner in = new Scanner(System.in);
    final public static String ICONIC_FILE_KING_WHITE = "";
    final public static String ICONIC_FILE_QUEEN_WHITE = "";
    final public static String ICONIC_FILE_BISHOP_WHITE = "";
    final public static String ICONIC_FILE_KNIGHT_WHITE = "";
    final public static String ICONIC_FILE_ROOK_WHITE = "";
    final public static String ICONIC_FILE_PAWN_WHITE = "";
    final public static String ICONIC_FILE_CANNON_WHITE = "img/cannon_w_sq.ico";

    final public static String ICONIC_FILE_KING_BLACK = "";
    final public static String ICONIC_FILE_QUEEN_BLACK = "";
    final public static String ICONIC_FILE_BISHOP_BLACK = "";
    final public static String ICONIC_FILE_KNIGHT_BLACK = "";
    final public static String ICONIC_FILE_ROOK_BLACK = "";
    final public static String ICONIC_FILE_PAWN_BLACK = "";
    final public static String ICONIC_FILE_CANNON_BLACK = "img/cannon_b_sq.ico";

    private final Game game;
    final Side self;
    final Board board;

    public boolean isWhite;
    boolean inCheck;
    public Side opponent;
    public King king;
    public List<Queen> queens;
    public List<Rook> rooks;
    public List<Bishop> bishops;
    public List<Knight> knights;
    public List<Pawn> pawns;
    public List<Cannon> cannons;

    public List<List> allKindsOfPieces;
    public Side(boolean isWhite, Game g)
    {
        game = g;
        this.isWhite = isWhite;
        this.board = g.board;
        inCheck = false;

        allKindsOfPieces = new ArrayList(6);

        queens = new ArrayList(1);
        allKindsOfPieces.add(queens);
        rooks = new ArrayList(2);
        allKindsOfPieces.add(rooks);
        bishops = new ArrayList(2);
        allKindsOfPieces.add(bishops);
        knights = new ArrayList(2);
        allKindsOfPieces.add(knights);
        //pawns = new ArrayList(8);
        // 初始化存储list for ALL Layout
        pawns = new ArrayList(g.board.totalRows);
        allKindsOfPieces.add(pawns);

        self = this;
        if (g.LayoutType == Game.STD_LAYOUT) {
            setupPieces();
        }
    }

    private void setupPieces()
    {
        // 初始化存储list
        king = new King(((isWhite) ? 0 : 7), 4);
        queens.add(new Queen(((isWhite) ? 0 : 7), 3));

        rooks.add(new Rook(((isWhite) ? 0 : 7), 0));
        rooks.add(new Rook(((isWhite) ? 0 : 7), 7));

        knights.add(new Knight(((isWhite) ? 0 : 7), 1));
        knights.add(new Knight(((isWhite) ? 0 : 7), 6));

        bishops.add(new Bishop(((isWhite) ? 0 : 7), 2));
        bishops.add(new Bishop(((isWhite) ? 0 : 7), 5));

        setupPawns();
    }

    private void setupPawns()
    {
        // 初始化存储 for STD Layout
        for (int file = 0, pawnRow = (isWhite) ? 1 : 6; file < 8; file++)
        {
            pawns.add(new Pawn(pawnRow, file));
        }
    }

    public void makeMove(Move m)
    {
        m.sourceSquare.piece.makeMove(m);
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

        public String Name;
        public ImageIcon imgICON = null;
        // Add this to hold Image with pieces.

        public Side getSide()
        {
            return self;
        }

        public String getSideName()
        {
            return self.isWhite? "(WHITE)" : "(BLACK)";
        }

        public boolean isWhite()
        {
            return self.isWhite;
        }

        abstract boolean isLegal(Board.Move move);
        abstract boolean canReachTo(Square sqTarget);
        // Need Check Not going to the same sq.
        abstract boolean canAttackTo(Square sqTarget);
        // Need Check Not going to the same sq.

        abstract public void makeMove(Move m);

        public void setPieceICONwithImage(String imgFileName)
        {
            if ( !(imgFileName == null) && !(imgFileName == "") )
            {
                // 传入文件名并创建
                this.imgICON = new ImageIcon(imgFileName);
                this.icon = "";
                // To remove .. on Piece's ICON
            }
        }

        public void moveTo(Square targetSquare)
        {
            if (this.square != null)
            {
                // 恢复出发格为空
                this.square.setPiece(null);
            }

            if (targetSquare.piece != null)
            {
                // 目标格非空，设置吃子
                game.markAsCaptured(targetSquare.piece);
                // But made addtionial capture when pawn promotation ???
            }

            // 设置本子所占据的格子
            this.square = targetSquare;
            targetSquare.setPiece(this);
        }                
    }

    public class Cannon extends Piece
    {
        boolean notMoved;

        public Cannon(int rank, int file)
        {
            //icon = "砲";
            icon = "\u7832";
            Name = "Cannon";
            // String ONLY.
            setPieceICONwithImage( (isWhite? ICONIC_FILE_CANNON_WHITE : ICONIC_FILE_CANNON_BLACK) );

            moveTo(board.get(rank, file));
            notMoved = true;
        }
        @Override
        public boolean canReachTo(Square sqTarget)
        {
            // Cannon's rule for move.
            Square sq0 = this.square;
            if (isSameSquareRankANDFile(sqTarget, sq0)) return false;
            return directlyReachableOnRankOrFileTo(sqTarget, sq0);
        }

        @Override
        public boolean canAttackTo(Square sqTarget)
        {
            // Cannon's rule for attack.
            if ( (!(isSameSquareRankANDFile(this.square, sqTarget))  && isOnSameRankOrFile(this.square, sqTarget)) )
            {
                return (this.square.getPiecesEnRoute(sqTarget) == 1);
            }
            return false;
        }
        @Override
        public boolean isLegal(Board.Move move)
        {
            // Cannon's rules
            if (move.targetSquare.piece == null)
            {
                // Target is null, let's see is there any Pieces in route
                return canReachTo(move.targetSquare);
            }
//            else if (isOnSameRankOrFile(move.targetSquare, move.sourceSquare) && isCapturablePiece(move.targetSquare.piece))
            else if ( isCapturablePiece(move.targetSquare.piece) )
            {
                // Target is not KING and not same side
                // MUST update KING's Checking!!!
                // Check is it legal to Capture with only 1 Piece as Launcher
                return canAttackTo(move.targetSquare);
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

    private static boolean isSameSquareRankANDFile(Square sqSrc, Square sqTarget) {
        if (sqSrc.rank == sqTarget.rank && sqSrc.file == sqTarget.file) {
            return true;
        }
        return false;
    }

    private static boolean directlyReachableOnRankOrFileTo(Square sqSrc, Square sqTarget) {
        if (isOnSameRankOrFile(sqSrc, sqTarget))
        {
            int piecesEnRoute = sqSrc.getPiecesEnRoute(sqTarget);
            // MOVE to Square CLASS.
            if (piecesEnRoute > 0) return false;
            else return true;
            // Yes, can go to the unoccupied Square.
        }
        return false;
    }

    private static boolean isOnSameRankOrFile(Square sqSrc, Square sqTarget) {
        return (sqSrc.file == sqTarget.file && sqSrc.rank != sqTarget.rank)
                || (sqSrc.rank == sqTarget.rank && sqSrc.file != sqTarget.file);
    }

    public class King extends Piece
    {
        List<Piece> checkingPiecesList;
        private boolean castlingAllowed;
        private boolean castlingInProgress;

        public King(int rank, int file)
        {
            icon = "\u265A";
            // TEXT直接使用unicode符号
            Name = "King";
            setPieceICONwithImage( (isWhite  ? ICONIC_FILE_KING_WHITE : ICONIC_FILE_KING_BLACK) );

            moveTo(board.get(rank, file));
            castlingAllowed = true;
            castlingInProgress = false;
        }

        public boolean isMated()
        {
            //将杀（Checkmate）：对手的国王正在被将军，且无法摆脱将军，则被将杀，棋局立刻结束
            //【逼和】你只剩一个王，对方下完一步棋之后，没有将军，而你的王走到哪里都会被将军，只有你本来王占据的那格是安全的，那么就是所谓的逼和。轮到一方走棋时，该方的王没有收到对方任何棋子的直接攻击，但己方却没有子力能做出合法的移动（王无法走到一个不被将军的格子，其他棋子也无法移动）。你无法移动自己的棋子，别人自然也轮不到顺序动他自己的棋子来攻击你。此种情况算逼和，以和棋告终。
            //【将死】是一方对另一方的王进行了直接攻击，而被攻击的一方，无法通过王的移动，子力的遮挡，或者吃掉对方的攻击棋子的方式回避将军的局面。此种局面为将杀，直接KO……
            //【将死是死，无子可动是逼和。】
            boolean b1 = inCheck;
            boolean b2 = noEscape();
            // 砲已补充完。 false == 还有路可走
            boolean b3 = noIntercepts();
            // false == 有子可以过来挡
            boolean b4 = checkingPieceNotCapturable();
            //  false == 正在将军的子可以被己方吃掉.

            return b1 && b2 && b3 && b4;
            // With ChineseTrebuchet, b2/b3/b4 all are false.
            //return inCheck && noEscape() && noIntercepts() && checkingPieceNotCapturable();
            // Original author Adeel's algorithm
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
            // King's RULE.
            boolean result = ( canReachTo(move.targetSquare)
                    && (move.targetSquare.piece == null || isCapturablePiece(move.targetSquare.piece))
                    && !move.targetSquare.isUnderAttackFrom(opponent, false))
                    || isLegalCastling(move.targetSquare);
            if (!result) {
                JOptionPane.showMessageDialog(null,"This King's Move is Either ILLEGAL OR Exposed to Attacker!");
            }
            return result;
        }

        @Override
        boolean canReachTo(Square sqTarget) {
            Square sqSrc = this.square;
            if (isSameSquareRankANDFile(sqSrc, sqTarget)) return false;

            int rankDiff = Math.abs(sqTarget.rank - sqSrc.rank);
            int fileDiff = Math.abs(sqTarget.file - sqSrc.file);

            return (rankDiff >= 0 && rankDiff <= 1 && fileDiff >= 0 && fileDiff <= 1);
        }

        @Override
        boolean canAttackTo(Square sqTarget) {
            return canReachTo(sqTarget);
        }

        private boolean isLegalCastling(Square sqTarget)
        {
            Square sqSrc = this.square;
            int rankDiff = sqTarget.rank - sqSrc.rank;
            int fileDiff = sqTarget.file - sqSrc.file;

            if (castlingAllowed && !inCheck)
            {
                int fileLocofKing = sqSrc.file;
                if (rankDiff == 0)
                {
                    if (fileDiff == 2)
                    // 2 for STD Layout KING's move steps
                    // 具体走法：王向一侧车的方向走两格，再把车越过王放在与王相邻的一格上。
                    {
                        if (rooks.get(1).notMoved)
                        // 1 stand for file located at rightmost
                        {
                            boolean bl1 = !(board.get(square.rank, (fileLocofKing + 1)).isUnderAttackFrom(opponent, false));
                            boolean bl2 = !(board.get(square.rank, (fileLocofKing + 2)).isUnderAttackFrom(opponent, false));
                            castlingInProgress = bl1 && bl2;
                            return castlingInProgress;
                        }
                    }
                    if (fileDiff == -2)
                    {
                        if (rooks.get(0).notMoved)
                        // 0 stand for file located at leftmost
                        {
                            boolean bl1 = !(board.get(square.rank, (fileLocofKing - 1)).isUnderAttackFrom(opponent, false));
                            boolean bl2 = !(board.get(square.rank, (fileLocofKing - 2)).isUnderAttackFrom(opponent, false));
                            castlingInProgress = bl1 && bl2;
                            return castlingInProgress;
                        }
                    }
                }
            }
            return false;
        }

        private boolean isLegalCastlingWithMove(Move move)
        {
            int rankDiff = move.targetSquare.rank - move.sourceSquare.rank;
            int fileDiff = move.targetSquare.file - move.sourceSquare.file;

            if (castlingAllowed && !inCheck)
            {
                int fileLocofKing = move.sourceSquare.file;
                if (rankDiff == 0)
                {
                    if (fileDiff == 2)
                    // 2 for STD Layout KING's move steps
                    // 具体走法：王向一侧车的方向走两格，再把车越过王放在与王相邻的一格上。
                    {
                        if (rooks.get(1).notMoved)
                        // 1 stand for file located at rightmost
                        {
                            boolean bl1 = !(board.get(square.rank, (fileLocofKing + 1)).isUnderAttackFrom(opponent, false));
                            boolean bl2 = !(board.get(square.rank, (fileLocofKing + 2)).isUnderAttackFrom(opponent, false));
                            castlingInProgress = bl1 && bl2;

                            return castlingInProgress;
                        }
                    }
                    if (fileDiff == -2)
                    {
                        if (rooks.get(0).notMoved)
                        // 0 stand for file located at leftmost
                        {
                            castlingInProgress = !(board.get(square.rank, (fileLocofKing-1)).isUnderAttackFrom(opponent, false))
                                    && !(board.get(square.rank, (fileLocofKing-2)).isUnderAttackFrom(opponent, false));
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
                int fileofKing = m.sourceSquare.file;
                // Adjust to Fit XL Layout
                //if (m.targetSquare.file == 2)
                if (m.targetSquare.file == (fileofKing-2))
                {
                    rooks.get(0).makeMove(new Move(rooks.get(0).square, board.get(m.sourceSquare.rank, (fileofKing-1))));
                    // Left Rook
                    System.out.println(m.sourceSquare.piece.getSideName().concat(" ==> Left Rook Castled."));
                }
                //if (m.targetSquare.file == 6)
                if (m.targetSquare.file == (fileofKing+2))
                {
                    //rooks.get(1).makeMove(new Move(rooks.get(0).square, board.get(m.sourceSquare.rank, 5)));
                    rooks.get(1).makeMove(new Move(rooks.get(1).square, board.get(m.sourceSquare.rank, (fileofKing+1))));
                    // Right Rook,                      ^^^^^^SHOULD BE 1 for Right Rook
                    System.out.println(m.sourceSquare.piece.getSideName().concat(" ==> Right Rook Castled."));
                }
                castlingInProgress = false;
            }
            moveTo(m.targetSquare);
        }

        private boolean noEscape()
        {
            // True when KING's next move all will be attack! 王本身将处于受攻击的位置
            Square sq;
            for (int rankInc = -1, rank, file; rankInc <= 1; rankInc++)
            {
                rank = square.rank + rankInc;
                //for (int fileInc = -1;(rank <= 7 && rank >= 0) && fileInc <= 1; fileInc++)
                for (int fileInc = -1;(rank <= (board.totalRows-1) && rank >= 0) && fileInc <= 1; fileInc++)
                //                          已经限制了不过界
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
                            // 新的格子上是否被攻击,只要有一个不被攻击即可
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
            // 只有一个将才考虑阻拦
            {
                if (!(checkingPiecesList.get(0).getClass().equals(Knight.class)
                        || checkingPiecesList.get(0).getClass().equals(Pawn.class)))
                // 既不是马，也不是兵，砲需要单独考虑（reachable）！！！
                {
                    int kRank = square.rank,
                        kFile = square.file;

                    int rankInc = (int) Math.signum(checkingPiecesList.get(0).square.rank - kRank),
                        fileInc = (int) Math.signum(checkingPiecesList.get(0).square.file - kFile);

                    int r = kRank + rankInc, f = kFile + fileInc;
                    while (Math.abs(checkingPiecesList.get(0).square.rank - r) >= 1
                            || Math.abs(checkingPiecesList.get(0).square.file - f) >= 1)
                    {
                        Square sq0 = board.get(r, f);
                        if (sq0.isUnderAttackFromNonKingPieces(self, true) )
//                            || board.isReachableByCannons(sq0, self.opponent,true))
                        {
                            // 是这一条线上可以走过来阻隔的位置的概念！
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
            boolean result = (checkingPiecesList.size() == 1);
            //result = result && checkingPiecesList.get(0).square.isUnderAttackFrom(opponent, true);
            // Above is meaningless ???
            result = result && !checkingPiecesList.get(0).square.isUnderAttackFrom(self, true);
            // SHOULD NOT it to make it NotCapturable, underAttack == Capturable
            return result;
        }

    }

    public class Queen extends Piece
    {
        public Queen(int rank, int file)
        {
            icon = "\u265B";
            Name = "Queen";
            setPieceICONwithImage( (isWhite? ICONIC_FILE_QUEEN_WHITE : ICONIC_FILE_QUEEN_BLACK) );

            moveTo(board.get(rank, file));
        }

        @Override
        public boolean isLegal(Board.Move move)
        {
            // Queen's rules
            if (move.targetSquare.piece == null)
            {
                return canReachTo(move.targetSquare);
            } else if (isCapturablePiece(move.targetSquare.piece)) {
                return canAttackTo(move.targetSquare);
            }
            return false;
        }

        @Override
        boolean canReachTo(Square sqTarget) {
            if (isSameSquareRankANDFile(this.square, sqTarget)) return false;

            return directlyReachableOnRankOrFileTo(this.square, sqTarget)
                    || directlyReachableOnDiagnal(this.square, sqTarget);
        }

        @Override
        boolean canAttackTo(Square sqTarget)
        {
            return this.canReachTo(sqTarget);
        }
        @Override
        public void makeMove(Move m)
        {
            moveTo(m.targetSquare);
        }
    }

    private boolean directlyReachableOnDiagnal(Square sqSrc, Square sqTarget) {
        int rankDiff = sqSrc.rank - sqTarget.rank;
        int fileDiff = sqSrc.file - sqTarget.file;
        if (Math.abs(rankDiff) == Math.abs(fileDiff))
        {
            return (sqSrc.getPiecesEnRoute(sqTarget) == 0);
        }
        return false;
    }

    public class Rook extends Piece
    {
        boolean notMoved;

        public Rook(int rank, int file)
        {
            icon = "\u265C";
            Name = "Rook";
            // String ONLY.
            setPieceICONwithImage( (isWhite? ICONIC_FILE_ROOK_WHITE : ICONIC_FILE_ROOK_BLACK) );

            moveTo(board.get(rank, file));
            notMoved = true;
        }

        @Override
        public boolean isLegal(Board.Move move)
        {
            // Rook's Rule
            if (move.targetSquare.piece == null)
            {
                return canReachTo(move.targetSquare);
            } else if (isCapturablePiece(move.targetSquare.piece)) {
                return canAttackTo(move.targetSquare);
            }
            return false;
        }

        @Override
        boolean canReachTo(Square sqTarget) {
            if (isSameSquareRankANDFile(this.square, sqTarget)) return false;

            return directlyReachableOnRankOrFileTo(this.square,sqTarget);
        }

        @Override
        boolean canAttackTo(Square sqTarget) {
            return canReachTo(sqTarget);
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
        public Bishop(int rank, int file)
        {
            icon = "\u265D";
            Name = "Bishop";
            setPieceICONwithImage( (isWhite? ICONIC_FILE_BISHOP_WHITE : ICONIC_FILE_BISHOP_BLACK) );

            moveTo(board.get(rank, file));
        }

        @Override
        public boolean isLegal(Board.Move move)
        {
            // Bishop's Rule.
            if (move.targetSquare.piece == null)
            {
                return canReachTo(move.targetSquare);
            } else if (isCapturablePiece(move.targetSquare.piece)) {
                return canAttackTo(move.targetSquare);
            }
            return false;
        }

        @Override
        boolean canReachTo(Square sqTarget) {
            if (isSameSquareRankANDFile(this.square, sqTarget)) return false;

            return directlyReachableOnDiagnal(this.square, sqTarget);
        }

        @Override
        boolean canAttackTo(Square sqTarget)
        {
            return canReachTo(sqTarget);
        }
        @Override
        public void makeMove(Move m)
        {
            moveTo(m.targetSquare);
        }
    }

    public class Knight extends Piece
    {
        public Knight(int rank, int file)
        {
            icon = "\u265E";
            Name = "Knight";
            setPieceICONwithImage( (isWhite? ICONIC_FILE_KNIGHT_WHITE : ICONIC_FILE_KNIGHT_BLACK) );
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
            if (move.targetSquare.piece == null)
            {
                return canReachTo(move.targetSquare);
            }
            else if (isCapturablePiece(move.targetSquare.piece))
            {
                return canAttackTo(move.targetSquare);
            }
            return false;
        }

        @Override
        boolean canReachTo(Square sqTarget) {
            Square sqSrc = this.square;
            if (isSameSquareRankANDFile(this.square, sqTarget)) return false;

            int rankDiff = Math.abs(sqSrc.rank - sqTarget.rank);
            int fileDiff = Math.abs(sqSrc.file - sqTarget.file);

            if ((rankDiff == 2 && fileDiff == 1) || (fileDiff == 2 && rankDiff == 1))
            {
                return true;
            }
            return false;
        }

        @Override
        boolean canAttackTo(Square sqTarget)
        {
            return canReachTo(sqTarget);
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

        public Pawn(int rank, int file)
        {
            initRow = rank;
            // 记录初始位置，通用化STD和XL布局
            icon = "\u265F";
            Name = "Pawn";
            setPieceICONwithImage( (isWhite? ICONIC_FILE_PAWN_WHITE : ICONIC_FILE_PAWN_BLACK) );

            moveTo(board.get(rank, file));
            enPassantInProgress = false;
        }
        @Override
        boolean canReachTo(Square sqTarget)
        {
            Square sqSrc = this.square;
            if (isSameSquareRankANDFile(this.square, sqTarget)) return false;

            int rankDiff = (sqTarget.rank - sqSrc.rank) * ((isWhite) ? 1 : -1);
            int fileDiff = Math.abs(sqTarget.file - sqSrc.file);
            if ((rankDiff == 1) && (fileDiff == 0)) // 1-square-forward move
            {
                return true;
            }
            if ((rankDiff == 2) && (fileDiff == 0)) // 2-squares-forward move
            {
                // 改成与记录的初始位置比较
                return ((sqSrc.rank == initRow) );
                // 第一步的判断，需要改进
                //return ((move.sourceSquare.rank == ((isWhite) ? 1 : 6)) && move.targetSquare.piece == null);
            }
            return false;
        }

        @Override
        boolean canAttackTo(Square sqTarget)
        {
            Square sqSrc = this.square;
            if (isSameSquareRankANDFile(this.square, sqTarget)) return false;

            int rankDiff = (sqTarget.rank - sqSrc.rank) * ((isWhite) ? 1 : -1);
            int fileDiff = Math.abs(sqTarget.file - sqSrc.file);
            boolean result = false;
            if ((rankDiff == 1) && (fileDiff == 1)) // capture
            {
                if (isCapturablePiece(sqTarget.piece))
                {
                    return true;
                }
                else
                {
                    enPassantInProgress = game.isLegalEnPassant(new Move(sqSrc,sqTarget));
                    return enPassantInProgress;
                }
            }
            return false;
        }
        @Override
        boolean isLegal(Board.Move move)
        {
            // Pawn's rules.
            if ( (move.targetSquare.piece == null) && canReachTo(move.targetSquare) ) {
                return true;
            } else if ( (move.targetSquare.piece == null) || (isCapturablePiece(move.targetSquare.piece)) ){
                // 需要考虑吃过路兵！！
                return canAttackTo(move.targetSquare);
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
                // NEED adjust CHECK After Promote. GAME.updateCheckStatus();
            }

            enPassantInProgress = false;
        }

        private void promote(Pawn p)
        {
            char choossen = swingJFramePromotionChoosser();

            int iRank = p.square.rank;
            int iFile = p.square.file;

            switch(choossen)
            {
                case 'q': queens.add(new Queen(iRank, iFile)); break;
                case 'r': rooks.add(new Rook(iRank, iFile)); break;
                case 'b': bishops.add(new Bishop(iRank, iFile)); break;
                case 'n': knights.add(new Knight(iRank, iFile)); break;
            }
        }

        private char swingJFramePromotionChoosser() {
            char pieceSelected = 'q';

            String str = JOptionPane.showInputDialog(null, "Promote to (q, r, b, n): ");
            if (!str.isEmpty()) {
                char aa = str.charAt(0);
                switch (aa){
                    case 'q': pieceSelected = 'q'; break;
                    case 'r': pieceSelected = 'r'; break;
                    case 'b': pieceSelected = 'b'; break;
                    case 'n': pieceSelected = 'n'; break;
                    default: pieceSelected = 'q';
                }
            }
            return pieceSelected;
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
