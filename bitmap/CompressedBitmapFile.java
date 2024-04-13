package bitmap;

import java.io.IOException;
import java.util.*;
import btree.*;
import columnar.ColumnarFile;
import columnar.ValueInt;
import columnar.ValueString;
import global.*;
import heap.*;

public class CompressedBitmapFile extends BitmapFile {

    private static final int MAX_RECORD_COUNT = 1008;

    // Constructor for an existing compressed file
    public CompressedBitmapFile(String filename, ColumnarFile columnFile)
            throws Exception {
        super(filename, columnFile);
    }

    // Constructor for creating a new compressed file
    public CompressedBitmapFile(String filename, ColumnarFile columnFile, int colNo, ValueClass value)
            throws Exception {
        super(filename, columnFile, colNo, value);
        compressBitmap(filename);
    }

    // This function will compress the bitmap using run-length encoding
    private void compressBitmap(String fileName) throws Exception {
        Scan scan = headerFile.openScan();
        Tuple tuple;
        RID rid = new RID();

        int lastBit = -1; // To keep track of the last bit read
        int runLength = 0; // To keep track of the run length

        // Temporary storage for RLE tuples before they are written to the new RLE heapfile
        ArrayList<Tuple> rleTuples = new ArrayList<>();

        // Iterate through all pages of the bitmap
        while ((tuple = scan.getNext(rid)) != null) {
            BMPage page = new BMPage();
            for (int i = 0; i < MAX_RECORD_COUNT; i++) {
                int currentBit = page.getBit(i);
                if (currentBit == lastBit) {
                    // If the current bit is the same as the last, increment the run length
                    runLength++;
                } else {
                    // If the current bit is different, save the run length and the bit value
                    if (lastBit != -1) {
                        rleTuples.add(createRLETuple(lastBit, runLength));
                    }
                    lastBit = currentBit;
                    runLength = 1;
                }
            }
        }

        // Don't forget to add the last run length
        if (lastBit != -1) {
            rleTuples.add(createRLETuple(lastBit, runLength));
        }

        scan.closescan();

        // Create a new heapfile to store the RLE compressed bitmap
        Heapfile rleHeapfile = new Heapfile(fileName + ".rle");

        // Insert RLE tuples into the new heapfile
        for (Tuple rleTuple : rleTuples) {
            rleHeapfile.insertRecord(rleTuple.getTupleByteArray());
        }
    }

    // Helper method to create a tuple for a run-length encoded bit
    private Tuple createRLETuple(int bitValue, int runLength) {
        // Assume that a tuple has two fields: the first for the bit value and the second for the run length
        Tuple tuple = new Tuple();
        try {
            tuple.setHdr((short) 2, new AttrType[]{new AttrType(AttrType.attrInteger), new AttrType(AttrType.attrInteger)}, new short[2]);
            tuple.setIntFld(1, bitValue);
            tuple.setIntFld(2, runLength);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tuple;
    }

    // Additional methods would be necessary to handle reading from and writing to the RLE compressed bitmap.
    // This would include decompressing runs of bits when reading and updating run lengths or adding new runs when writing.

    // Insert a bit at a specific position
    public boolean insert(int position, String filename) throws Exception {
        // Decompress the run-length encoded bitmap into the original format
        // (this would actually need to modify the underlying bitmap storage)
        decompressBitmap(filename);

        // Insert the bit into the bitmap
        super.insert(position);

        // Compress the bitmap again
        compressBitmap(filename);

        return true;
    }

    // Delete a bit at a specific position
    public boolean delete(int position, String filename) throws Exception {
        // Decompress the run-length encoded bitmap into the original format
        decompressBitmap(filename);

        // Delete the bit from the bitmap
        super.delete(position);

        // Compress the bitmap again
        compressBitmap(filename);

        return true;
    }

    // This function will decompress the bitmap from its run-length encoded version
    private void decompressBitmap(String filename) throws Exception {
        // Open the RLE heapfile
        Heapfile rleHeapfile = new Heapfile(filename + ".rle");
        Scan scan = rleHeapfile.openScan();
        Tuple tuple;
        RID rid = new RID();

        // Prepare an empty bitmap to store the decompressed bits
        BMPage bitmapPage = new BMPage(); // Assuming a new page to store the decompressed bitmap
        int currentBitPosition = 0;

        // Iterate through each RLE tuple
        while ((tuple = scan.getNext(rid)) != null) {
            int bitValue = tuple.getIntFld(1);
            int runLength = tuple.getIntFld(2);

            // For each RLE entry, write out the run length of bits to the bitmap
            for (int i = 0; i < runLength; i++) {
                bitmapPage.setBit(currentBitPosition, (byte) bitValue);
                currentBitPosition++;

                // Check if we've reached the end of the current bitmap page
                if (currentBitPosition >= MAX_RECORD_COUNT) {
                    // Write the current bitmap page to the heapfile
                    headerFile.insertRecord(new BMDataPageInfo(bitmapPage.curPage,1, MAX_RECORD_COUNT).convertToTuple().getTupleByteArray());

                    // Prepare a new bitmap page
                    bitmapPage = new BMPage();
                    currentBitPosition = 0;
                }
            }
        }
        scan.closescan();

        // If there's any remaining bits, write the final bitmap page to the heapfile
        if (currentBitPosition > 0) {
            headerFile.insertRecord(new BMDataPageInfo(bitmapPage.curPage,1, MAX_RECORD_COUNT).convertToTuple().getTupleByteArray());
        }
    }
}
