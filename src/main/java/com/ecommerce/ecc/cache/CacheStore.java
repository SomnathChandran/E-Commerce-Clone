package com.ecommerce.ecc.cache;

import java.time.Duration;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;


public class CacheStore<T>{
	
	private Cache<String, T> cache ;

	public CacheStore(Duration expire) {
		
		this.cache = CacheBuilder.newBuilder()
				.expireAfterWrite(expire)
				.concurrencyLevel(Runtime.getRuntime().availableProcessors())
				.build();
	}
	
	public void add( String key , T value) {
		cache.put(key, value);
	}
	
	public T get(String key) {
		return cache.getIfPresent(key);
	}
	public String remove(String key) {
		 cache.invalidate(key);
		 return "SuccessFully Removed!!";
	}
	
	

}
