/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.modeler.client.gxtextends;

import java.util.List;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.GroupRef;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.ScreenPairRef;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.TreePanelDropTarget;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.dnd.DND.Operation;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;

/**
 * The Class defines a dragSource for the <b>ProfilePanel</b> can drop its nodes on self.
 */
public class TreePanelDropTargetPanelTreeExt extends TreePanelDropTarget {
   public TreePanelDropTargetPanelTreeExt(TreePanel<BeanModel> tree) {
      super(tree);
      setAllowSelfAsSource(true);
      setOperation(Operation.MOVE);
      setAutoExpand(false);
      setFeedback(Feedback.BOTH);
      setAllowDropOnLeaf(true);
   }

   @SuppressWarnings("unchecked")
   @Override
   protected void onDragDrop(DNDEvent event) {
      boolean successed = false;

      if (activeItem == null || event.getData() == null) {
         event.setCancelled(true);
         return;
      }

      BeanModel targetNode = (BeanModel) activeItem.getModel();
      BeanModel sourceNode = ((List<ModelData>) event.getData()).get(0).get("model");
      BeanModel sourceParentNode = (BeanModel) tree.getStore().getParent(sourceNode);
      BeanModel targetParentNode = (BeanModel) tree.getStore().getParent(targetNode);

      if (status == -1) { // append operation
         tree.getView().onDropChange(activeItem, false);
         if (sourceParentNode == targetNode) {
            tree.getStore().remove(sourceNode);
            handleAppendDrop(event, activeItem);
            doAppend(sourceParentNode, sourceNode, targetNode);
            successed = true;
         } else if (sourceNode.getBean() instanceof ScreenPairRef && targetNode.getBean() instanceof GroupRef
               && inSamePanel(sourceParentNode, targetNode) && canMove(sourceNode, targetNode)) {
            tree.getStore().remove(sourceNode);
            handleAppendDrop(event, activeItem);
            appendScreen(sourceParentNode, sourceNode, targetNode);
            successed = true;
         }
      } else if (targetParentNode == sourceParentNode) { // insert operation
         tree.getStore().remove(sourceNode);
         handleInsertDrop(event, activeItem, status);
         doInsert(sourceParentNode, sourceNode, targetNode);
         successed = true;
      } else if (sourceNode.getBean() instanceof ScreenPairRef && targetParentNode.getBean() instanceof GroupRef
            && inSamePanel(sourceParentNode, targetParentNode) && canMove(sourceNode, targetParentNode)) {
         tree.getStore().remove(sourceNode);
         handleInsertDrop(event, activeItem, status);
         reorderScreen(sourceParentNode, sourceNode, targetNode);
         successed = true;
      }
      if (!successed) {
         event.setCancelled(true);
      }
   }
   
   @Override
   public void setGroup(String group) {
      super.setGroup(Constants.REORDER_PANEL_GROUP);
   }
   private boolean canMove(BeanModel scrRefBean, BeanModel groupRefBean) {
      GroupRef groupRef = groupRefBean.getBean();
      ScreenPairRef scrRef = scrRefBean.getBean();
      List<ScreenPairRef> screenRefs = groupRef.getGroup().getScreenRefs();
      for (ScreenPairRef ref : screenRefs) {
         if (ref.getScreenId() == scrRef.getScreenId()) {
            return false;
         }
      }
      return true;
   }
   private boolean inSamePanel(BeanModel sourceGroupRef, BeanModel targetGroupRef) {
      BeanModel sourceGrandFatherNode = (BeanModel) tree.getStore().getParent(sourceGroupRef);
      BeanModel targetGrandFatherNode = (BeanModel) tree.getStore().getParent(targetGroupRef);
      return sourceGrandFatherNode.equals(targetGrandFatherNode);
   }
   private void appendScreen(BeanModel sourceGroupRefBeanModel, BeanModel sourceScreenRefBeanModel,
         BeanModel targetGroupRefBeanModel) {
      ScreenPairRef sourceScreenRef = sourceScreenRefBeanModel.getBean();
      GroupRef targetGroupRef = targetGroupRefBeanModel.getBean();
      targetGroupRef.getGroup().addScreenRef(sourceScreenRef);
      GroupRef sourceGroupRef = sourceGroupRefBeanModel.getBean();
      sourceGroupRef.getGroup().removeScreenRef(sourceScreenRef);
      sourceScreenRef.getScreen().setParentGroup(targetGroupRef.getGroup());
      sourceScreenRef.setGroup(targetGroupRef.getGroup());
   }
   private void reorderScreen(BeanModel sourceGroupRefBean, BeanModel fromBean, BeanModel toBean) {
      Group sourceGroup = ((GroupRef) sourceGroupRefBean.getBean()).getGroup();
      Group targetGroup = ((ScreenPairRef) toBean.getBean()).getGroup();
      ScreenPairRef from = fromBean.getBean();
      ScreenPairRef to = toBean.getBean();
      if (!sourceGroup.equals(to.getGroup())) {
         targetGroup = to.getGroup();
      }
      sourceGroup.removeScreenRef(from);
      targetGroup.insertScreenRef(to, from);
   }

   private void doAppend(BeanModel sourceParent, BeanModel source, BeanModel target) {
      if (sourceParent.getBean() instanceof GroupRef && source.getBean() instanceof ScreenPairRef
            && target.getBean() instanceof GroupRef) {
         appendScreen(sourceParent, source, target);
      } else if (sourceParent.getBean() instanceof Panel && source.getBean() instanceof GroupRef
            && target.getBean() instanceof Panel) {
         appendGroup(sourceParent, source, target);
      }
   }

   private void doInsert(BeanModel sourceParent, BeanModel source, BeanModel insertTo) {
      if (sourceParent.getBean() instanceof GroupRef && source.getBean() instanceof ScreenPairRef
            && insertTo.getBean() instanceof ScreenPairRef) {
         reorderScreen(sourceParent, source, insertTo);
      } else if (sourceParent.getBean() instanceof Panel && source.getBean() instanceof GroupRef
            && insertTo.getBean() instanceof GroupRef) {
         reorderGroup(sourceParent, source, insertTo);
      }
   }

   private void appendGroup(BeanModel sourcePanelBean, BeanModel groupRefBean, BeanModel targetPanelBean) {
      Panel sourcePanel = sourcePanelBean.getBean();
      Panel targetpanel = targetPanelBean.getBean();
      GroupRef groupRef = groupRefBean.getBean();
      sourcePanel.removeGroupRef(groupRef);
      targetpanel.addGroupRef(groupRef);
   }
   private void reorderGroup(BeanModel sourcePanelBean, BeanModel fromBean, BeanModel toBean) {
      Panel panel = sourcePanelBean.getBean();
      GroupRef from = fromBean.getBean();
      panel.removeGroupRef(from);
      GroupRef to = toBean.getBean();
      panel.insertGroupRef(to, from);
   }

}
