package com.example.demo;

public class MessageBuilder {
	
	public static MessageDao toDao(MessageBean bean) {
		
		MessageDao dao = new MessageDao();
		
		try {
			dao.setChannel(bean.getChannel());
			dao.setDate(bean.getDate());
			dao.setId(bean.getId());
			dao.setMessage(bean.getMessage());
			dao.setNameReceiver(bean.getNamereceiver());
			dao.setNameSender(bean.getNamesender());
			dao.setObjectid(Long.parseLong(bean.getObjectid()));
			dao.setReceiver(bean.getReceiver());
			dao.setSender(bean.getSender());
			dao.setIdconversation(bean.getIdconversation());
			
		}catch(Exception e) {
			e.printStackTrace();
			dao = null;
		}
		
		return dao;
	}
	
	public static MessageBean toBean(MessageDao dao) {
		
		MessageBean bean = new MessageBean();
		
		try {
			bean.setChannel(dao.getChannel());
			bean.setDate(dao.getDate());
			bean.setId(dao.getId());
			bean.setMessage(dao.getMessage());
			bean.setNamereceiver(dao.getNameReceiver());
			bean.setNamesender(dao.getNameSender());
			bean.setObjectid(dao.getObjectid()+"");
			bean.setReceiver(dao.getReceiver());
			bean.setSender(dao.getSender());
			bean.setIdconversation(dao.getIdconversation());
			
		}catch(Exception e) {
			e.printStackTrace();
			bean = null;
		}
		
		return bean;
	}

}
