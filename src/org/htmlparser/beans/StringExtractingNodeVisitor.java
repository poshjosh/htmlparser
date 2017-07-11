package org.htmlparser.beans;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import org.htmlparser.Tag;
import org.htmlparser.Text;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.Translate;
import org.htmlparser.visitors.AbstractNodeVisitor;

/**
 * @author Chinomso Bassey Ikwuagwu on Nov 5, 2016 9:18:00 AM
 */
public class StringExtractingNodeVisitor extends AbstractNodeVisitor implements Serializable
{
    /**
     * Property name in event where the URL contents changes.
     */
    public static final String PROP_STRINGS_PROPERTY = "strings";

    /**
     * Property name in event where the 'embed links' state changes.
     */
    public static final String PROP_LINKS_PROPERTY = "links";

    /**
     * Property name in event where the 'replace non-breaking spaces'
     * state changes.
     */
    public static final String PROP_REPLACE_SPACE_PROPERTY =
        "replaceNonBreakingSpaces";

    /**
     * Property name in event where the 'collapse whitespace' state changes.
     */
    public static final String PROP_COLLAPSE_PROPERTY = "collapse";

    /**
     * A newline.
     */
    private static final String NEWLINE = System.getProperty ("line.separator");

    /**
     * The length of the NEWLINE.
     */
    private static final int NEWLINE_SIZE = NEWLINE.length ();
    
    private final int bufferSize;
    
    private final int maxSize;

    /**
     * Bound property support.
     */
    protected PropertyChangeSupport mPropertySupport;

    /**
     * The strings extracted from the URL.
     */
    protected String mStrings;

    /**
     * If <code>true</code> the link URLs are embedded in the text output.
     */
    protected boolean mLinks;

    /**
     * If <code>true</code> regular space characters are substituted for
     * non-breaking spaces in the text output.
     */
    protected boolean mReplaceSpace;

    /**
     * If <code>true</code> sequences of whitespace characters are replaced
     * with a single space character.
     */
    protected boolean mCollapse;

    /**
     * The state of the collapse processiung state machine.
     */
    protected int mCollapseState;

    /**
     * The buffer text is stored in while traversing the HTML.
     */
    protected StringBuilder mBuffer;

    /**
     * Set <code>true</code> when traversing a SCRIPT tag.
     */
    protected boolean mIsScript;

    /**
     * Set <code>true</code> when traversing a PRE tag.
     */
    protected boolean mIsPre;

    /**
     * Set <code>true</code> when traversing a STYLE tag.
     */
    protected boolean mIsStyle;

    public StringExtractingNodeVisitor () {
        this(4096, Integer.MAX_VALUE);
    }
    
    public StringExtractingNodeVisitor (int bufferSize, int maxSize) {
        this(true, true, bufferSize, maxSize);
    }
    
   /**
     * Create a StringBean object.
     * Default property values are set to 'do the right thing':
     * <p><code>Links</code> is set <code>false</code> so text appears like a
     * browser would display it, albeit without the colour or underline clues
     * normally associated with a link.</p>
     * <p><code>ReplaceNonBreakingSpaces</code> is set <code>true</code>, so
     * that printing the text works, but the extra information regarding these
     * formatting marks is available if you set it false.</p>
     * <p><code>Collapse</code> is set <code>true</code>, so text appears
     * compact like a browser would display it.</p>
     * @param recurseChildren
     * @param recurseSelf
     * @param bufferSize
     * @param maxSize
     */
    public StringExtractingNodeVisitor (boolean recurseChildren, boolean recurseSelf, int bufferSize, int maxSize)
    {
        super (recurseChildren, recurseSelf);
        this.bufferSize = bufferSize;
        this.maxSize = maxSize;
        this.reset(this.bufferSize);
    }
    
    public void reset() {
        this.reset(this.bufferSize);
    }
    
    private void reset(int bufferSize) {
        mPropertySupport = new PropertyChangeSupport (this);
        mStrings = null;
        mLinks = false;
        mReplaceSpace = true;
        mCollapse = true;
        mCollapseState = 0;
        mBuffer = new StringBuilder (bufferSize);
        mIsScript = false;
        mIsPre = false;
        mIsStyle = false;
    }

    //
    // internals
    //

    /**
     * Appends a newline to the buffer if there isn't one there already.
     * Except if the buffer is empty.
     */
    protected void carriageReturn ()
    {
        int length;

        length = mBuffer.length ();
        if ((0 != length) // don't append newlines to the beginning of a buffer
            && length + NEWLINE_SIZE < maxSize    
            && ((NEWLINE_SIZE <= length) // not enough chars to hold a NEWLINE
            && (!mBuffer.substring (
                length - NEWLINE_SIZE, length).equals (NEWLINE))))
            mBuffer.append (NEWLINE);
        mCollapseState = 0;
    }

    /**
     * Add the given text collapsing whitespace.
     * Use a little finite state machine:
     * <pre>
     * state 0: whitepace was last emitted character
     * state 1: in whitespace
     * state 2: in word
     * A whitespace character moves us to state 1 and any other character
     * moves us to state 2, except that state 0 stays in state 0 until
     * a non-whitespace and going from whitespace to word we emit a space
     * before the character:
     *    input:     whitespace   other-character
     * state\next
     *    0               0             2
     *    1               1        space then 2
     *    2               1             2
     * </pre>
     * @param buffer The buffer to append to.
     * @param string The string to append.
     */
    protected void collapse (StringBuilder buffer, String string)
    {
        
        char character;
        final int chars = string.length ();

        if(buffer.length() + chars >= maxSize) {
            return;
        }
        
        if (0 != chars)
        {
            for (int i = 0; i < chars; i++)
            {
                character = string.charAt (i);
                switch (character)
                {
                    // see HTML specification section 9.1 White space
                    // http://www.w3.org/TR/html4/struct/text.html#h-9.1
                    case '\u0020':
                    case '\u0009':
                    case '\u000C':
                    case '\u200B':
                    case '\r':
                    case '\n':
                        if (0 != mCollapseState)
                            mCollapseState = 1;
                        break;
                    default:
                        if (1 == mCollapseState)
                            buffer.append (' ');
                        mCollapseState = 2;
                        buffer.append (character);
                }
            }
        }
    }

    /**
     * Assign the <code>Strings</code> property, firing the property change.
     * @param strings The new value of the <code>Strings</code> property.
     */
    protected void updateStrings (String strings)
    {
        String oldValue;

        if ((null == mStrings) || !mStrings.equals (strings))
        {
            oldValue = mStrings;
            mStrings = strings;
            mPropertySupport.firePropertyChange (
                PROP_STRINGS_PROPERTY, oldValue, strings);
        }
    }
    
    protected void resetStrings ()
    {
        if (null != mStrings) 
        {
            mStrings = null;
            mBuffer = new StringBuilder (this.bufferSize);
        }    
    }
    
    //
    // Property change support.
    //

    /**
     * Add a PropertyChangeListener to the listener list.
     * The listener is registered for all properties.
     * @param listener The PropertyChangeListener to be added.
     */
    public void addPropertyChangeListener (PropertyChangeListener listener)
    {
        mPropertySupport.addPropertyChangeListener (listener);
    }

    /**
     * Remove a PropertyChangeListener from the listener list.
     * This removes a registered PropertyChangeListener.
     * @param listener The PropertyChangeListener to be removed.
     */
    public void removePropertyChangeListener (PropertyChangeListener listener)
    {
        mPropertySupport.removePropertyChangeListener (listener);
    }

    //
    // Properties
    //

    /**
     * Return the textual content extracted by this NodeVisitor
     * This is the primary output of the bean.
     * @return The user visible (what would be seen in a browser) text.
     */
    public String getStrings ()
    {
        if (null == mStrings) {
            if (0 != mBuffer.length ()) {
                updateStrings (mBuffer.toString ());
            }    
        }    

        return (mStrings);
    }

    /**
     * Get the current 'include links' state.
     * @return <code>true</code> if link text is included in the text extracted
     * from the URL, <code>false</code> otherwise.
     */
    public boolean getLinks ()
    {
        return (mLinks);
    }

    /**
     * Set the 'include links' state.
     * If the setting is changed after the URL has been set, the text from the
     * URL will be reacquired, which is possibly expensive.
     * @param links Use <code>true</code> if link text is to be included in the
     * text extracted from the URL, <code>false</code> otherwise.
     */
    public void setLinks (boolean links)
    {
        boolean oldValue = mLinks;
        if (oldValue != links)
        {
            mLinks = links;
            mPropertySupport.firePropertyChange (
                PROP_LINKS_PROPERTY, oldValue, links);
            resetStrings ();
        }
    }

    /**
     * Get the current 'replace non breaking spaces' state.
     * @return <code>true</code> if non-breaking spaces (character '&#92;u00a0',
     * numeric character reference &amp;#160; or character entity
     * reference &amp;nbsp;) are to be replaced with normal
     * spaces (character '&#92;u0020').
     */
    public boolean getReplaceNonBreakingSpaces ()
    {
        return (mReplaceSpace);
    }

    /**
     * Set the 'replace non breaking spaces' state.
     * If the setting is changed after the URL has been set, the text from the
     * URL will be reacquired, which is possibly expensive.
     * @param replace <code>true</code> if non-breaking spaces
     * (character '&#92;u00a0', numeric character reference &amp;#160;
     * or character entity reference &amp;nbsp;) are to be replaced with normal
     * spaces (character '&#92;u0020').
     */
    public void setReplaceNonBreakingSpaces (boolean replace)
    {
        boolean oldValue = mReplaceSpace;
        if (oldValue != replace)
        {
            mReplaceSpace = replace;
            mPropertySupport.firePropertyChange (PROP_REPLACE_SPACE_PROPERTY,
                oldValue, replace);
            resetStrings ();
        }
    }

    /**
     * Get the current 'collapse whitespace' state.
     * If set to <code>true</code> this emulates the operation of browsers
     * in interpretting text where <quote>user agents should collapse input
     * white space sequences when producing output inter-word space</quote>.
     * See HTML specification section 9.1 White space
     * <a href="http://www.w3.org/TR/html4/struct/text.html#h-9.1">
     * http://www.w3.org/TR/html4/struct/text.html#h-9.1</a>.
     * @return <code>true</code> if sequences of whitespace (space '&#92;u0020',
     * tab '&#92;u0009', form feed '&#92;u000C', zero-width space '&#92;u200B',
     * carriage-return '\r' and NEWLINE '\n') are to be replaced with a single
     * space.
     */
    public boolean getCollapse ()
    {
        return (mCollapse);
    }

    /**
     * Set the current 'collapse whitespace' state.
     * If the setting is changed after the URL has been set, the text from the
     * URL will be reacquired, which is possibly expensive.
     * The internal state of the collapse state machine can be reset with
     * code like this:
     * <code>setCollapse (getCollapse ());</code>
     * @param collapse If <code>true</code>, sequences of whitespace
     * will be reduced to a single space.
     */
    public void setCollapse (boolean collapse)
    {
        mCollapseState = 0;
        boolean oldValue = mCollapse;
        if (oldValue != collapse)
        {
            mCollapse = collapse;
            mPropertySupport.firePropertyChange (
                    PROP_COLLAPSE_PROPERTY, oldValue, collapse);
            resetStrings ();
        }
    }

    //
    // AbstractNodeVisitor overrides
    //

    /**
     * Appends the text to the output.
     * @param string The text node.
     */
    @Override
    public void visitStringNode (Text string)
    {
        if (!mIsScript && !mIsStyle)
        {
            String text = string.getText ();
            
            if(mBuffer.length() + text.length() < maxSize) 
            {
                if (!mIsPre)
                {
                    text = Translate.decode (text);
                    if (getReplaceNonBreakingSpaces ())
                        text = text.replace ('\u00a0', ' ');
                    if (getCollapse ())
                        collapse (mBuffer, text);
                    else
                        mBuffer.append (text);
                }
                else
                    mBuffer.append (text);
            }
        }
    }

    /**
     * Appends a NEWLINE to the output if the tag breaks flow, and
     * possibly sets the state of the PRE and SCRIPT flags.
     * @param tag The tag to examine.
     */
    @Override
    public void visitTag (Tag tag)
    {
        if (getLinks() && tag instanceof LinkTag)
        { 
            // appends the link as text between angle brackets to the output.
            final String link = ((LinkTag)tag).getLink ();
            if(link != null) 
            {
                final int lengthToAppend = link.length() + 2;
                if(mBuffer.length() + lengthToAppend < maxSize)
                {    
                    mBuffer.append ('<');
                    mBuffer.append (link);
                    mBuffer.append ('>');
                }
            }
        }
        
        final String name = tag.getTagName ();
        
        if (name.equalsIgnoreCase ("PRE"))
            mIsPre = true;
        else if (name.equalsIgnoreCase ("SCRIPT"))
            mIsScript = true;
        else if (name.equalsIgnoreCase ("STYLE"))
            mIsStyle = true;
        if (tag.breaksFlow ())
            carriageReturn ();
    }

    /**
     * Resets the state of the PRE and SCRIPT flags.
     * @param tag The end tag to process.
     */
    @Override
    public void visitEndTag (Tag tag)
    {
        String name;

        name = tag.getTagName ();
        if (name.equalsIgnoreCase ("PRE"))
            mIsPre = false;
        else if (name.equalsIgnoreCase ("SCRIPT"))
            mIsScript = false;
        else if (name.equalsIgnoreCase ("STYLE"))
            mIsStyle = false;
    }

    public final int getBufferSize() {
        return bufferSize;
    }
}
