package tests;

import java.io.IOException;

import columnar.ColumnFile;
import columnar.ColumnarFiles;
import global.AttrType;
import global.GlobalConst;
import global.SystemDefs;
import heap.Heapfile;

public class ColumnarTest extends TestDriver implements GlobalConst {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static void main(String[] args) {
        ColumnarTest colFileTest = new ColumnarTest();
        if (!colFileTest.test1()) {
            System.out.println(ANSI_RED + "ColumnarTest.test1() failed" + ANSI_RESET);
            System.exit(1);
        }
        System.out.println(ANSI_GREEN + "ColumnarTest.test1() passed" + ANSI_RESET);
        if (!colFileTest.test2()) {
            System.out.println(ANSI_RED + "ColumnarTest.test2() failed" + ANSI_RESET);
            System.exit(1);
        }
        System.out.println(ANSI_GREEN + "ColumnarTest.test2() passed" + ANSI_RESET);
        System.out.println(ANSI_GREEN + "🎉 All ColumnarTest tests passed! 🎉" + ANSI_RESET);
    }

    public ColumnarTest() {
        super("ColumnarTest");
        System.out.print("\n" + "Running ColumnarTest...." + "\n");

        try {
            SystemDefs sysdef = new SystemDefs(dbpath, NUMBUF + 20, NUMBUF, "Clock");
        }

        catch (Exception e) {
            Runtime.getRuntime().exit(1);
        }

        // Kill anything that might be hanging around
        String newdbpath;
        String newlogpath;
        String remove_logcmd;
        String remove_dbcmd;
        String remove_cmd = "/bin/rm -rf ";

        newdbpath = dbpath;
        newlogpath = logpath;

        remove_logcmd = remove_cmd + logpath;
        remove_dbcmd = remove_cmd + dbpath;

        // Commands here is very machine dependent. We assume
        // user are on UNIX system here. If we need to port this
        // program to other platform, the remove_cmd have to be
        // modified accordingly.
        try {
            Runtime.getRuntime().exec(remove_logcmd);
            Runtime.getRuntime().exec(remove_dbcmd);
        } catch (IOException e) {
            System.err.println("" + e);
        }

        remove_logcmd = remove_cmd + newlogpath;
        remove_dbcmd = remove_cmd + newdbpath;

        // This step seems redundant for me. But it's in the original
        // C++ code. So I am keeping it as of now, just in case
        // I missed something
        try {
            Runtime.getRuntime().exec(remove_logcmd);
            Runtime.getRuntime().exec(remove_dbcmd);
        } catch (IOException e) {
            System.err.println("" + e);
        }
    }

    protected boolean test1() {
        System.out.println(
                "Running Test 1: Create a new ColumnFile, setting some values, copying it using getBytes() and checking it with original data");
        String columnFileName = "Test_Column_File_1.col";
        String columnName = "Test Column Int";
        AttrType attrType = new AttrType(1);
        ColumnFile columnFile = new ColumnFile(columnFileName, columnName, attrType);
        columnFile.setHasBitmap(true);

        // Check the byte parsing
        ColumnFile columnFileCheck = new ColumnFile(columnFile.getBytes());

        if (!columnFileCheck.getName().equals(columnFileName)) {
            return false;
        }
        if (!columnFileCheck.getColumnName().equals(columnName)) {
            return false;
        }
        if (!columnFileCheck.getAttrType().toString().equals(attrType.toString())) {
            return false;
        }
        if (columnFileCheck.hasBtree()) {
            return false;
        }
        if (!columnFileCheck.hasBitmap()) { // should be true set above
            return false;
        }
        return true;
    }

    protected boolean test2() {
        String[] colNames = { "Test Col 1", "Test Col 2", "Test Col 3", "Test Col 4" };
        AttrType[] colAttrs = { new AttrType(AttrType.attrInteger), new AttrType(AttrType.attrString),
                new AttrType(AttrType.attrReal), new AttrType(AttrType.attrString) };
        ColumnarFiles columnarFiles = new ColumnarFiles("ColumarFilesTest", colNames, colAttrs, colNames.length);

        ColumnFile testCol2 = columnarFiles.getColumnFile(1);
        testCol2.setHasBtree(true);

        ColumnarFiles columnarFilesCheck = new ColumnarFiles("ColumarFilesTest");

        testCol2 = columnarFilesCheck.getColumnFile(1);

        if (columnarFilesCheck.getNumColumns() != 4) {
            return false;
        }
        try {
            // This should fail
            columnarFilesCheck.getColumnFile(5);
            return false;
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Getting a non-existent column fails as expected!");
        }

        try {
            columnarFilesCheck.getTidFile();
        } catch (Exception e) {
            return false;
        }

        try {
            Heapfile headerFile = columnarFilesCheck.getHeaderFile();
            if (headerFile.getRecCnt() != 4) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace(); // oops!
        }

        if (testCol2.getAttrType().toString() != new AttrType(0).toString()) {
            System.out.println("testCol2 attrType compare failed");
            return false;
        }

        if(!testCol2.getColumnName().equals("Test Col 2")) {
            System.out.println("testCol2.getColumnName() failed");
            return false;
        }

        return true;
    }
}
