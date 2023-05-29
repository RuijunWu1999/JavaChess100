package chess;

import java.util.ArrayList;
import java.util.List;

public class Side_XL_Cannons_4m8c extends Side_XL{

    public Side_XL_Cannons_4m8c(boolean isWhite, Game g) {
        super(isWhite, g);
        cannons = new ArrayList(2);
        allKindsOfPieces.add(cannons);
        if (g.LayoutType == Game.XL_PLUS_CANNON_4m8c) setupPieces();
    }

    protected void setupPieces() {
        int totalRows = this.board.totalRows;

        king = new King(((isWhite) ? 0 : (totalRows -1)), 5);
        queens.add(new Queen(((isWhite) ? 0 : (totalRows -1)), 4));

        rooks.add(new Rook(((isWhite) ? 0 : (totalRows -1)), 1));
        rooks.add(new Rook(((isWhite) ? 0 : (totalRows -1)), 8));

        knights.add(new Knight(((isWhite) ? 0 : (totalRows -1)), 2));
        knights.add(new Knight(((isWhite) ? 0 : (totalRows -1)), 7));

        bishops.add(new Bishop(((isWhite) ? 0 : (totalRows -1)), 3));
        bishops.add(new Bishop(((isWhite) ? 0 : (totalRows -1)), 6));

        cannons.add(new Cannon_4m8c(((isWhite) ? 0 : (totalRows -1)), 0));
        cannons.add(new Cannon_4m8c(((isWhite) ? 0 : (totalRows -1)), 9));
        //ChineseTrebuchet on end position
        setupPawns();
    }

    private void setupPawns()
    {
        for (int file = 0, pawnRow = (isWhite) ? 2 : (this.board.totalRows-3); file < this.board.totalRows; file++)
        {
            pawns.add(new Pawn(pawnRow, file));
        }
    }

    public class Cannon_4m8c extends Cannon
    {

        public Cannon_4m8c(int rank, int file) {
            super(rank, file);
        }

        @Override
        public boolean canReachTo(Square sqTarget)
        {
            // Cannon's rule for move.
            Square sq0 = this.square;
            if (isSameSquareRankANDFile(sqTarget, sq0)) return false;
            return directlyReachableOnRankOrFileTo(sqTarget, sq0);
        }

        private boolean isAllignedDiagnal(Square sqTarget, Square sqSrc)
        {
            int rankDiff = Math.abs(sqSrc.rank - sqTarget.rank);
            int fileDiff = Math.abs(sqSrc.file - sqTarget.file);
            if ( (rankDiff*fileDiff == 0) || (rankDiff != fileDiff) ) return false;
            return true;
        }
        private boolean directlyReachableDianal(Square sqTarget, Square sqSrc) {
            if ( !isAllignedDiagnal(sqTarget,sqSrc) ) return false;
            else {
                int piecesEnRoute = sqSrc.getPiecesEnRoute(sqTarget);
                // MOVE to Square CLASS.
                if (piecesEnRoute > 0) return false;
                else return true;
                // Yes, can go to the unoccupied Square.
            }
        }

        @Override
        public boolean canAttackTo(Square sqTarget)
        {
            // Cannon's rule for attack.
            boolean bl1 = !isSameSquareRankANDFile(this.square, sqTarget);
            boolean bl2 = isOnSameRankOrFile(this.square, sqTarget);
            boolean bl3 = isAllignedDiagnal(this.square, sqTarget);
            if ( bl1 && (bl2 || bl3)  )
            {
                return (this.square.getPiecesEnRoute(sqTarget) == 1);
            }
            return false;
        }

    }
}

