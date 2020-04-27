import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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
                if (!st.contains(word))
                    st.put(word, 1);
                else st.put(word, st.get(word) + 1);
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
                st.delete(key);
                keyList.remove(deletedIndex);
            }
            System.out.println("\n### 키 삭제 후 트리 정보");
            print_tree(st);
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
    private Node<String , Integer> root;
    private int nodeSize = 0;

    public void put(String key , Integer value) {
        Node<String,Integer> x = root;
        if(x == null) {
            root = new Node<>(key,value);
            return;
        }


    }
    private  Element<String,Integer> treeSearch(String key){
        Node<String,Integer> x = root;
        if(x == null)
            return null;
        int pos;
        while(true){
            pos = getDataPos(x,key);
            if(x.data.get(pos).key.toString().compareTo(key.toString()) == 0)
                return x.data.get(pos);
            else if(x.data.get(pos).key.toString().compareTo(key.toString()) < 0) {
                if(x.child.get(pos) == null)
                    return null;
                x = x.child.get(pos);
            }
            else {
                if(x.child.get(pos+1) == null)
                    return null;
                x = x.child.get(pos + 1);
            }
        }
    }

    private int getDataPos(Node<String,Integer> node,String key) {
        int pos;
        int dataSize = node.data.size();
        for(pos =0; pos < dataSize; pos++){
            if(node.data.get(pos).key.toString().compareTo(key.toString()) > 0)
                return pos;
        }
        return pos;
    }

    public int get(String key) {
    }

    public boolean contains(String key){
    }

    public Iterable<String> keys() {
    }

    public int size() {
        return nodeSize;
    }

    public int depth() {
    }

    public void delete(String key) {
    }


}

class Node<K, V> {
    ArrayList<Element<K,V>> data = new ArrayList<>(3);
    Node<K, V> parent;
    ArrayList<Node<K,V>> child = new ArrayList<>(4);

    Node(K key, V value) {
        data.add(new Element<>(key,value));
    }

}
class Element<K ,V> {
    K key;
    V value;

    public Element(K key, V value) {
        this.key = key;
        this.value = value;
    }
}
