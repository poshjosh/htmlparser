/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.htmlparser.lexer;

import java.util.List;
import org.htmlparser.Attribute;
import org.htmlparser.Node;
import org.htmlparser.Tag;
import org.htmlparser.util.ParserException;
import org.junit.Test;

/**
 *
 * @author Josh
 */
public class LexerTest {
    
    public LexerTest() {
    }
    
    @Test
    public void testNext() {
        
        final StringSource source = new StringSource("");
        
        final Page page = new Page(source);
        
        final Lexer lexer = new Lexer(page);
        
        final String [] nodeTextArr = {
            "<HTML>","<BODY>","<div class=\"site-container\">","<div class=\"site-inner\">","<div class=\"content-sidebar-wrap\">","<div class=\"entry-content\" itemprop=\"text\">","<p>","<A>","<IMG alt=\"#matches(.+?)\" class=\"#matches(.+?)\" height=\"#matches(.+?)\" src=\"#matches(.+?)\" src-large=\"#matches(.+?)\" src-medium=\"#matches(.+?)\" src-thumbnail=\"#matches(.+?)\" title=\"#matches(.+?)\" width=\"#matches(.+?)\">"
        };
        
        for(String nodeText : nodeTextArr) {
            
            lexer.reset();
            
            source.setString(nodeText);
            
//            lexer.setPage(new Page(nodeText));
            
            try{
            
                final Node node = lexer.next();
                
                System.out.println(node.toHtml());
                
                if(node instanceof Tag) {
                    final List<Attribute> attributes = ((Tag)node).getAttributes();
                    attributes.forEach((attr) -> System.out.println(attr + ", " + attr.getName() + '=' + attr.getValue()));
                }
                
            }catch(ParserException e) {
                e.printStackTrace();
            }
        }
    }
}
