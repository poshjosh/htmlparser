package org.htmlparser.dom;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Tag;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.BodyTag;
import org.htmlparser.tags.MetaTag;
import org.htmlparser.tags.TitleTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.NodeListImpl;

public class HtmlDocumentImpl extends NodeListImpl implements HtmlDocument, Serializable {

    private List<MetaTag> metaTags;
    private Optional<MetaTag> robots;
    private Optional<MetaTag> keywords;
    private Optional<MetaTag> description;
    private Optional<Tag> ico;
    private Optional<Tag> icon;
    private TitleTag title;
    private BodyTag body;
    
    public HtmlDocumentImpl() { } 
    
    private void initMetaTags() {
        if(this.metaTags != null) {
            return;
        }
        final NodeList allNodes = this.getElements();
        NodeList metaNodes = allNodes.extractAllNodesThatMatch(new TagNameFilter("META"), true);
        if(metaNodes == null || metaNodes.isEmpty()) {
            this.metaTags = Collections.EMPTY_LIST;
        }else{
            List<MetaTag> temp = new ArrayList();
            for(Node metaNode:metaNodes) {
                temp.add((MetaTag)metaNode);
            }
            this.metaTags = temp.isEmpty() ? Collections.EMPTY_LIST : Collections.unmodifiableList(temp);
        }
        
        MetaTag mrobots = null;
        MetaTag mkeywords = null;
        MetaTag mdescription = null;
        for (MetaTag metaTag : metaTags) {

            String name = metaTag.getAttributeValue("name");
            if ((mrobots == null) && ("robots".equals(name))) {
                mrobots = metaTag;
            } else if ((mkeywords == null) && ("keywords".equals(name))) {
                mkeywords = metaTag;
            } else if ((mdescription == null) && ("description".equals(name))) {
                mdescription = metaTag;
            }
        }
        
        this.robots = Optional.ofNullable(mrobots);
        this.keywords = Optional.ofNullable(mkeywords);
        this.description = Optional.ofNullable(mdescription);
    }
    
    private void initIcons() {
        if(this.ico != null && this.icon != null) {
            return;
        }
        final NodeList nodes = this.getElements();
        Tag mico = null;
        Tag micon = null;

        NodeList links = nodes.extractAllNodesThatMatch(new TagNameFilter("LINK"), true);
        for (Node node : links) {

            Tag link = (Tag)node;
            String rel = link.getAttributeValue("rel");
            if (rel != null)  {

                String lower = rel.toLowerCase().trim();
                
                if ((mico == null) && ("shortcut icon".equals(lower))) {
                    mico = link;
                } else if ((micon == null) && ("icon".equals(lower))) {
                    micon = link;
                }

                if ((mico != null) && (micon != null)) {
                    break;
                }
            }
        }
        
        this.ico = Optional.ofNullable(mico);
        this.icon = Optional.ofNullable(micon);
    }
    
    @Override
    public List<MetaTag> getMetaTags(NodeFilter filter) {
        this.initMetaTags();
        List<MetaTag> output = null;
        for(MetaTag metaTag : this.metaTags) {
            if(filter.accept(metaTag)) {
                if(output == null) {
                    output = new ArrayList<>();
                }
                output.add(metaTag);
            }
        }
        return output == null ? Collections.EMPTY_LIST : output;
    }
    
    @Override
    public String getURL() {
        return this.isEmpty() ? null : this.get(0) == null ? null : this.get(0).getPage().getUrl();
    }

    @Override
    public Optional<MetaTag> getRobots() {
        this.initMetaTags();
        return this.robots;
    }

    @Override
    public Optional<MetaTag> getKeywords() {
        this.initMetaTags();
        return this.keywords;
    }

    @Override
    public Optional<MetaTag> getDescription() {
        this.initMetaTags();
        return this.description;
    }

    /**
     * @return Tag of signature &lt;link rel="shortcut icon" href="..."/>
     */    
    @Override
    public Optional<Tag> getIco(){
        this.initIcons();
        return this.ico;
    }

    /**
     * @return Tag of signature &lt;link rel="icon" type="image/..." href="..."/>
     */    
    @Override
    public Optional<Tag> getIcon() {
        this.initIcons();
        return this.icon;
    }

    @Override
    public String getTitleText(String outputIfNone) {
        final String output;
        final TitleTag tag = this.getTitle();
        if(tag != null) {
            output = tag.getTitle();
        }else{
            output = null;
        }
        return output == null || output.isEmpty() ? outputIfNone : null;
    }

    @Override
    public String getDescriptionText(String outputIfNone) {
        final MetaTag tag = this.getDescription().orElse(null);
        return this.getMetaTagContent(tag, outputIfNone);
    }

    @Override
    public String getKeywordsText(String outputIfNone) {
        final MetaTag tag = this.getKeywords().orElse(null);
        return this.getMetaTagContent(tag, outputIfNone);
    }

    private String getMetaTagContent(MetaTag tag, String outputIfNone) {
        final String output;
        if(tag != null) {
            output = tag.getAttributeValue("content");
        }else{
            output = null;
        }
        return output == null || output.isEmpty() ? outputIfNone : output;
    }
  
    @Override
    public TitleTag getTitle() {
        if(this.title == null) {
            NodeList titles = this.extractAllNodesThatMatch(new NodeClassFilter(TitleTag.class), true);
            this.title = titles == null || titles.isEmpty() ? null : (TitleTag)titles.get(0);
        }
        return this.title;
    }

    @Override
    public BodyTag getBody() {
        if(this.body == null) {
            NodeList bodies = this.extractAllNodesThatMatch(new NodeClassFilter(BodyTag.class), true);
            this.body = bodies == null || bodies.isEmpty() ? null : (BodyTag)bodies.get(0);
        }
        return this.body;
    }

    @Override
    public List<MetaTag> getMetaTags() {
        this.initMetaTags();
        return this.metaTags;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getName());
        builder.append("{URL=").append(this.getURL());
        builder.append(", Elements=").append(this.getElements() == null ? null : this.getElements().size());
        builder.append('}');
        return builder.toString();
    }
}
