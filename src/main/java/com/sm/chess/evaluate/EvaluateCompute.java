package com.sm.chess.evaluate;

import com.sm.chess.BitBoard;
import com.sm.chess.ChessConstant;
import com.sm.chess.Tools;
import com.sm.chess.chessparam.ChessParam;

import static com.sm.chess.ChessConstant.*;

public abstract class EvaluateCompute {
    protected final static BitBoard[][] AttackDirection = new BitBoard[2][3];
    protected final static BitBoard[][] DefenseDirection = new BitBoard[2][3];
    protected final static int LEFTSITE = 0, RIGHTSITE = 1, MIDSITE = 2, OTHERSITE = 3;
    //攻击区域分数
    static final int[] attackChessPartitionScore = new int[]{
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 4, 4, 4, 4, 4, 4, 3, 3, 3, 3, 2, 2, 2, 2, 2,
            0, 4, 4, 4, 4, 4, 4, 3, 3, 3, 3, 2, 2, 2, 2, 2
    };
    //防守区域分数
    static final int[] defenseChessPartitionScore = new int[]{
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 4, 4, 4, 4, 4, 4, 3, 3, 3, 3, 2, 2, 2, 2, 2,
            0, 4, 4, 4, 4, 4, 4, 3, 3, 3, 3, 2, 2, 2, 2, 2
    };
    //单士或单象价值下降
    private static final int[] guarAndElephantNumScore = new int[]{0, 2, 3};
    //对手'士'数量决定炮与马的区域价值
    private static final int[] gunNumScoreDependGuard = new int[]{2, 3, 4};
    private static final int[] knightNumScoreDependGuard = new int[]{5, 5, 4};
    public static int KINGSCORE = 3000;
    public static int CHARIOTSCORE = 1300;
    public static int KNIGHTSCORE = 490;
    public static int GUNSCORE = 610;
    public static int ELEPHANTSCORE = 200;
    public static int GUARDSCORE = 200;
    public static int SOLDIERSCORE = 100;
    //棋子基本分数
    public static final int[] chessBaseScore = new int[]{
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            KINGSCORE, CHARIOTSCORE, CHARIOTSCORE, KNIGHTSCORE, KNIGHTSCORE, GUNSCORE, GUNSCORE, ELEPHANTSCORE, ELEPHANTSCORE, GUARDSCORE, GUARDSCORE, SOLDIERSCORE, SOLDIERSCORE, SOLDIERSCORE, SOLDIERSCORE, SOLDIERSCORE,
            KINGSCORE, CHARIOTSCORE, CHARIOTSCORE, KNIGHTSCORE, KNIGHTSCORE, GUNSCORE, GUNSCORE, ELEPHANTSCORE, ELEPHANTSCORE, GUARDSCORE, GUARDSCORE, SOLDIERSCORE, SOLDIERSCORE, SOLDIERSCORE, SOLDIERSCORE, SOLDIERSCORE
    };
    static int[] ChariotPartitionSite = new int[]{ //车
            1, 1, 1, 31, 3, 32, 2, 2, 2
            , 1, 1, 1, 31, 33, 32, 2, 2, 2
            , 1, 1, 1, 31, 33, 32, 2, 2, 2
            , 0, 0, 0, 3, 3, 3, 0, 0, 0
            , 0, 0, 0, 3, 3, 3, 0, 0, 0

            , 0, 0, 0, 6, 6, 6, 0, 0, 0
            , 0, 0, 0, 6, 6, 6, 0, 0, 0
            , 4, 4, 4, 64, 66, 65, 5, 5, 5
            , 4, 4, 4, 64, 66, 65, 5, 5, 5
            , 4, 4, 4, 64, 6, 65, 5, 5, 5
    };
    static int[] KnightPartitionSite = new int[]{ //马
            1, 1, 1, 0, 0, 0, 2, 2, 2
            , 1, 1, 1, 31, 33, 32, 2, 2, 2
            , 1, 1, 1, 31, 33, 32, 2, 2, 2
            , 0, 1, 1, 31, 33, 32, 2, 2, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0

            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 4, 4, 64, 66, 65, 5, 5, 0
            , 4, 4, 4, 64, 66, 65, 5, 5, 5
            , 4, 4, 4, 64, 66, 65, 5, 5, 5
            , 4, 4, 4, 0, 0, 0, 5, 5, 5
    };
    static int[] GunPartitionSite = new int[]{ //炮
            1, 1, 1, 0, 0, 0, 2, 2, 2
            , 1, 1, 1, 0, 0, 0, 2, 2, 2
            , 1, 1, 1, 0, 3, 0, 2, 2, 2
            , 0, 0, 0, 32, 33, 32, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0

            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 64, 66, 65, 0, 0, 0
            , 4, 4, 4, 0, 6, 0, 5, 5, 5
            , 4, 4, 4, 0, 0, 0, 5, 5, 5
            , 4, 4, 4, 0, 0, 0, 5, 5, 5
    };
    static int[] SoldierPartitionSite = new int[]{ //卒
            0, 0, 0, 3, 3, 3, 0, 0, 0
            , 0, 0, 0, 3, 3, 3, 0, 0, 0
            , 0, 0, 0, 3, 3, 3, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0

            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 6, 6, 6, 0, 0, 0
            , 0, 0, 0, 6, 6, 6, 0, 0, 0
            , 0, 0, 0, 6, 6, 6, 0, 0, 0
    };
    static int[] DefensePartitionSite = new int[]{ //象士
            1, 1, 1, 31, 33, 32, 2, 2, 2
            , 1, 1, 1, 31, 33, 32, 2, 2, 2
            , 1, 1, 1, 31, 33, 32, 2, 2, 2
            , 1, 1, 1, 31, 3, 32, 2, 2, 2
            , 1, 1, 1, 31, 3, 32, 2, 2, 2

            , 4, 4, 4, 64, 6, 65, 5, 5, 5
            , 4, 4, 4, 64, 6, 65, 5, 5, 5
            , 4, 4, 4, 64, 66, 65, 5, 5, 5
            , 4, 4, 4, 64, 66, 65, 5, 5, 5
            , 4, 4, 4, 64, 66, 65, 5, 5, 5
    };
    static int[] KingPartitionSite = new int[]{ //王
            0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0

            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0
    };
    static final int[][] chessRolePartitionSite = new int[][]{
            {}, SoldierPartitionSite, DefensePartitionSite, DefensePartitionSite, GunPartitionSite, KnightPartitionSite, ChariotPartitionSite, KingPartitionSite
            , SoldierPartitionSite, DefensePartitionSite, DefensePartitionSite, GunPartitionSite, KnightPartitionSite, ChariotPartitionSite, KingPartitionSite
    };

    static {
        int[] AttackRedLeftSite = new int[]{
                1, 1, 1, 1, 1, 0, 0, 0, 0
                , 1, 1, 1, 1, 1, 0, 0, 0, 0
                , 1, 1, 1, 1, 1, 0, 0, 0, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0

                , 0, 0, 0, 0, 0, 0, 0, 0, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0
        };
        int[] AttackRightSite = new int[]{
                0, 0, 0, 0, 2, 2, 2, 2, 2
                , 0, 0, 0, 0, 2, 2, 2, 2, 2
                , 0, 0, 0, 0, 2, 2, 2, 2, 2
                , 0, 0, 0, 0, 0, 0, 0, 0, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0

                , 0, 0, 0, 0, 0, 0, 0, 0, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0
        };
        int[] AtackRedMidSite = new int[]{
                0, 0, 0, 3, 3, 3, 0, 0, 0
                , 0, 0, 0, 3, 3, 3, 0, 0, 0
                , 0, 0, 0, 3, 3, 3, 0, 0, 0
                , 0, 0, 0, 3, 3, 3, 0, 0, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0

                , 0, 0, 0, 0, 0, 0, 0, 0, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0
                , 0, 0, 0, 0, 0, 0, 0, 0, 0
        };
        int[] AttackBlackLeftSite = Tools.exchange(AttackRedLeftSite);
        int[] AttackBlackRightSite = Tools.exchange(AttackRightSite);
        int[] AttackBlackMidSite = Tools.exchange(AtackRedMidSite);

        BitBoard AttackBlackLeftBit = new BitBoard(AttackBlackLeftSite);
        BitBoard AttackBlackRightBit = new BitBoard(AttackBlackRightSite);
        BitBoard AttackBlackMidBit = new BitBoard(AttackBlackMidSite);
        BitBoard AttackRedLeftBit = new BitBoard(AttackRedLeftSite);
        BitBoard AttackRedRightBit = new BitBoard(AttackRightSite);
        BitBoard AttackRedMidBit = new BitBoard(AtackRedMidSite);

        AttackDirection[REDPLAYSIGN][LEFTSITE] = AttackRedLeftBit;
        AttackDirection[REDPLAYSIGN][RIGHTSITE] = AttackRedRightBit;
        AttackDirection[REDPLAYSIGN][MIDSITE] = AttackRedMidBit;

        AttackDirection[BLACKPLAYSIGN][LEFTSITE] = AttackBlackLeftBit;
        AttackDirection[BLACKPLAYSIGN][RIGHTSITE] = AttackBlackRightBit;
        AttackDirection[BLACKPLAYSIGN][MIDSITE] = AttackBlackMidBit;


        DefenseDirection[BLACKPLAYSIGN][LEFTSITE] = AttackRedLeftBit;
        DefenseDirection[BLACKPLAYSIGN][RIGHTSITE] = AttackRedRightBit;
        DefenseDirection[BLACKPLAYSIGN][MIDSITE] = AttackRedMidBit;

        DefenseDirection[REDPLAYSIGN][LEFTSITE] = AttackBlackLeftBit;
        DefenseDirection[REDPLAYSIGN][RIGHTSITE] = AttackBlackRightBit;
        DefenseDirection[REDPLAYSIGN][MIDSITE] = AttackBlackMidBit;

    }

    protected ChessParam chessParam;
    /**
     * 动态调整攻击区域分数
     */
    int redElephantNum, redGuardNum, blackElephantNum, blackGuardNum, redGun, redKnight, blackGun, blackKnight;

    public static BitBoard getBitBoard(int chessRole, int srcSite, int play, BitBoard bitBoard, ChessParam chessParam) {
        switch (chessRole) {
            case REDCHARIOT:
            case BLACKCHARIOT:
                int row = chessParam.boardBitRow[boardRow[srcSite]];
                int col = chessParam.boardBitCol[boardCol[srcSite]];
                //取出行列能攻击到的位置
                bitBoard = BitBoard.assignXorToNew(ChariotBitBoardOfAttackRow[srcSite][row], ChariotBitBoardOfAttackCol[srcSite][col]);
                bitBoard.assignXor(BitBoard.assignXorToNew(MoveChariotOrGunBitBoardRow[srcSite][row], MoveChariotOrGunBitBoardCol[srcSite][col]));
                break;
            case REDKNIGHT:
            case BLACKKNIGHT:
                //取出被别马腿的位置
                BitBoard legBoard = BitBoard.assignAndToNew(KnightLegBitBoards[srcSite], chessParam.maskBoardCaresses);
                //能走到的位置
                bitBoard = new BitBoard(KnightBitBoardOfAttackLimit[srcSite][legBoard.checkSumOfKnight()]);
                break;
            case REDGUN:
            case BLACKGUN:
                row = chessParam.boardBitRow[boardRow[srcSite]];
                col = chessParam.boardBitCol[boardCol[srcSite]];
                //取出行列能攻击到的位置
                bitBoard = BitBoard.assignXorToNew(GunBitBoardOfAttackRow[srcSite][row], GunBitBoardOfAttackCol[srcSite][col]);
                //炮伪攻击位置
                bitBoard.assignXor(BitBoard.assignXorToNew(GunBitBoardOfFakeAttackRow[srcSite][row], GunBitBoardOfFakeAttackCol[srcSite][col]));
                break;
            case REDELEPHANT:
            case BLACKELEPHANT:
                //取出被塞象眼的位置
                legBoard = BitBoard.assignAndToNew(ElephanLegBitBoards[srcSite], chessParam.maskBoardCaresses);
                bitBoard = new BitBoard(ElephanBitBoardOfAttackLimit[srcSite][legBoard.checkSumOfElephant()]);
                break;
            case REDKING:
            case BLACKKING:
                //将能走到的位置
                bitBoard = new BitBoard(KingBitBoard[srcSite]);
                break;
            case REDGUARD:
            case BLACKGUARD:
                bitBoard = new BitBoard(GuardBitBoard[srcSite]);
                break;
            case REDSOLDIER:
            case BLACKSOLDIER:
                bitBoard = new BitBoard(SoldiersBitBoard[play][srcSite]);
                break;
            default:
                System.out.println("没有这个棋子:" + srcSite);
        }
        return bitBoard;
    }

    public abstract int evaluate(int play);

    public abstract int chessAttachScore(int chessRole, int chessSite);

    protected BitBoard getMainAttackCheesesBitBad(int play) {
        BitBoard bitBoard = new BitBoard(chessParam.getBitBoardByPlayRole(play, CHARIOT));
        bitBoard = BitBoard.assignXorToNew(chessParam.getBitBoardByPlayRole(play, KNIGHT), bitBoard);
        bitBoard = BitBoard.assignXorToNew(chessParam.getBitBoardByPlayRole(play, GUN), bitBoard);
        return bitBoard;
    }

    protected BitBoard getDefenseCheesesBitBad(int play) {
        BitBoard bitBoard = new BitBoard(chessParam.getBitBoardByPlayRole(play, ELEPHANT));
        bitBoard = BitBoard.assignXorToNew(chessParam.getBitBoardByPlayRole(play, GUARD), bitBoard);
        bitBoard = BitBoard.assignXorToNew(chessParam.getBitBoardByPlayRole(play, SOLDIER), bitBoard);
        return bitBoard;
    }

    protected int chessMobility(int chessRole, int srcSite, BitBoard bitBoard) {
        int mobility = 0;
        int row = chessParam.boardBitRow[boardRow[srcSite]];
        int col = chessParam.boardBitCol[boardCol[srcSite]];
        switch (chessRole) {
            //车炮
            case REDCHARIOT:
            case BLACKCHARIOT:
            case REDGUN:
            case BLACKGUN:
                mobility = ChariotAndGunMobilityRow[srcSite][row] + ChariotAndGunMobilityCol[srcSite][col];
                break;
            //马
            case REDKNIGHT:
            case BLACKKNIGHT:
                BitBoard legBoard = BitBoard.assignAndToNew(KnightLegBitBoards[srcSite], chessParam.maskBoardCaresses);
                BitBoard knightAttackSite = KnightBitBoardOfAttackLimit[srcSite][legBoard.checkSumOfKnight()];
                mobility = knightAttackSite.Count() - BitBoard.assignAndToNew(knightAttackSite, bitBoard).Count();
                break;
            case REDKING:
            case BLACKKING:
                BitBoard kingMove = BitBoard.assignAndToNew(KingBitBoard[srcSite], chessParam.maskBoardCaresses);
                kingMove.assignXor(KingBitBoard[srcSite]);
                mobility = kingMove.Count();
                break;
            default:
                System.out.println("没有这个棋子:" + srcSite);
        }
        return mobility;
    }

    protected BitBoard chessAllMove(int chessRole, int srcSite, int play) {
        BitBoard bitBoard = null;
        bitBoard = getBitBoard(chessRole, srcSite, play, bitBoard, chessParam);
        return bitBoard;
    }

    /**
     * @author pengjiu
     * 功能：空头炮
     */
    protected int exposedCannon(int play, int oppkingSite, int row, int col) {
        BitBoard bitBoard = BitBoard.assignXorToNew(ChariotBitBoardOfAttackRow[oppkingSite][row], ChariotBitBoardOfAttackCol[oppkingSite][col]);
        bitBoard.assignAnd(chessParam.getBitBoardByPlayRole(play, ChessConstant.GUN));
        if (!bitBoard.isEmpty()) {
            return bitBoard.MSB(play);
        }
        return -1;
    }

    /**
     * @author pengjiu
     * 功能：沉底炮
     */
    protected int bottomCannon(int play, int oppkingSite, int row, int col) {
        BitBoard bitBoard = BitBoard.assignXorToNew(GunBitBoardOfMoreRestAttackRow[oppkingSite][row], GunBitBoardOfMoreRestAttackCol[oppkingSite][col]);
        bitBoard.assignAnd(chessParam.getBitBoardByPlayRole(play, ChessConstant.GUN));
        if (!bitBoard.isEmpty()) {
            return bitBoard.MSB(play);
        }
        return -1;
    }

    /**
     * @author pengjiu
     * 功能：隔子车
     */
    protected int restChariot(int play, int oppkingSite, int row, int col) {
        BitBoard bitBoard = BitBoard.assignXorToNew(GunBitBoardOfAttackRow[oppkingSite][row], GunBitBoardOfAttackCol[oppkingSite][col]);
        bitBoard.assignAnd(chessParam.getBitBoardByPlayRole(play, ChessConstant.CHARIOT));
        if (!bitBoard.isEmpty()) {
            return bitBoard.MSB(play);
        }
        return -1;
    }

    public void trimPartitionScore(int[][] partitionScore, int[][] attackPartition, int[][] defensePartition) {

        attackPartition[REDPLAYSIGN][LEFTSITE] = partitionScore[REDPLAYSIGN][1];
        attackPartition[REDPLAYSIGN][RIGHTSITE] = partitionScore[REDPLAYSIGN][2];
        attackPartition[REDPLAYSIGN][MIDSITE] = partitionScore[REDPLAYSIGN][3];
        defensePartition[REDPLAYSIGN][LEFTSITE] = partitionScore[REDPLAYSIGN][4];
        defensePartition[REDPLAYSIGN][RIGHTSITE] = partitionScore[REDPLAYSIGN][5];
        defensePartition[REDPLAYSIGN][MIDSITE] = partitionScore[REDPLAYSIGN][6];


        attackPartition[BLACKPLAYSIGN][LEFTSITE] = partitionScore[BLACKPLAYSIGN][4];
        attackPartition[BLACKPLAYSIGN][RIGHTSITE] = partitionScore[BLACKPLAYSIGN][5];
        attackPartition[BLACKPLAYSIGN][MIDSITE] = partitionScore[BLACKPLAYSIGN][6];
        defensePartition[BLACKPLAYSIGN][LEFTSITE] = partitionScore[BLACKPLAYSIGN][1];
        defensePartition[BLACKPLAYSIGN][RIGHTSITE] = partitionScore[BLACKPLAYSIGN][2];
        defensePartition[BLACKPLAYSIGN][MIDSITE] = partitionScore[BLACKPLAYSIGN][3];

    }

    public void dynamicCMPChessPartitionScore() {
        redGuardNum = chessParam.getCaressesNumb(REDPLAYSIGN, ChessConstant.GUARD);
        redElephantNum = chessParam.getCaressesNumb(REDPLAYSIGN, ChessConstant.ELEPHANT);
        blackGuardNum = chessParam.getCaressesNumb(BLACKPLAYSIGN, ChessConstant.GUARD);
        blackElephantNum = chessParam.getCaressesNumb(BLACKPLAYSIGN, ChessConstant.ELEPHANT);
        //单士或单象价值下降
        defenseChessPartitionScore[23] = defenseChessPartitionScore[24] = guarAndElephantNumScore[blackElephantNum];
        defenseChessPartitionScore[25] = defenseChessPartitionScore[26] = guarAndElephantNumScore[blackGuardNum];

        defenseChessPartitionScore[39] = defenseChessPartitionScore[40] = guarAndElephantNumScore[redElephantNum];
        defenseChessPartitionScore[41] = defenseChessPartitionScore[42] = guarAndElephantNumScore[redGuardNum];

        //动态改变炮与马的攻击价值
        attackChessPartitionScore[21] = attackChessPartitionScore[22] = gunNumScoreDependGuard[redGuardNum];
        attackChessPartitionScore[19] = attackChessPartitionScore[20] = knightNumScoreDependGuard[redGuardNum];
        attackChessPartitionScore[37] = attackChessPartitionScore[38] = gunNumScoreDependGuard[blackGuardNum];
        attackChessPartitionScore[35] = attackChessPartitionScore[36] = knightNumScoreDependGuard[blackGuardNum];
    }

    /**
     * 区域分数
     *
     * @param site
     * @param chess
     * @param partitionScore
     */
    public void compPartitionScore(int play, int site, int chess, int[] partitionScore) {
        int parSiteTemp = chessRolePartitionSite[chessRoles[chess]][site];
        if (play == REDPLAYSIGN) { //红
            deal(chess, partitionScore, parSiteTemp, attackChessPartitionScore, defenseChessPartitionScore);
        } else { //黑
            deal(chess, partitionScore, parSiteTemp, defenseChessPartitionScore, attackChessPartitionScore);
        }

    }

    private void deal(int chess, int[] partitionScore, int parSiteTemp, int[] defenseChessPartitionScore, int[] attackChessPartitionScore) {
        switch (parSiteTemp) {
            case 1:
                partitionScore[parSiteTemp] += defenseChessPartitionScore[chess];
                break;
            case 2:
                partitionScore[parSiteTemp] += defenseChessPartitionScore[chess];
                break;
            case 3:
                partitionScore[parSiteTemp] += defenseChessPartitionScore[chess];
                break;
            case 31:
                partitionScore[3] += defenseChessPartitionScore[chess];
                partitionScore[1] += defenseChessPartitionScore[chess];
                break;
            case 32:
                partitionScore[3] += defenseChessPartitionScore[chess];
                partitionScore[2] += defenseChessPartitionScore[chess];
                break;
            case 33:
                partitionScore[3] += defenseChessPartitionScore[chess];
                partitionScore[2] += defenseChessPartitionScore[chess];
                partitionScore[1] += defenseChessPartitionScore[chess];
                break;
            case 4:
                partitionScore[parSiteTemp] += attackChessPartitionScore[chess];
                break;
            case 5:
                partitionScore[parSiteTemp] += attackChessPartitionScore[chess];
                break;
            case 6:
                partitionScore[parSiteTemp] += attackChessPartitionScore[chess];
                break;
            case 64:
                partitionScore[6] += attackChessPartitionScore[chess];
                partitionScore[4] += attackChessPartitionScore[chess];
                break;
            case 65:
                partitionScore[6] += attackChessPartitionScore[chess];
                partitionScore[5] += attackChessPartitionScore[chess];
                break;
            case 66:
                partitionScore[6] += attackChessPartitionScore[chess];
                partitionScore[5] += attackChessPartitionScore[chess];
                partitionScore[4] += attackChessPartitionScore[chess];
                break;
        }
    }

}