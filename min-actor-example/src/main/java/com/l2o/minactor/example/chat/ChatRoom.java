package com.l2o.minactor.example.chat;

import com.google.inject.ImplementedBy;
import com.l2o.minactor.Actor;

@ImplementedBy(ChatRoomImpl.class)
public interface ChatRoom {
  void register(Actor<String> user);

  void sendMessage(Actor<String> user, String message);

  void unregister(Actor<String> user);
}
