package global;

import heap.Tuple;

import java.io.IOException;

public class TID {

    int numRIDs;

    int position;
    RID[] rids;

    boolean isDeleted =  false;

    public TID(int numRIDs, int position, RID[] rids) {
        this.numRIDs = numRIDs;
        this.position = position;
        this.rids = rids;
    }

    public TID(byte[] data) {
        try {
            numRIDs = Convert.getIntValue(0,data);
            position = Convert.getIntValue(4,data);
            isDeleted = Convert.getIntValue(8,data) == 1;
            rids = new RID[numRIDs];

            int offset = 12;
            for(int i = 0; i< numRIDs;i++) {
                int slotNo = Convert.getIntValue(offset,data);
                int pageNo = Convert.getIntValue(offset+4,data);
                rids[i] = new RID(new PageId(pageNo),slotNo);

                offset+=8;
            }
        } catch (IOException e) {
            throw new RuntimeException("Error getting value from byte array",e);
        }
    }

    public int getNumRIDs() {
        return numRIDs;
    }

    public int getPosition() {
        return position;
    }

    public RID[] getRids() {
        return rids;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public byte[] getBytes() {
        byte[] data = new byte[8*numRIDs+12];

        try {
            Convert.setIntValue(numRIDs, 0,data);
            Convert.setIntValue(position, 4,data);
            Convert.setIntValue(isDeleted ? 1 : 0,8,data);

            int offset = 12;
            for(int i = 0; i<numRIDs;i++){
                rids[i].writeToByteArray(data,offset);
                offset +=8;
            }

        } catch (IOException e) {
            throw new RuntimeException("Error wrting byte value",e);
        }

        return data;
    }

}
