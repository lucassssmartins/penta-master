package br.com.pentamc.common.utils.supertype;

import lombok.Getter;

@Getter
public abstract class Callback<T> {
	
	private T callback;
	
	public abstract void callback(T t);
	
}
