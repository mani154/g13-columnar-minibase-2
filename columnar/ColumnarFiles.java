package columnar;

import global.AttrType;
import global.RID;
import heap.*;

import java.io.IOException;

public class ColumnarFiles {

    private final String name;

    private final Heapfile tidFile;

    private final Heapfile deleteFile;

    private final Heapfile headerFile;

    private final ColumnFile[] columnFiles;
    private final  int numColumns;

    public ColumnarFiles(
            String name,
            String[] columnNames,
            AttrType[] attrTypes,
            int numColumns
    ) {
        this.name = name;
        this.numColumns = numColumns;
        try {
            headerFile = new Heapfile(name + ".hdr");
            deleteFile = new Heapfile(name+".del");
            tidFile = new Heapfile(name+".tid");
        } catch (HFException | HFBufMgrException | HFDiskMgrException | IOException e) {
            throw new RuntimeException("Error creating header file",e);
        }
        columnFiles = new ColumnFile[numColumns];

        for(int i = 0 ; i< numColumns;i++) {
            columnFiles[i] = new ColumnFile(name+"."+i, columnNames[i], attrTypes[i]);
            try {
                RID rid = headerFile.insertRecord(columnFiles[i].getBytes());
                columnFiles[i].setRid(rid);
            } catch (InvalidSlotNumberException | InvalidTupleSizeException | SpaceNotAvailableException | HFException |
                     HFBufMgrException | HFDiskMgrException | IOException e) {
                throw new RuntimeException("Error inserting records",e);
            }
        }
    }

    public ColumnarFiles(String name) {
        this.name = name;
        try {
            headerFile = new Heapfile(name + ".hdr");
            deleteFile = new Heapfile(name+".del");
            tidFile = new Heapfile(name+".tid");

            numColumns = headerFile.getRecCnt();
            columnFiles = new ColumnFile[numColumns];

            Scan scan = headerFile.openScan();
            RID rid = new RID();

            for (int i = 0; i < numColumns; i++) {
                columnFiles[i] = new ColumnFile(scan.getNext(rid).getTupleByteArray());
                columnFiles[i].setRid(rid);
            }
        } catch (HFException | HFBufMgrException | HFDiskMgrException | IOException e) {
            throw new RuntimeException("Error creating header file",e);
        } catch (InvalidSlotNumberException | InvalidTupleSizeException e) {
            throw new RuntimeException("Error fetching record count",e);
        }
    }

    public boolean createBtreeIndex(int columnNo) {
        try {
            columnFiles[columnNo].setHasBtree(true);
            headerFile.updateRecord(columnFiles[columnNo].getRid(), columnFiles[columnNo].getTuple());
            return ColumnarFileUtils.createBtreeIndex(columnFiles[columnNo], tidFile);
        } catch (Exception e) {
            throw new RuntimeException("Error updating headerfile record",e);
        }

    }

    public ColumnFile getColumnFile(int columnNo) {
        return columnFiles[columnNo];
    }


    public Heapfile getTidFile() {
        return tidFile;
    }

    public Heapfile getDeleteFile() {
        return deleteFile;
    }
}
