package com.sm.chess.chessmove;

import com.sm.chess.ChessConstant;

import static com.sm.chess.ChessBoardMain.chessName;
import static com.sm.chess.ChessConstant.boardCol;
import static com.sm.chess.ChessConstant.boardRow;

public class MoveNode implements java.io.Serializable {
    public int destChess;
    public int srcChess;
    public int srcSite;
    public int destSite;
    public int score;
    public boolean isOppProtect = false;

    public MoveNode() {

    }

    public MoveNode(int srcSite, int destSite, int srcChess, int destChess, int score) {
        this.srcSite = srcSite;
        this.destSite = destSite;
        this.destChess = destChess;
        this.srcChess = srcChess;
        this.score = score;
    }


    //是否有吃子
    public boolean isEatChess() {
        return destChess != ChessConstant.NOTHING;
    }

    public String toString() {
        return "\t原位置:" + boardRow[srcSite] + "行" + boardCol[srcSite] + "列  原棋子：" +
                chessName[srcChess] + "\t目标位置：" + boardRow[destSite] + "行  " + boardCol[destSite]
                + "列   目标棋子：" + (destChess != ChessConstant.NOTHING ? chessName[destChess] : "无 \t");

    }

    public boolean equals(MoveNode moveNode) {
        return moveNode != null
                &&
                (moveNode == this || (this.srcSite == moveNode.srcSite && this.destSite == moveNode.destSite));
    }
}
