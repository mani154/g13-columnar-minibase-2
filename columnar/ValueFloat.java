package columnar;

import global.ValueClass;

public class ValueFloat extends ValueClass<Float> {

    public ValueFloat(float value) {
        this.value = value;
    }

    @Override
    public Float getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Float.toString(this.value);
    }

    @Override
    public int compareTo(Float key) {
        return value.compareTo(key);
    }

}