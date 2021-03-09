package com.sm.chess.movelist;

import com.sm.chess.chessmove.MoveNode;

public class MoveNodeList {
    public MoveNode[] tables;
    public int size;

    public MoveNodeList(int length) {
        tables = new MoveNode[length];
        size = 0;
    }

    public void set(int index, MoveNode moveNode) {
        tables[index] = moveNode;
    }

    public void add(MoveNode moveNode) {
        if (moveNode != null) {
            tables[size++] = moveNode;
        }
    }

    public MoveNode get(int index) {
        if (index < size)
            return tables[index];
        else
            return null;
    }
}
