package com.sm.chess;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sm.chess.ChessConstant.*;

public class Tools {
    public static int[] arrayCopy(int[] srcArray) {
        int length = srcArray.length;
        int[] toArray = new int[length];
        System.arraycopy(srcArray, 0, toArray, 0, length);
        return toArray;
    }

    public static String[] fenToFENArray(String fen) {
        String[] fenArray = new String[8];
        Pattern p = Pattern.compile("([^\\s])++");
        Matcher m = p.matcher(fen);
        int i = 0;
        while (m.find()) {
            fenArray[i++] = m.group(0);
        }
        return fenArray;
    }

    public static int[] parseFEN(String str) {

        Map<Object, Integer> m = new HashMap<>();
        {
            m.put('k', 16); //王
            m.put('r', 17); //车
            m.put('n', 19); //马
            m.put('b', 23); //象
            m.put('a', 25); //士
            m.put('c', 21); //炮
            m.put('p', 27); //卒
            //=============================================================
            m.put('K', 32); //王
            m.put('R', 33); //车
            m.put('N', 35); //马
            m.put('B', 39); //象
            m.put('A', 41); //士
            m.put('C', 37); //炮
            m.put('P', 43); //卒
        }

        int[] board = new int[90];
        int boardIndex = 0;

        String[] libArr = new String[]{str};
        int i = 0;
        while (libArr[0].length() > i) {
            if (libArr[0].charAt(i) >= 'a' && libArr[0].charAt(i) <= 'z') {
                int chess = m.get(libArr[0].charAt(i));
                m.put(libArr[0].charAt(i), chess + 1);
                board[boardIndex] = chess;
                boardIndex++;
            } else if (libArr[0].charAt(i) >= 'A' && libArr[0].charAt(i) <= 'Z') {
                int chess = m.get(libArr[0].charAt(i));
                m.put(libArr[0].charAt(i), chess + 1);
                board[boardIndex] = chess;
                boardIndex++;
            } else if (libArr[0].charAt(i) >= '0' && libArr[0].charAt(i) <= '9') {
                boardIndex += Integer.valueOf(libArr[0].charAt(i) + "");
            }
            i++;
        }
        return board;
    }

    public static int[] exchange(int[] srcArray) {
        int[] temp = arrayCopy(srcArray);
        for (int srcSite = 48; srcSite < 8 * 16; srcSite++) {
            int row = srcSite / 16;
            int col = srcSite % 16;
            int destSite = (15 - row) * 16 + col;
            int srcSiteTrue = boardMap[srcSite];
            int destSiteTrue = boardMap[destSite];
            int t = temp[srcSiteTrue];
            temp[srcSiteTrue] = temp[destSiteTrue];
            temp[destSiteTrue] = t;
        }
        return temp;
    }


    public static boolean isBoardTo255(int site) {
        int row = site / 16;
        int col = site % 16;
        if (row >= 3 && row <= 12 && col >= 3 && col <= 11) {
            return true;
        }
        return false;
    }

    public static void saveFEN(int[] board, NodeLink backMove) {


        String[] sFen = new String[]{"", "P", "A", "B", "C", "N", "R", "K", "p", "a", "b", "c", "n", "r", "k"};


        int t = 0;
        StringBuilder sb = null;
        for (int i = 0; i < board.length; i++) {
            int row = boardRow[i];
            int col = boardCol[i];
            if (col == 0) {
                if (sb == null) {
                    sb = new StringBuilder("0000 ");
                } else {
                    if (t != 0) {
                        sb.append(t);
                        t = 0;
                    }
                    sb.append("/");
                }
            }
            if (board[i] != ChessConstant.NOTHING) {
                if (t != 0) {
                    sb.append(t);
                }
                int role = chessRoles[board[i]];
                sb.append(sFen[role]);
                t = 0;
            } else {
                t++;
            }
        }
        java.io.BufferedOutputStream buff = null;
        ObjectOutputStream out = null;
        try {
            buff = new java.io.BufferedOutputStream(new java.io.FileOutputStream("chess.txt"));
            buff.write(sb.toString().getBytes());
            buff.flush();

            out = new ObjectOutputStream(new FileOutputStream("moves.dat"));
            out.writeObject(backMove);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (buff != null) {
                    buff.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
