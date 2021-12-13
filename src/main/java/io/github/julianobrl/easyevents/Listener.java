package io.github.julianobrl.easyevents;

import java.util.EventListener;

public interface Listener extends EventListener {
	  public void EventOccurred(Object evt);
}
