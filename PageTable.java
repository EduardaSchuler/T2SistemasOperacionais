import java.io.*;


public class PageTable{

    private final int levels;
    private final Map<Integer,Object> root = new HashMap<>();

    public PageTable(int l) { levels = l; }

    public Integer lookup(long vpn) {
        int[] idx = split(vpn);
        Map<Integer,Object> cur = root;
        for (int i = 0; i < levels - 1; i++) {
            Object o = cur.get(idx[i]);
            if (!(o instanceof Map)) return null;
            cur = (Map)o;
        }
        return (Integer)cur.getOrDefault(idx[levels-1], null);
    }


    public void insert(long vpn, int frame) {
        int[] idx = split(vpn);
        Map<Integer,Object> cur = root;
        for (int i = 0; i < levels - 1; i++) {
            cur.putIfAbsent(idx[i], new HashMap<Integer,Object>());
            cur = (Map)cur.get(idx[i]);
        }
        cur.put(idx[levels-1], frame);
    }


    public void remove(long vpn) {
        int[] idx = split(vpn);
        removeRec(root, idx, 0);
    }


    private boolean removeRec(Map<Integer,Object> cur, int[] idx, int d) {
        Object o = cur.get(idx[d]);
        if (o == null) return false;
        if (d == levels - 1) cur.remove(idx[d]);
        else if (o instanceof Map) {
            boolean empty = removeRec((Map)o, idx, d+1);
            if (((Map)o).isEmpty()) cur.remove(idx[d]);
        }
        return cur.isEmpty();
    }


    public void dump(PrintWriter pw) { dumpRec(root, pw, ""); }
    
    private void dumpRec(Map<Integer,Object> cur, PrintWriter pw, String p) {
        for (var e : cur.entrySet()) {
            if (e.getValue() instanceof Integer)
                pw.println(p+"["+e.getKey()+"] -> frame="+e.getValue());
            else
                dumpRec((Map)e.getValue(), pw, p+" ");
        }
    }


    private int[] split(long vpn) {
        int baseBits = 10;
        int[] out = new int[levels];
        for (int i = levels - 1; i >= 0; i--) {
            out[i] = (int)(vpn & ((1<<baseBits)-1));
            vpn >>= baseBits;
        }
        return out;
    }
}