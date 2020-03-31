package net.colors_wind.submitmanager;

import java.util.Optional;

public enum ConflictStrategy {
	KEEP_INDEX_SMALL, KEEP_INDEX_BIG, COMBINE_BY_ASCEND, DO_NOT_MODIFY; 
	
	public static Optional<ConflictStrategy> getStrategy(String name) {
		try {
			return Optional.of(ConflictStrategy.valueOf(name));
		} catch (IllegalArgumentException e) {
		}
		return Optional.empty();
	}

}
