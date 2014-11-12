//
//  MarkdownHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 16 Aug 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.helper;

import java.io.IOException;

import org.markdown4j.ExtDecorator;
import org.markdown4j.Markdown4jProcessor;

/**
 * @author William Shakour (billy1380)
 * 
 */
public class MarkdownHelper {
	public static final Markdown4jProcessor PROCESSOR = new Markdown4jProcessor().setDecorator(new ExtDecorator() {
		@Override
		public void openLink(StringBuilder out) {
			super.openLink(out);

			out.append(" target=\"_blank\"");
		}
	});

	public static String process(String input) {
		String processed = null;

		try {
			processed = PROCESSOR.process(input);
		} catch (IOException ex) {}

		return processed == null ? "" : processed;
	}
}
