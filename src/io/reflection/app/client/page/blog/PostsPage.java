//
//  PostsPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 16 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.blog;

import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.PostController;
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.page.blog.part.BlogSidePanel;
import io.reflection.app.client.page.blog.part.PostSummaryCell;
import io.reflection.app.client.part.BootstrapGwtCellList;
import io.reflection.app.client.part.PageSizePager;
import io.reflection.app.client.part.ReflectionProgressBar;
import io.reflection.app.datatypes.shared.Post;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class PostsPage extends Page implements NavigationEventHandler {

	private static PostsPageUiBinder uiBinder = GWT.create(PostsPageUiBinder.class);

	interface PostsPageUiBinder extends UiBinder<Widget, PostsPage> {}

	@UiField(provided = true) CellList<Post> posts = new CellList<Post>(new PostSummaryCell(), BootstrapGwtCellList.INSTANCE);
	@UiField(provided = true) PageSizePager pager = new PageSizePager(ServiceConstants.SHORT_STEP_VALUE);

	@UiField BlogSidePanel blogSidePanel;

	private Element atomLink;
	private Element head;

	public PostsPage() {
		initWidget(uiBinder.createAndBindUi(this));

		NodeList<Element> nodes = Document.get().getElementsByTagName("head");
		if (nodes != null && nodes.getLength() > 0) {
			head = HeadElement.as(nodes.getItem(0));
			createAtomLink();
		}

		ReflectionProgressBar progress = new ReflectionProgressBar();
		progress.getElement().getStyle().setTextAlign(TextAlign.CENTER);

		posts.setPageSize(ServiceConstants.SHORT_STEP_VALUE);
		posts.setLoadingIndicator(progress);
		posts.setEmptyListWidget(new HTMLPanel("No posts found!"));

		PostController.get().addDataDisplay(posts);
		pager.setDisplay(posts);
	}

	public void createAtomLink() {
		atomLink = Document.get().createElement("link");
		atomLink.setAttribute("rel", "alternate");
		atomLink.setAttribute("type", "application/atom+xml");
		atomLink.setAttribute("title", "Blog");
		atomLink.setAttribute("href", "blogatom");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		register(EventController.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));

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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {
		blogSidePanel.setBlogHomeLinkActive();

	}

}
