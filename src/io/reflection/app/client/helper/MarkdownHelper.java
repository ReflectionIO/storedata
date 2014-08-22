//
//  MarkdownHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 16 Aug 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.helper;

import org.markdown4j.ExtDecorator;
import org.markdown4j.Markdown4jProcessor;

/**
 * @author William Shakour (billy1380)
 * 
 */
public class MarkdownHelper {
    public static final Markdown4jProcessor PROCESSOR = buildProcessor();

    static Markdown4jProcessor buildProcessor() {
        Markdown4jProcessor processor = new Markdown4jProcessor();
        processor.setDecorator(new ExtDecorator() {

            @Override
            public void openLink(StringBuilder out) {
                out.append("<a target=\"_blank\" ");
            }
        });
        return processor;
    }
}
