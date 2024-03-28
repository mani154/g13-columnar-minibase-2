package columnar;

import global.AttrType;
import global.Convert;
import global.ValueClass;

import java.io.IOException;

public class ValueFactory {

    public static ValueClass getValueClass(byte[] data, AttrType type) throws IOException {

        ValueClass value = null;
        switch (type.attrType) {
            case 0:
                String s = new String(data);
                value = new ValueString(s);
                break;
            case 1:
                Integer i = Convert.getIntValue(0, data);
                value = new ValueInt(i);
                break;
            case 2:
                Float f = Convert.getFloValue(0, data);
                value = new ValueFloat(f);
                break;
        }

        return value;
    }
}