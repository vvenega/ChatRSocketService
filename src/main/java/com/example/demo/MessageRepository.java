package com.example.demo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class MessageRepository {
	
	private HashOperations hashOperations;
    private final String TABLE="MESSAGES";
	
	public MessageRepository(RedisTemplate<String,MessageDao> redisTemplate) {
		this.hashOperations = redisTemplate.opsForHash();
	}
	
	public void saveMessage(MessageDao message) {
		List<MessageDao> channel; 
		
		
		try {
			String destChannel="mychannel_"+message.getReceiver();
			System.err.println("destChannel:"+destChannel);
			ObjectMapper mapper = new ObjectMapper();
			String strChannel;
			message.setIdconversation(message.getId());
			message.setId(destChannel);
			
			if(hashOperations.hasKey(TABLE, message.getId())) {
				try {
					strChannel = (String)hashOperations.get(TABLE, message.getId());
				}catch(Exception e) {
					deleteTable(destChannel);
					strChannel="";
				}
				channel = (List<MessageDao>)mapper.readValue(strChannel, Collection.class);
			}else {
				channel=new ArrayList<MessageDao>();
			}
			channel.add(message);
			strChannel = mapper.writeValueAsString(channel);
			hashOperations.put(TABLE, message.getId(), strChannel);
	        //System.err.println("User with ID %s saved"+message.getId());
	        System.err.println(hashOperations.entries(TABLE));
	        
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<MessageBean> getMessages(String channel){
		
		//System.err.println("getMessages Channel:"+channel);
		List<Map<String,Object>> lstMessages= new ArrayList<Map<String,Object>>();
		List<MessageBean> lstMessagesBean = new ArrayList<MessageBean>();
		
		ObjectMapper mapper = new ObjectMapper();
		String strMessages;
		
		try {
			MessageDao dao = new MessageDao();
			dao.setId(channel);
			if(hashOperations.hasKey(TABLE, dao.getId())) {
				
				strMessages =(String)hashOperations.get(TABLE, dao.getId());
				lstMessages =(List<Map<String,Object>>)mapper.readValue(strMessages, Collection.class);
				
				String strChannel = mapper.writeValueAsString(new ArrayList<MessageBean>());
				hashOperations.put(TABLE, dao.getId(), strChannel);
				
				Iterator<Map<String,Object>> itr = lstMessages.iterator();
				
				while(itr.hasNext()) {
					Iterator<Map.Entry<String,Object>> itr2 =itr.next().entrySet().iterator();
					MessageDao newdao = new MessageDao();
					while(itr2.hasNext()) {
						Map.Entry<String,Object> entry =itr2.next();
						String key = entry.getKey();
						
						
						if(key!=null && key.equals("id"))
							newdao.setId((String)entry.getValue());
						else if(key!=null && key.equals("message"))
							newdao.setMessage((String)entry.getValue());
						else if(key!=null && key.equals("date"))
							newdao.setDate((String)entry.getValue());
						else if(key!=null && key.equals("sender"))
							newdao.setSender((String)entry.getValue());
						else if(key!=null && key.equals("receiver"))
							newdao.setReceiver((String)entry.getValue());
						else if(key!=null && key.equals("channel"))
							newdao.setChannel((String)entry.getValue());
						else if(key!=null && key.equals("nameSender"))
							newdao.setNameSender((String)entry.getValue());
						else if(key!=null && key.equals("nameReceiver"))
							newdao.setNameReceiver((String)entry.getValue());
						else if(key!=null && key.equals("objectid"))
							newdao.setObjectid((Integer)entry.getValue());
						else if(key!=null && key.equals("idconversation"))
							newdao.setIdconversation((String)entry.getValue());
						
						
					}
					lstMessagesBean.add(MessageBuilder.toBean(newdao));
				}
				
				lstMessages.clear();
				
			}else
				lstMessagesBean = new ArrayList<MessageBean>();
			
		}catch(Exception e) {
			e.printStackTrace();
			lstMessagesBean = new ArrayList<MessageBean>();
		}
		
		
		
		//System.err.println("Get Messages....:"+lstMessagesBean);
		return lstMessagesBean;
	}
	
	public void deleteTable(String id) {
		try {
			hashOperations.delete(TABLE, id);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
