# FTP Connection Pool Utils
[![](https://jitpack.io/v/menghx/FTPConnectPool.svg)](https://jitpack.io/#menghx/FTPConnectPool)

Fork from https://github.com/jayknoxqu/ftp-pool

建议使用FtpClientUtils来初始化和操作FTP

### 项目启动时执行
```
FTPClientUtils.initConfig(FTPPoolConfig config);
```
### 调用相关方法

#####上传文件

```
FTPClientUtils.uploadFile(File localFile, String remotePath) 
```

#####下载文件

```
FTPClientUtils.downloadFile(String remotePath, String fileName, String localPath)
```

#####删除文件

```
FTPClientUtils.deleteFile(String remotePath, String fileName)
```