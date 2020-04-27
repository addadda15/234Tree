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
    protected Node<String , Integer> root;

    public void put(String key , Integer value) {
        if(root == null) {
            root = new Node<>(key,value,"left");
            return;
        }
    }
    private  Element<String,Integer> treeSearch(String key){
        Node<String,Integer> x = root;
        while(true){
            int cmp = key.toString().compareTo(x.data_l.key.toString());
            if(cmp == 0)
                return x.data_l;
            else if(cmp < 0) {
                if (x.left_child == null)
                    return null;
                x = x.left_child;
            }else{
                if(x.data_m ==null)
                    return null;
                cmp = key.toString().compareTo(x.data_m.key.toString());
            }
        }
    }
    public int get(String key) {
    }

    public boolean contains(String key){

    }

    public Iterable<String> keys() {
    }

    public int size() {
    }

    public int depth() {
    }

    public void delete(String key) {
    }


}

class Node<K, V> {
    Element<K, V> data_l, data_m, data_r;
    Node<K, V> parent, left_child, left_mid_child, right_mid_child, right_child;

    Node(K key, V value, String pos) {
        switch (pos) {
            case "left":
                data_l = new Element<>(key, value);
                break;
            case "middle":
                data_m = new Element<>(key, value);
                break;
            case "right":
                data_r = new Element<>(key, value);
                break;
        }
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
