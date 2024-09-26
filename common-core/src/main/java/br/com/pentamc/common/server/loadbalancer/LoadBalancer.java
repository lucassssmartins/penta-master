package br.com.pentamc.common.server.loadbalancer;

import br.com.pentamc.common.server.loadbalancer.element.LoadBalancerObject;

public interface LoadBalancer<T extends LoadBalancerObject> {

	public T next();

}
