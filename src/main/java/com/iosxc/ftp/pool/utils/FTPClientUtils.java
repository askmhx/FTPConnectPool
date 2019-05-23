package com.iosxc.ftp.pool.utils;

import com.iosxc.ftp.pool.config.FTPPoolConfig;
import com.iosxc.ftp.pool.core.FTPClientPoolManager;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 *
 * @author Crazz
 */
public class FTPClientUtils {

    private static Logger log = LoggerFactory.getLogger(FTPClientUtils.class);

    private static GenericObjectPool<FTPClient> ftpClientPool;

    private static boolean isInit = false;

    public static void initConfig(FTPPoolConfig config){
        FTPClientPoolManager.initConfig(config);
        ftpClientPool = FTPClientPoolManager.getPool();
        isInit = true;
    }

    /***
     * 上传Ftp文件
     * @param remotePath 上传服务器路径 - 应该以/结束
     * @param fileName 保存的文件名
     * @param inStream 上传的文件流
     * @return true or false
     */
    public static boolean uploadFile( String remotePath ,String fileName,InputStream inStream) {
        if (!isInit) {
            log.error("Please init ftp factory first! @see FTPClientUtils.initFactory(FTPPoolConfig properties)");
            return false;
        }
        FTPClient ftpClient = null;
        try {
            ftpClient = ftpClientPool.borrowObject();
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                log.warn("ftpServer refused connection, replyCode:{}", replyCode);
                return false;
            }
            ftpClient.changeWorkingDirectory("/");
            if (!ftpClient.changeWorkingDirectory(remotePath)){
                ftpClient.changeWorkingDirectory("/");
                if(remotePath.contains("/")){
                    String[] paths = remotePath.split("/");
                    for(String tph : paths){
                        try {
                            ftpClient.makeDirectory(tph);
                            ftpClient.changeWorkingDirectory(tph);
                        } catch (IOException e) {
                            e.printStackTrace();
                            log.error("3010创建目录 {} 失败: {}" , tph,e.toString() );
                        }
                    }
                }
            }
            log.info("start upload to:{} store name:{}",remotePath, fileName);
            boolean success = ftpClient.storeFile(fileName, inStream);
            if (success) {
                log.info("upload file success! {}", fileName);
                return true;
            }
            log.warn("upload file failure!");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("upload file failure!", e);
        } finally {
            IOUtils.closeQuietly(inStream);
            ftpClientPool.returnObject(ftpClient);
        }
        return false;
    }

    /***
     * 手工取得FTP Client
     * **/
    public static FTPClient borrowClient( ) {
        if (!isInit) {
            log.error("Please init ftp factory first! @see FTPClientUtils.initFactory(FTPPoolConfig properties)");
            return null;
        }
        try {
            return ftpClientPool.borrowObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /***
        * 归还FTP Client
     * **/
    public static void returnClient(FTPClient ftpClient ) {
        if (!isInit) {
            log.error("Please init ftp factory first! @see FTPClientUtils.initFactory(FTPPoolConfig properties)");
        }
        ftpClientPool.returnObject(ftpClient);
    }

    /***
     * 上传Ftp文件
     *
     * @param localFile 当地文件
     * @param remotePath 上传服务器路径 - 应该以/结束
     * @return true or false
     */
    public static boolean uploadFile(File localFile, String remotePath) {
        if (!isInit) {
            log.error("Please init ftp factory first! @see FTPClientUtils.initFactory(FTPPoolConfig properties)");
            return false;
        }
        try {
            return uploadFile(remotePath,localFile.getName(),new BufferedInputStream(new FileInputStream(localFile)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.error("file not found!{}", localFile);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("upload file failure!", e);
        }
        return false;
    }

    /**
     * 下载文件
     *
     * @param remotePath FTP服务器文件目录
     * @param fileName   需要下载的文件名称
     * @param localPath  下载后的文件路径
     * @return true or false
     */
    public  static boolean downloadFile(String remotePath, String fileName, String localPath) {
        if (!isInit) {
            log.error("Please init ftp factory first! @see FTPClientUtils.initFactory(FTPPoolConfig properties)");
            return false;
        }
        FTPClient ftpClient = null;
        OutputStream outputStream = null;
        try {
            ftpClient = ftpClientPool.borrowObject();
            // 验证FTP服务器是否登录成功
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                log.warn("ftpServer refused connection, replyCode:{}", replyCode);
                return false;
            }
            ftpClient.changeWorkingDirectory(remotePath);
            FTPFile[] ftpFiles = ftpClient.listFiles();
            for (FTPFile file : ftpFiles) {
                if (fileName.equalsIgnoreCase(file.getName())) {
                    File localFile = new File(localPath);
                    outputStream = new FileOutputStream(localFile);
                    ftpClient.retrieveFile(file.getName(), outputStream);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("download file failure!", e);
        } finally {
            IOUtils.closeQuietly(outputStream);
            ftpClientPool.returnObject(ftpClient);
        }
        return false;
    }

    /**
     * 删除文件
     *
     * @param remotePath FTP服务器保存目录
     * @param fileName   要删除的文件名称
     * @return true or false
     */
    public static boolean deleteFile(String remotePath, String fileName) {
        if (!isInit) {
            log.error("Please init ftp factory first! @see FTPClientUtils.initFactory(FTPPoolConfig properties)");
            return false;
        }
        FTPClient ftpClient = null;
        try {
            ftpClient = ftpClientPool.borrowObject();
            // 验证FTP服务器是否登录成功
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                log.warn("ftpServer refused connection, replyCode:{}", replyCode);
                return false;
            }
            // 切换FTP目录
            ftpClient.changeWorkingDirectory(remotePath);
            int delCode = ftpClient.dele(fileName);
            log.debug("delete file reply code:{}", delCode);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("delete file failure!", e);
        } finally {
            ftpClientPool.returnObject(ftpClient);
        }
        return false;
    }


}