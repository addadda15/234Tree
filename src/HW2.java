import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;

public class HW2 {
    public static void main(String[] args) {
        Tree234<String, Integer> st = new Tree234<>();
        Scanner sc = new Scanner(System.in);
        System.out.print("입력 파일 이름? ");
        String fname = sc.nextLine();    // 파일 이름을 입력
        System.out.print("난수 생성을 위한 seed 값? ");
        long seed = sc.nextLong();
        Random rand = new Random(seed);
        sc.close();
        try {
            sc = new Scanner(new File(fname));
            long start = System.currentTimeMillis();
            while (sc.hasNext()) {
                String word = sc.next();
                if (!st.contains(word)) {
                    st.put(word, 1);
                } else st.put(word, st.get(word) + 1);
            }
            long end = System.currentTimeMillis();
            System.out.println("입력 완료: 소요 시간 = " + (end - start) + "ms");

            System.out.println("### 생성 시점의 트리 정보");
            print_tree(st);

            ArrayList<String> keyList = (ArrayList<String>) st.keys();
            int loopCount = (int) (keyList.size() * 0.95);
            for (int i = 0; i < loopCount; i++) {
                int deletedIndex = rand.nextInt(keyList.size());
                String key = keyList.get(deletedIndex);
                //st.delete(key);
                keyList.remove(deletedIndex);
            }
            //System.out.println("\n### 키 삭제 후 트리 정보");
            // print_tree(st);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (sc != null)
            sc.close();
    }

    private static void print_tree(Tree234<String, Integer> st) {
        System.out.println("등록된 단어 수 = " + st.size());
        System.out.println("트리의 깊이 = " + st.depth());

        String maxKey = "";
        int maxValue = 0;
        for (String word : st.keys())
            if (st.get(word) > maxValue) {
                maxValue = st.get(word);
                maxKey = word;
            }
        System.out.println("가장 빈번히 나타난 단어와 빈도수: " + maxKey + " " + maxValue);
    }
}

class Tree234<String, Integer> {
    private Node<String, Integer> root;
    private int nodeSize = 0;

    public void put(String key, Integer value) {

        Node<String, Integer> x = root;

        if (x == null) {
            root = new Node<>(key, value);
            nodeSize++;
            return;
        }
        Element<String, Integer> searchData = dataSearch(key);
        if (searchData != null)
            searchData.value = value;
        else {
            nodeSize++;
            Node<String, Integer> searchNode = nodeSearch(key);
            insert(searchNode, key, value);
        }
    }

    private void insert(Node<String, Integer> node, String key, Integer value) {
        node.data.add(new Element<>(key, value));
        node.data.sort((o1, o2) -> o1.compareTo(o2.key));
        if (node.data.size() == 4) {
            if (node.parent == null) {
                node.parent = new Node<>(node.data.get(2).key, node.data.remove(2).value);
                node.parent.child.add(node);
                node.parent.child.add(new Node<>(node.data.get(2).key, node.data.remove(2).value));
                node.parent.child.get(1).parent = node.parent;
                root = node.parent;
            } else {
                insert(node.parent, node.data.get(2).key, node.data.remove(2).value);
                node.parent.child.add(new Node<>(node.data.get(2).key, node.data.remove(2).value));
                node.parent.child.get(node.parent.child.size() - 1).parent = node.parent;
                node.parent.child.sort(new Comparator<Node<String, Integer>>() {
                    @Override
                    public int compare(Node<String, Integer> o1, Node<String, Integer> o2) {
                        return o1.data.get(0).compareTo(o2.data.get(0).key);
                    }
                });
                if (node.parent.child.size() == 5) {
                    int pos = 0;
                    for (int i = 0; i < node.parent.parent.child.size(); i++) {
                        if (node.parent.parent.child.get(i).equals(node.parent))
                            pos = i;
                    }
                    node.parent.parent.child.get(pos).child.add(node.parent.child.remove(3));
                    node.parent.parent.child.get(pos).child.add(node.parent.child.remove(3));
                    node.parent = node.parent.parent.child.get(pos);
                    node.parent.child.get(1).parent = node.parent;
                }

            }
        }
    }


    private Element<String, Integer> dataSearch(String key) {
        Node<String, Integer> x = root;
        if (root == null)
            return null;
        int pos;
        while (true) {
            pos = getDataPos(x, key);
            if (x.data.get(pos).compareTo(key) == 0)
                return x.data.get(pos);
            else if (x.data.get(pos).compareTo(key) > 0) {
                if (x.child.size() == 0)
                    return null;
                x = x.child.get(pos);
            } else {
                if (x.child.size() == 0)
                    return null;
                x = x.child.get(pos + 1);
            }
        }
    }

    private Node<String, Integer> nodeSearch(String key) {
        Node<String, Integer> x = root;
        int pos;
        while (true) {
            pos = getDataPos(x, key);
            if (x.data.get(pos).compareTo(key) == 0)
                return x;
            else if (x.data.get(pos).compareTo(key) > 0) {
                if (x.child.size() == 0)
                    return x;
                x = x.child.get(pos);
            } else {
                if (x.child.size() == 0)
                    return x;
                x = x.child.get(pos + 1);
            }
        }
    }

    private int getDataPos(Node<String, Integer> node, String key) {
        int pos;
        int dataSize = node.data.size();
        for (pos = 0; pos < dataSize; pos++) {
            if (node.data.get(pos).compareTo(key) >= 0)
                return pos;
        }
        return pos -1;
    }

    public Integer get(String key) {
        Element<String, Integer> searchData = dataSearch(key);
        if (searchData == null)
            return null;
        else {
            if (searchData.key.equals(key))
                return searchData.value;
        }
        return null;
    }

    public boolean contains(String key) {
        return dataSearch(key) != null;
    }

    public Iterable<String> keys() {
        ArrayList<String> keyList = new ArrayList<String>(size());
        inorder(root, keyList);
        return keyList;
    }

    private void inorder(Node<String, Integer> x, ArrayList<String> keyList) {
        int dataSize = x.data.size();
        if (x.child.size() == 0) {
            for (int i = 0; i < dataSize; i++)
                keyList.add(x.data.get(i).key);
        } else {
            for (int i = 0; i < dataSize; i++) {
                inorder(x.child.get(i), keyList);
                keyList.add(x.data.get(i).key);
            }
            inorder(x.child.get(dataSize), keyList);
        }
    }

    public int size() {
        return nodeSize;
    }

    public int depth() {
        int depth = 0;
        Node<String, Integer> x = root;
        while (true) {
            depth++;
            if (x.child.size() == 0)
                return depth;
            else
                x = x.child.get(0);
        }
    }

//    public void delete(String key) {
//    }


}

class Node<K, V> {
    ArrayList<Element<K, V>> data = new ArrayList<>(4);
    Node<K, V> parent;
    ArrayList<Node<K, V>> child = new ArrayList<>(5);

    Node(K key, V value) {
        data.add(new Element<>(key, value));
        parent = null;
    }
}

class Element<K, V> implements Comparable<K> {
    K key;
    V value;

    public Element(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public int compareTo(K key) {
        return this.key.toString().compareTo(key.toString());
    }
}
