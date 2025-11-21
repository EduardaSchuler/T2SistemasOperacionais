import java.io.*;
import java.util.*;


public class TLB {
    private final int capacity;
    private final LinkedHashMap<Long,Integer> map;


    public TLB(int capacity) {
        this.capacity = capacity;
        this.map = new LinkedHashMap<>();
    }


    public Integer lookup(long vpn) {
        return map.get(vpn);
    }


    public void add(long vpn, int frame) {
        if (capacity == 0) return;
        if (map.size() >= capacity) {
            Long eldest = map.keySet().iterator().next();
            map.remove(eldest);
        }
        map.put(vpn, frame);
    }


    public void dump(PrintWriter pw) {
        for (var e : map.entrySet()) pw.println("VPN="+e.getKey()+" -> frame="+e.getValue());
    }
}