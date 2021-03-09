package com.sm.chess.history;

import com.sm.chess.ChessConstant;
import com.sm.chess.chessmove.MoveNode;

public class CHistoryHeuristic {
    public static int[][] cHistory = new int[ChessConstant.chessRoles_eight.length][256];

    public void setCHistoryGOOD(MoveNode moveNode, int depth) {
        if (moveNode != null) {
            cHistory[ChessConstant.chessRoles_eight[moveNode.srcChess]][moveNode.destSite] += 2 << depth;
        }
    }
}
