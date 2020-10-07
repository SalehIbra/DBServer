package com.mixer.raw;

import java.util.HashMap;

/**
 * Class to store indexed fields. When we search records, then we
 * search in this index object, and we can get the position of the records/rows in the database
 * Index object is final and singleton.
 */
public class Index {
    private static Index index;
    // row number, byte position
    private HashMap<Long, Long> rowIndex;
    private long totalRowNumber = 0;

    private Index() {
        this.rowIndex = new HashMap<>();
    }

    public static Index getInstance() {
        if (index == null) {
            index = new Index();
        }
        return index;
    }

    public void add(long bytePosition) {
        this.rowIndex.put(totalRowNumber, bytePosition);
        this.totalRowNumber++;
    }

    public void remove(int row) {
        this.rowIndex.remove(row);
        this.totalRowNumber--;
    }

    public long getTotalNumberOfRows() {
        return this.totalRowNumber;
    }

    public long getBytePosition(long rowNumber) {
    /*    if(!this.rowIndex.containsKey(rowNumber)){
            return -1;
        }
        return this.rowIndex.get(rowNumber);*/

        return this.rowIndex.getOrDefault(rowNumber, -1L);
    }

    public void clear(){
        this.totalRowNumber = 0;
        this.rowIndex.clear();
    }

}
