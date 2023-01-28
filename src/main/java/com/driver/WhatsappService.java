package com.driver;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WhatsappService {


    HashMap<String,User> userHashMap=new HashMap<>(); //key as mobile
    HashMap<Group,List<User>> groupHashMap=new HashMap<>(); //group Name as key
    HashMap<Group,List<Message>> messagesInGroup=new HashMap<>();
    List<Message> messageList=new ArrayList<>();
    HashMap<User,List<Message>> userMessageList=new HashMap<>();


    private int groupCount=0;
    private int messageCount=0;


    public String  createUser(String name,String mobile)throws Exception{

        if(userHashMap.containsKey(mobile)){
            throw new Exception("User already exists");
        }
        User user=new User(name, mobile);
        userHashMap.put(mobile,user);
      return "SUCCESS";

    }

    public Group createGroup(List<User> users){
        if(users.size()==2){
            Group group=new Group(users.get(1).getName(),2);
            groupHashMap.put(group,users);
            return group;
        }
        Group group=new Group("Group "+ ++groupCount,users.size());
        groupHashMap.put(group,users);
        return group;
    }

    public int createMessage(String content){
        Message message=new Message(++messageCount,content);
        message.setTimestamp(new Date());
        messageList.add(message);
        return messageCount;
    }

    public int sendMessage(Message message,User sender,Group group)throws Exception{

        if(!groupHashMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }
        boolean checker=false;
        for(User user:groupHashMap.get(group)){
            if(user.equals(sender)){
                checker=true;
                break;
            }
        }

        if(!checker){
            throw new Exception("You are not allowed to send message");
        }

        if(messagesInGroup.containsKey(group)){
            messagesInGroup.get(group).add(message);
        }
        else {
            List<Message> messages=new ArrayList<>();
            messages.add(message);
            messagesInGroup.put(group,messages);
        }

        if(userMessageList.containsKey(sender)){
            userMessageList.get(sender).add(message);
        }
        else {
            List<Message> messages=new ArrayList<>();
            messages.add(message);
            userMessageList.put(sender,messages);
        }

        return messagesInGroup.get(group).size();

    }


    public void changeAdmin(User approver, User user, Group group)throws Exception{

        if(!groupHashMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }

        User pastAdmin=groupHashMap.get(group).get(0);
        if(!approver.equals(pastAdmin)){
            throw new Exception("Approver does not have rights");
        }

        boolean check=false;
        for(User user1:groupHashMap.get(group)){
            if(user1.equals(user)){
                check=true;
            }
        }

        if(!check){
            throw new Exception("User is not a participant");
        }

        User newAdmin=null;
        Iterator<User> userIterator=groupHashMap.get(group).iterator();

        while(userIterator.hasNext()){
            User u=userIterator.next();
            if(u.equals(user)){
                newAdmin=u;
                userIterator.remove();
            }
        }

        groupHashMap.get(group).add(0,newAdmin);

    }
    public int removeUser(User user)throws Exception{

        boolean check=false;
        Group group1=null;
        for(Group group:groupHashMap.keySet()){
            for(User user1:groupHashMap.get(group)){
                if(user1.equals(user)){
                    check=true;
                    group1=group;
                    break;
                }
            }
        }
        if(!check){
            throw new Exception("User not found");
        }

        if(groupHashMap.get(group1).get(0).equals(user)){
            throw new Exception("Cannot remove admin");
        }

        List<Message> userMessages=userMessageList.get(user);

        for(Group group:messagesInGroup.keySet()){
            for(Message message:messagesInGroup.get(group)){
                if(userMessages.contains(message)){
                    messagesInGroup.get(group).remove(message);
                }
            }
        }
        for(Message message:messageList){
            if(userMessages.contains(message)){
                messageList.remove(message);
            }
        }

        groupHashMap.get(group1).remove(user);

        userMessageList.remove(user);

        return groupHashMap.get(group1).size()+messagesInGroup.get(group1).size()+messageList.size();


    }

}
