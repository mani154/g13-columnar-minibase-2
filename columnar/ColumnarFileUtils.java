package columnar;

import btree.*;
import global.KeyClassFactory;
import global.RID;
import heap.Heapfile;
import heap.InvalidTupleSizeException;
import heap.Scan;
import heap.Tuple;

import java.io.IOException;

public class ColumnarFileUtils {

    public static boolean createBtreeIndex(ColumnFile columnFile, Heapfile tidFile) {

        try {

            BTreeFile bTreeFile = new BTreeFile(columnFile.getName()+".bt");
            Scan dataFileScan = columnFile.getFile().openScan();
            Scan tidFileScan = tidFile.openScan();

            RID dataRid = new RID();
            RID tidRid = new RID();

            Tuple dataTuple;

            while ( (dataTuple = dataFileScan.getNext(dataRid)) != null) {

                tidFileScan.getNext(tidRid);

                try {
                    bTreeFile.insert(KeyClassFactory.getKey(
                                columnFile.getAttrType().attrType,
                                new Tuple(dataTuple.getTupleByteArray())
                            ),
                            tidRid
                    );
                } catch (KeyTooLongException | KeyNotMatchException | LeafInsertRecException | IndexInsertRecException |
                         UnpinPageException | NodeNotMatchException | ConvertException | DeleteRecException |
                         IndexSearchException | IteratorException | LeafDeleteException | InsertException e) {
                    throw new RuntimeException("Error inserting record in btree file",e);
                }
            }


        } catch (GetFileEntryException | PinPageException | ConstructPageException e) {
            throw new RuntimeException("Error creating Btree file",e);
        } catch (InvalidTupleSizeException | IOException e) {
            throw new RuntimeException("Error creating scans", e);
        }

        return true;
    }
}
