package columnar;

import global.ValueClass;

public class ValueString extends ValueClass<String> {

    public ValueString(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @Override
    public int compareTo(String key) {
        return value.compareTo(key);
    }

}