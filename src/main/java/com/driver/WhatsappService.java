package com.driver;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WhatsappService {

    private Map<String, User> users = new HashMap<>();
    private Map<String, Group> groups = new HashMap<>();
    private Map<Group, List<User>> groupUserDb = new HashMap<>();
    private HashMap<Integer, Message> messages = new HashMap<>();


    private int messageCount = 0;

    public String createUser(String name, String mobile) {
        if (users.containsKey(mobile)) {
            throw new RuntimeException("User already exists");
        } else {
            User user = new User(name, mobile);
            users.put(mobile, user);
            return "SUCCESS";
        }
    }

    private int count = 1;
    public Group createGroup(List<User> users) {
            Group group = new Group();
        if (users.size() == 2) {
            group.setName(users.get(1).getName());
            group.setNumberOfParticipants(2);
            groups.put(users.get(0).getName(), group);
            groupUserDb.put(group, users);
        } else if (users.size() > 2) {
            group.setName("Group" + count);
            group.setNumberOfParticipants(users.size());
            groups.put(group.getName(), group);
            groupUserDb.put(group, users);
            count++;
        }
        return group;
    }

    public int createMessage(String content) {
        Message message = new Message(++messageCount, content,new Date());
        messages.put(messageCount, message);
        return messageCount;
    }

    public String findMessage(Date start, Date end, int k) {

        return "No Message Available Between Given Dats";
    }


    public int sendMessage(Message message, User sender, Group group) {
         if (!groupUserDb.containsKey(group)) {
            throw new RuntimeException("Group does not exist");
        }
        List<User> userList = groupUserDb.get(group);
        if (!userList.contains(sender)) {
            throw new RuntimeException("You are not allowed to send message");
        }
       return 0;
    }

    public int removeUser(User user) {

        Group group = null;
        for (Group groups : groupUserDb.keySet()) {
            if (groupUserDb.get(groups).contains(user)) {
                group = groups;
            }
        }
        if (group == null) {
            throw new RuntimeException("User not found");
        } else if (groupUserDb.get(group).get(0).equals(user)) {
            throw new RuntimeException("Cannot remove admin");
//        } else {
//            groupUserDb.get(group).remove(user);
//            groups.put(group.getName(), group);
//            int totalMessages = 0;
//            for (Map.Entry<String, List<Message>> entry : messages.entrySet()) {
//                List<Message> groupMessages = entry.getValue();
//                for (int i = 0; i < groupMessages.size(); i++) {
////                    if (groupMessages.get(i).getUser().equals(user)) {
////                        groupMessages.remove(i);
////                        i--;
////                    }
//                }
//                totalMessages += groupMessages.size();
//                messages.put(entry.getKey(), groupMessages);
//            }
//          //  return group.getUsers().size() + group.getMessages().size() + totalMessages;

        }
        return 0;
    }

    public String changeAdmin(User approver, User user, Group group) {
        if (!groups.containsKey(group.getName())) {
            throw new RuntimeException("Group does not exist");
        } else if (!groupUserDb.get(group).get(0).equals(approver)) {
            throw new RuntimeException("Approver does not have rights");
        } else if (!groupUserDb.get(group).contains(user)) {
            throw new RuntimeException("User is not a participant");
        } else {
            groupUserDb.get(group).set(0, user);
            groups.put(group.getName(), group);
            return "SUCCESS";
        }
    }
}
