package bitmap;

import columnar.ValueFloat;
import columnar.ValueInt;
import columnar.ValueString;
import global.AttrType;
import global.ValueClass;
import heap.FieldNumberOutOfBoundException;
import heap.Tuple;

import java.io.IOException;

public class BitMapFileMeta {
    int columnNo;
    String bitMapFileName;
    String value;

    public BitMapFileMeta(int columnNo, String bitMapFileName, String value) {
        this.columnNo = columnNo;
        this.bitMapFileName = bitMapFileName;
        this.value = value;
    }

    public BitMapFileMeta(byte[] data) throws FieldNumberOutOfBoundException, IOException {
        Tuple tuple = new Tuple(data);
        this.columnNo = tuple.getIntFld(1);
        this.bitMapFileName = tuple.getStrFld(2);
        this.value = tuple.getStrFld(3);
    }

    public String getName() {
        return bitMapFileName;
    }

    public ValueClass getValue(int attrType) {
        switch (attrType) {
            case AttrType.attrString: {
                return new ValueString(value);
            }

            case AttrType.attrInteger: {
                return new ValueInt(Integer.parseInt(value));
            }

            case AttrType.attrReal: {
                return new ValueFloat(Float.parseFloat(value));
            }

            default: {
                throw  new RuntimeException("Attr not found");
            }
        }
    }

    public Tuple convertToTuple() {
        Tuple tuple = new Tuple();
        try {
            tuple.setHdr((short)3, new AttrType[] {
                new AttrType(AttrType.attrInteger), // Column No
                new AttrType(AttrType.attrString), // bitmapfileName
                new AttrType(AttrType.attrString), // value
            }, new short[]{(short)bitMapFileName.length(), (short)value.length()});
            tuple.setIntFld(1, columnNo);
            tuple.setStrFld(2, bitMapFileName);
            tuple.setStrFld(3, value);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return tuple;
    }
}