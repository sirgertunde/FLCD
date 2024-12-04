public class HashNode<T> {
    T token;
    int hashCode;
    HashNode<T> next;

    public HashNode(T t, int h){
        token = t;
        hashCode = h;
    }
}
