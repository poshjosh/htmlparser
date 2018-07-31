package org.htmlparser.dom;

import java.util.List;
import java.util.Optional;
import org.htmlparser.NodeFilter;
import org.htmlparser.Tag;
import org.htmlparser.tags.BodyTag;
import org.htmlparser.tags.MetaTag;
import org.htmlparser.tags.TitleTag;
import org.htmlparser.util.NodeList;

public interface HtmlDocument extends NodeList {
    
    String getURL();

    List<MetaTag> getMetaTags();
    
    List<MetaTag> getMetaTags(NodeFilter filter);
    
    default boolean isRobotsMetaTagContentContaining(String target) {
        final Optional<MetaTag> meta = this.getRobots();
        final String content = !meta.isPresent() ? null : meta.get().getAttributeValue("content");
        return content != null && content.toLowerCase().contains(target);
    }
    
    Optional<MetaTag> getRobots();

    String getKeywordsText(String outputIfNone);
    
    Optional<MetaTag> getKeywords();

    String getDescriptionText(String outputIfNone);
    
    Optional<MetaTag> getDescription();
    
    /**
     * @return Tag of signature &lt;link rel="shortcut icon" href="..."/>
     */    
    Optional<Tag> getIco();

    /**
     * @return Tag of signature &lt;link rel="icon" type="image/..." href="..."/>
     */    
    Optional<Tag> getIcon();

    TitleTag getTitle();
    
    String getTitleText(String outputIfNone);

    BodyTag getBody();
}
