package com.sm.chess.zobrist;

import com.sm.chess.chessmove.MoveNode;

public class HashItem {
    //校验和
    public long checkSum;
    //hash类型  上边界  下边界  精确值
    public int entry_type;
    //值
    public int value;
    //步数
    public int depth;

    public MoveNode moveNode;
    //是否过期数据
    public boolean isExists = true;

    public HashItem() {
    }
}
