package br.com.pentamc.common.server.loadbalancer.element;

/**
 * 
 * 
 * 
 * @author yandv
 *
 */

public interface LoadBalancerObject {
	
	String getServerId();

	boolean canBeSelected();
	
}
