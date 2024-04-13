package columnar;

import global.*;
import heap.*;
import index.IndexException;
import iterator.*;

import java.io.IOException;

public class ColumnarNestedLoopJoins extends Iterator {

    private AttrType[] outerAttrTypes;
    private int outerAttrLength;
    private short[] outerStringSizes;

    private AttrType[] innerAttrTypes;
    private int innerAttrLength;
    private short[] innerStringSizes;

    private int memoryAmount;
    private Iterator outerIterator;
    private String innerColumnarFileName;
    private CondExpr[] joinConditions;
    private CondExpr[] innerTableConditions;
    private FldSpec[] projectionList;
    private int numOutputFields;

    private ColumnarFiles outerColumnFile;
    private ColumnarFiles innerColumnFile;
    private Tuple outerTuple;
    private Tuple innerTuple;

    public ColumnarNestedLoopJoins(
            AttrType[] outerAttrTypes,
            int outerAttrLength,
            short[] outerStringSizes,
            AttrType[] innerAttrTypes,
            int innerAttrLength,
            short[] innerStringSizes,
            int memoryAmount,
            Iterator outerIterator,
            String innerColumnarFileName,
            CondExpr[] joinConditions,
            CondExpr[] innerTableConditions,
            FldSpec[] projectionList,
            int numOutputFields) throws IOException, FileScanException, TupleUtilsException, InvalidRelation {

        this.outerAttrTypes = outerAttrTypes;
        this.outerAttrLength = outerAttrLength;
        this.outerStringSizes = outerStringSizes;

        this.innerAttrTypes = innerAttrTypes;
        this.innerAttrLength = innerAttrLength;
        this.innerStringSizes = innerStringSizes;

        this.memoryAmount = memoryAmount;
        this.outerIterator = outerIterator;
        this.innerColumnarFileName = innerColumnarFileName;
        this.joinConditions = joinConditions;
        this.innerTableConditions = innerTableConditions;
        this.projectionList = projectionList;
        this.numOutputFields = numOutputFields;

        this.outerColumnFile = new ColumnarFile(innerColumnarFileName); // Open the outer columnar file
        this.innerColumnFile = ((FileScan) outerIterator).getColumnarFile(); // Retrieve the inner columnar file from the iterator

        this.outerTuple = new Tuple();
        this.innerTuple = new Tuple();
    }

    @Override
    public Tuple get_next() throws IOException, JoinsException, IndexException, InvalidTupleSizeException, InvalidTypeException, TupleUtilsException, PredEvalException, SortException, LowMemException, UnknowAttrType, UnknownKeyTypeException, Exception {
        while ((outerTuple = outerIterator.get_next()) != null) {
            Scan innerScan = innerColumnFile.openColumnScan();
            RID innerRid = new RID();

            while ((innerTuple = innerScan.getNext(innerRid)) != null) {
                if (PredEval.Eval(joinConditions, outerTuple, innerTuple, outerAttrTypes, innerAttrTypes)) {
                    Tuple joinedTuple = new Tuple();
                    AttrType[] joinedTupleAttrTypes = new AttrType[numOutputFields];
                    short[] joinedTupleSize = TupleUtils.setup_op_tuple(joinedTuple, joinedTupleAttrTypes, outerAttrTypes,
                            outerAttrLength, innerAttrTypes, innerAttrLength, outerStringSizes, innerStringSizes,
                            projectionList, numOutputFields);
                    innerScan.closescan();
                    return joinedTuple;
                }
            }
            innerScan.closescan();
        }
        return null;
    }

    @Override
    public void close() throws IOException, JoinsException, SortException, IndexException {
        outerIterator.close();
    }
}
