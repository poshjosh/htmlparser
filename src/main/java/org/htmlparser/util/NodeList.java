// HTMLParser Library - A java-based parser for HTML
// http://htmlparser.org
// Copyright (C) 2006 Somik Raha
//
// Revision Control Information
//
// $URL: https://svn.sourceforge.net/svnroot/htmlparser/trunk/lexer/src/main/java/org/htmlparser/util/NodeList.java $
// $Author: derrickoswald $
// $Date: 2006-09-16 10:44:17 -0400 (Sat, 16 Sep 2006) $
// $Revision: 4 $
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the Common Public License; either
// version 1.0 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// Common Public License for more details.
//
// You should have received a copy of the Common Public License
// along with this library; if not, the license is available from
// the Open Source Initiative (OSI) website:
//   http://opensource.org/licenses/cpl1.0.php

package org.htmlparser.util;

import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.VisitableNodes;

public interface NodeList extends List<Node>, VisitableNodes {

    NodeList getElements();
    
    Node getElementById(String id);
    
    NodeList getElementsByClassName(String className);
    
    NodeList getElementsByTagName(String nodeName);
    
    NodeList getElementsByTagName(String nodeName, String attributeName, String attributeValue);
    
    NodeList getElementsByAttribute(String attributeName, String attributeValue);
    
    NodeList getElements(NodeFilter filter);

    ////////////////////////////////////////////////////////////////////////////
    
//    void add (NodeList list);
    
    /**
     * Insert the given node at the head of the list.
     * @param node The new first element.
     */
    void prepend (Node node);
    
    Node elementAt (int i);
    
    Node [] toNodeArray ();
    
    void copyToNodeArray (Node[] array);
    
    String asString ();
    
    /**
     * Convert this nodelist into the equivalent HTML.
     * @param verbatim If <code>true</code> return as close to the original
     * page text as possible.
     * @return The contents of the list as HTML text.
     */
    String toHtml (boolean verbatim);

    /**
     * Convert this nodelist into the equivalent HTML.
     * @return The contents of the list as HTML text.
     */
    String toHtml ();

    void removeAll ();

    /**
     * Filter the list with the given filter non-recursively.
     * @param filter The filter to use.
     * @return A new node array containing the nodes accepted by the filter.
     * This is a linear list and preserves the nested structure of the returned
     * nodes only.
     */
    NodeList extractAllNodesThatMatch (NodeFilter filter);

    /**
     * Filter the list with the given filter.
     * @param filter The filter to use.
     * @param recursive If <code>true<code> digs into the children recursively.
     * @return A new node array containing the nodes accepted by the filter.
     * This is a linear list and preserves the nested structure of the returned
     * nodes only.
     */
    NodeList extractAllNodesThatMatch (NodeFilter filter, boolean recursive);

    /**
     * Remove nodes not matching the given filter non-recursively.
     * @param filter The filter to use.
     */
    void keepAllNodesThatMatch (NodeFilter filter);

    /**
     * Remove nodes not matching the given filter.
     * @param filter The filter to use.
     * @param recursive If <code>true<code> digs into the children recursively.
     */
    void keepAllNodesThatMatch (NodeFilter filter, boolean recursive);
}
