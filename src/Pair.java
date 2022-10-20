/**
 * Class to encapulate two objects of different or similar data types
 */
public class Pair<T,K>{

    public T first;
    public K second;

    /**
     * Pair Constructor
     * @param _first first element of any object type
     * @param _second second element of any object type
     */
    public Pair(T _first, K _second){
        first = _first;
        second = _second;
    }


}
