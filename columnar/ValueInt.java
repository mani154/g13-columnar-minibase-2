package columnar;

import global.ValueClass;

public class ValueInt extends ValueClass<Integer> {


    public ValueInt(int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Integer.toString(this.value);
    }

    @Override
    public int compareTo(Integer key) {
        return value.compareTo(key);
    }


}