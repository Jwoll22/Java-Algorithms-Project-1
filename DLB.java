import java.util.*;

public class DLB implements DictInterface
{
    private Node root;
    private Node n;
    private Node pointer;

    private static class Node
    {
        private Object val;
        private char key = (char)0;
        private Node next;
        private Node child;
    }

    public DLB()
    {
        root = new Node();
    }

    public void put(String s, String val)
    {
        pointer = put(root, s, val, 0);
    }

    private Node put(Node x, String s, String val, int d)
    {
        if (x == null) x = new Node();
        if (d == s.length())
        {
            x.val = val;
          //  System.out.println("val: " + x.val);
            return x;
        }

        char c = s.charAt(d);
        do
        {
            if (x.key == (char)0)  //new child
            {
                x.key = c; break;
            }
            else if (x.key == c)
            {
                break;
            }
            else if (x.next == null)
            {
                x.next = new Node();
                x.next.key = c;
                x = x.next; break;
            }

            x = x.next;
        }   while (x != null);

        //System.out.print("key: " +x.key);
        //System.out.println();

        if (x.child == null) x.child = put(x.child, s, val, d+1);
        else    put(x.child, s, val, d+1);
        return x;
    }

    public boolean add(String s)
    {
        put(s, "*");
        return true;
    }

    public int searchPrefix(StringBuilder s, int size)
    {
        n = root;
        return searchPrefix(s, 0, s.length()-1, size);
    }

    public int searchPrefix(StringBuilder s, int start, int end, int size)
    {
        boolean prefix, word, possible;
        char c = s.charAt(start);

        if (n == null) return 0;
        do
        {
            //System.out.print(n.key);
            if (c == n.key) break;
            n = n.next;
        }   while (n != null);
        //System.out.println("   next level");

        if (n == null) return 0;
        prefix = start == s.length()-1 && n.child != null && n.child.key != (char)0;
        word = start == s.length()-1 && n.child != null && n.child.val!= null && n.child.val.equals("*");
        possible = false;
        //if (prefix) System.out.println(n.child.val + " and " + n.child.key);

        if (prefix && possible) return 3;
        else if (word && prefix) return 2;
        else if (word) return 1;
        else if (prefix) return 3;

        n = n.child;
        return searchPrefix(s, start+1, end, size);
    }
}
