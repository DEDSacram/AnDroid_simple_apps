package com.example.akvarko;

import java.lang.reflect.Array;

public class Player {
    int[][] Grid = ((int[][]) Array.newInstance(int.class, new int[]{4, 4}));
    String name;
    int score;

    public Player(String NAME) {
        this.name = NAME;
        for (int row = 0; row < this.Grid.length; row++) {
            int col = 0;
            while (true) {
                int[][] iArr = this.Grid;
                if (col >= iArr[row].length) {
                    break;
                }
                iArr[row][col] = 22;
                col++;
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x001b, code lost:
        r0 = r0 + 1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean Check() {
        /*
            r4 = this;
            r0 = 0
        L_0x0001:
            int[][] r1 = r4.Grid
            int r1 = r1.length
            if (r0 >= r1) goto L_0x001e
            r1 = 0
        L_0x0007:
            int[][] r2 = r4.Grid
            r3 = r2[r0]
            int r3 = r3.length
            if (r1 >= r3) goto L_0x001b
            r2 = r2[r0]
            r2 = r2[r1]
            r3 = 22
            if (r2 != r3) goto L_0x0018
            r2 = 0
            return r2
        L_0x0018:
            int r1 = r1 + 1
            goto L_0x0007
        L_0x001b:
            int r0 = r0 + 1
            goto L_0x0001
        L_0x001e:
            r0 = 1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.example.akvarko.Player.Check():boolean");
    }
}
