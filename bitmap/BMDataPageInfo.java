package bitmap;

import java.io.IOException;

import global.AttrType;
import global.PageId;
import heap.FieldNumberOutOfBoundException;
import heap.InvalidTupleSizeException;
import heap.InvalidTypeException;
import heap.Tuple;

public class BMDataPageInfo {
    public int recCount;
    public int isFull;
    public PageId pageId;

    public BMDataPageInfo(PageId pageId, int isFull, int recCount) {
        this.recCount = 0;
        this.isFull = 0;
        this.pageId = pageId;
    }

    public BMDataPageInfo(Tuple oTuple) throws IOException, FieldNumberOutOfBoundException {

        Tuple tuple = new Tuple(oTuple.getTupleByteArray());
        this.recCount = tuple.getIntFld(1);
        this.isFull = tuple.getIntFld(2);
        this.pageId = new PageId(tuple.getIntFld(3));
    }

    public Tuple convertToTuple() throws IOException {
        Tuple atuple = new Tuple();

        try {
            atuple.setHdr((short) 3, new AttrType[] {
                    new AttrType(AttrType.attrInteger),
                    new AttrType(AttrType.attrInteger),
                    new AttrType(AttrType.attrInteger),
            }, new short[0]);
            atuple.setIntFld(1, recCount);
            atuple.setIntFld(2, isFull);
            atuple.setIntFld(3, pageId.pid);
        } catch (InvalidTypeException e) {
            throw new RuntimeException(e);
        } catch (InvalidTupleSizeException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (FieldNumberOutOfBoundException e) {
            throw new RuntimeException(e);
        }
        return atuple;
    }
}