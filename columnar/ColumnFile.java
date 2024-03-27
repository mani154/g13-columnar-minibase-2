package columnar;

import global.AttrType;
import heap.*;

import java.io.IOException;

public class ColumnFile {

    AttrType[] headerRecordAttrType = new AttrType[] {
            new AttrType(AttrType.attrString),
            new AttrType(AttrType.attrString),
            new AttrType(AttrType.attrInteger),
            new AttrType(AttrType.attrInteger),
            new AttrType(AttrType.attrInteger)
    };

    private final String name;

    private final Heapfile dataFile;

    private final String columnName;

    private final AttrType attrType;

    private boolean hasBtree = false;

    private boolean hasBitmap = false;


    public ColumnFile(String columnFileName, String columnName, AttrType attrType) {
        this.name = columnFileName;
        this.columnName = columnName;
        this.attrType = attrType;

        try {
            dataFile = new Heapfile(name);
        } catch (HFException | HFBufMgrException | HFDiskMgrException | IOException e) {
            throw new RuntimeException("error creating data file",e);
        }
    }

    public ColumnFile(byte[] data) {
        try {
            Tuple tuple = new Tuple(data);
            name = tuple.getStrFld(1);
            columnName = tuple.getStrFld(2);
            attrType = new AttrType(tuple.getIntFld(3));
            hasBtree = tuple.getIntFld(4) == 1;
            hasBitmap = tuple.getIntFld(5) == 1;

            try {
                dataFile = new Heapfile(name);
            } catch (HFException | HFBufMgrException | HFDiskMgrException | IOException e) {
                throw new RuntimeException("error creating data file",e);
            }

        } catch (IOException e) {
            throw new RuntimeException("Error creating tuple",e);
        } catch (FieldNumberOutOfBoundException e) {
            throw new RuntimeException("Error getting field",e);
        }
    }

    public Heapfile getFile() {
       return dataFile;
    }

    public boolean hasBtree() {
        return hasBtree;
    }

    public void setHasBtree(boolean hasBtree) {
        this.hasBtree = hasBtree;
    }

    public boolean hasBitmap() {
        return hasBitmap;
    }

    public void setHasBitmap(boolean hasBitmap) {
        this.hasBitmap = hasBitmap;
    }

    public String getName() {
        return name;
    }

    public String getColumnName() {
        return columnName;
    }

    public AttrType getAttrType() {
        return attrType;
    }

    public byte[] getBytes() {

        short[] strSizes = new short[] {
                (short) name.length(),
                (short) columnName.length()
        };
        Tuple tuple = new Tuple();
        try {
            tuple.setHdr((short) 5,headerRecordAttrType,strSizes);
        } catch (IOException | InvalidTypeException | InvalidTupleSizeException e) {
            throw new RuntimeException("Error setting tuple header",e);
        }

        try {
            tuple.setStrFld(1, name);
            tuple.setStrFld(2,columnName);
            tuple.setIntFld(3,attrType.attrType);
            tuple.setIntFld(4,hasBtree ? 1 : 0);
            tuple.setIntFld(5, hasBitmap? 1 : 0);
        } catch (IOException | FieldNumberOutOfBoundException e) {
            throw new RuntimeException("Error setting field",e);
        }

        return tuple.getTupleByteArray();
    }
}
