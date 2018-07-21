// HTMLParser Library - A java-based parser for HTML
// http://htmlparser.org
// Copyright (C) 2006 Somik Raha
//
// Revision Control Information
//
// $URL: https://svn.sourceforge.net/svnroot/htmlparser/trunk/parser/src/main/java/org/htmlparser/util/IteratorImpl.java $
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

import java.util.Iterator;
import java.util.Objects;
import org.htmlparser.Node;
import org.htmlparser.Tag;
import org.htmlparser.lexer.Cursor;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.lexer.Page;
import org.htmlparser.scanners.Scanner;

public class IteratorImpl implements NodeIterator
{
    
    public static class Iter implements Iterator<Node> {
        private final IteratorImpl delegate;
        public Iter(IteratorImpl delegate) {
            this.delegate = Objects.requireNonNull(delegate);
        }
        @Override
        public boolean hasNext() {
            try{
                return delegate.hasNext();
            }catch(ParserException e) {
                throw new RuntimeException(e);
            }
        }
        @Override
        public Node next() {
            try{
                return delegate.next();
            }catch(ParserException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    private Lexer mLexer;
    private ParserFeedback mFeedback;
    private Cursor mCursor;
    private boolean recurse;

    public IteratorImpl (Lexer lexer, ParserFeedback fb) {
        this(lexer, fb, true);
    }
    
    public IteratorImpl (Lexer lexer, ParserFeedback fb, boolean recurse)
    {
        mLexer = lexer;
        mFeedback = fb;
        this.recurse = recurse;
        mCursor = new Cursor (mLexer.getPage (), 0);
    }
    
    @Override
    public Iterator<Node> iterator() {
        return new Iter(this);
    }

    /**
     * Check if more nodes are available.
     * @return <code>true</code> if a call to <code>next()</code> will succeed.
     */
    @Override
    public boolean hasNext() throws ParserException
    {
        boolean ret;

        mCursor.setPosition (mLexer.getPosition ());
        ret = Page.EOF != mLexer.getPage ().getCharacter (mCursor); // more characters?

        return (ret);
    }

    /**
     * Get the next node.
     * @return The next node in the HTML stream, or null if there are no more nodes.
     * @exception ParserException If an unrecoverable error occurs.
     */
    @Override
    public Node next () throws ParserException
    {
        Tag tag;
        Scanner scanner;
        NodeList stack;
        Node ret;

        try
        {
            ret = mLexer.next ();
            if (null != ret)
            {
                // kick off recursion for the top level node
                if (ret instanceof Tag)
                {
                    tag = (Tag)ret;
                    if (!tag.isEndTag ())
                    {
                        // now recurse if there is a scanner for this type of tag
                        scanner = recurse ? tag.getThisScanner () : null;
                        
                        if (null != scanner)
                        {
                            stack = new NodeListImpl();
                            ret = scanner.scan (tag, mLexer, stack);
                        }
                    }
                }
            }
        }
        catch (ParserException pe)
        {
            throw pe; // no need to wrap an existing ParserException
        }
        catch (Exception e)
        {
            
            StringBuilder msgBuffer = new StringBuilder();
            msgBuffer.append('@').append(this.getClass().getName()).append("#next()");
            msgBuffer.append ("\nUnexpected Exception occurred while reading ");
            msgBuffer.append (mLexer.getPage ().getUrl ());
            // TODO: appendLineDetails (msgBuffer)
            msgBuffer.append("Line ").append(mLexer.getCurrentLineNumber()).append(':').append(' ').append(mLexer.getCurrentLine());
            ParserException ex = new ParserException (msgBuffer.toString (), e);
            mFeedback.error (msgBuffer.toString (), ex);
            throw ex;
        }
        
        return (ret);
    }
}
