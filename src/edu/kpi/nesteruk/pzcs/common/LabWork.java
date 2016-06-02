package edu.kpi.nesteruk.pzcs.common;

/**
 * Created by Yurii on 2016-06-02.
 */
public enum LabWork {

    LAB_2(2),
    LAB_3(3),
    LAB_4(4),
    LAB_6(6),
    LAB_7(7)
    ;

    private final int number;

    LabWork(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

}
