package com.stark.video.chat.model.hot;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

public class HotVideoProvider implements IQProvider{
	public IQ parseIQ(XmlPullParser parser) throws Exception {
        HotVideoIQ hotVedios = new HotVideoIQ();
        boolean done = false;
        HotVideoIQ.Item item;
      //<item user="chris" lon="22.323009" lat="29.098763" sex="0" online="30min"/>
        
        String authorId = "";
		String authorName = "";
    	String avatarUrl = "";
    	
    	String videoId = "";
    	String coverImgRatio = "";
    	String coverUrl = "";
    	String videoUrl = "";
    	
    	String title = "";
    	String description = "";
    	String likeNum = "";
    	String flowerNum = "";
        
        while (!done) {
            int eventType = parser.next();

            if (eventType == XmlPullParser.START_TAG && "item".equals(parser.getName())) {
                // Initialize the variables from the parsed XML
            	authorId = parser.getAttributeValue("", "authorId");
            	authorName = parser.getAttributeValue("", "authorName");
            	avatarUrl = parser.getAttributeValue("", "avatarUrl");
            	
            	videoId = parser.getAttributeValue("", "videoId");
            	coverImgRatio = parser.getAttributeValue("", "coverImgRatio");
            	coverUrl = parser.getAttributeValue("", "coverUrl");
            	videoUrl = parser.getAttributeValue("", "videoUrl");
            	
            	title = parser.getAttributeValue("", "title");
            	description = parser.getAttributeValue("", "description");
            	likeNum = parser.getAttributeValue("", "likeNum");
            	flowerNum = parser.getAttributeValue("", "flowerNum");  
            	
            }
            else if (eventType == XmlPullParser.END_TAG && "item".equals(parser.getName())) {
                // Create a new Item and add it to DiscoverItems.
                item = new HotVideoIQ.Item();
                item.setAuthorId(authorId);
                item.setAuthorName(authorName);
                item.setAvatarUrl(avatarUrl);
                
                item.setVideoId(videoId);
                item.setCoverImgRatio(coverImgRatio);
                item.setCoverUrl(coverUrl);
                item.setVideoId(videoId);
                
                item.setTitle(title);
                item.setDescription(description);
                item.setLikeNum(likeNum);
                item.setFlowerNum(flowerNum);            
                
                hotVedios.addItem(item);
            }
            else if (eventType == XmlPullParser.END_TAG && "query".equals(parser.getName())) {
                done = true;
            }
        }
        return hotVedios;
    }
}
