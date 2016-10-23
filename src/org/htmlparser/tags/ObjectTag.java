// HTMLParser Library - A java-based parser for HTML
// http://htmlparser.org
// Copyright (C) 2006 Enrico Triolo
//
// Revision Control Information
//
// $URL: https://svn.sourceforge.net/svnroot/htmlparser/trunk/parser/src/main/java/org/htmlparser/tags/ObjectTag.java $
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.htmlparser.Attribute;
import org.htmlparser.Node;
import org.htmlparser.Tag;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.NodeList;
import java.util.Iterator;
import java.util.Map;

/**
 * ObjectTag represents an &lt;Object&gt; tag.
 * It extends a basic tag by providing accessors to the
 * type, codetype, codebase, classid, data, height, width, standby attributes and parameters.
 */
public class ObjectTag extends CompositeTag
{
    /**
     * The set of names handled by this tag.
     */
    private static final String[] mIds = new String[] {"OBJECT"};

    /**
     * The set of end tag names that indicate the end of this tag.
     */
    private static final String[] mEndTagEnders = new String[] {"BODY", "HTML"};

    /**
     * Create a new object tag.
     */
    public ObjectTag ()
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
     * Return the set of end tag names that cause this tag to finish.
     * @return The names of following end tags that stop further scanning.
     */
    public String[] getEndTagEnders ()
    {
        return (mEndTagEnders);
    }

    /**
     * Extract the object <code>PARAM</code> tags from the child list.
     * @return The list of object parameters (keys and values are String objects).
     */
    public Map createObjectParamsTable ()
    {
        NodeList kids;
        Node node;
        Tag tag;
        String paramName;
        String paramValue;
        Map ret;

        ret =  new HashMap (){
            @Override
            public Object put(Object key, Object value) {
                if(key == null || value == null) {
                    throw new NullPointerException("This Map implementation does not support either null keys or values");
                }
                return super.put(key, value); 
            }
        };
        kids = getChildren ();
        if (null != kids)
            for (int i = 0; i < kids.size (); i++)
            {
                node = children.get(i);
                if (node instanceof Tag)
                {
                    tag = (Tag)node;
                    if (tag.getTagName().equals ("PARAM"))
                    {
                        paramName = tag.getAttributeValue ("NAME");
                        if (null != paramName && 0 != paramName.length ())
                        {
                            paramValue = tag.getAttributeValue ("VALUE");
                            ret.put (paramName.toUpperCase(),paramValue);
                        }
                    }
                }
            }

        return (ret);
    }

    /**
     * Get the classid of the object.
     * @return The value of the <code>CLASSID</code> attribute.
     */
    public String getObjectClassId ()
    {
        return getAttributeValue ("CLASSID");
    }

    /**
     * Get the codebase of the object.
     * @return The value of the <code>CODEBASE</code> attribute.
     */
    public String getObjectCodeBase ()
    {
        return getAttributeValue ("CODEBASE");
    }
    
    /**
     * Get the codetype of the object.
     * @return The value of the <code>CODETYPE</code> attribute.
     */
    public String getObjectCodeType ()
    {
        return getAttributeValue ("CODETYPE");
    }
    
    /**
     * Get the data of the object.
     * @return The value of the <code>DATA</code> attribute.
     */
    public String getObjectData ()
    {
        return getAttributeValue ("DATA");
    }
    
    /**
     * Get the height of the object.
     * @return The value of the <code>HEIGHT</code> attribute.
     */
    public String getObjectHeight ()
    {
        return getAttributeValue ("HEIGHT");
    }
    
    /**
     * Get the standby of the object.
     * @return The value of the <code>STANDBY</code> attribute.
     */
    public String getObjectStandby ()
    {
        return getAttributeValue ("STANDBY");
    }
    
    /**
     * Get the type of the object.
     * @return The value of the <code>TYPE</code> attribute.
     */
    public String getObjectType ()
    {
        return getAttributeValue ("TYPE");
    }
    
    /**
     * Get the width of the object.
     * @return The value of the <code>WIDTH</code> attribute.
     */
    public String getObjectWidth ()
    {
        return getAttributeValue ("WIDTH");
    }
    
    /**
     * Get the object parameters.
     * @return The list of parameter values (keys and values are String objects).
     */
    public Map getObjectParams ()
    {
        return createObjectParamsTable ();
    }
    
    /**
     * Get the <code>PARAM<code> tag with the given name.
     * @param key The object parameter name to get.
     * @return The value of the parameter or <code>null</code> if there is no parameter of that name.
     */
    public String getParameter (String key)
    {
        return ((String)(getObjectParams ().get (key.toUpperCase ())));
    }
    
    /**
     * Get an enumeration over the (String) parameter names.
     * @return An enumeration of the <code>PARAM<code> tag <code>NAME<code> attributes.
     */
    public Iterator getParameterNames ()
    {
        return getObjectParams ().keySet ().iterator();
    }
    
    /**
     * Set the <code>CLASSID<code> attribute.
     * @param newClassId The new classid.
     */
    public void setObjectClassId (String newClassId)
    {
        setAttribute ("CLASSID", newClassId);
    }
    
    /**
     * Set the <code>CODEBASE<code> attribute.
     * @param newCodeBase The new codebase.
     */
    public void setObjectCodeBase (String newCodeBase)
    {
        setAttribute ("CODEBASE", newCodeBase);
    }
    
    /**
     * Set the <code>CODETYPE<code> attribute.
     * @param newCodeType The new codetype.
     */
    public void setObjectCodeType (String newCodeType)
    {
        setAttribute ("CODETYPE", newCodeType);
    }
    
    /**
     * Set the <code>DATA<code> attribute.
     * @param newData The new data.
     */
    public void setObjectData (String newData)
    {
        setAttribute ("DATA", newData);
    }
    
    /**
     * Set the <code>HEIGHT<code> attribute.
     * @param newHeight The new height.
     */
    public void setObjectHeight (String newHeight)
    {
        setAttribute ("HEIGHT", newHeight);
    }
    
    /**
     * Set the <code>STANDBY<code> attribute.
     * @param newStandby The new standby.
     */
    public void setObjectStandby (String newStandby)
    {
        setAttribute ("STANDBY", newStandby);
    }
    
    /**
     * Set the <code>TYPE<code> attribute.
     * @param newType The new type.
     */
    public void setObjectType (String newType)
    {
        setAttribute ("TYPE", newType);
    }
    
    /**
     * Set the <code>WIDTH<code> attribute.
     * @param newWidth The new width.
     */
    public void setObjectWidth (String newWidth)
    {
        setAttribute ("WIDTH", newWidth);
    }
    
    /**
     * Set the enclosed <code>PARAM<code> children.
     * @param newObjectParams The new parameters.
     */
    public void setObjectParams (Map newObjectParams)
    {
        NodeList kids;
        Node node;
        Tag tag;
        String paramName;
        String paramValue;
        List attributes;
        TextNode string;
        
        kids = getChildren ();
        if (null == kids)
            kids = new NodeList ();
        else
            // erase objectParams from kids
            for (int i = 0; i < kids.size (); )
            {
                node = kids.get (i);
                if (node instanceof Tag)
                    if (((Tag)node).getTagName ().equals ("PARAM"))
                    {
                        kids.remove (i);
                        // remove whitespace too
                        if (i < kids.size ())
                        {
                            node = kids.get (i);
                            if (node instanceof TextNode)
                            {
                                string = (TextNode)node;
                                if (0 == string.getText ().trim ().length ())
                                    kids.remove (i);
                            }
                        }
                    }
                    else
                        i++;
                else
                    i++;
            }
        
        // add newObjectParams to kids
        for (Iterator e = newObjectParams.keySet ().iterator(); e.hasNext (); )
        {
            attributes = new ArrayList (); // should the tag copy the attributes?
            paramName = (String)e.next ();
            paramValue = (String)newObjectParams.get (paramName);
            attributes.add (new Attribute ("PARAM", null));
            attributes.add (new Attribute (" "));
            attributes.add (new Attribute ("VALUE", paramValue, '"'));
            attributes.add (new Attribute (" "));
            attributes.add (new Attribute ("NAME", paramName.toUpperCase (), '"'));
            tag = new TagNode (null, 0, 0, attributes);
            kids.add (tag);
        }
        
        //set kids as new children
        setChildren (kids);
    }
    
    /**
     * Output a string representing this object tag.
     * @return A string showing the contents of the object tag.
     */
    public String toString ()
    {
        Map parameters;
        Iterator params;
        String paramName;
        String paramValue;
        boolean found;
        Node node;
        StringBuilder ret;
        
        ret = new StringBuilder (500);
        ret.append ("Object Tag\n");
        ret.append ("**********\n");
        ret.append ("ClassId = ");
        ret.append (getObjectClassId ());
        ret.append ("\n");
        ret.append ("CodeBase = ");
        ret.append (getObjectCodeBase ());
        ret.append ("\n");
        ret.append ("CodeType = ");
        ret.append (getObjectCodeType ());
        ret.append ("\n");
        ret.append ("Data = ");
        ret.append (getObjectData ());
        ret.append ("\n");
        ret.append ("Height = ");
        ret.append (getObjectHeight ());
        ret.append ("\n");
        ret.append ("Standby = ");
        ret.append (getObjectStandby ());
        ret.append ("\n");
        ret.append ("Type = ");
        ret.append (getObjectType ());
        ret.append ("\n");
        ret.append ("Width = ");
        ret.append (getObjectWidth ());
        ret.append ("\n");
        parameters = getObjectParams ();
        params = parameters.keySet ().iterator();
        if (null == params)
            ret.append ("No Params found.\n");
        else
            for (int cnt = 0; params.hasNext (); cnt++)
            {
                paramName = (String)params.next ();
                paramValue = (String)parameters.get (paramName);
                ret.append (cnt);
                ret.append (": Parameter name = ");
                ret.append (paramName);
                ret.append (", Parameter value = ");
                ret.append (paramValue);
                ret.append ("\n");
            }
        found = false;
        for (java.util.Iterator<Node> e = children (); e.hasNext ();)
        {
            node = e.next ();
            if (node instanceof Tag)
                if (((Tag)node).getTagName ().equals ("PARAM"))
                    continue;
            if (!found)
                ret.append ("Miscellaneous items :\n");
            else
                ret.append (" ");
            found = true;
            ret.append (node.toString ());
        }
        if (found)
            ret.append ("\n");
        ret.append ("End of Object Tag\n");
        ret.append ("*****************\n");
        
        return (ret.toString ());
    }
}
