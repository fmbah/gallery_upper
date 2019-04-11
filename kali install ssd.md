### install Kali linux on pssd
###1. 优点
* 它是非破坏性的-它不会对主机系统的硬盘或已安装的操作系统进行任何更改，要恢复正常操作，只需移除“kali live”USB驱动器并重新启动系统即可。
* 它是便携式的-你可以把kali linux放在口袋里，在一个可用的系统上运行几分钟。
* 它是可定制的-你可以滚动你自己定制的kali-linux-iso映像，然后用同样的程序把它放到一个usb驱动器上。
* 它可能是永久性的-通过一点额外的努力，您可以配置您的kali-linux“live”USB驱动器具有永久性存储，因此您收集的数据会在重新启动时保存。

###2. 你需要什么
* 您将要运行的系统的最新kali构建映像的适当iso映像的验证副本：请参阅有关下载官方kali Linux映像的详细信息。
* 如果您在Windows下运行，您还需要下载win32 disk imager实用程序。在Linux和OS X上，可以使用dd命令，该命令是预先安装在这些平台上的。
* 一个4GB或更大的USB U盘。（具有直接SD卡插槽的系统可以使用具有类似容量的SD卡。程序相同。）

###3. Kali Linux Live USB安装过程
1. 在Windows上创建可引导的Kali USB驱动器
    * 将USB驱动器插入Windows PC上可用的USB端口，记下安装后使用的驱动器指示符（例如“F:\”），然后启动下载的win32磁盘映像仪软件。
    * 选择要映像的kali linux iso文件，并验证要覆盖的USB驱动器是否正确。单击“写入”按钮。
    * 成像完成后，从Windows计算机安全弹出USB驱动器。现在您可以使用USB设备引导到kali linux。
    
###4. 向Kali Linux“Live”USB驱动器添加持久性
* 在这个例子中，我们将创建一个新的分区来存储持久数据，从第二个kali-live分区的正上方开始，到7GB结束，将ext3文件系统放到它上面，并在新分区上创建persistence.conf文件。
    * 首先，如本文所述，首先将最新的kali linux iso（当前为2016.2）映像到您的USB驱动器。我们假设图像创建的两个分区是/dev/sdb1和/dev/sdb2。这可以通过命令“fdisk-l”进行验证。
    * 在USB驱动器上创建并格式化其他分区。
     ````      
     end=7gb
     read start _ < <(du -bcm kali-linux-2016.2-amd64.iso | tail -1); echo $start
     parted /dev/sdb mkpart primary $start $end
     ````
     parted命令可能会建议您不能使用您指定的确切起始值；如果是这样，请接受建议的值。如果建议分区没有放置在最佳位置，请忽略它。当parted完成时，应该在/dev/sdb3处创建新分区；同样，可以使用命令“fdisk-l”验证这一点。
    * 接下来，在分区中创建一个ext3文件系统，并将其标记为“持久性”。
    ````
    mkfs.ext3 -L persistence /dev/sdb3
    e2label /dev/sdb3 persistence
    ````
    * 创建一个装入点，在那里装入新分区，然后创建配置文件以启用持久性。最后，卸载分区。
    ````
        mkdir -p /mnt/my_usb
        mount /dev/sdb3 /mnt/my_usb
        echo "/ union" > /mnt/my_usb/persistence.conf
        umount /dev/sdb3
        ````
###5. 使用luks加密添加USB持久性
* 您可以创建一个luks加密的持久存储区域。当您使用Kali Live在USB设备上旅行时，这为您的敏感文件添加了额外的安全层。在下面的示例中，我们将创建一个新的分区来存储持久性数据，从第二个kali-live分区的正上方开始，到7GB结束，在新分区上设置luks加密，将ext3文件系统放到它上面，并在它上面创建persistence.conf文件。
    * 如本文所述，将最新的kali linux iso（当前为2016.2）映像到您的USB驱动器。
    * 在Kali Live分区上方的空白空间中创建新分区。
    * asdfasdf
    * asdfasdf


