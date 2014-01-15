/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.jboss.devstudio.core.installer;

import java.io.File;
import java.util.Iterator;

import javax.swing.JLabel;

import com.izforge.izpack.Pack;
import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.installer.InstallerFrame;
import com.izforge.izpack.installer.IzPanel;
import com.izforge.izpack.util.IoHelper;

public class DiskSpaceCheckPanel extends IzPanel
{

   private static final long serialVersionUID = 1233443616359322170L;
   
   public DiskSpaceCheckPanel(InstallerFrame parent, InstallData idata) {
      super(parent, idata);
      setHidden(true);
   }

   public void panelActivate() {
     
      long freeSpace = getFreeSpace();
      long totalSize = getInstallationSize();

      this.idata.setVariable("TIMESTAMP",Long.toString(System.currentTimeMillis()));
      this.idata.setVariable("ESTIMATED_SIZE",Long.toString(totalSize/1024));

      if(freeSpace >= 0)
      {
         long freeAfterInstall = freeSpace - totalSize;
         if(freeAfterInstall >= 0)
         {
            parent.skipPanel();
         }
         else
         {
            parent.lockNextButton();
            this.removeAll();
            add(new JLabel("Insufficient space! The available space is "
                  + getFreeSpaceString() +". At least " +
                  Pack.toByteUnitsString(totalSize) + " are required."));
         }
      }
   }
   
   private long getInstallationSize()
   {
      long totalSize = 0;
      Iterator iter = idata.selectedPacks.iterator();
      while( iter.hasNext() )
      {
         Pack p = (Pack) iter.next();
         totalSize += p.nbytes;
      }
      return totalSize;
   }

   public String getSummaryBody()
   {
      return "Available Disk Space: " + longToBytes(getFreeSpace()) + "<br>"
      + "Required Disk Space: " + longToBytes(getInstallationSize()) + "<br>";
   }
   
   protected long getFreeSpace()
   {
       long freeBytes = -1;
       if (IoHelper.supported("getFreeSpace"))
       {
           freeBytes = IoHelper.getFreeSpace(IoHelper.existingParent(
                   new File(idata.getInstallPath())).getAbsolutePath());
       }
       return freeBytes;
   }
   
   private String getFreeSpaceString()
   {
      String msg = null;
      long freeBytes = getFreeSpace();
      if (freeBytes < 0)
         msg = parent.langpack.getString("PacksPanel.notAscertainable");
      else
         msg = Pack.toByteUnitsString(freeBytes);
      return msg;
   }
   
   private String longToBytes(long bytes)
   {
      if(bytes < 0) return "N/A";
      return Pack.toByteUnitsString(bytes);
   }
   
}
