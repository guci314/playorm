package com.alvazan.orm.layer9z.spi.db.inmemory;

import java.util.Comparator;

import com.alvazan.orm.api.z8spi.conv.ByteArray;
import com.alvazan.orm.api.z8spi.conv.StandardConverters;

public class Utf8Comparator implements Comparator<ByteArray> {

	@Override
	public int compare(ByteArray o1, ByteArray o2) {
		if(o1.getKey() == null && o2.getKey() != null)
			return -1;
		else if(o2.getKey() == null && o1.getKey() != null)
			return 1;
		else if(o2.getKey() == null && o1.getKey() == null)
			return 0;
		
		String left = StandardConverters.convertFromBytes(String.class, o1.getKey());
		String right = StandardConverters.convertFromBytes(String.class, o2.getKey());
		int result = left.compareTo(right);
		if(result != 0)
			return result;
		
		//otherwise, in the case of certain composites, we must now compare ByteArrays to match cassandra
		return o1.compareTo(o2);
	}

}
