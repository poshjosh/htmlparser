package org.htmlparser.beans;

import org.htmlparser.util.EncodingChangeException;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

/**
 * Extract strings from a URL.
 * <p>Text within &lt;SCRIPT&gt;&lt;/SCRIPT&gt; tags is removed.</p>
 * <p>The text within &lt;PRE&gt;&lt;/PRE&gt; tags is not altered.</p>
 * <p>The property <code>Strings</code>, which is the output property is null
 * until a URL is set. So a typical usage is:</p>
 * <pre>
 *     StringBean sb = new StringBean ();
 *     sb.setLinks (false);
 *     sb.setReplaceNonBreakingSpaces (true);
 *     sb.setCollapse (true);
 *     sb.setURL ("http://www.netbeans.org"); // the HTTP is performed here
 *     String s = sb.getStrings ();
 * </pre>
 * <p>
 You can also use the StringBean as a AbstractNodeVisitor on your own parser,
 in which case you have to re-fetch your page if you change one of the
 properties because it resets the Strings property:</p>
 * <pre>
 *     StringBean sb = new StringBean ();
 *     Parser parser = new Parser ("http://cbc.ca");
 *     parser.visitAllNodesWith (sb);
 *     String s = sb.getStrings ();
 *     sb.setLinks (true);
 *     parser.reset ();
 *     parser.visitAllNodesWith (sb);
 *     String sl = sb.getStrings ();
 * </pre>
 * According to Nick Burch, who contributed the patch, this is handy if you
 * don't want StringBean to wander off and get the content itself, either
 * because you already have it, it's not on a website etc.
 * 
 * @author Chinomso Bassey Ikwuagwu on Nov 5, 2016 8:30:45 AM
 */
public class StringBeanForNodeList extends StringExtractingNodeVisitor 
{
    
    /**
     * Property name in event where the URL changes.
     */
    public static final String PROP_NODES_PROPERTY = "nodes";
    
    private NodeList mNodes;

    public StringBeanForNodeList() {
    }

    public StringBeanForNodeList(int bufferSize, int maxSize) {
        super(bufferSize, maxSize);
    }

    public StringBeanForNodeList(boolean recurseChildren, boolean recurseSelf, int bufferSize, int maxSize) {
        super(recurseChildren, recurseSelf, bufferSize, maxSize);
    }

    /**
     * Extract the text from a page.
     * @return The textual contents of the page.
     * @exception ParserException If a parse error occurs.
     */
    protected String extractStrings ()
        throws
            ParserException
    {
        String ret;

        mCollapseState = 0;
        mNodes.visitAllNodesWith (this);
        ret = mBuffer.toString ();
        mBuffer = new StringBuilder(this.getBufferSize());

        return (ret);
    }

    /**
     * Fetch the URL contents.
     * Only do work if there is a valid parser with it's URL set.
     */
    protected void setStrings ()
    {
        mCollapseState = 0;
        if (null != getNodes ())
            try
            {
                try
                {
                    mNodes.visitAllNodesWith (this);
                    updateStrings (mBuffer.toString ());
                }
                finally
                {
                    mBuffer = new StringBuilder (this.getBufferSize());
                }
            }
            catch (EncodingChangeException ece)
            {
                mIsPre = false;
                mIsScript = false;
                mIsStyle = false;
                try
                {   // try again with the encoding now in force
//                    mParser.reset ();
                    mBuffer = new StringBuilder (this.getBufferSize());
                    mCollapseState = 0;
                    mNodes.visitAllNodesWith (this);
                    updateStrings (mBuffer.toString ());
                }
                catch (ParserException pe)
                {
                    updateStrings (pe.toString ());
                }
                finally
                {
                    mBuffer = new StringBuilder (this.getBufferSize());
                }
             }
            catch (ParserException pe)
            {
                updateStrings (pe.toString ());
            }
        else
        {
            // reset in case this StringBean is used as a visitor
            // on another parser, not it's own
            mStrings = null;
            mBuffer = new StringBuilder (this.getBufferSize());
        }
    }

    /**
     * Refetch the URL contents.
     * Only need to worry if there is already a valid parser and it's
     * been spent fetching the string contents.
     */
    @Override
    protected void resetStrings ()
    {
        if (null != mStrings)
            
            setStrings ();
    }

    //
    // Properties
    //

    /**
     * Return the textual contents of the URL.
     * This is the primary output of the bean.
     * @return The user visible (what would be seen in a browser) text.
     */
    @Override
    public String getStrings ()
    {
        if (null == mStrings)
            if (0 == mBuffer.length ())
                setStrings ();
            else
                updateStrings (mBuffer.toString ());

        return (mStrings);
    }

    /**
     * Get the current NodeList.
     * @return The NodeList from which text has been extracted, or <code>null</code>
     * if this property has not been set yet.
     */
    public NodeList getNodes ()
    {
         return (mNodes);
    }

    /**
     * Set the URL to extract strings from.
     * The text from the URL will be fetched, which may be expensive, so this
     * property should be set last.
     * @param nodes The NodeList that text should be fetched from.
     */
    public void setNodes (NodeList nodes)
    {
        NodeList old;

        old = getNodes ();
        
        if (((null == old) && (null != nodes)) || ((null != old)
            && !old.equals (nodes)))
        {
            mNodes = nodes;
            mPropertySupport.firePropertyChange (
                PROP_NODES_PROPERTY, old, getNodes ());
            setStrings ();
        }
    }
}
