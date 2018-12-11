package org.alexdev.roseau.dao.util;

public abstract class IProcessStorage<T, I> {

	public T fill(I data) throws Exception {
		return null;
	}
	
	public T fill(T instance, I data) throws Exception {
		return null;
	}
}
