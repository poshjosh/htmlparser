package org.htmlparser.filters;

import java.util.List;
import java.util.regex.Pattern;
import org.htmlparser.Attribute;
import org.htmlparser.Node;
import org.htmlparser.Tag;

/**
 * The text to match the attribute value against is a regular expression in this case.
 * @author Josh
 */
public class HasAttributeRegexFilter extends HasAttributeFilter{

    private final int flags;
    private Pattern namePattern;
    private Pattern valuePattern;

    public HasAttributeRegexFilter (){
        this("", null);
    }

    public HasAttributeRegexFilter (String attributeNameRegex, String attributeValueRegex){
        this(attributeNameRegex, attributeValueRegex, Pattern.CASE_INSENSITIVE);
    }
    
    public HasAttributeRegexFilter (String attributeNameRegex, String attributeValueRegex, int flags){
        super(attributeNameRegex, attributeValueRegex);
        this.flags = flags;
        this.setAttributeName(attributeNameRegex);
        this.setAttributeValue(attributeValueRegex);
    }

    @Override
    public void setAttributeName(String name) {
        super.setAttributeName(name); 
        this.namePattern = name == null ? null : Pattern.compile(name, flags);
    }

    @Override
    public void setAttributeValue(String value) {
        super.setAttributeValue(value); 
        this.valuePattern = value == null ? null : Pattern.compile(value, flags);
    }

    @Override
    public boolean accept (Node node){
        
        if( !(node instanceof Tag) ) return false;

        List<Attribute> attributes = ((Tag)node).getAttributes();
        
        boolean accept =  false;
        
        if(attributes != null && !attributes.isEmpty()) {
            
            for(Attribute attribute : attributes) {
                
                if(attribute == null) {
                    continue;
                }

                final String name = attribute.getName();
                final String value = attribute.getValue();

                if(this.find(name, this.namePattern) && this.find(value, this.valuePattern)) {
                    accept = true;
                    break;
                }
            }
        }

        return accept;
    }
    
    private boolean find(String toFind, Pattern pattern) {
        return pattern == null || toFind == null || toFind.isEmpty() ? false : pattern.matcher(toFind).find();
    }
}
/**
 * 

    public final boolean accept_old(Node node) {

        if( !(node instanceof Tag) ) return false;

        Attribute attribute = ((Tag)node).getAttribute(mAttribute);

        boolean accept = false;
        
        if (attribute != null && valuePattern != null) {
            accept = valuePattern.matcher(attribute.getValue()).find();
        }

        return accept;
    }
 * 
 */