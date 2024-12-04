package src;

import src.HashNode;
import src.MyPair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HashTable<T> {
    List<HashNode<T>> hashtable;
    int numberOfBuckets;
    int size;

    public HashTable() {
        hashtable = new ArrayList<>();
        numberOfBuckets = 100;
        size = 0;
        for (int i = 0; i < numberOfBuckets; i++)
            hashtable.add(null);
    }

    public int hashCode(T token) {
        return Objects.hashCode(token);
    }

    public int size() {
        return size;
    }

    public List<T> getKeys() {
        List<T> keys = new ArrayList<>();
        for (int i = 0; i < numberOfBuckets; i++) {
            HashNode<T> head = hashtable.get(i);
            while (head != null) {
                keys.add(head.token);
                head = head.next;
            }
        }
        return keys;
    }

    public int getBucketIndex(T token) {
        int hashCode = hashCode(token);
        return Math.abs(hashCode % numberOfBuckets);
    }

    public MyPair<Integer, Integer> get(T token) {
        int bucketIndex = getBucketIndex(token);
        int hashCode = hashCode(token);
        HashNode<T> head = hashtable.get(bucketIndex);
        int positionInBucket = 0;
        while (head != null) {
            if (head.token.equals(token) && head.hashCode == hashCode)
                return new MyPair<>(bucketIndex, positionInBucket);
            head = head.next;
            positionInBucket += 1;
        }
        return null;
    }

    public void add(T token) {
        if(get(token) == null){
            int bucketIndex = getBucketIndex(token);
            int hashCode = hashCode(token);
            HashNode<T> head = hashtable.get(bucketIndex);
            HashNode<T> newNode = new HashNode<>(token, hashCode);
            newNode.next = head;
            hashtable.set(bucketIndex, newNode);
            size++;
            if ((1.0 * size) / numberOfBuckets >= 0.7) {
                List<HashNode<T>> temp = hashtable;
                hashtable = new ArrayList<>();
                numberOfBuckets = 2 * numberOfBuckets;
                size = 0;
                for (int i = 0; i < numberOfBuckets; i++)
                    hashtable.add(null);

                for (HashNode<T> headNode : temp) {
                    while (headNode != null) {
                        int bucketIndexNew = getBucketIndex(headNode.token);
                        HashNode<T> newNodeRehash = new HashNode<>(headNode.token, headNode.hashCode);
                        newNodeRehash.next = hashtable.get(bucketIndexNew);
                        hashtable.set(bucketIndexNew, newNodeRehash);
                        headNode = headNode.next;
                    }
                }
            }
        }
    }
}
