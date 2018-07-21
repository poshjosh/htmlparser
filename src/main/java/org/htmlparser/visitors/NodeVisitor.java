/*
 * Copyright 2016 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.htmlparser.visitors;

import org.htmlparser.Remark;
import org.htmlparser.Tag;
import org.htmlparser.Text;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 19, 2016 7:15:27 PM
 */
public interface NodeVisitor {

    /**
     * Override this method if you wish to do special
     * processing prior to the start of parsing.
     */
    void beginParsing();

    /**
     * Override this method if you wish to do special
     * processing upon completion of parsing.
     */
    void finishedParsing();

    /**
     * Depth traversal predicate.
     * @return <code>true</code> if children are to be visited.
     */
    boolean shouldRecurseChildren();

    /**
     * Self traversal predicate.
     * @return <code>true</code> if a node itself is to be visited.
     */
    boolean shouldRecurseSelf();

    /**
     * Called for each <code>Tag</code> visited that is an end tag.
     * @param tag The end tag being visited.
     */
    void visitEndTag(Tag tag);

    /**
     * Called for each <code>RemarkNode</code> visited.
     * @param remark The remark node being visited.
     */
    void visitRemarkNode(Remark remark);

    /**
     * Called for each <code>StringNode</code> visited.
     * @param string The string node being visited.
     */
    void visitStringNode(Text string);

    /**
     * Called for each <code>Tag</code> visited.
     * @param tag The tag being visited.
     */
    void visitTag(Tag tag);

}
