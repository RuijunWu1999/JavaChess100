package chess;

import java.util.ArrayList;
import java.util.List;

public class Side_XL_Cannons extends Side_XL{

    public Side_XL_Cannons(boolean isWhite, Game g) {
        super(isWhite, g);
        cannons = new ArrayList(2);
        allKindsOfPieces.add(cannons);
        if (g.LayoutType == Game.XL_PLUS_CANNON) setupPieces();
    }

    protected void setupPieces() {
        int totalRows = this.board.totalRows;

        king = new King(((isWhite) ? 0 : (totalRows -1)), 5);
        queens.add(new Queen(((isWhite) ? 0 : (totalRows -1)), 4));

        rooks.add(new Rook(((isWhite) ? 0 : (totalRows -1)), 0));
        rooks.add(new Rook(((isWhite) ? 0 : (totalRows -1)), 9));
        // rooks on end position

        knights.add(new Knight(((isWhite) ? 0 : (totalRows -1)), 2));
        knights.add(new Knight(((isWhite) ? 0 : (totalRows -1)), 7));

        bishops.add(new Bishop(((isWhite) ? 0 : (totalRows -1)), 3));
        bishops.add(new Bishop(((isWhite) ? 0 : (totalRows -1)), 6));

        cannons.add(new Cannon(((isWhite) ? 0 : (totalRows -1)), 1));
        cannons.add(new Cannon(((isWhite) ? 0 : (totalRows -1)), 8));
        //ChineseTrebuchet
        setupPawns();
    }

    private void setupPawns()
    {
        for (int file = 0, pawnRow = (isWhite) ? 2 : (this.board.totalRows-3); file < this.board.totalRows; file++)
        {
            pawns.add(new Pawn(pawnRow, file));
        }
    }

}
