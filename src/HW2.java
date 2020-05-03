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

            /*ArrayList<String> keyList = (ArrayList<String>) st.keys();
            int loopCount = (int) (keyList.size() * 0.95);
            for (int i = 0; i < loopCount; i++) {
                int deletedIndex = rand.nextInt(keyList.size());
                String key = keyList.get(deletedIndex);
                st.delete(key);
                keyList.remove(deletedIndex);
            }*/
            st.delete("a0");
            st.delete("a1");
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
        st.show();
    }
}

class Tree234<K, V> {

    Node<K, V> root;
    int size = 0;

    public boolean contains(K key) {
        return get(key) != null;
    }

    public V get(K key) {
        if (root == null)
            return null;
        Node<K, V> x = nodeSearch(key);    //TODO 만약 get뿐만 아니라 다른 메소드에서도 찾는다면 root가 null일때도 추가해줘야함 datasearch도
        Node<K, V>.Element searchData = dataSearch(x, key);
        if (searchData != null)
            return searchData.value;
        return null;
    }

    private Node<K, V>.Element dataSearch(Node<K, V> x, K key) {
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

    private Node<K, V> nodeSearch(K key) {
        Node<K, V> x = root;
        int pos;
        while (true) {
            pos = getDataPos(x, key);
            if (x.data.get(pos).compareTo(key) == 0) {
                return x;
            } else if (x.data.get(pos).compareTo(key) > 0) {
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

    private int getDataPos(Node<K, V> x, K key) {
        int dataSize = x.data.size();
        for (int i = 0; i < dataSize; i++) {
            if (x.data.get(i).compareTo(key) >= 0)
                return i;
        }
        return dataSize - 1;
    }

    public void put(K key, V value) {
        Node<K, V> x = root;

        if (x == null) {
            root = new Node<>(key, value);
            size++;
            return;
        }
        Node<K, V> searchNode = nodeSearch(key);
        Node<K, V>.Element searchData = dataSearch(x, key);
        if (searchData != null)
            searchData.value = value;
            //TODO 혹시모르니까 안되면 고치기
        else {
            size++;
            insert(searchNode, key, value);
        }

    }

    private void insert(Node<K, V> node, K key, V value) {
        node.dataInsert(key, value);
        node.data.sort((o1, o2) -> o1.compareTo(o2.key));
        if (node.data.size() == 4) {
            if (node.parent == null) { //TODO 리펙토링하기
                node.parent = new Node<>(node.data.get(2).key, node.data.remove(2).value);
                node.parent.child.add(node);
                node.parent.child.add(new Node<>(node.data.get(2).key, node.data.remove(2).value));
                node.parent.child.get(1).parent = node.parent;
                root = node.parent;
                if (node.child.size() != 0) {
                    node.parent.child.get(1).child.add(node.child.get(3));
                    node.parent.child.get(1).child.add(node.child.get(4));
                    node.child.remove(3).parent = node.parent.child.get(1);
                    node.child.remove(3).parent = node.parent.child.get(1);
                }
            } else {
                int insertIndex = node.parent.child.indexOf(node) + 1;
                node.parent.child.add(insertIndex, new Node<>(node.data.get(3).key, node.data.remove(3).value));
                node.parent.child.get(insertIndex).parent = node.parent;
                if (node.child.size() != 0) {
                    node.parent.child.get(insertIndex).child.add(node.child.get(3));
                    node.parent.child.get(insertIndex).child.add(node.child.get(4));
                    node.child.remove(3).parent = node.parent.child.get(insertIndex);
                    node.child.remove(3).parent = node.parent.child.get(insertIndex);
                }
                insert(node.parent, node.data.get(2).key, node.data.remove(2).value);
            }
        }
    }

    public Iterable<K> keys() {
        ArrayList<K> keyList = new ArrayList<>(size());
        inorder(root, keyList);
        return keyList;
    }

    private void inorder(Node<K, V> x, ArrayList<K> keyList) {
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
        return size;
    }

    public int depth() {
        int depth = 0;
        Node<K, V> x = root;
        while (true) {
            depth++;
            if (x.child.size() == 0)
                return depth;
            else
                x = x.child.get(0);
        }
    }

    public void delete(K key) {
        size--;
        Node<K, V> searchNode = nodeSearch(key);
        int deletePos = getDataPos(searchNode , key);

        if(searchNode.child.size() != 0) // leaf node 가 아니라면 바꿔서 leaf node로 만들기
            searchNode = changeInorderSuccessor(searchNode,deletePos);

        remove(searchNode,key,deletePos); // 지우기

    }

    private void remove(Node<K, V> node, K key,int pos) {
        if(node.data.size() > 1)
            node.data.remove(pos);
        else{
            // case 1 : if(sibling size > 1)  : rotate
            // case 2 : if(sibling size == 1) : combine
            // case 2.1 : if ( parent data size = 1)
            // case 2.1.1 : parent.sibling size > 1 : node.parent == node.p.p.c
            int nodePos = node.parent.child.indexOf(node);
            int childSize = node.parent.child.size();
            int siblingPos = -1;
            Node<K,V> sibling = null;

            if (nodePos != 0 && nodePos + 1 < childSize) {
                if (node.parent.child.get(nodePos + 1).data.size() == 1) {
                    sibling = node.parent.child.get(nodePos - 1);
                    siblingPos = nodePos -1;
                }
                else {
                    sibling = node.parent.child.get(nodePos + 1);
                    siblingPos = nodePos +1;
                }
            } else {
                if (nodePos == 0) {
                    sibling = node.parent.child.get(1);
                    siblingPos = 1;
                }
                else if (nodePos + 1 == childSize) {
                    sibling = node.parent.child.get(nodePos - 1);
                    siblingPos = nodePos -1;
                }
            }

            if (sibling.data.size() > 1)
                rotate(node,sibling,siblingPos);
            else
                combine(node,sibling,siblingPos);
        }
    }

    private void combine(Node<K, V> node, Node<K, V> sibling, int siblingPos) {

        if(siblingPos < node.parent.child.indexOf(node)) {
            node.parent.child.remove(siblingPos + 1);
            sibling.data.add(sibling.parent.data.get(siblingPos));
            if(node.child.size() != 0) {
                node.child.get(0).parent =sibling;
                sibling.child.add(node.child.get(0));
            }
        }else{
            /*System.out.println("-------------------------");
            System.out.println(node.parent.child.size());
            System.out.println(node.parent.child.indexOf(node));
            System.out.println(siblingPos);
            System.out.println("-------------------------");*/
            node.parent.child.remove(siblingPos -1);
            sibling.data.add(0,sibling.parent.data.get(siblingPos -1));
            siblingPos -= 1;
            if(node.child.size() != 0) {
                node.child.get(0).parent =sibling;
                sibling.child.add(0,node.child.get(0));
            }
        }

        if (sibling.parent.data.size() == 1) {
            if (sibling.parent == root) {
                root = sibling;
                sibling.parent = null;
                if(node.child.size() != 0) {
                    node.child.get(0).parent =sibling;
                    if(node.data.get(0).compareTo(sibling.data.get(0).key) < 0)
                        sibling.child.add(0,node.child.get(0));
                    else
                        sibling.child.add(node.child.get(0));
                }
            }else
                remove(sibling.parent, sibling.parent.data.get(siblingPos).key, siblingPos);
        }
    }


    private void rotate(Node<K, V> node, Node<K, V> sibling ,int siblingPos) {
        if(siblingPos > node.parent.child.indexOf(node)) {
            node.data.set(0, node.parent.data.get(siblingPos - 1));
            node.parent.data.set(siblingPos - 1, sibling.data.remove(0));
            if(node.child.size() != 0){
                node.parent.child.get(siblingPos).child.get(0).parent = node;
                node.child.add(node.parent.child.get(siblingPos).child.remove(0));
            }
        }else{
            node.data.set(0, node.parent.data.get(siblingPos));
            node.parent.data.set(siblingPos, sibling.data.remove(sibling.data.size() -1));
            if(node.child.size() != 0){
                node.parent.child.get(siblingPos).child.get(sibling.child.size() -1).parent = node;
                node.child.add(node.parent.child.get(siblingPos).child.remove(sibling.child.size() -1));
            }
        }
    }


    private Node<K,V> changeInorderSuccessor(Node<K, V> node, int pos) {
        Node<K,V> inorderSuccessor = node.child.get(pos+1);
        while(true){
            if(inorderSuccessor.child.size() == 0) {
                node.swap(pos, inorderSuccessor.data);
                return inorderSuccessor;
            }
            else
                inorderSuccessor = inorderSuccessor.child.get(0);
        }
    }

    public void show() {
        for(K word : keys())
            System.out.print(word + " -");
        System.out.println("--- " +root.data.get(0).key + "  " + root.data.size() + "   " + root.child.size());
    }
}

class Node<K, V> {
    ArrayList<Element> data = new ArrayList<>(4);
    Node<K, V> parent;
    ArrayList<Node<K, V>> child = new ArrayList<>(5);

    public Node(K key, V value) {
        dataInsert(key, value);
    }

    public void dataInsert(K key, V value) {
        data.add(new Element(key, value));
    }

    public void swap(int pos, ArrayList<Element> inorderSuccessor) {
        Element tmp;
        tmp = inorderSuccessor.get(0);
        inorderSuccessor.set(0,data.get(pos));
        data.set(pos,tmp);
    }


    public class Element implements Comparable<K> {
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
}