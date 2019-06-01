package com.l2o.minactor.example.chat;

import java.util.ArrayList;
import java.util.List;

import com.l2o.minactor.Actor;
import com.l2o.minactor.guice.GuiceBaseActor;

public class ChatRoomImpl extends GuiceBaseActor<Runnable> implements ChatRoom {
  private List<Actor<String>> users = new ArrayList<>();

  @Override
  public void handleEvent(Runnable event) {
    event.run();
  }

  @Override
  public void register(Actor<String> user) {
    postEvent(() -> users.add(user));
  }

  @Override
  public void sendMessage(Actor<String> user, String message) {
    postEvent(() -> doSend(message));
  }

  private void doSend(String message) {
    for (Actor<String> user : users) {
      user.postEvent(message);
    }
  }

  @Override
  public void unregister(Actor<String> user) {
    postEvent(() -> users.remove(user));
  }

}
