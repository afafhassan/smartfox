package sfs2x.extension.login.src;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

import com.smartfoxserver.bitswarm.sessions.ISession;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.exceptions.SFSLoginException;
import com.smartfoxserver.v2.exceptions.SFSErrorData;
import com.smartfoxserver.v2.exceptions.SFSErrorCode;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;
import com.smartfoxserver.v2.extensions.ExtensionLogLevel;

public class LoginHandler extends BaseServerEventHandler {

   @Override
   public void handleServerEvent(ISFSEvent event) throws SFSException
   {
        String username = (String) event.getParameter(SFSEventParam.LOGIN_NAME);

        //ISession session = (ISession)event.getParameter(SFSEventParam.SESSION);

        try {
            //get a connection to the database
            Connection conn = getParentExtension().getParentZone().getDBManager().getConnection();

            //This will strip potential SQL injections
            PreparedStatement sql = conn.prepareStatement("SELECT id FROM players WHERE name = ?");
            sql.setString(1, username);

            // Obtain ResultSet
            ResultSet result = sql.executeQuery();

            //Put the result into an SFSobject array
            SFSArray row = SFSArray.newFromResultSet(result);
            
            if(row.size()>0){
            	//ISession session = (ISession)event.getParameter(SFSEventParam.SESSION);
            	//session.setProperty("me", row.getText(0));
            	trace("Login successful");
            }else{
            	SFSErrorData data = new SFSErrorData(SFSErrorCode.LOGIN_BAD_PASSWORD);
                data.addParameter(username);
                throw new SFSLoginException("You must enter a valid UserName.", data);
            }

            conn.close();

          

        } catch (SQLException e) {
            trace(ExtensionLogLevel.WARN, " SQL Failed: " + e.toString());
        }
    }
}