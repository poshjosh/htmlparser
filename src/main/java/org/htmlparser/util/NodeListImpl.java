package org.htmlparser.util;

import java.util.ArrayList;
import java.util.List;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.visitors.NodeVisitor;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 22, 2018 10:37:33 PM
 */
public class NodeListImpl extends ArrayList<Node> implements NodeList {

    public NodeListImpl () { }
    
    /**
     * Create a one element node list.
     * @param node The initial node to add.
     */
    public NodeListImpl (Node node) {
        this ();
        add (node);
    }
    
    /**
     * @param nodes The initial nodes to add.
     */
    public NodeListImpl (List<Node> nodes) {
        this ();
        addAll (nodes);
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

        ret = new NodeListImpl ();
        for (int i = 0; i < size(); i++)
        {
            node = get(i);
            if (filter.accept (node))
                ret.add (node);
            if (recursive)
            {
                children = node.getChildren ();
                if (null != children)
                    ret.addAll (children.extractAllNodesThatMatch (filter, recursive));
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
     * @param visitor The visitor to visit all nodes with.
     * @throws ParserException If a parse error occurs while visiting the nodes in this NodeList.
     */
    @Override
    public void visitAllNodesWith (NodeVisitor visitor)
        throws ParserException {
        
        visitor.beginParsing ();
        
        for (int i = 0; i < size(); i++)
            get(i).accept (visitor);
        
        visitor.finishedParsing ();
    }

    @Override
    public boolean equals(Object o) {
        
        if (o == this)
            return true;
        
        if (!(o instanceof List))
            return false;
        
        if(this.size() != ((List)o).size()) {
            return false;
        }
        
        return super.equals(o);
    }    

////////////////////////////////////////////////////////////////////////////////
    
    @Override
    public Node getElementById(String id) {
        
        NodeList nodes = this.getElementsByAttribute("id", id);
        
        return nodes == null || nodes.isEmpty() ? Node.BLANK_NODE : nodes.get(0);
    }
    
    @Override
    public NodeList getElementsByClassName(String className) {
        
        return this.getElementsByAttribute("class", className);
    }
    
    @Override
    public NodeList getElementsByTagName(String nodeName) {
        
        TagNameFilter filter = new TagNameFilter(nodeName);
        
        NodeList output = this.extractAllNodesThatMatch(filter, true);
        
        return output;
    }
    
    @Override
    public NodeList getElementsByTagName(String nodeName, String attributeName, String attributeValue) {
        
        TagNameFilter tagNameFilter = new TagNameFilter(nodeName);
        HasAttributeFilter hasAttributeFilter = new HasAttributeFilter(attributeName, attributeValue);
        NodeFilter filter = new AndFilter(tagNameFilter, hasAttributeFilter);
        
        NodeList output = this.extractAllNodesThatMatch(filter, true);
        
        return output;
    }
    
    @Override
    public NodeList getElementsByAttribute(String attributeName, String attributeValue) {
        
        return this.getElements(new HasAttributeFilter(attributeName, attributeValue));
    }
    
    @Override
    public NodeList getElements(NodeFilter filter) {
        
        NodeList output = this.extractAllNodesThatMatch(filter, true);
        
        return output;
    }
    
    @Override
    public NodeList getElements(){
        return this;
    }
}
