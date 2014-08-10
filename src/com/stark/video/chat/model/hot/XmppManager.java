package com.stark.video.chat.model.hot;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;

import android.util.Log;

public class XmppManager {
	  
	  final private static String mHost = "182.92.180.24";
	  final private static int port = 5222;
	    
	  private static XmppManager uniqueInstance = null;
	  private XMPPConnection mCon = null;
	  
	  
	    private XmppManager() {
	    	if(mCon == null){
				try {
					configure(ProviderManager.getInstance());
					ConnectionConfiguration connConfig = new ConnectionConfiguration(mHost, port);
					connConfig.setSASLAuthenticationEnabled(true);
					mCon = new XMPPConnection(connConfig);
					mCon.DEBUG_ENABLED = true;
					mCon.connect();
				} catch (XMPPException xe) {
					xe.printStackTrace();
				}
			}
	    }
	 
	    public static XmppManager getInstance() {
	       if (null == uniqueInstance) {
	    	   synchronized(XmppManager.class) {
	    		   if (null == uniqueInstance) {
	    			   uniqueInstance = new XmppManager();
	    		   }
	    	   }
	       }
	       return uniqueInstance;
	    }
	    
	    public boolean login(String username, String pwd)
	    {
	    	if (!mCon.isConnected()) 
	    		return false;
	    	try {
	    		mCon.login(username, pwd);
	    		Presence presence = new Presence(Presence.Type.available);
	    		mCon.sendPacket(presence);
	    	}
	    	catch (Exception e) {
				e.printStackTrace();
				return false;
			}
	    	return true;
	    }
	    
	public List<VideoInfo> getVideoInfo(int page) {
		ArrayList<VideoInfo> videoInfo = new ArrayList<VideoInfo>();
		if (!mCon.isConnected())
			return videoInfo;

		HotVideoIQ hotVideoIQ = new HotVideoIQ();
		hotVideoIQ.setType(IQ.Type.GET);
		HotVideoIQ.Item pageItem = new HotVideoIQ.Item();
		pageItem.setPage(page + "");
		if (page > 0) {
			hotVideoIQ.addItem(pageItem);
		}
		else
		{
			return videoInfo;
		}
		
		PacketFilter filter = new AndFilter(new PacketIDFilter(hotVideoIQ.getPacketID()),
				new PacketTypeFilter(IQ.class));
		PacketCollector collector = mCon.createPacketCollector(filter);
		mCon.sendPacket(hotVideoIQ);

		Object re = collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
		try {
			HotVideoIQ hotvideo = (HotVideoIQ) re;
			
			List<HotVideoIQ.Item> videoItems = hotvideo.getItems();
			for (HotVideoIQ.Item videoItem : videoItems) {
				VideoInfo vi = new VideoInfo();
				vi.authorId = videoItem.getAuthorId();
				vi.authorName = videoItem.getAuthorName();
				vi.avatarUrl = "http://182.92.98.241/img/" + videoItem.getAvatarUrl();
				vi.coverImgRatio = Float.valueOf(videoItem.getCoverImgRatio());
				vi.coverUrl = "http://182.92.98.241/img/" + videoItem.getCoverUrl();
				vi.description = videoItem.getDescription();
				vi.flowerNum = Integer.parseInt(videoItem.getFlowerNum());
				vi.likeNum = Integer.parseInt(videoItem.getLikeNum());
				vi.title = videoItem.getTitle();
				vi.videoId = videoItem.getVideoId();
				vi.videoUrl = videoItem.getVideoUrl();
				videoInfo.add(vi);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	        
	        return videoInfo;
	    }
	    
	    
		private void configure(ProviderManager pm) 
		{
			//  Private Data Storage
			pm.addIQProvider("query","jabber:iq:private", new PrivateDataManager.PrivateDataIQProvider());

			//  Time
			try {
			    pm.addIQProvider("query","jabber:iq:time", Class.forName("org.jivesoftware.smackx.packet.Time"));
			} catch (ClassNotFoundException e) {
			    Log.w("TestClient", "Can't load class for org.jivesoftware.smackx.packet.Time");
			}

			//  Roster Exchange
			pm.addExtensionProvider("x","jabber:x:roster", new RosterExchangeProvider());

			//  Message Events
			pm.addExtensionProvider("x","jabber:x:event", new MessageEventProvider());

			//  Chat State
			pm.addExtensionProvider("active","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
			pm.addExtensionProvider("composing","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider()); 
			pm.addExtensionProvider("paused","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
			pm.addExtensionProvider("inactive","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
			pm.addExtensionProvider("gone","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());

			//  XHTML
			pm.addExtensionProvider("html","http://jabber.org/protocol/xhtml-im", new XHTMLExtensionProvider());

			//  Group Chat Invitations
			pm.addExtensionProvider("x","jabber:x:conference", new GroupChatInvitation.Provider());

			//  Service Discovery # Items    
			pm.addIQProvider("query","http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());

			//  Service Discovery # Info
			pm.addIQProvider("query","http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());

			//  Data Forms
			pm.addExtensionProvider("x","jabber:x:data", new DataFormProvider());

			//  MUC User
			pm.addExtensionProvider("x","http://jabber.org/protocol/muc#user", new MUCUserProvider());

			//  MUC Admin    
			pm.addIQProvider("query","http://jabber.org/protocol/muc#admin", new MUCAdminProvider());

			//  MUC Owner    
			pm.addIQProvider("query","http://jabber.org/protocol/muc#owner", new MUCOwnerProvider());

			//  Delayed Delivery
			pm.addExtensionProvider("x","jabber:x:delay", new DelayInformationProvider());

			//  Version
			try {
			    pm.addIQProvider("query","jabber:iq:version", Class.forName("org.jivesoftware.smackx.packet.Version"));
			} catch (ClassNotFoundException e) {
			    //  Not sure what's happening here.
			}

			//  VCard
			pm.addIQProvider("vCard","vcard-temp", new VCardProvider());

			//  Offline Message Requests
			pm.addIQProvider("offline","http://jabber.org/protocol/offline", new OfflineMessageRequest.Provider());

			//  Offline Message Indicator
			pm.addExtensionProvider("offline","http://jabber.org/protocol/offline", new OfflineMessageInfo.Provider());

			//  Last Activity
			pm.addIQProvider("query","jabber:iq:last", new LastActivity.Provider());

			//  User Search
			pm.addIQProvider("query","jabber:iq:search", new UserSearch.Provider());

			//  SharedGroupsInfo
			pm.addIQProvider("sharedgroup","http://www.jivesoftware.org/protocol/sharedgroup", new SharedGroupsInfo.Provider());

			//  JEP-33: Extended Stanza Addressing
			pm.addExtensionProvider("addresses","http://jabber.org/protocol/address", new MultipleAddressesProvider());

			//   FileTransfer
			pm.addIQProvider("si","http://jabber.org/protocol/si", new StreamInitiationProvider());

			pm.addIQProvider("query","http://jabber.org/protocol/bytestreams", new BytestreamsProvider());
			
			//  Privacy
			pm.addIQProvider("query","jabber:iq:privacy", new PrivacyProvider());
			pm.addIQProvider("command", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider());
			
			// HotVideo
			pm.addIQProvider("query","com.stark.openfire.hotvideo", new HotVideoProvider());
			
			pm.addExtensionProvider("malformed-action", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.MalformedActionError());
			pm.addExtensionProvider("bad-locale", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadLocaleError());
			pm.addExtensionProvider("bad-payload", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadPayloadError());
			pm.addExtensionProvider("bad-sessionid", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadSessionIDError());
			pm.addExtensionProvider("session-expired", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.SessionExpiredError());
		}

}
