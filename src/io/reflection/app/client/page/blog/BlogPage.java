//
//  PostsPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 16 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.blog;

import io.reflection.app.api.blog.shared.call.GetPostsRequest;
import io.reflection.app.api.blog.shared.call.GetPostsResponse;
import io.reflection.app.api.blog.shared.call.event.GetPostsEventHandler;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.component.CellListElem;
import io.reflection.app.client.component.LoadingBar;
import io.reflection.app.client.component.Selector;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.PostController;
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.helper.FilterHelper;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.page.blog.part.PostSummaryCell;
import io.reflection.app.client.part.SimplePager;
import io.reflection.app.client.res.Images;
import io.reflection.app.datatypes.shared.Post;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class BlogPage extends Page implements NavigationEventHandler, GetPostsEventHandler {

	private static PostsPageUiBinder uiBinder = GWT.create(PostsPageUiBinder.class);

	interface PostsPageUiBinder extends UiBinder<Widget, BlogPage> {}

	@UiField Selector blogCategories;
	@UiField Selector blogSortBy;
	@UiField(provided = true) CellListElem<Post> postsCellListElem = new CellListElem<Post>(false, new PostSummaryCell());

	@UiField(provided = true) SimplePager simplePager = new SimplePager(false, false);
	@UiField Button viewAllBtn;
	@UiField SpanElement viewAllSpan;

	private Element atomLink;
	private Element head;
	private LoadingBar loadingBar = new LoadingBar(false);
	@UiField DivElement searchPanel;

	public BlogPage() {
		initWidget(uiBinder.createAndBindUi(this));

		// TODO remove unused components if not admin
		if (!SessionController.get().isAdmin()) {
			blogCategories.removeFromParent();
			blogSortBy.removeFromParent();
			searchPanel.removeFromParent();
		}

		FilterHelper.addBlogCategories(blogCategories, SessionController.get().isAdmin());
		FilterHelper.addBlogSortBy(blogSortBy, SessionController.get().isAdmin());

		NodeList<Element> nodes = Document.get().getElementsByTagName("head");
		if (nodes != null && nodes.getLength() > 0) {
			head = HeadElement.as(nodes.getItem(0));
			createAtomLink();
		}

		postsCellListElem.setPageSize(ServiceConstants.SHORT_STEP_VALUE);
		postsCellListElem.setEmptyListWidget(new HTMLPanel("No posts found!"));
		HTMLPanel loader = new HTMLPanel(new Image(Images.INSTANCE.preloader()).toString());
		loader.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		postsCellListElem.setLoadingIndicator(loader);

		PostController.get().addDataDisplay(postsCellListElem);

		simplePager.setDisplay(postsCellListElem);
		loadingBar.show();
	}

	public void createAtomLink() {
		atomLink = Document.get().createElement("link");
		atomLink.setAttribute("rel", "alternate");
		atomLink.setAttribute("type", "application/atom+xml");
		atomLink.setAttribute("title", "Blog");
		atomLink.setAttribute("href", "blogatom");
	}

	@UiHandler("blogCategories")
	void onCountryValueChanged(ChangeEvent event) {

		// FilterController.get().setCountry(countryListBox.getValue(countryListBox.getSelectedIndex()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		register(DefaultEventBus.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(GetPostsEventHandler.TYPE, PostController.get(), this));

		if (head != null) {
			head.appendChild(atomLink);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#getTitle()
	 */
	@Override
	public String getTitle() {
		return "Reflection.io: Blog";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onDetach()
	 */
	@Override
	protected void onDetach() {
		if (head != null) {
			head.removeChild(atomLink);
		}

		super.onDetach();

		loadingBar.reset();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {
		// Show pager if data loaded in Admin Blog page

		// if (PostController.get().hasPosts() && PostController.get().getPostsCount() > postsCellListElem.getVisibleItemCount()) {
		// simplePager.setVisible(true);
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.blog.shared.call.event.GetPostsEventHandler#getPostsSuccess(io.reflection.app.api.blog.shared.call.GetPostsRequest,
	 * io.reflection.app.api.blog.shared.call.GetPostsResponse)
	 */
	@Override
	public void getPostsSuccess(GetPostsRequest input, GetPostsResponse output) {
		if (output.status.equals(StatusType.StatusTypeSuccess)) {
			if (output.posts.size() > ServiceConstants.SHORT_STEP_VALUE) {
				viewAllBtn.setVisible(true);
			}
			// if (PostController.get().getPostsCount() > output.pager.count) {
			// simplePager.setVisible(true);
			// } else {
			// simplePager.setVisible(false);
			// }
			loadingBar.hide(true);
		} else {
			loadingBar.hide(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.blog.shared.call.event.GetPostsEventHandler#getPostsFailure(io.reflection.app.api.blog.shared.call.GetPostsRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void getPostsFailure(GetPostsRequest input, Throwable caught) {
		loadingBar.hide(false);
	}

	@UiHandler("viewAllBtn")
	void onViewAllButtonClicked(ClickEvent event) {
		if (((Button) event.getSource()).isEnabled()) {
			if (postsCellListElem.getVisibleItemCount() == ServiceConstants.SHORT_STEP_VALUE) {
				postsCellListElem.setVisibleRange(0, Integer.MAX_VALUE);
				viewAllSpan.setInnerText("View Less Posts");
			} else {
				postsCellListElem.setVisibleRange(0, ServiceConstants.SHORT_STEP_VALUE);
				viewAllSpan.setInnerText("View All Posts");
			}
		}
	}

}
