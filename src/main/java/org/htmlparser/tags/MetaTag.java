// HTMLParser Library - A java-based parser for HTML
// http://htmlparser.org
// Copyright (C) 2006 Somik Raha
//
// Revision Control Information
//
// $URL: https://svn.sourceforge.net/svnroot/htmlparser/trunk/parser/src/main/java/org/htmlparser/tags/MetaTag.java $
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

package org.htmlparser.tags;

import org.htmlparser.Attribute;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.ParserException;

/**
 * A Meta Tag
 */
public class MetaTag
    extends
        TagNode
{
    /**
     * The set of names handled by this tag.
     */
    private static final String[] mIds = new String[] {"META"};

    /**
     * Create a new meta tag.
     */
    public MetaTag ()
    {
    }

    /**
     * Return the set of names handled by this tag.
     * @return The names to be matched that create tags of this type.
     */
    public String[] getIds ()
    {
        return (mIds);
    }

    /**
     * Get the <code>HTTP-EQUIV</code> attribute, if any.
     * @return The value of the <code>HTTP-EQUIV</code> attribute,
     * or <code>null</code> if the attribute doesn't exist.
     */
    public String getHttpEquiv ()
    {
        return (getAttributeValue ("HTTP-EQUIV"));
    }

    /**
     * Get the <code>CONTENT</code> attribute, if any.
     * @return The value of the <code>CONTENT</code> attribute,
     * or <code>null</code> if the attribute doesn't exist.
     */
    public String getMetaContent ()
    {
        return (getAttributeValue ("CONTENT"));
    }

    /**
     * Get the <code>NAME</code> attribute, if any.
     * @return The value of the <code>NAME</code> attribute,
     * or <code>null</code> if the attribute doesn't exist.
     */
    public String getMetaTagName ()
    {
        return (getAttributeValue ("NAME"));
    }

    /**
     * Set the <code>HTTP-EQUIV</code> attribute.
     * @param httpEquiv The new value of the <code>HTTP-EQUIV</code> attribute.
     */
    public void setHttpEquiv (String httpEquiv)
    {
        Attribute equiv;
        equiv = getAttribute ("HTTP-EQUIV");
        if (null != equiv)
            equiv.setValue (httpEquiv);
        else
            getAttributes ().add (new Attribute ("HTTP-EQUIV", httpEquiv));
    }

    /**
     * Set the <code>CONTENT</code> attribute.
     * @param metaTagContents The new value of the <code>CONTENT</code> attribute.
     */
    public void setMetaTagContents (String metaTagContents)
    {
        Attribute content;
        content = getAttribute ("CONTENT");
        if (null != content)
            content.setValue (metaTagContents);
        else
            getAttributes ().add (new Attribute ("CONTENT", metaTagContents));
    }

    /**
     * Set the <code>NAME</code> attribute.
     * @param metaTagName The new value of the <code>NAME</code> attribute.
     */
    public void setMetaTagName (String metaTagName)
    {
        Attribute name;
        name = getAttribute ("NAME");
        if (null != name)
            name.setValue (metaTagName);
        else
            getAttributes ().add (new Attribute ("NAME", metaTagName));
    }
    
    /**
     * Perform the META tag semantic action.
     * Check for a charset directive, and if found, set the charset for the page.
     * @exception ParserException If setting the encoding fails.
     */
    public void doSemanticAction ()
        throws
            ParserException
    {
        String httpEquiv;
        String charset;

        httpEquiv = getHttpEquiv ();
        if ("Content-Type".equalsIgnoreCase (httpEquiv))
        {
            charset = getPage ().getCharset (getAttributeValue ("CONTENT"));
            getPage ().setEncoding (charset);
        }
    }
}
