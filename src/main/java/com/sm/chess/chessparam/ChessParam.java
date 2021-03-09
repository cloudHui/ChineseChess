package com.sm.chess.chessparam;

import com.sm.chess.BitBoard;
import com.sm.chess.ChessConstant;

import static com.sm.chess.ChessConstant.*;

/**
 * @author pengjiu
 * 为防止多线程下，一些所需要的参数变量同步问题
 */
public class ChessParam {
    //每个棋子对应attackAndDefenseCheeses 的下标表
    public static final int[] indexOfAttackAndDefense = new int[]{0,
            0, 1, 1, 0, 0, 0, 1,
            0, 1, 1, 0, 0, 0, 1
    };
    public int[] board;     // 棋盘->棋子
    public int[] allChess; //棋子->棋盘
    public int[] baseScore = new int[2];
    public int[] boardBitRow; //位棋盘  行
    public int[] boardBitCol; //位棋盘  列
    //所有棋子位棋盘
    public BitBoard maskBoardCaresses;
    //各自的位棋盘
    public BitBoard[] maskBoardPersonalCaresses;
    //各自按角色分类的位棋盘[角色]
    public BitBoard[] maskBoardPersonalRoleCaresses;
    private int[] boardRemainChess; //剩余棋子数量
    //[玩家][0攻击棋子数量  1防御棋子数量]
    private int[][] attackAndDefenseCaresses = new int[2][2];

    public ChessParam(int[] board, int[] allChess, int[] baseScore, int[] boardBitRow, int[] boardBitCol, int[] boardRemainChess, BitBoard maskBoardCaresses, BitBoard[] maskBoardPersonalCaresses, BitBoard[] maskBoardPersonalRoleCaresses) {
        this.board = board;
        this.allChess = allChess;
        this.baseScore = baseScore;
        this.boardBitRow = boardBitRow;
        this.boardBitCol = boardBitCol;
        this.boardRemainChess = boardRemainChess;
        this.maskBoardCaresses = maskBoardCaresses;
        this.maskBoardPersonalCaresses = maskBoardPersonalCaresses;
        this.maskBoardPersonalRoleCaresses = maskBoardPersonalRoleCaresses;
    }

    public ChessParam(ChessParam param) {
        this.copyToSelf(param);
    }

    public void copyToSelf(ChessParam param) {
        //棋子copy
        int[] allChessTemp = param.allChess;
        this.allChess = new int[allChessTemp.length];
        for (int i = 0; i < allChessTemp.length; i++) {
            this.allChess[i] = allChessTemp[i];
        }
        //棋盘copy
        int[] boardTemp = param.board;
        this.board = new int[boardTemp.length];
        for (int i = 0; i < boardTemp.length; i++) {
            this.board[i] = boardTemp[i];
        }
        //位棋盘行
        int[] boardBitRowTemp = param.boardBitRow;
        this.boardBitRow = new int[boardBitRowTemp.length];
        for (int i = 0; i < boardBitRowTemp.length; i++) {
            this.boardBitRow[i] = boardBitRowTemp[i];
        }
        //位横向列
        int[] boardBitColTemp = param.boardBitCol;
        this.boardBitCol = new int[boardBitColTemp.length];
        for (int i = 0; i < boardBitColTemp.length; i++) {
            this.boardBitCol[i] = boardBitColTemp[i];
        }
        //棋子数量
        int[] boardRemainChessTemp = param.boardRemainChess;
        this.boardRemainChess = new int[boardRemainChessTemp.length];
        for (int i = 0; i < boardRemainChessTemp.length; i++) {
            this.boardRemainChess[i] = boardRemainChessTemp[i];
        }
        // 攻击性棋子和防御性棋子数量
        int[][] attackAndDefenseChessesTemp = param.attackAndDefenseCaresses;
        for (int i = 0; i < attackAndDefenseChessesTemp.length; i++) {
            for (int j = 0; j < attackAndDefenseChessesTemp[i].length; j++) {
                this.attackAndDefenseCaresses[i][j] = attackAndDefenseChessesTemp[i][j];
            }
        }
        //所有子力的位棋盘
        this.maskBoardCaresses = new BitBoard(param.maskBoardCaresses);

        this.maskBoardPersonalCaresses = new BitBoard[param.maskBoardPersonalCaresses.length];
        //各方子力的位棋盘
        this.maskBoardPersonalCaresses[ChessConstant.REDPLAYSIGN] = new BitBoard(param.maskBoardPersonalCaresses[ChessConstant.REDPLAYSIGN]);
        this.maskBoardPersonalCaresses[ChessConstant.BLACKPLAYSIGN] = new BitBoard(param.maskBoardPersonalCaresses[ChessConstant.BLACKPLAYSIGN]);
        //各方子力按角色分类
        maskBoardPersonalRoleCaresses = new BitBoard[param.maskBoardPersonalRoleCaresses.length];
        for (int i = 0; i < param.maskBoardPersonalRoleCaresses.length; i++) {
            this.maskBoardPersonalRoleCaresses[i] = new BitBoard(param.maskBoardPersonalRoleCaresses[i]);
        }


        //分数
        this.baseScore[ChessConstant.REDPLAYSIGN] = param.baseScore[ChessConstant.REDPLAYSIGN];
        this.baseScore[ChessConstant.BLACKPLAYSIGN] = param.baseScore[ChessConstant.BLACKPLAYSIGN];

    }

    private int getPlayByChessRole(int chessRole) {
        return chessRole > ChessConstant.REDKING ? ChessConstant.BLACKPLAYSIGN : ChessConstant.REDPLAYSIGN;
    }


    public int getCaressesNumb(int play, int chessRole) {
        return boardRemainChess[getRoleIndexByPlayRole(play, chessRole)];
    }

    /**
     * @param chessRole 棋子角色
     *                  减少棋子数量
     */
    public void reduceCaressesNumb(int chessRole) {
        boardRemainChess[chessRole]--;
        attackAndDefenseCaresses[getPlayByChessRole(chessRole)][indexOfAttackAndDefense[chessRole]]--;
    }

    /**
     * @param chessRole 柜子角色
     *                  增加棋子数量
     */
    public void increaseCaressesNumb(int chessRole) {
        boardRemainChess[chessRole]++;
        int play = getPlayByChessRole(chessRole);
        attackAndDefenseCaresses[play][indexOfAttackAndDefense[chessRole]]++;
    }

    /**
     * @return 所有棋子数量
     */
    public int getAllCaressesNumb() {
        int num = 0;
        for (int i : boardRemainChess) {
            num += i;
        }
        return num;
    }

    //所有攻击棋子数量
    public int getAttackCaressesNumb(int play) {
        return attackAndDefenseCaresses[play][0];
    }

    //所有防御棋子数量
    public int getDefenseCaressesNumb(int play) {
        return attackAndDefenseCaresses[play][1];
    }

    public BitBoard getBitBoardByPlayRole(int play, int role) {
        return maskBoardPersonalRoleCaresses[this.getRoleIndexByPlayRole(play, role)];
    }

    public int getRoleIndexByPlayRole(int play, int role) {
        return role = play == ChessConstant.REDPLAYSIGN ? role : (role + 7);
    }


    public void initChessBaseScoreAndNum() {

        for (int i = 16; i < allChess.length; i++) {
            if (allChess[i] != NOTHING) {
                int site = allChess[i];
                int chessRole = chessRoles[board[allChess[i]]];
                int play = i < 32 ? BLACKPLAYSIGN : REDPLAYSIGN;
                increaseCaressesNumb(chessRole);
                maskBoardCaresses.assignXor(MaskChesses[site]);
                maskBoardPersonalCaresses[play].assignXor(MaskChesses[site]);
                maskBoardPersonalRoleCaresses[chessRole].assignXor(MaskChesses[site]);
            }
        }

    }
}
