package com.stark.video.chat.model.hot;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;

import com.stark.video.chat.util.XmlStringBuilder;


//仿照org.jivesoftware.smackx.packet.DiscoverItems写成
public class HotVideoIQ extends IQ{
	
	public static final String NAMESPACE = "com.stark.openfire.hotvideo";

    private final List<Item> items = new LinkedList<Item>();


    /**
     * Adds a new item to the discovered information.
     * 
     * @param item the discovered entity's item
     */
    public void addItem(HotVideoIQ.Item item) {
        items.add(item);
    }

    /**
     * Adds a collection of items to the discovered information. Does nothing if itemsToAdd is null
     *
     * @param itemsToAdd
     */
    public void addItems(Collection<Item> itemsToAdd) {
        if (itemsToAdd == null) return;
        for (Item i : itemsToAdd) {
            addItem(i);
        }
    }


    /**
     * Returns the discovered items of the queried XMPP entity. 
     *
     * @return an unmodifiable list of the discovered entity's items
     */
    public List<HotVideoIQ.Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    

	//smack4.0中，getChildElementXML()的返回类型为XmlStringBuilder，而asmack中getChildElementXML()的返回类型为String
    public String getChildElementXML() {
        XmlStringBuilder xml = new XmlStringBuilder();
        xml.halfOpenElement("query");
        xml.xmlnsAttribute(NAMESPACE);
        //xml.optAttribute("node", getNode());
        xml.rightAngelBracket();

        for (Item item : items) {
            xml.append(item.toXML());
        }

        xml.closeElement("query");
        return xml.toString();
    }

    /**
     * An item is associated with an XMPP Entity, usually thought of a children of the parent 
     * entity and normally are addressable as a JID.<p> 
     * 
     * An item associated with an entity may not be addressable as a JID. In order to handle 
     * such items, Service Discovery uses an optional 'node' attribute that supplements the 
     * 'jid' attribute.
     */
    public static class Item {
    	
    	String authorId;
 		String authorName;
     	String avatarUrl;
     	
     	String videoId;
     	String coverImgRatio;
     	String coverUrl;
     	String videoUrl;
     	
     	String title;
     	String description;
     	String likeNum;
     	String flowerNum;

     	String page;
    
    	public String getPage() {
			return page;
		}



		public void setPage(String page) {
			this.page = page;
		}



		public String getVideoId() {
			return videoId;
		}



		public void setVideoId(String videoId) {
			this.videoId = videoId;
		}



		public String getAuthorId() {
			return authorId;
		}



		public void setAuthorId(String authorId) {
			this.authorId = authorId;
		}



		public String getAvatarUrl() {
			return avatarUrl;
		}



		public void setAvatarUrl(String avatarUrl) {
			this.avatarUrl = avatarUrl;
		}



		public String getCoverUrl() {
			return coverUrl;
		}



		public void setCoverUrl(String coverUrl) {
			this.coverUrl = coverUrl;
		}



		public String getVideoUrl() {
			return videoUrl;
		}



		public void setVideoUrl(String videoUrl) {
			this.videoUrl = videoUrl;
		}



		public String getAuthorName() {
			return authorName;
		}



		public void setAuthorName(String authorName) {
			this.authorName = authorName;
		}



		public String getTitle() {
			return title;
		}



		public void setTitle(String title) {
			this.title = title;
		}



		public String getDescription() {
			return description;
		}



		public void setDescription(String description) {
			this.description = description;
		}



		public String getLikeNum() {
			return likeNum;
		}



		public void setLikeNum(String likeNum) {
			this.likeNum = likeNum;
		}



		public String getFlowerNum() {
			return flowerNum;
		}



		public void setFlowerNum(String flowerNum) {
			this.flowerNum = flowerNum;
		}


		
			
    	

        /**
         * Create a new Item associated with a given entity.
         * 
         * @param entityID the id of the entity that contains the item
         */
        public Item() {
            
        }

      

        public XmlStringBuilder toXML() {
            XmlStringBuilder xml = new XmlStringBuilder();
            xml.halfOpenElement("item");
            
            xml.optAttribute("page", page);
            xml.optAttribute("authorId", authorId);
            xml.optAttribute("authorName", authorName);
            xml.optAttribute("avatarUrl", avatarUrl);
            
            xml.optAttribute("videoId", videoId);         
            xml.optAttribute("ratio", coverImgRatio);
            xml.optAttribute("coverUrl", coverUrl);
            xml.optAttribute("videoUrl", videoUrl);
            
            xml.optAttribute("title", title);
            xml.optAttribute("description", description);
            xml.optAttribute("likeNum", likeNum);
            xml.optAttribute("flowerNum", flowerNum);

            xml.closeEmptyElement();
            return xml;
        }



		public String getCoverImgRatio() {
			return coverImgRatio;
		}



		public void setCoverImgRatio(String coverImgRatio) {
			this.coverImgRatio = coverImgRatio;
		}



	
    }

}
