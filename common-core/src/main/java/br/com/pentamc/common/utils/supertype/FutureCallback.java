package br.com.pentamc.common.utils.supertype;

public interface FutureCallback<T> {

	void result(T result, Throwable error);

}