package columnar;

import global.*;
import heap.*;
import iterator.*;
import index.*;

import java.io.*;

public class ColumnarSort extends Iterator {
    private AttrType[] attrTypes;
    private short lenIn;
    private short[] strSizes;
    private String columnarFileName;
    private int sortFld;
    private TupleOrder sortOrder;
    private int sortFldLen;
    private int nPages;

    private ColumnarFiles columnarFiles;
    private Heapfile sortedDataHeapfile;
    private BTreeFile bTreeFile;
    private BitMapIndex bitmapIndex;

    public ColumnarSort(
        AttrType[] in, 
        short len_in, 
        short[] str_sizes,
        String columnarFileName,
        int sort_fld, 
        TupleOrder sort_order, 
        int sort_fld_len, 
        int n_pages) throws Exception {

        this.attrTypes = in;
        this.lenIn = len_in;
        this.strSizes = str_sizes;
        this.columnarFileName = columnarFileName;
        this.sortFld = sort_fld;
        this.sortOrder = sort_order;
        this.sortFldLen = sort_fld_len;
        this.nPages = n_pages;

        // Initialize columnar file
        this.columnarFiles = new ColumnarFiles(columnarFileName);

        // Setup for sorting
        this.sortedDataHeapfile = new Heapfile(null); // create a temporary heapfile to store sorted data
        performSorting();
        updateIndexes();
    }

    private void performSorting() throws Exception {
        // Using external sorting
        ExternalSort extSort = new ExternalSort(
            columnarFiles.getColumnFile(sortFld).getDataFile(),
            attrTypes,
            strSizes,
            sortFld,
            sortOrder,
            sortFldLen,
            nPages
        );
        RID rid = new RID();
        Tuple temp;

        while ((temp = extSort.get_next()) != null) {
            sortedDataHeapfile.insertRecord(temp.returnTupleByteArray());
        }
    }

    private void updateIndexes() throws Exception {
        // Assume index files are named with a convention
        String btreeFileName = columnarFileName + ".BTree." + sortFld;
        String bitmapFileName = columnarFileName + ".Bitmap." + sortFld;

        // Rebuild B-Tree Index
        this.bTreeFile = new BTreeFile(btreeFileName, AttrType.attrInteger, 4, 1); // assuming integer type for simplicity
        Scan scan = sortedDataHeapfile.openScan();
        RID rid;
        Tuple t;

        while ((t = scan.getNext(rid)) != null) {
            Integer key = Convert.getIntValue(sortFld, t.getTupleByteArray());
            bTreeFile.insert(new IntegerKey(key), rid);
        }
        scan.closescan();

        // Rebuild Bitmap Index, similar logic would apply
        this.bitmapIndex = new BitMapIndex(bitmapFileName);
        // Reinsertion logic for bitmap indexes depending on the library specifics
    }

    @Override
    public Tuple get_next() throws IOException, SortException, UnknowAttrType, LowMemException, JoinsException, Exception {
        // This method would return sorted tuples
        return sortedDataHeapfile.getNext(new RID());
    }

    @Override
    public void close() throws IOException {
        sortedDataHeapfile.deleteFile();
        bTreeFile.close();
        bitmapIndex.close();
    }
}
