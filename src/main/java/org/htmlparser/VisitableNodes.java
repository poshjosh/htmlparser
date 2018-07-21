package org.htmlparser;

import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.NodeVisitor;

/**
 * @author Chinomso Bassey Ikwuagwu on Nov 5, 2016 9:40:35 AM
 */
public interface VisitableNodes {

    void visitAllNodesWith (NodeVisitor visitor) throws ParserException;
}
