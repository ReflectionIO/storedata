//
//  MarkdownHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 16 Aug 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.helper;

import org.markdown4j.ExtDecorator;
import org.markdown4j.client.MarkdownProcessor;

/**
 * @author William Shakour (billy1380)
 * 
 */
public class MarkdownHelper {
	private static MarkdownProcessor processor = null;

	public static String process(String input) {
		if (processor == null) {
			processor = new MarkdownProcessor();
			processor.setDecorator(new ExtDecorator() {

				@Override
				public void openLink(StringBuilder out) {
					super.openLink(out);

					out.append(" target=\"_blank\"");
				}
			});
		}

		String processed = processor.process(input);

		return (processed == null ? "" : processed);
	}
}