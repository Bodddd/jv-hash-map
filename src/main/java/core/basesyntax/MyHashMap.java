package core.basesyntax;

import java.util.Objects;

public class MyHashMap<K, V> implements MyMap<K, V> {
    static final int DEFAULT_INITIAL_CAPACITY = 16;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    static final int RESIZE_FACTOR = 2;

    private Node<K, V>[] table;
    private int size;
    private int threshold;

    public MyHashMap() {
        table = new Node[DEFAULT_INITIAL_CAPACITY];
        size = 0;
        threshold = (int) (table.length * DEFAULT_LOAD_FACTOR);
    }

    @Override
    public void put(K key, V value) {
        Node<K,V> node = new Node<>(hash(key), key, value, null);
        int index = getIndex(key);
        if (table[index] == null) {
            table[index] = node;
        } else {
            putInNodeLink(node, index);
        }
        if (++size >= threshold) {
            resize();
        }
    }

    @Override
    public V getValue(K key) {
        if (table[getIndex(key)] == null) {
            return null;
        }
        Node<K,V> node = getNodeByKey(key);
        return (node != null ? node.value : null);
    }

    private Node<K,V> getNodeByKey(K key) {
        Node<K,V> node = table[getIndex(key)];
        while (true) {
            if (Objects.equals(node.key, key)) {
                return node;
            }
            node = node.next;
            if (node == null) {
                return null;
            }
        }
    }

    @Override
    public int getSize() {
        return size;
    }

    private int getIndex(K key) {
        return hash(key) % table.length;
    }

    private int hash(Object key) {
        return (key == null) ? 0 : (Math.abs(key.hashCode()));
    }

    private void putInNodeLink(Node node, int index) {
        Node<K,V> findNode = table[index];
        while (true) {
            if (Objects.equals(findNode.key, node.key)) {
                findNode.value = (V) node.value;
                size--;
                return;
            }
            if (findNode.next == null) {
                findNode.next = node;
                return;
            }
            findNode = findNode.next;
        }
    }

    private void resize() {
        size = 0;
        Node<K, V>[] tableOld = table.clone();
        table = new Node[table.length * RESIZE_FACTOR];
        threshold = (int) (table.length * DEFAULT_LOAD_FACTOR);
        for (Node<K, V> kvNode : tableOld) {
            if (kvNode != null) {
                Node<K, V> replaceNode = kvNode;
                while (replaceNode != null) {
                    put(replaceNode.key, replaceNode.value);
                    replaceNode = replaceNode.next;
                }
            }
        }
    }

    private static class Node<K, V> {
        private final int hash;
        private final K key;
        private V value;
        private Node<K, V> next;

        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
}
