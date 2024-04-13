package bitmap;

import global.RID;
import heap.*;

import java.io.IOException;

public class BMFileScan {

    private BitmapFile bmFile;

    private Scan bitMapFileScan;

    private int position = 0;

    private RID rid;

    private BMDataPageInfo bmDataPageInfo;

    private static final int MAX_RECORD_COUNT = 1008;

    public BMFileScan(BitmapFile bmFile) {

        try {
            this.bmFile = bmFile;
        bitMapFileScan = bmFile.headerFile.openScan();

        rid = new RID();
        bmDataPageInfo = new BMDataPageInfo(bitMapFileScan.getNext(rid));
        } catch (InvalidTupleSizeException e) {
            e.printStackTrace();
        } catch (FieldNumberOutOfBoundException e) {
            e.printStackTrace();
        } catch ( IOException e) {
            e.printStackTrace();
        }
        

    }

    public void close() {
        bitMapFileScan.closescan();
    }

    public Integer getNext() {

        if ( position > MAX_RECORD_COUNT) {
            try {
                bmDataPageInfo = new BMDataPageInfo(bitMapFileScan.getNext(rid));
            } catch (FieldNumberOutOfBoundException | InvalidTupleSizeException | IOException e) {
                // TODO Auto-generated catch block
              throw new RuntimeException("error getting bmDataPageInfo", e);
            } 

            if(rid == null) return null;
            position = 0;  
        }

        Integer bit;
        try {
            bit = bmFile.getBit(bmDataPageInfo.pageId, position);
        } catch ( Exception e) {
            // TODO Auto-generated catch block
            throw new RuntimeException("error getting bit",e);
        }
        position++;
        return bit;

    }

    // test failing, maybe can only use it in index
    // public boolean position(TID tid) {

    //     boolean returnValue = true;
    //     try {
    //         for(int i = 0; i < columnarFile.numColumns; i++) {
    //             returnValue = returnValue && columnFileScans[i].position(tid.getRID(i));
    //         }

    //         return returnValue;
    //     } catch (InvalidTupleSizeException | IOException e) {
    //         throw new RuntimeException("Error updating position",e);
    //     }
    // }
}