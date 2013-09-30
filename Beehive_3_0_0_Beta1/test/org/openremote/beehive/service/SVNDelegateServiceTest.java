/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.openremote.beehive.service;

import org.openremote.beehive.SpringTestContext;
import org.openremote.beehive.TestBase;
import org.openremote.beehive.api.service.SVNDelegateService;

/**
 * @author Tomsky
 * 
 */
public class SVNDelegateServiceTest extends TestBase {
	private SVNDelegateService svnDelegateService = (SVNDelegateService) SpringTestContext.getInstance().getBean(
         "svnDelegateService");

   public void testCopyFromScrapToWC() {
      // svnDelegateService.copyFromScrapToWC("c:/remotes", "c:/workCopy");
   }

   public void testCommit() {
      // String[] paths = {"/audiovox/Sirius_PNP2"};
      // svnDelegateService.commit(paths, "modify /audiovox/Sirius_PNP2", "admin");

      // String path = "";
      // DiffStatus diffStatus = svnDelegateService.getDiffStatus(path);
      // for (Element element: diffStatus.getDiffStatus()) {
      // System.out.println(element.getPath());
      // }
      // svnDelegateService.commit(paths, "commit all changes", "admin");

      // String[] paths = {"/3m","/3m/MP8640"};
      // List<UpdatedFile> updatedFiles = svnDelegateService.commit(paths, "remove", "admin");
      // System.out.println(updatedFiles.size());
      // for (UpdatedFile updatedFile : updatedFiles) {
      // System.out.println(updatedFile.getFile().getPath()+"=="+updatedFile.getStatus()+"--"+updatedFile.isIsDir());
      // }
   }

   public void testRevert() {
      // String path = "/orava/RC_5219";
      // svnDelegateService.revert(path, false);
   }

   public void testDoExport() {
      // String srcUrl = "/sky";
      // String destPath = "d:/tst2";
      // svnDelegateService.doExport(srcUrl, destPath, 26, true);
   }

   public void testRollback() {
      // String path = "/";
      // svnDelegateService.rollback(path, 104);
   }

   public void testGetList() {
      // String url = "/orava";
      // List<LIRCEntryDTO> ls = svnDelegateService.getList(url, 28);
      // for (LIRCEntryDTO entryDTO : ls) {
      // System.out.println("path: " + entryDTO.getPath()+ " version: " + entryDTO.getVersion());
      // if(entryDTO.isFile()){
      // System.out.println(entryDTO.getContent());
      // }
      // }
   }

   public void testGetLogs() {
      // String path = "/3m";
      // svnDelegateService.getDiffStatus(path);
      // List<LogMessage> lms = svnDelegateService.getLogs(path);
      // System.out.println(lms.get(lms.size()-1).getRevision());

      // for (LogMessage log : lms) {
      // System.out.print(log.getRevision()+" , "+log.getAuthor()+" , "+log.getComment()+" actions= ");
      // for (Character action : log.getActions()) {
      // System.out.print(action+" ");
      // }
      // System.out.println();
      // for (ChangePath ch : log.getChangePaths()) {
      // System.out.println(ch.getPath()+", "+ch.getAction());
      // }
      // }
   }

   public void testCopyFromUpload() {
      // String srcPath = "d:/sky/Rev4";
      // String destPath = "/sky/Rev4";
      // svnDelegateService.copyFromUploadToWC(srcPath, destPath);
   }

   public void testDeleteFile() {
      // String path = "/sky/Rev4";
      // svnDelegateService.deleteFileFromRepo(path, "test");
   }

   /**
    * 
    * this is test the workCopy with head version
    */
   public void testDiff() {
      // String url = "/abit/AU10";
      // DiffResult dr = svnDelegateService.diff(url);
      // List<Line> leftLines = dr.getLeft();
      // System.out.println(leftLines.size());
      // if(leftLines.isEmpty()){
      // System.out.println("--------------------------------");
      // }else{
      // for (Line leftLine : leftLines) {
      // System.out.println(leftLine.getChangeType()+": "+leftLine.getLine());
      // }
      // }
      //		
      // List<Line> rightLines = dr.getRight();
      // System.out.println(rightLines.size());
      // if(rightLines.isEmpty()){
      // System.out.println("++++++++++++++++++++++++++++++++");
      //			
      // }else{
      // for (Line rightLine : rightLines) {
      // System.out.println(rightLine.getChangeType()+": "+rightLine.getLine());
      // }
      // }
   }

   public void testGetdiffStatus() {
      // String path = "/3m";
      // DiffStatus ds = svnDelegateService.getDiffStatus(path);
      // for (Element e : ds.getDiffStatus()) {
      // System.out.println(e.getPath()+": ");
      // }
   }
}
