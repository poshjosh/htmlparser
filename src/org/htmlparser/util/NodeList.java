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

import java.util.ArrayList;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.visitors.NodeVisitor;

public class NodeList extends ArrayList<Node> {

    public NodeList () { }
    
    /**
     * Create a one element node list.
     * @param node The initial node to add.
     */
    public NodeList (Node node) {
        this ();
        add (node);
    }
    
    /**
     * Add another node list to this one.
     * @param list The list to add.
     */
    public void add (NodeList list) {
        super.addAll(list);
    }
    
    /**
     * Insert the given node at the head of the list.
     * @param node The new first element.
     */
    public void prepend (Node node) {
        this.add(0, node);
    }
    
    public Node elementAt (int i) {
        return this.get(i);
    }
    
    public Node [] toNodeArray () {
        return this.toArray(new Node[0]);
    }
    
    public void copyToNodeArray (Node[] array) {
        Node [] nodeData = this.toArray(new Node[0]);
        System.arraycopy (nodeData, 0, array, 0, size());
    }
    
    public String asString () {
        StringBuilder buff = new StringBuilder();
        for (int i=0;i<size();i++)
            buff.append (get(i).toPlainTextString ());
        return buff.toString ();
    }
    
    /**
     * Convert this nodelist into the equivalent HTML.
     * @param verbatim If <code>true</code> return as close to the original
     * page text as possible.
     * @return The contents of the list as HTML text.
     */
    public String toHtml (boolean verbatim) {
        StringBuilder ret = new StringBuilder ();
        for (int i = 0; i < size(); i++)
            ret.append (get(i).toHtml (verbatim));

        return (ret.toString ());
    }

    /**
     * Convert this nodelist into the equivalent HTML.
     * @return The contents of the list as HTML text.
     */
    public String toHtml () {
        return (toHtml (false));
    }

    public void removeAll () {
        super.clear();
    }
    
    /**
     * Return the contents of the list as a string.
     * Suitable for debugging.
     * @return A string representation of the list. 
     */
    @Override
    public String toString(){
        StringBuilder ret = new StringBuilder ();
        for (int i = 0; i < size(); i++)
            ret.append (get(i));

        return (ret.toString ());
    }

    /**
     * Filter the list with the given filter non-recursively.
     * @param filter The filter to use.
     * @return A new node array containing the nodes accepted by the filter.
     * This is a linear list and preserves the nested structure of the returned
     * nodes only.
     */
    public NodeList extractAllNodesThatMatch (NodeFilter filter) {
        return (extractAllNodesThatMatch (filter, false));
    }

    /**
     * Filter the list with the given filter.
     * @param filter The filter to use.
     * @param recursive If <code>true<code> digs into the children recursively.
     * @return A new node array containing the nodes accepted by the filter.
     * This is a linear list and preserves the nested structure of the returned
     * nodes only.
     */
    public NodeList extractAllNodesThatMatch (NodeFilter filter, boolean recursive) {
        Node node;
        NodeList children;
        NodeList ret;

        ret = new NodeList ();
        for (int i = 0; i < size(); i++)
        {
            node = get(i);
            if (filter.accept (node))
                ret.add (node);
            if (recursive)
            {
                children = node.getChildren ();
                if (null != children)
                    ret.add (children.extractAllNodesThatMatch (filter, recursive));
            }
        }

        return (ret);
    }

    /**
     * Remove nodes not matching the given filter non-recursively.
     * @param filter The filter to use.
     */
    public void keepAllNodesThatMatch (NodeFilter filter) {
        keepAllNodesThatMatch (filter, false);
    }

    /**
     * Remove nodes not matching the given filter.
     * @param filter The filter to use.
     * @param recursive If <code>true<code> digs into the children recursively.
     */
    public void keepAllNodesThatMatch (NodeFilter filter, boolean recursive)
    {
        Node node;
        NodeList children;

        for (int i = 0; i < size(); )
        {
            node = get(i);
            if (!filter.accept (node))
                remove (i);
            else
            {
                if (recursive)
                {
                    children = node.getChildren ();
                    if (null != children)
                        children.keepAllNodesThatMatch (filter, recursive);
                }
                i++;
            }
        }
    }

    /**
     * Utility to apply a visitor to a node list.
     * Provides for a visitor to modify the contents of a page and get the
     * modified HTML as a string with code like this:
     * <pre>
     * Parser parser = new Parser ("http://whatever");
     * NodeList list = parser.parse (null); // no filter
     * list.visitAllNodesWith (visitor);
     * System.out.println (list.toHtml ());
     * </pre>
     */
    public void visitAllNodesWith (NodeVisitor visitor)
        throws ParserException {
        
        visitor.beginParsing ();
        
        for (int i = 0; i < size(); i++)
            get(i).accept (visitor);
        
        visitor.finishedParsing ();
    }
}
