package global;

import btree.FloatKey;
import btree.IntegerKey;
import btree.KeyClass;
import btree.StringKey;
import heap.FieldNumberOutOfBoundException;
import heap.Tuple;

import java.io.IOException;

public class KeyClassFactory {

    public static KeyClass getKey(int attrType, Tuple tuple) throws IOException {

        try {
            switch (attrType) {
                case AttrType.attrString:{
                    return new StringKey(tuple.getStrFld(1));
                }

                case AttrType.attrReal: {
                    return new FloatKey(tuple.getFloFld(1));
                }

                case AttrType.attrInteger: {
                    return new IntegerKey(tuple.getIntFld(1));
                }

                default:{
                    throw new RuntimeException("Attribute not found");
                }
            }
        } catch (FieldNumberOutOfBoundException e) {
            throw new RuntimeException("Error getting tuple",e);
        }
    }
}
