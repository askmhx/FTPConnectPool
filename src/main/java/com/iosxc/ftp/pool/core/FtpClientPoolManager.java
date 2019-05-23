package com.iosxc.ftp.pool.core;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * User: askmhx@gmail.com
 */
public class FtpClientPoolManager extends GenericObjectPool<FTPClient>{


    private static GenericObjectPool<FTPClient> ftpClientPool ;

    private static FtpClientFactory ftpClientFactory;

    private FtpClientPoolManager(PooledObjectFactory<FTPClient> factory) {
        super(factory);
    }


    public static void initFactory(FtpClientFactory factory){
        ftpClientFactory = factory;
    }

    public static GenericObjectPool<FTPClient> getPool(){
        if (ftpClientPool==null){
            synchronized (FtpClientPoolManager.class){
                if (ftpClientPool==null){
                    ftpClientPool = new FtpClientPoolManager(ftpClientFactory);
                }
            }
        }
        return ftpClientPool;
    }

}
