package sfs2x.extension.login.src;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.exceptions.SFSErrorCode;
import com.smartfoxserver.v2.exceptions.SFSErrorData;
import com.smartfoxserver.v2.exceptions.SFSLoginException;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;
import com.smartfoxserver.v2.extensions.ExtensionLogLevel;;

public class FriendRequstHandler extends BaseClientRequestHandler {

	@Override
	public void handleClientRequest(User player, ISFSObject param){
		//need to verify that the user is logged in and get its id 
		String myID="";  //getting my id from the session or may be can get it from User
		//the first parameter will be command and the second will be the friend name 
		String command=param.getText("command");
		String friend=param.getText("friend");
		//get a connection to the database
        try {
			Connection conn = getParentExtension().getParentZone().getDBManager().getConnection();
		

			if(command.equals("sendFriendRequest")){
				if(sendFriendRequest(myID,friend,conn)){
					trace("Request successful");
				}else{
	            	SFSErrorData data = new SFSErrorData(SFSErrorCode.GENERIC_ERROR);
	               
	                throw new SFSLoginException("BadRequest.", data);
	            }
			 
			
			
			}else if(command.equals("approveFriendRequest")){
				if(approveFriendRequest(myID,friend,conn)){
					trace("Request successful");
				}else{
	            	SFSErrorData data = new SFSErrorData(SFSErrorCode.GENERIC_ERROR);
	               
	                throw new SFSLoginException("BadRequest.", data);
	            }			
			}
			 conn.close();
        } catch (Exception e) {
			trace(ExtensionLogLevel.WARN, " SQL Failed: " + e.toString());
		}
		
	}

	private boolean approveFriendRequest(String myID,String friend, Connection conn) throws SQLException{
		//check if this user exist in players table
		PreparedStatement sql = conn.prepareStatement("SELECT id FROM players WHERE name = ?");
        sql.setString(1, friend);

        // Obtain ResultSet
        ResultSet result = sql.executeQuery();

        //Put the result into an SFSobject array
        SFSArray row = SFSArray.newFromResultSet(result);
        if(row.size()>0){
        	//if yes check if this user already sent a friend request
        	String userid=row.getText(0);
        	sql = conn.prepareStatement("SELECT status FROM friendship WHERE fromUser=? and toUser = ?");
        	sql.setString(1,userid);
        	sql.setString(2,myID);
        	result = sql.executeQuery();
        	row = SFSArray.newFromResultSet(result);
        	if(row.size()>0){
        		String status=userid=row.getText(0);
        		if(status.equals("pending")){
        			//approve request by editing row in friendship table
        			sql = conn.prepareStatement("update friendship set status='approved' where fromUser=? and toUser = ?");
            		sql.setString(1,userid);
                	sql.setString(2,myID);
                	sql.executeUpdate();
                	return true;
            	}
        		
        	}
        	//if this user dosn't exist or this the friend didn't send a friendship request or the request already approved
        	return false;
        }
		 
		
		
		return false;
	}

	private boolean sendFriendRequest(String myID,String friend,Connection conn) throws SQLException {
		//check if this user exist in players table
		PreparedStatement sql = conn.prepareStatement("SELECT id FROM players WHERE name = ?");
        sql.setString(1, friend);

        // Obtain ResultSet
        ResultSet result = sql.executeQuery();

        //Put the result into an SFSobject array
        SFSArray row = SFSArray.newFromResultSet(result);
		//if yes check if this user already friend or there is already request sent
        if(row.size()>0){
        	String userid=row.getText(0);
        	sql = conn.prepareStatement("SELECT status FROM friendship WHERE fromUser=? and toUser = ?");
        	sql.setString(1,myID);
        	sql.setString(2,userid);
        	result = sql.executeQuery();
        	row = SFSArray.newFromResultSet(result);
        	//if no add row in relationship table  and send request
        	if(row.size()==0){
        		sql = conn.prepareStatement("insert into friendship (fromUser,toUser,status) values ( ? , ? , 'pending')");
        		sql.setString(1,myID);
            	sql.setString(2,userid);
            	sql.executeUpdate();
            	return true;
        	}
        }
		
		//if this user dosn't exist or this user already friend send error response// 
		return false;
	}

}
