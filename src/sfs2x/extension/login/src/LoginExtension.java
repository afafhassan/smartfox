package sfs2x.extension.login.src;

import com.smartfoxserver.v2.extensions.SFSExtension;
import com.smartfoxserver.v2.core.SFSEventType;

public class LoginExtension extends SFSExtension {

 @Override
 public void init()
 {
	 trace("Login extension starting.");

	 // Register the login event
	 addEventHandler(SFSEventType.USER_LOGIN, LoginHandler.class);
	 addRequestHandler("FriendRequest",FriendRequstHandler.class);
 }

 @Override
 public void destroy()
 {
	 trace("Login extension stopped.");
	 super.destroy();
 }
}