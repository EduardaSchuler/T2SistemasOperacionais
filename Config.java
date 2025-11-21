public class Config {
    public int tlbEntriesBits;
    public int virtualAddrBits;
    public int physicalAddrBits;
    public int pageSizeBits;
    public int pageTableLevels;
    public int textBits;
    public int dataBits;
    public int stackBits;


    public int tlbEntries;
    public long virtualSpaceSize;
    public long physicalSpaceSize;
    public int pageSize;
    public int numFrames;
    public long textSize, dataSize, stackSize, bssSize;


    public static Config fromJson(String json) {
        Config c = new Config();
        json = json.replaceAll("[{}\s]", "");
        for (String kv : json.split(",")) {
            String[] p = kv.split(":");
            String k = p[0];
            long v = Long.parseLong(p[1]);
            switch (k) {
                case "tlbEntriesBits": c.tlbEntriesBits = (int)v; break;
                case "virtualAddrBits": c.virtualAddrBits = (int)v; break;
                case "physicalAddrBits": c.physicalAddrBits = (int)v; break;
                case "pageSizeBits": c.pageSizeBits = (int)v; break;
                case "pageTableLevels": c.pageTableLevels = (int)v; break;
                case "textBits": c.textBits = (int)v; break;
                case "dataBits": c.dataBits = (int)v; break;
                case "stackBits": c.stackBits = (int)v; break;
            }
        }
        c.postInit();
        return c;
    }


    private void postInit() {
        tlbEntries = tlbEntriesBits <= 0 ? 0 : 1 << tlbEntriesBits;
        virtualSpaceSize = 1L << virtualAddrBits;
        physicalSpaceSize = 1L << physicalAddrBits;
        pageSize = 1 << pageSizeBits;
        numFrames = (int)(physicalSpaceSize / pageSize);
        textSize = 1L << textBits;
        dataSize = 1L << dataBits;
        stackSize = 1L << stackBits;
        long used = textSize + dataSize + stackSize;
        bssSize = virtualSpaceSize - used;
    }    
}
