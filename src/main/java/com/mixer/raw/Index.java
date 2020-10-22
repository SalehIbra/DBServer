package com.mixer.raw;

import java.util.HashMap;
import java.util.Set;

/**
 * Class to store indexed fields. When we search records, then we
 * search in this index object, and we can get the position of the records/rows in the database
 * Index object is final and singleton.
 */
public class Index {
    private static Index index;
    // row number, byte position
    private final HashMap<Long, Long> rowIndex;
    //for update by row, String name long rownumber
    private final HashMap<String,Long> nameIndex;
    private long totalRowNumber = 0;

    private Index() {
        this.rowIndex = new HashMap<>();
        this.nameIndex = new HashMap<>();
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

    public void remove(long row) {
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

    public void addNameToIndex(final String name,long rowIndex){
        this.nameIndex.put(name,rowIndex);
    }
    public boolean hasNameInIndex(final String name){
        return this.nameIndex.containsKey(name);
    }
    public long getRowNumberByName(final String name){
        return this.nameIndex.getOrDefault(name,-1L);
    }
    public Set<String> getNames(){
        return this.nameIndex.keySet();
    }

    public void clear(){
        this.totalRowNumber = 0;
        this.rowIndex.clear();
        this.nameIndex.clear();
    }

}
