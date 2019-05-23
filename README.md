# FTP Connection Pool Utils
[![](https://jitpack.io/v/menghx/FTPConnectPool.svg)](https://jitpack.io/#menghx/FTPConnectPool)

Fork from https://github.com/jayknoxqu/ftp-pool

建议使用FtpClientUtils来初始化和操作FTP

### 项目启动时执行
```
FtpClientUtils.initFactory(FtpClientProperties properties);
```
### 调用相关方法
#####上传文件
```
FtpClientUtils.uploadFile(File localFile, String remotePath) 
```
#####下载文件
```
FtpClientUtils.downloadFile(String remotePath, String fileName, String localPath)
```
#####删除文件
```
FtpClientUtils.deleteFile(String remotePath, String fileName)
```