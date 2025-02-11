package br.com.pentamc.common.server.loadbalancer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import br.com.pentamc.common.server.loadbalancer.element.LoadBalancerObject;

public abstract class BaseBalancer<T extends LoadBalancerObject> implements LoadBalancer<T> {

	private Map<String, T> objects;
	protected List<T> nextObj;

	public BaseBalancer() {
		objects = new HashMap<>();
		nextObj = new ArrayList<>();
	}

	public BaseBalancer(Map<String, T> map) {
		addAll(map);
	}

	public void add(String id, T obj) {
		objects.put(id, obj);
		update();
	}

	public T get(String id) {
		return objects.get(id);
	}

	public void remove(String id) {
		objects.remove(id);
		update();
	}

	public void addAll(Map<String, T> map) {
		if (objects != null)
			objects.clear();
		objects = map;
		update();
	}

	public List<T> getList() {
		return nextObj;
	}

	public void update() {
		if (nextObj != null)
			nextObj.clear();
		nextObj = new ArrayList<>();
		nextObj.addAll(objects.values().stream().sorted((o1, o2) -> o1.getServerId().compareTo(o2.getServerId()))
				.collect(Collectors.toList()));
	}

	public abstract int getTotalNumber();
}
