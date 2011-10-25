/*
 * HistoryPanel.java
 *
 * Copyright (C) 2009-11 by RStudio, Inc.
 *
 * This program is licensed to you under the terms of version 3 of the
 * GNU Affero General Public License. This program is distributed WITHOUT
 * ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING THOSE OF NON-INFRINGEMENT,
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. Please refer to the
 * AGPL (http://www.gnu.org/licenses/agpl-3.0.txt) for more details.
 *
 */
package org.rstudio.studio.client.workbench.views.vcs.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.HasData;
import com.google.inject.Inject;
import org.rstudio.core.client.widget.*;
import org.rstudio.studio.client.workbench.commands.Commands;
import org.rstudio.studio.client.workbench.views.vcs.BranchToolbarButton;
import org.rstudio.studio.client.workbench.views.vcs.dialog.HistoryPresenter.CommitDetailDisplay;
import org.rstudio.studio.client.workbench.views.vcs.dialog.HistoryPresenter.CommitListDisplay;
import org.rstudio.studio.client.workbench.views.vcs.dialog.HistoryPresenter.Display;

import java.util.ArrayList;

public class HistoryPanel extends Composite implements Display
{
   public interface Resources extends ClientBundle
   {
      @Source("HistoryPanel.css")
      Styles styles();
   }

   public interface Styles extends SharedStyles
   {
      String commitDetail();
      String commitTableScrollPanel();

      String ref();
      String head();
      String branch();
      String remote();
      String tag();
   }

   interface Binder extends UiBinder<Widget, HistoryPanel>
   {}

   @Inject
   public HistoryPanel(BranchToolbarButton branchToolbarButton,
                       Commands commands)
   {
      Styles styles = GWT.<Resources>create(Resources.class).styles();
      commitTable_ = new CommitListTable(styles);
      splitPanel_ = new SplitLayoutPanel(4);
      pager_ = new SimplePager(
            TextLocation.CENTER,
            GWT.<SimplePager.Resources>create(SimplePager.Resources.class),
            true, 500, true);

      initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));

      commitDetail_.setScrollPanel(detailScrollPanel_);

      topToolbar_.addStyleName(styles.toolbar());

      switchViewButton_ = new LeftRightToggleButton("Changes", "History", false);
      topToolbar_.addLeftWidget(switchViewButton_);
      topToolbar_.addLeftWidget(branchToolbarButton);


      filterText_ = new SearchWidget(new MultiWordSuggestOracle(),
                                     new TextBoxWithCue("Search"),
                                     null);
      topToolbar_.addRightWidget(filterText_);
      topToolbar_.addRightSeparator();

      refreshButton_ = new ToolbarButton(
            "Refresh", commands.vcsRefresh().getImageResource(),
            (ClickHandler) null);
      topToolbar_.addRightWidget(refreshButton_);

      topToolbar_.addRightSeparator();

      topToolbar_.addRightWidget(new ToolbarButton(
            "Pull", commands.vcsPull().getImageResource(),
            commands.vcsPull()));

      topToolbar_.addRightSeparator();

      topToolbar_.addRightWidget(new ToolbarButton(
            "Push", commands.vcsPush().getImageResource(),
            commands.vcsPush()));

      pager_.setPageSize(100);
      pager_.setDisplay(commitTable_);

   }

   @Override
   public void setData(ArrayList<CommitInfo> commits)
   {
      commitTable_.setData(commits);
   }

   @Override
   public HasClickHandlers getSwitchViewButton()
   {
      return switchViewButton_;
   }

   @Override
   public CommitListDisplay getCommitList()
   {
      return commitTable_;
   }

   @Override
   public CommitDetailDisplay getCommitDetail()
   {
      return commitDetail_;
   }

   @Override
   public HasClickHandlers getRefreshButton()
   {
      return refreshButton_;
   }

   @Override
   public HasData<CommitInfo> getDataDisplay()
   {
      return commitTable_;
   }

   @Override
   public HasValue<String> getFilterTextBox()
   {
      return filterText_;
   }

   @UiField(provided = true)
   SplitLayoutPanel splitPanel_;
   @UiField
   Toolbar topToolbar_;
   @UiField(provided = true)
   CommitListTable commitTable_;
   @UiField
   CommitDetail commitDetail_;
   @UiField
   ScrollPanel detailScrollPanel_;
   @UiField(provided = true)
   SimplePager pager_;

   SearchWidget filterText_;

   private LeftRightToggleButton switchViewButton_;

   static
   {
      GWT.<Resources>create(Resources.class).styles().ensureInjected();
   }

   private ToolbarButton refreshButton_;
}
