package columnar;

import bitmap.BitmapFile;
import global.*;
import heap.*;
import iterator.*;

import java.io.IOException;

public class ColumnarBitmapEquiJoins extends Iterator {

    private AttrType[] leftAttrTypes;
    private int leftAttrLength;
    private short[] leftStringSizes;

    private AttrType[] rightAttrTypes;
    private int rightAttrLength;
    private short[] rightStringSizes;

    private int memoryAmount;
    private String leftColumnarFileName;
    private int leftJoinField;
    private String rightColumnarFileName;
    private int rightJoinField;
    private IndexType[] rightIndexTypes;
    private String[] rightIndexNames;

    private FldSpec[] projectionList;
    private int numOutputFields;

    private ColumnarFile leftColumnarFile;
    private ColumnarFile rightColumnarFile;
    private BMFileScan leftScan;
    private BMFileScan rightScan;

    public ColumnarBitmapEquiJoins(
            AttrType[] in1,
            int len_in1,
            short[] t1_str_sizes,
            AttrType[] in2,
            int len_in2,
            short[] t2_str_sizes,
            int amt_of_mem,
            String leftColumnarFileName,
            int leftJoinField,
            String rightColumnarFileName,
            int rightJoinField,
            IndexType[] rightIndex,
            String[] rightIndName,
            FldSpec[] proj_list,
            int n_out_flds) throws IOException {

        this.leftAttrTypes = in1;
        this.leftAttrLength = len_in1;
        this.leftStringSizes = t1_str_sizes;

        this.rightAttrTypes = in2;
        this.rightAttrLength = len_in2;
        this.rightStringSizes = t2_str_sizes;

        this.memoryAmount = amt_of_mem;
        this.leftColumnarFileName = leftColumnarFileName;
        this.leftJoinField = leftJoinField;
        this.rightColumnarFileName = rightColumnarFileName;
        this.rightJoinField = rightJoinField;
        this.rightIndexTypes = rightIndex;
        this.rightIndexNames = rightIndName;

        this.projectionList = proj_list;
        this.numOutputFields = n_out_flds;

        // Open columnar files
        this.leftColumnarFile = new ColumnarFile(leftColumnarFileName);
        this.rightColumnarFile = new ColumnarFile(rightColumnarFileName);

        // Initialize bitmap scans for the join fields
        this.leftScan = new BMFileScan(new BitmapFile(leftColumnarFileName + "." + leftJoinField, leftColumnarFile));
        this.rightScan = new BMFileScan(new BitmapFile(rightColumnarFileName + "." + rightJoinField, rightColumnarFile));
    }

    @Override
    public Tuple get_next() throws IOException, JoinsException, InvalidTupleSizeException, InvalidTypeException, TupleUtilsException, PredEvalException, SortException, LowMemException, UnknowAttrType, UnknownKeyTypeException, Exception {
        // Logic to iterate over bitmap indexes, fetch matching tuples and perform join
        while (true) {
            if (!leftScan.position(null) || !rightScan.position(null)) {
                return null;  // No more tuples to join
            }

            Integer leftBit = leftScan.getNext();
            Integer rightBit = rightScan.getNext();

            while (leftBit != null && rightBit != null) {
                if (leftBit.equals(rightBit)) {
                    // Perform the projection and return the tuple
                    Tuple joinedTuple = TupleUtils.joins(
                            leftColumnarFile.getTuple(null), leftAttrTypes,
                            rightColumnarFile.getTuple(null), rightAttrTypes,
                            leftAttrLength, rightAttrLength,
                            leftStringSizes, rightStringSizes,
                            projectionList, numOutputFields
                    );
                    return joinedTuple;
                }
            }
        }
    }

    @Override
    public void close() throws IOException, JoinsException, SortException {
        leftScan.close();
        rightScan.close();
    }
}

