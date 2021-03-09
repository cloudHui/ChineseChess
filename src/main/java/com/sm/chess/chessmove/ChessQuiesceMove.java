package com.sm.chess.chessmove;

import com.sm.chess.ChessConstant;
import com.sm.chess.chessparam.ChessParam;
import com.sm.chess.evaluate.EvaluateCompute;
import com.sm.chess.history.CHistoryHeuristic;
import com.sm.chess.zobrist.TranspositionTable;

import static com.sm.chess.ChessConstant.NOTHING;
import static com.sm.chess.ChessConstant.chessRoles;

public class ChessQuiesceMove extends ChessMoveAbs {


    public ChessQuiesceMove(ChessParam chessParam, TranspositionTable tranTable, EvaluateCompute evaluateCompute) {
        super(chessParam, tranTable, evaluateCompute);
    }


    /**
     * 记录下所有可走的方式
     *
     * @param srcSite
     * @param destSite
     * @param play
     */
    public void savePlayChess(int srcSite, int destSite, int play) {
        int destChess = board[destSite];
        int srcChess = board[srcSite];
        MoveNode moveNode;
        if (destChess != NOTHING) {
            int destScore = 0;
            int srcScore = 0;
            destScore = EvaluateCompute.chessBaseScore[destChess] + evaluateCompute.chessAttachScore(chessRoles[destChess], destSite);
            if (destScore >= 150) {  //吃子
                //要吃的柜子被对手保护
                srcScore = EvaluateCompute.chessBaseScore[srcChess] + evaluateCompute.chessAttachScore(chessRoles[srcChess], srcSite);
                //按被吃棋子价值排序
                moveNode = new MoveNode(srcSite, destSite, srcChess, destChess, destScore - srcScore);
                goodMoveList.add(moveNode);
                return;
            }
        }
        //历吏表排序
        moveNode = new MoveNode(srcSite, destSite, srcChess, destChess, CHistoryHeuristic.cHistory[ChessConstant.chessRoles_eight[srcChess]][destSite]);
        generalMoveList.add(moveNode); //不吃子
    }
}
















