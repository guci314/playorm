package com.alvazan.orm.layer9z.spi.db.mongodb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.alvazan.orm.api.z8spi.Key;
import com.alvazan.orm.api.z8spi.Row;
import com.alvazan.orm.api.z8spi.action.Column;
import com.alvazan.orm.api.z8spi.action.IndexColumn;
import com.alvazan.orm.api.z8spi.conv.StandardConverters;
import com.alvazan.orm.api.z8spi.meta.DboColumnMeta;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class MongoDbUtil {
	static void processColumns(DBObject mdbRow, Row r) {
		Set<String> columns = mdbRow.keySet();
		for (String col : columns) {
			if(!col.equalsIgnoreCase("_id")) {
				byte[] name = StandardConverters.convertFromString(byte[].class, col);
				byte[] val = StandardConverters.convertToBytes(mdbRow.get(col));
				Column c = new Column();
				c.setName(name);
				if (val.length != 0)
					c.setValue(val);
				r.put(c);
			}
		}
	}

	public static IndexColumn convertToIndexCol(DBObject col) {
		Object indValue = col.get("k");
		Object pk = col.get("v");
		IndexColumn c = new IndexColumn();
		// c.setColumnName(columnName); Will we ever need this now?
		if (pk != null) {
			c.setPrimaryKey((byte[]) pk);
		}
		if (indValue != null) {
			c.setIndexedValue(StandardConverters.convertToBytes(indValue));
		}

		c.setValue(null);
		return c;
	}

	public static BasicDBObject createRowQuery(Key from, Key to, DboColumnMeta colMeta) {
		BasicDBObject query = new BasicDBObject();
		Object valFrom = null, valTo = null;
		if (colMeta != null) {
			if (from != null) {
				valFrom = colMeta.getStorageType().convertFromNoSql(from.getKey());
				valFrom = checkForBigDecimal(valFrom);
			}
			if (to != null) {
				valTo = colMeta.getStorageType().convertFromNoSql(to.getKey());
				valTo = checkForBigDecimal(valTo);
			}
		} else
			return query;

		if (from != null) {
			if (from.isInclusive())
				query.append("$gte", valFrom);
			else
				query.append("$gt", valFrom);
		}
		if (to != null) {
			if (to.isInclusive())
				query.append("$lte", valTo);
			else
				query.append("$lt", valTo);
		}
		return query;
	}

	public static Object checkForBigDecimal(Object val) {
		// This is a hack as MongoDb doesn't support BigDecimal. See https://jira.mongodb.org/browse/SCALA-23
		if (val instanceof BigDecimal) {
			BigDecimal bigDec = (BigDecimal) val;
			return bigDec.doubleValue();
		}
		if (val instanceof Boolean) {
			Boolean b = (Boolean) val;
			if (b)
				return 1;
			else
				return 0;
		}
		return val;
	}

	public static BasicDBObject createRowQueryFromValues(List<byte[]> values, DboColumnMeta colMeta) {
		BasicDBObject query = new BasicDBObject();
		List<Object> valObjList = new ArrayList<Object>();
		for (byte[] val : values) {
			Object valObj = colMeta.getStorageType().convertFromNoSql(val);
			valObj = checkForBigDecimal(valObj);
			valObjList.add(valObj);
		}
		query.put("$in", valObjList);
		return query;
	}

}
