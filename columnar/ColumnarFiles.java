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
                headerFile.insertRecord(columnFiles[i].getBytes());
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
            }
        } catch (HFException | HFBufMgrException | HFDiskMgrException | IOException e) {
            throw new RuntimeException("Error creating header file",e);
        } catch (InvalidSlotNumberException | InvalidTupleSizeException e) {
            throw new RuntimeException("Error fetching record count",e);
        }
    }

    /**
     * Note: This is 0-indexed
     * @param columnNo
     * @return ColumnFile at the given index in which it was created.
     */
    public ColumnFile getColumnFile(int columnNo) {
        return columnFiles[columnNo];
    }

    public int getNumColumns() {
        return numColumns;
    }

    public Heapfile getHeaderFile() {
        return headerFile;
    }

    public Heapfile getTidFile() {
        return tidFile;
    }

    public Heapfile getDeleteFile() {
        return deleteFile;
    }

    public Scan openColumnScan(int column) throws IOException, InvalidTupleSizeException{
        return columnFiles[column].dataFile.openScan();
    }
}
