package chess;

public class Side_XL extends Side {

    public Side_XL(boolean isWhite, Game g) {
        super(isWhite, g);
        if (g.LayoutType == Game.XL_LAYOUT) setupPieces();
    }

    //@Override
    protected void setupPieces() {
        // 初始化存储list
        int totalRows = this.board.totalRows;
        king = new King(((isWhite) ? 0 : (totalRows - 1)), 5);
        queens.add(new Queen(((isWhite) ? 0 : (totalRows - 1)), 4));

        rooks.add(new Rook(((isWhite) ? 0 : (totalRows - 1)), 1));
        rooks.add(new Rook(((isWhite) ? 0 : (totalRows - 1)), 8));

        knights.add(new Knight(((isWhite) ? 0 : (totalRows - 1)), 2));
        knights.add(new Knight(((isWhite) ? 0 : (totalRows - 1)), 7));

        bishops.add(new Bishop(((isWhite) ? 0 : (totalRows - 1)), 3));
        bishops.add(new Bishop(((isWhite) ? 0 : (totalRows - 1)), 6));

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