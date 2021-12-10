package io.github.julianobrl.events;

import java.io.InvalidClassException;
import java.util.HashMap;
import java.util.Map;

import javax.management.ListenerNotFoundException;
import javax.swing.event.EventListenerList;

import io.github.julianobrl.events.exeptions.EventNameExists;
import io.github.julianobrl.events.exeptions.EventNotExists;

public class EventManager {
	
	private static Map<String,Event> eventMappers = new HashMap<String,Event>();
	
	public static void registerEvent(String eventName,Class<?> eventTpe) throws EventNotExists, EventNameExists {
		
		if(eventMappers.containsKey(eventName))
			throw new EventNameExists("Event with name <"+eventName+"> already exists.");
		
		eventMappers.put(eventName, new Event(eventTpe, new HashMap<Integer,Listener>(),new EventListenerList()));
	}
	
	public static void unregisterEvent(String eventName) throws EventNotExists {
		
		existsEvent(eventName);
		
		Map<Integer, Listener> eventsListeners = eventMappers.get(eventName).getListeners();
		EventListenerList listenerList = eventMappers.get(eventName).getListenerList();
		
		eventsListeners.forEach((index,listener) -> {
			listenerList.remove(Listener.class,listener);
		});
		
		eventMappers.remove(eventName);
		
	}
	
	public static int registerListener(String eventName,Listener listener) throws EventNotExists {
		
		existsEvent(eventName);
		
		EventListenerList listenerList = eventMappers.get(eventName).getListenerList();
		listenerList.add(Listener.class,listener);
		Map<Integer,Listener> listeners = eventMappers.get(eventName).getListeners();
		
		int index = listeners.size()+1;
		eventMappers.get(eventName).getListeners().put(index,listener);
		
		return index;
		
	}
	
	public static void unregisterListener(String eventName,int index) throws ListenerNotFoundException, EventNotExists {
		
		existsEvent(eventName);
		if(!eventMappers.get(eventName).getListeners().containsKey(index))
			throw new ListenerNotFoundException("Listener with index <"+index+"> does not exist in event <"+eventName+">.");
		
		Listener toDelete = eventMappers.get(eventName).getListeners().get(index);
		eventMappers.get(eventName).getListenerList().remove(Listener.class, toDelete);
		eventMappers.get(eventName).getListeners().remove(index);
		
	}
	
	public static void callEvent(String eventName, Object evt) throws EventNotExists, InvalidClassException {
		
		existsEvent(eventName);
		
		if(eventMappers.get(eventName).getEventType() != evt.getClass())
			throw new InvalidClassException("The event requires an object of type <"+eventMappers.get(eventName).getEventType()+"> and one of type <"+evt.getClass()+"> was received.");
				
		Object[] listeners = eventMappers.get(eventName).getListenerList().getListenerList();
	    for (int i = 0; i < listeners.length; i = i+2) {
	      if (listeners[i] == Listener.class) {
	        ((Listener) listeners[i+1]).EventOccurred(evt);
	      }
	    }
	    
	    
	}
	
	public static boolean eventExists(String eventName) {
		return eventMappers.containsKey(eventName);
	}
	
	private static void existsEvent(String eventName) throws EventNotExists {
		if(!eventExists(eventName))
			throw new EventNotExists("Event with name <"+eventName+"> not found, does not exist or not registered.");
	}
	
}
