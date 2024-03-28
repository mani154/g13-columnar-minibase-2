package tests;

import bitmap.BitmapFile;
import global.*;

public class BitmapTest extends TestDriver {

    public BitmapTest() {
        super("BitmapTest");
    }

    @Override
    protected boolean test1() {
        boolean isTestOkay = false;
        try {
            BitmapFile bitmapFile = new BitmapFile("testBitmap", null); // Assuming ColumnarFile is not required for this test
            // Insert a bit at position 10
            isTestOkay = bitmapFile.insert(10);
            // Check if bit at position 10 is set
            isTestOkay = (bitmapFile.getBit(new PageId(1), 10) == 1);

            // Delete the bit at position 10
            isTestOkay = bitmapFile.delete(10);
            // Check if bit at position 10 is unset
            isTestOkay = (bitmapFile.getBit(new PageId(1), 10) == 0);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return isTestOkay;
    }

    @Override
    protected boolean test2() {
        boolean isTestOkay = false;
        try {
            BitmapFile bitmapFile = new BitmapFile("testBitmap", null); // Assuming ColumnarFile is not required for this test

            // Set bit at position 20 to 1
            isTestOkay = bitmapFile.set(20, 1);
            // Check if bit at position 20 is set
            isTestOkay = (bitmapFile.getBit(new PageId(1), 20) == 1);

            // Set bit at position 20 to 0
            isTestOkay = bitmapFile.set(20, 0);
            // Check if bit at position 20 is unset
            isTestOkay = (bitmapFile.getBit(new PageId(1), 20) == 0);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return isTestOkay;
    }

    @Override
    protected boolean runAllTests() {
        boolean _passAll = OK;

        //Running test1() to test6()
        if (!test1()) {
            _passAll = FAIL;
        }
        if (!test2()) {
            _passAll = FAIL;
        }
        // Add calls to other test methods here

        return _passAll;
    }

    @Override
    protected String testName() {
        return "BitmapFileTest";
    }

    public static void main(String[] args) {
        BitmapTest bitmapFileTest = new BitmapTest();
        bitmapFileTest.runTests();
    }
}