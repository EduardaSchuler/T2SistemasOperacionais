import java.util.Map;
import java.util.HashMap;
import java.io.PrintWriter;

public class PageTable {

    private final int levels;
    private final int vpnBits;
    private final int[] bitsPerLevel;
    private final Map<Integer, Object> root = new HashMap<>();

    public PageTable(int l, int vpnBits) {
        levels = l;
        this.vpnBits = Math.max(0, vpnBits);
        this.bitsPerLevel = computeBitsPerLevel(this.vpnBits, levels);
    }

    @SuppressWarnings("unchecked")
    public Integer lookup(long vpn) {
        int[] idx = split(vpn);
        Map<Integer, Object> cur = root;
        for (int i = 0; i < levels - 1; i++) {
            Object o = cur.get(idx[i]);
            if (!(o instanceof Map)) return null;
            cur = (Map<Integer, Object>) o;
        }
        return (Integer) cur.getOrDefault(idx[levels - 1], null);
    }

    @SuppressWarnings("unchecked")
    public void insert(long vpn, int frame) {
        int[] idx = split(vpn);
        Map<Integer, Object> cur = root;
        for (int i = 0; i < levels - 1; i++) {
            cur.putIfAbsent(idx[i], new HashMap<Integer, Object>());
            cur = (Map<Integer, Object>) cur.get(idx[i]);
        }
        cur.put(idx[levels - 1], frame);
    }

    public void remove(long vpn) {
        int[] idx = split(vpn);
        removeRec(root, idx, 0);
    }

    @SuppressWarnings("unchecked")
    private boolean removeRec(Map<Integer, Object> cur, int[] idx, int d) {
        Object o = cur.get(idx[d]);
        if (o == null) return false;
        if (d == levels - 1) cur.remove(idx[d]);
        else if (o instanceof Map) {
            boolean empty = removeRec((Map<Integer, Object>) o, idx, d + 1);
            if (((Map<Integer, Object>) o).isEmpty()) cur.remove(idx[d]);
        }
        return cur.isEmpty();
    }

    public void dump(PrintWriter pw) { dumpRec(root, pw, 0, 0L); }

    @SuppressWarnings("unchecked")
    private void dumpRec(Map<Integer, Object> cur, PrintWriter pw, int depth, long accVpn) {
        for (var e : cur.entrySet()) {
            int key = (Integer) e.getKey();
            Object val = e.getValue();
            if (depth >= levels - 1) {
                if (val instanceof Integer) {
                    long fullVpn = (accVpn << bitsPerLevel[depth]) | (key & ((1 << bitsPerLevel[depth]) - 1));
                    pw.println("VPN=" + fullVpn + " -> frame=" + val);
                } else if (val instanceof Map) {
                    dumpRec((Map<Integer, Object>) val, pw, depth + 1, (accVpn << bitsPerLevel[depth]) | key);
                }
            } else {
                if (val instanceof Map) {
                    long newAcc = (accVpn << bitsPerLevel[depth]) | (key & ((1 << bitsPerLevel[depth]) - 1));
                    dumpRec((Map<Integer, Object>) val, pw, depth + 1, newAcc);
                } else if (val instanceof Integer) {
                    // Unexpected: an integer at non-leaf depth; still reconstruct a vpn using zeros for remaining levels
                    long fullVpn = (accVpn << bitsPerLevel[depth]) | (key & ((1 << bitsPerLevel[depth]) - 1));
                    for (int d = depth + 1; d < levels; d++) fullVpn <<= bitsPerLevel[d];
                    pw.println("VPN=" + fullVpn + " -> frame=" + val);
                }
            }
        }
    }

    private int[] split(long vpn) {
        int[] out = new int[levels];
        for (int i = levels - 1; i >= 0; i--) {
            int b = bitsPerLevel[i];
            int mask = b == 0 ? 0 : ((1 << b) - 1);
            out[i] = (int) (vpn & mask);
            if (b > 0) vpn >>= b;
        }
        return out;
    }

    private int[] computeBitsPerLevel(int vpnBits, int levels) {
        int[] out = new int[levels];
        if (levels <= 0) return out;
        int base = vpnBits / levels;
        int rem = vpnBits % levels;
        // distribute remainder to the least-significant levels (rightmost)
        for (int i = 0; i < levels; i++) {
            out[i] = base + (i >= levels - rem ? 1 : 0);
        }
        return out;
    }
}