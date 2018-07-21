package org.htmlparser.beans;

import java.net.URLConnection;

import org.htmlparser.Parser;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.EncodingChangeException;

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
 * @author Chinomso Bassey Ikwuagwu on Nov 5, 2016 9:02:29 AM
 */
public class StringBean extends StringExtractingNodeVisitor 
{
    
    /**
     * Property name in event where the URL changes.
     */
    public static final String PROP_URL_PROPERTY = "URL";

    /**
     * Property name in event where the connection changes.
     */
    public static final String PROP_CONNECTION_PROPERTY = "connection";
    
    /**
     * The parser used to extract strings.
     */
    private final Parser mParser;

    public StringBean() {
        mParser = new Parser();
    }

    public StringBean(int bufferSize, int maxSize) {
        super(bufferSize, maxSize);
        mParser = new Parser();
    }

    public StringBean(boolean recurseChildren, boolean recurseSelf, int bufferSize, int maxSize) {
        super(recurseChildren, recurseSelf, bufferSize, maxSize);
        mParser = new Parser();
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
        mParser.visitAllNodesWith (this);
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
        if (null != getURL ())
            try
            {
                try
                {
                    mParser.visitAllNodesWith (this);
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
                    mParser.reset ();
                    mBuffer = new StringBuilder (this.getBufferSize());
                    mCollapseState = 0;
                    mParser.visitAllNodesWith (this);
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
            try
            {
                mParser.setURL (getURL ());
                setStrings ();
            }
            catch (ParserException pe)
            {
                updateStrings (pe.toString ());
            }
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
     * Get the current URL.
     * @return The URL from which text has been extracted, or <code>null</code>
     * if this property has not been set yet.
     */
    public String getURL ()
    {
         return ((null != mParser) ? mParser.getURL () : null);
    }

    /**
     * Set the URL to extract strings from.
     * The text from the URL will be fetched, which may be expensive, so this
     * property should be set last.
     * @param url The URL that text should be fetched from.
     */
    public void setURL (String url)
    {
        String old;
        URLConnection conn;

        old = getURL ();
        conn = getConnection ();
        if (((null == old) && (null != url)) || ((null != old)
            && !old.equals (url)))
        {
            try
            {
                
                mParser.setURL (url);
                
                mPropertySupport.firePropertyChange (
                    PROP_URL_PROPERTY, old, getURL ());
                mPropertySupport.firePropertyChange (
                    PROP_CONNECTION_PROPERTY, conn, mParser.getConnection ());
                setStrings ();
            }
            catch (ParserException pe)
            {
                updateStrings (pe.toString ());
            }
        }
    }

    /**
     * Get the current connection.
     * @return The connection that the parser has or <code>null</code> if it
     * hasn't been set or the parser hasn't been constructed yet.
     */
    public URLConnection getConnection ()
    {
        return ((null != mParser) ? mParser.getConnection () : null);
    }

    /**
     * Set the parser's connection.
     * The text from the URL will be fetched, which may be expensive, so this
     * property should be set last.
     * @param connection New value of property Connection.
     */
    public void setConnection (URLConnection connection)
    {
        String url;
        URLConnection conn;

        url = getURL ();
        conn = getConnection ();
        if (((null == conn) && (null != connection))
            || ((null != conn) && !conn.equals (connection)))
        {
            try
            {
                
                mParser.setConnection (connection);
                
                mPropertySupport.firePropertyChange (
                    PROP_URL_PROPERTY, url, getURL ());
                mPropertySupport.firePropertyChange (
                    PROP_CONNECTION_PROPERTY, conn, mParser.getConnection ());
                setStrings ();
            }
            catch (ParserException pe)
            {
                updateStrings (pe.toString ());
            }
        }
    }

    public final Parser getParser() {
        return mParser;
    }
    
    /**
     * Unit test.
     * @param args Pass arg[0] as the URL to process.
     */
    public static void main (String[] args)
    {
        if (0 >= args.length)
            System.out.println ("Usage: java -classpath htmlparser.jar"
                + " org.htmlparser.beans.StringBean <http://whatever_url>");
        else
        {
            StringBean sb = new StringBean ();
            sb.setLinks (false);
            sb.setReplaceNonBreakingSpaces (true);
            sb.setCollapse (true);
            sb.setURL (args[0]);
            System.out.println (sb.getStrings ());
        }
    }
}
