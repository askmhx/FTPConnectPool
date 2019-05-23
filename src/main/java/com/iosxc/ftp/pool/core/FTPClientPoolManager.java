package com.iosxc.ftp.pool.core;

import com.iosxc.ftp.pool.config.FTPPoolConfig;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * User: askmhx@gmail.com
 */
public class FTPClientPoolManager extends GenericObjectPool<FTPClient>{


    private static GenericObjectPool<FTPClient> ftpClientPool ;

    private static FTPPoolConfig ftpPoolConfig;

    private FTPClientPoolManager(PooledObjectFactory<FTPClient> factory) {
        super(factory);
    }


    public static void initConfig(FTPPoolConfig config){
        ftpPoolConfig = config;
    }

    public static GenericObjectPool<FTPClient> getPool(){
        if (ftpClientPool==null){
            synchronized (FTPClientPoolManager.class){
                if (ftpClientPool==null){
                    ftpClientPool = new FTPClientPoolManager(new FTPClientFactory(ftpPoolConfig));
                    ftpClientPool.setMaxIdle(ftpPoolConfig.getMaxIdle());
                    ftpClientPool.setMaxTotal(ftpPoolConfig.getMaxTotal());
                    ftpClientPool.setMinIdle(ftpPoolConfig.getMinIdle());
                }
            }
        }
        return ftpClientPool;
    }

}
