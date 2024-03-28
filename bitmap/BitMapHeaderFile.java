package bitmap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import global.RID;
import heap.FieldNumberOutOfBoundException;
import heap.HFBufMgrException;
import heap.HFDiskMgrException;
import heap.HFException;
import heap.Heapfile;
import heap.InvalidSlotNumberException;
import heap.InvalidTupleSizeException;
import heap.Scan;
import heap.SpaceNotAvailableException;
import heap.Tuple;

public class BitMapHeaderFile {
    private Heapfile headerFile;

    public BitMapHeaderFile(String fileName, int columnNo) throws HFException, HFBufMgrException, HFDiskMgrException, IOException {
        this.headerFile = new Heapfile("BM."+fileName+"."+columnNo+".hdr");
    }

    public void addFile(int columnNo, String bitMapFileName, String value) throws InvalidSlotNumberException, InvalidTupleSizeException, SpaceNotAvailableException, HFException, HFBufMgrException, HFDiskMgrException, IOException {
        headerFile.insertRecord(new BitMapFileMeta(columnNo, bitMapFileName, value).convertToTuple().getTupleByteArray());
    }

    public List<BitMapFileMeta> getFiles() throws InvalidTupleSizeException, IOException, FieldNumberOutOfBoundException {
        Tuple tuple;
        RID rid = new RID();
        Scan scan = headerFile.openScan();
        List<BitMapFileMeta> BMFileList = new ArrayList<BitMapFileMeta> (); 
        while ((tuple = scan.getNext(rid)) != null) {
            BitMapFileMeta bitMapFileMeta = new BitMapFileMeta(tuple.getTupleByteArray());
            BMFileList.add(bitMapFileMeta);
        }
        return BMFileList;
    }
}