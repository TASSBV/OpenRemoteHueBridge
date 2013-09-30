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
package org.openremote.modeler.client.widget.uidesigner;

import org.openremote.modeler.client.icon.IconResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

public class UIDesignerToolbarImpl extends Composite implements UIDesignerToolbar {

  private static UIDesignerToolbarUiBinder uiBinder = GWT.create(UIDesignerToolbarUiBinder.class);

  @UiTemplate("UIDesignerToolbar.ui.xml")
  interface UIDesignerToolbarUiBinder extends UiBinder<Widget, UIDesignerToolbarImpl> {
  }

  public UIDesignerToolbarImpl() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  private Presenter presenter;
  
  @UiField(provided=true)
  final IconResources resources = IconResources.INSTANCE;
  
  @UiField
  PushButton horizontalLeftAlignButton;
  @UiField
  PushButton horizontalCenterAlignButton;
  @UiField
  PushButton horizontalRightAlignButton;
  
  @UiField
  PushButton verticalTopAlignButton;
  @UiField
  PushButton verticalCenterAlignButton;
  @UiField
  PushButton verticalBottomAlignButton;
  
  @UiField
  PushButton sameSizeButton;
  
  @UiField
  PushButton horizontalSpreadButton;
  @UiField
  PushButton verticalSpreadButton;
  
  @UiField
  PushButton horizontalCenterButton;
  @UiField
  PushButton verticalCenterButton;

  public UIDesignerToolbarImpl(String firstName) {
    initWidget(uiBinder.createAndBindUi(this));
  }

  @UiHandler("horizontalLeftAlignButton")
  void onLeftAlignClick(ClickEvent e) {
    if (presenter != null) {
      presenter.onHorizontalLeftAlignButtonClicked();
    }
  }
  
  @UiHandler("horizontalCenterAlignButton")
  void onMiddleAlignClicked(ClickEvent e) {
    if (presenter != null) {
      presenter.onHorizontalCenterAlignButtonClicked();
    }
  }

  @UiHandler("horizontalRightAlignButton")
  void onRightAlignClicked(ClickEvent e) {
    if (presenter != null) {
      presenter.onHorizontalRightAlignButtonClicked();
    }
  }

  @UiHandler("verticalTopAlignButton")
  void onVerticalTopAlignClicked(ClickEvent e) {
    if (presenter != null) {
      presenter.onVerticalTopAlignButtonClicked();
    }
  }
  
  @UiHandler("verticalCenterAlignButton")
  void onVerticalCenterAlignClicked(ClickEvent e) {
    if (presenter != null) {
      presenter.onVerticalCenterAlignButtonClicked();
    }
  }
  
  @UiHandler("verticalBottomAlignButton")
  void onVerticalBottomAlignClicked(ClickEvent e) {
    if (presenter != null) {
      presenter.onVerticalBottomAlignButtonClicked();
    }
  }
  
  @UiHandler("sameSizeButton")
  void onSameSizeButtonClicked(ClickEvent e) {
    if (presenter != null) {
      presenter.onSameSizeButtonClicked();
    }
  }

  @UiHandler("horizontalSpreadButton")
  void onHorizontalSpreadButtonClicked(ClickEvent e) {
    if (presenter != null) {
      presenter.onHorizontalSpreadButtonClicked();
    }
  }
  
  @UiHandler("verticalSpreadButton")
  void onVerticalSpreadButtonClicked(ClickEvent e) {
    if (presenter != null) {
      presenter.onVerticalSpreadButtonClicked();
    }
  }

  @UiHandler("horizontalCenterButton")
  void onHorizontalCenterButtonClicked(ClickEvent e) {
    if (presenter != null) {
      presenter.onHorizontalCenterButtonClicked();
    }
  }
  
  @UiHandler("verticalCenterButton")
  void onVerticalCenterButtonClicked(ClickEvent e) {
    if (presenter != null) {
      presenter.onVerticalCenterButtonClicked();
    }
  }

  public void setPresenter(Presenter presenter) {
    this.presenter = presenter;
  }

  public PushButton getHorizontalLeftAlignButton() {
    return horizontalLeftAlignButton;
  }

  public PushButton getHorizontalCenterAlignButton() {
    return horizontalCenterAlignButton;
  }

  public PushButton getHorizontalRightAlignButton() {
    return horizontalRightAlignButton;
  }

  public PushButton getVerticalTopAlignButton() {
    return verticalTopAlignButton;
  }

  public PushButton getVerticalCenterAlignButton() {
    return verticalCenterAlignButton;
  }

  public PushButton getVerticalBottomAlignButton() {
    return verticalBottomAlignButton;
  }

  public PushButton getSameSizeButton() {
    return sameSizeButton;
  }

  public PushButton getHorizontalSpreadButton() {
    return horizontalSpreadButton;
  }

  public PushButton getVerticalSpreadButton() {
    return verticalSpreadButton;
  }

  public PushButton getHorizontalCenterButton() {
    return horizontalCenterButton;
  }

  public PushButton getVerticalCenterButton() {
    return verticalCenterButton;
  }

}
