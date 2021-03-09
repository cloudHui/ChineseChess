package com.sm.chess.chessmove;

import com.sm.chess.BitBoard;
import com.sm.chess.ChessConstant;
import com.sm.chess.chessparam.ChessParam;
import com.sm.chess.evaluate.EvaluateCompute;
import com.sm.chess.history.CHistoryHeuristic;
import com.sm.chess.zobrist.TranspositionTable;

import static com.sm.chess.ChessConstant.*;

public class ChessMovePlay extends ChessMoveAbs {


    public ChessMovePlay(ChessParam chessParam, TranspositionTable tranTable, EvaluateCompute evaluateCompute) {
        super(chessParam, tranTable, evaluateCompute);
    }

    private ChessMovePlay() {

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
/*		if(!isOpponentCheck(destChess,play)){
			System.out.println("有问题。。。。。。");
			//攻击的棋子为已方棋子
			return;
		}*/
        for (int i = 0; i < repeatMoveList.size; i++) {
            MoveNode repeatMoveNode = repeatMoveList.get(i);
            if (repeatMoveNode != null && repeatMoveNode.srcSite == srcSite && repeatMoveNode.destSite == destSite) {
                repeatMoveList.set(i, null);
                return;
            }
        }

        int srcChess = board[srcSite];

        boolean isOppProtect = !BitBoard.assignAndToNew(oppAttackSite, MaskChesses[destSite]).isEmpty();
        if (destChess != NOTHING) {

            int srcScore = 0;
            //要吃的柜子被对手保护
            if (isOppProtect) {
                srcScore = EvaluateCompute.chessBaseScore[srcChess] +
                        evaluateCompute.chessAttachScore(chessRoles[srcChess], srcSite);
            } else {
                srcScore = -500;
            }
            int destScore = EvaluateCompute.chessBaseScore[destChess] +
                    evaluateCompute.chessAttachScore(chessRoles[destChess], destSite);
            if (destScore >= srcScore) {  //吃子
                //按被吃棋子价值排序
                MoveNode moveNode = new MoveNode(srcSite, destSite, srcChess, destChess, destScore - srcScore);
                moveNode.isOppProtect = isOppProtect;
                goodMoveList.add(moveNode);
                return;
            }
        }
        MoveNode moveNode = new MoveNode(srcSite, destSite, srcChess, destChess,
                CHistoryHeuristic.cHistory[ChessConstant.chessRoles_eight[srcChess]][destSite] +
                        (isOppProtect ? 0 : 256));
        moveNode.isOppProtect = isOppProtect;
        generalMoveList.add(moveNode); //不吃子
    }

}









