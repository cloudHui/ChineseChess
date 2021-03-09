package com.sm.chess.searchengine;

import com.sm.chess.NodeLink;
import com.sm.chess.chessmove.MoveNode;
import com.sm.chess.chessmove.MoveNodesSort;
import com.sm.chess.chessparam.ChessParam;
import com.sm.chess.evaluate.EvaluateCompute;
import com.sm.chess.movelist.MoveNodeList;
import com.sm.chess.zobrist.TranspositionTable;

import static com.sm.chess.ChessConstant.*;

public class PrincipalVariation extends SearchEngine {
    private static int[][] FutilityScore = new int[32][80];

    static {
        for (int d = 0; d < 32; d++) {
            for (int k = 0; k < 80; k++) {
                int v = (int) (d * (1.29) * 155) - (k * d * 10);
                FutilityScore[d][k] = v;
            }
        }
    }

    public int values[] = new int[8];
    public int checkNum = 0;
    public int test1Num[] = new int[10];
    public int test1Numpv[] = new int[10];
    public PrincipalVariation(ChessParam chessParam, EvaluateCompute evaluate,
                              TranspositionTable transTable, NodeLink moveHistory) {
        super(chessParam, evaluate, transTable, moveHistory);
    }

    public int searchMove(int alpha, int beta, int depth) {
        MoveNodesSort.trancount1 = 0;
        MoveNodesSort.trancount2 = 0;
        MoveNodesSort.killcount1 = 0;
        MoveNodesSort.killcount2 = 0;
        MoveNodesSort.eatmovecount = 0;
        MoveNodesSort.othercount = 0;
        checkNum = 0;
        test1Num = new int[10];
        test1Numpv = new int[10];
        moveHistory.depth = 0;
        this.setStretchNeedNumByDepth(depth);
        int s = 0;
        MoveNodesSort moveNodeSort = new MoveNodesSort(swapPlay(moveHistory.play), new MoveNodeList(2), killerMove[depth], chessMove, false);
        MoveNodeList moveNodeList = new MoveNodeList(100);
        MoveNode moveNode = null;
        int initScore = 100;
        int currPlay = swapPlay(moveHistory.play);
        while ((moveNode = moveNodeSort.next()) != null && !moveNodeSort.isOver()) {
            chessMove.moveOperate(moveNode);
            if (!chessMove.checked(currPlay)) {
                moveNode.score = initScore--;
                moveNodeList.add(moveNode);
            }
            chessMove.unMoveOperate(moveNode);
        }
        //迭代加深
        for (int d = 4; d <= depth && !isStop; d++) {

            s = this.rootNegaScout(alpha, beta, d, moveNodeList, moveHistory);
            NodeLink nextLink = moveHistory.getNextLink();
            int k = d + 1;
            while (nextLink != null && k >= 0) {
                killerMove[k][1] = killerMove[k][0];
                killerMove[k][0] = nextLink.getMoveNode();
                k--;
                nextLink = nextLink.getNextLink();
            }

        }

        System.out.println(moveHistory.getNextLink());

        return s;
    }

    public int rootNegaScout(int alpha, int beta, int depth, MoveNodeList moveNodeList, NodeLink lastLink) {

        boolean isMove = false;
        int play = swapPlay(lastLink.play);

//		是否被将军
        boolean isChecked = chessMove.checked(play);
        //设置前上步是否将军
        lastLink.chk = isChecked;
        int thisValue, bestValue = -maxScore - 2;
        NodeLink nodeLinkTemp = null, bestNodeLink = null;
        int i = 0, thisAlpha = alpha;
        //排序后的所有着法
        MoveNode moveNode = null;
        while (i < moveNodeList.size) {
            moveNode = this.getSortAfterBestMove(moveNodeList, i++);
            chessMove.moveOperate(moveNode);
            nodeLinkTemp = new NodeLink(play, moveNode, transTable.boardZobrist32, transTable.boardZobrist64);
            nodeLinkTemp.setLastLink(lastLink);
            if (isMove) {
                // 第一次用全窗口以后用极小窗口(第一次为准确值以后都为级小窗口值)
                thisValue = -negaScout(-thisAlpha - 1, -thisAlpha, depth - 1, nodeLinkTemp, false);
                if (thisValue > thisAlpha) {
                    // 重新查找
                    thisValue = -negaScout(-beta, -thisAlpha, depth - 1, nodeLinkTemp, true);
                }
            } else {
                thisValue = -negaScout(-beta, -thisAlpha, depth - 1, nodeLinkTemp, true);
                isMove = true;
            }
            chessMove.unMoveOperate(moveNode);
            moveNode.score = thisValue;
            if (thisValue > bestValue) {
                bestValue = thisValue;
                bestNodeLink = nodeLinkTemp;

                if (thisValue > thisAlpha) {
                    thisAlpha = thisValue;
                }
            }
            if (isStop) {
                break;
            }
        }

        if (isMove) {
            if (bestNodeLink != null) lastLink.setNextLink(bestNodeLink);
            return bestValue;
        } else {
            return -(maxScore - lastLink.depth);
        }
        // 返回最佳值边界

    }


    public int negaScout(int alpha, int beta, int depth, NodeLink lastLink, boolean isPVNode) {

        int play = swapPlay(lastLink.play);
        // 自己帅被吃
        if (chessParam.allChess[chessPlay[play]] == NOTHING) {
            return -(maxScore - lastLink.depth);
        }
        int bestValue = lastLink.depth - maxScore;
        if (bestValue > beta) return bestValue;

        MoveNodeList tranGodMoveNode = new MoveNodeList(2);
        int[] value = new int[1];
        int score = transTable.getTranZabrina(alpha, beta, depth, play, tranGodMoveNode, value);
        if (score != TranspositionTable.FAIL) { // 置换表探测
            return score;
        }
        //是否被将军
        boolean isChecked = chessMove.checked(play);
        //设置将军状态
        lastLink.chk = isChecked;
        //判断长将
        if (isLongChk(lastLink)) {
            return LONGCHECKSCORE;
        }
        if (!lastLink.isNullMove && lastLink.getMoveNode().isEatChess()) {
            //和棋判断
            if (isDraw(lastLink)) {
                return drawScore;
            }
        }
        //将军增加延伸因子
        if (isChecked) {
            depth++;
            checkNum++;
        }

        int entryType = TranspositionTable.hashAlpha;

        if (depth <= stopDepth) {
            //静态搜索
            return this.quiescSearch(alpha, beta, lastLink, isChecked);
        }


        //空着向前(note: 不能两次连续的空裁剪) 并且不能为被将状态 攻击性棋子要>=3
        if (!lastLink.isNullMove && !isChecked && !isPVNode) {
            if (depth >= 2) {
                R = RAdapt(depth);
                int attackChessNum = chessParam.getAttackCaressesNumb(play);
                if (attackChessNum > 0) {
                    int val = 0;
                    NodeLink nodeLinkNULL = new NodeLink(play, true, transTable.boardZobrist32, transTable.boardZobrist64);
                    nodeLinkNULL.setLastLink(lastLink);
                    val = -negaScout(-beta, -beta + 1, depth - R - 1, nodeLinkNULL, false);
                    if (val >= beta) {
                        if (attackChessNum > 2 && depth < 6) {
                            return val;
                        }
                        //做带验证的空向前搜索
                        val = -negaScout(-beta, -beta + 1, depth - R + 1, nodeLinkNULL, false);
                        if (val >= beta) {
                            return val;
                        }
                    }
                }
            }
        }
        //内部迭代加深
        if (depth >= 6 && isPVNode && tranGodMoveNode.get(0) == null) {
            negaScout(alpha, beta, depth - 2, lastLink, isPVNode);
            if (lastLink.getNextLink() != null) {
                tranGodMoveNode.set(0, lastLink.getNextLink().getMoveNode());
                transTable.setRootTranZabrina(play, lastLink.getNextLink().getMoveNode());
            }
        }
        boolean isMove = false;
        int thisValue = alpha, thisAlpha = alpha;
        NodeLink nodeLinkTemp = null, bestNodeLink = null;
        int movesSearched = 0, newDepth = depth, curType = -1;
        //排序后的所有着法
        MoveNodesSort moveNodeSort = new MoveNodesSort(play, tranGodMoveNode, killerMove[depth], chessMove, isChecked);
        MoveNode moveNode = null;
        int movesSearchedCount = isPVNode ? 10 : 5;
        while ((moveNode = moveNodeSort.next()) != null && !moveNodeSort.isOver()) {

            chessMove.moveOperate(moveNode);
            //走完自己被将军
            if (chessMove.checked(play)) {
                //历史表对这样的着法要减分，
                chessMove.unMoveOperate(moveNode);
                continue;
            }
            newDepth = depth;
            if (!isChecked && !isPVNode && newDepth < 6 && !isDanger(play) && movesSearched >= movesSearchedCount && FutilityScore[newDepth][movesSearched] + (isPVNode ? (140 * (newDepth * 1.4)) : 0) + this.roughEvaluate(play) < thisAlpha) {
                chessMove.unMoveOperate(moveNode);
                movesSearched++;
                continue;
            }

            nodeLinkTemp = new NodeLink(play, moveNode, transTable.boardZobrist32, transTable.boardZobrist64);
            nodeLinkTemp.setLastLink(lastLink);

            if (isMove) {
                int kk = 2;
                if (!isChecked && newDepth >= 3 && movesSearched >= movesSearchedCount) {

                    if (movesSearched >= movesSearchedCount && !isDanger(1 - play)) {
                        if (movesSearched >= (movesSearchedCount + (5 + newDepth)) * 2) kk = 4;
                        else if (movesSearched >= (movesSearchedCount + 5 + newDepth)) kk = 3;
                    }
                    thisValue = -negaScout(-thisAlpha - 1, -thisAlpha, newDepth - kk, nodeLinkTemp, false);
                } else {
                    thisValue = thisAlpha + 1;
                }
                if (thisValue > thisAlpha) {
                    if (kk > 1) {
                        // 用极小窗口
                        thisValue = -negaScout(-thisAlpha - 1, -thisAlpha, newDepth - 1, nodeLinkTemp, false);
                    }
                    if (thisValue > thisAlpha) {// 重新查找
                        thisValue = -negaScout(-beta, -thisAlpha, newDepth - 1, nodeLinkTemp, true);
                    }
                }
            } else {
                //第一次用全窗口搜索
                thisValue = -negaScout(-beta, -thisAlpha, newDepth - 1, nodeLinkTemp, true);
                isMove = true;
            }

            chessMove.unMoveOperate(moveNode);
            movesSearched++;
            if (thisValue > bestValue) {
                bestValue = thisValue;
                bestNodeLink = nodeLinkTemp;
                // 超出下边界
                if (thisValue >= beta) {
                    //不为空着法
                    if (!lastLink.isNullMove && moveNodeSort.currType != moveNodeSort.kill1 && moveNodeSort.currType != moveNodeSort.kill2) {
                        killerMove[depth][1] = killerMove[depth][0];
                        killerMove[depth][0] = moveNode;
                    }
                    curType = moveNodeSort.currType;
                    entryType = TranspositionTable.hashBeta;
                    break;
                }
                if (thisValue > thisAlpha) {
                    curType = moveNodeSort.currType;
                    thisAlpha = thisValue;
                    entryType = TranspositionTable.hashPV;
                }

            }
        }

        if (isMove) {
            lastLink.setNextLink(bestNodeLink);
            MoveNode bestMoveNode = null;
            if (bestNodeLink != null) {
                if (entryType != TranspositionTable.hashAlpha) {
                    bestMoveNode = bestNodeLink.getMoveNode();
                    cHistorySort.setCHistoryGOOD(bestMoveNode, depth);
                }
            }
            if (curType > -1) {
                values[curType]++;
            }
            transTable.setTranZabrina(entryType, bestValue, depth, play, bestMoveNode);
            return bestValue;
        } else {
            return -(maxScore - lastLink.depth);
        }
        // 返回最佳值边界

    }

    public MoveNode getSortAfterBestMove(MoveNodeList AllmoveNode, int index) {
        int replaceIndex = index;
        for (int i = index + 1; i < AllmoveNode.size; i++) {
            if (AllmoveNode.get(i).score > AllmoveNode.get(replaceIndex).score) {
                replaceIndex = i;
            }
        }
        if (replaceIndex != index) {
            MoveNode t = AllmoveNode.get(index);
            AllmoveNode.set(index, AllmoveNode.get(replaceIndex));
            AllmoveNode.set(replaceIndex, t);
        }
        return AllmoveNode.get(index);
    }

    public void run() {

    }
}