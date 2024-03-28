package bitmap;

import java.io.IOException;

import btree.IteratorException;
import btree.ConstructPageException;
import bufmgr.HashEntryNotFoundException;
import bufmgr.InvalidFrameNumberException;
import bufmgr.PageUnpinnedException;
import bufmgr.ReplacerException;
import global.PageId;
import global.RID;
import global.SystemDefs;
import global.GlobalConst;

public class BM implements GlobalConst {
    public BM() {

    }

    public static void printBitmap(BMPage header)
        throws IOException,
        ConstructPageException,
        IteratorException,
        HashEntryNotFoundException,
        InvalidFrameNumberException,
        PageUnpinnedException,
        ReplacerException {
        if (header.getCurPage().pid == INVALID_PAGE) {
            System.out.println("The Bitmap is Empty!!!");
            return;
        }

        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("---------------The Bitmap Structure---------------");

        System.out.println(1 + "     " + header.getCurPage());

        _printBitmap(header.getCurPage(), "     ", 1);

        System.out.println("--------------- End ---------------");
        System.out.println("");
        System.out.println("");
    }

    private static void _printBitmap(PageId curPageId, String prefix, int i)
            throws IOException,
            ConstructPageException,
            IteratorException,
            HashEntryNotFoundException,
            InvalidFrameNumberException,
            PageUnpinnedException,
            ReplacerException {

        BMPage bitmapPage = new BMPage(curPageId);
        prefix = prefix + "       ";
        i++;

        RID rid = new RID();
        for (int slotNo = 0; slotNo < bitmapPage.getSlotCnt(); slotNo++) {
            if (bitmapPage.getSlotDataByPos(slotNo) == 1) {
                System.out.println(i + prefix + "Slot " + slotNo + ": Set");
            } else {
                System.out.println(i + prefix + "Slot " + slotNo + ": Not Set");
            }
        }

        SystemDefs.JavabaseBM.unpinPage(curPageId, false);
    }
}
