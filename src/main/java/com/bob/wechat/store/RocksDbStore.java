package com.bob.wechat.store;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.rocksdb.FlushOptions;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

@Slf4j
public class RocksDbStore {

    private static final String PATH = "./rocks.db";

    private static RocksDbStore INSTASNCE = new RocksDbStore();

    private final RocksDB rocksDB;

    static {
        RocksDB.loadLibrary();
    }

    public static RocksDbStore getInstance() {
        return INSTASNCE;
    }

    @SneakyThrows
    private RocksDbStore() {
        Options options = new Options();
        options.setCreateIfMissing(true);
        this.rocksDB = RocksDB.open(options, PATH);
    }

    public void save(byte[] key, byte[] value,boolean flush) {
        try {
            rocksDB.put(key, value);
            if(flush){
                rocksDB.flush(new FlushOptions());
            }
        } catch (RocksDBException e) {
            log.error("save rocksdb fail, key: {}", new String(key), e);
        }
    }

    public byte[] get(byte[] key) {
        try {
            return rocksDB.get(key);
        } catch (RocksDBException e) {
            log.error("get from rocksdb fail, key: {}", new String(key), e);
        }
        return null;
    }

    public void delete(byte[] key) {
        try {
            rocksDB.delete(key);
        } catch (RocksDBException e) {
            log.error("delete from rocksdb fail, key: {}", new String(key), e);
        }
    }
}
