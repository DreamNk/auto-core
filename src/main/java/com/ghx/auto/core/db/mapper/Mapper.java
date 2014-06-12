/*
 * Copyright (c) 2014 Global Healthcare Exchange, LLC. All rights reserved.
 */

package com.ghx.auto.core.db.mapper;

import java.util.List;

public interface Mapper<T extends Mapper<T>> {

	public List<? extends Object> getData();

}
