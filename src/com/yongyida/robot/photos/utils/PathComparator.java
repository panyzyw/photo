package com.yongyida.robot.photos.utils;

import com.yongyida.robot.photos.entity.GridItem;

import java.util.Comparator;

public class PathComparator implements Comparator<GridItem> {

	@Override
	public int compare(GridItem o1, GridItem o2) {
		return o2.getPath().compareTo(o1.getPath());
	}

}
