package btree;

/**  FloatKey: It extends the KeyClass.
 *   It defines the integer Key.
 */
public class FloatKey extends KeyClass {

    private Float key;

    public String toString(){
        return key.toString();
    }

    /** Class constructor
     *  @param     value   the value of the integer key to be set
     */
    public FloatKey(float value)
    {
        key=value;
    }



    /** get a copy of the integer key
     *  @return the reference of the copy
     */
    public Float getKey()
    {
        return key;
    }

    /** set the integer key value
     */
    public void setKey(Float value)
    {
        key =value;
    }
}
