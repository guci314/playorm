package com.alvazan.orm.api.z8spi.conv;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.alvazan.orm.api.z8spi.ColumnType;

public enum StorageTypeEnum {

	STRING("String", String.class), DECIMAL("Decimal", BigDecimal.class), INTEGER("Integer", Integer.class), 
	BYTES("Bytes", byte[].class), BOOLEAN("Boolean", Boolean.class), NULL("Null", void.class);
	
	private static Map<String, StorageTypeEnum> dbCodeToVal = new HashMap<String, StorageTypeEnum>();
	static {
		for(StorageTypeEnum type : StorageTypeEnum.values()) {
			dbCodeToVal.put(type.getDbValue(), type);
		}
	}
	
	private String dbValue;
	@SuppressWarnings("rawtypes")
	private Class javaType;

	@SuppressWarnings("rawtypes")
	private StorageTypeEnum(String dbValue, Class clazz) {
		this.dbValue = dbValue;
		this.javaType = clazz;
	}

	public String getDbValue() {
		return dbValue;
	}
	
	public static StorageTypeEnum lookupValue(String dbCode) {
		return dbCodeToVal.get(dbCode);
	}

	public String getIndexTableName() {
		StorageTypeEnum storedType = getStoredType();
		String name = storedType.dbValue;
		return name+"Indice";
	}

	public ColumnType translateStoreToColumnType() {
		switch (this) {
		case DECIMAL:
			return ColumnType.COMPOSITE_DECIMALPREFIX;
		case INTEGER:
			return ColumnType.COMPOSITE_INTEGERPREFIX;
		case STRING:
			return ColumnType.COMPOSITE_STRINGPREFIX;
		case BOOLEAN:
			return ColumnType.COMPOSITE_INTEGERPREFIX;
		case BYTES:
			throw new UnsupportedOperationException("not sure if we need this one or not yet");
		default:
			throw new UnsupportedOperationException("We don't translate type="+this+" just yet");
		}
	}

	@SuppressWarnings("rawtypes")
	public Class getJavaType() {
		return javaType;
	}
	
	@SuppressWarnings("unchecked")
	public Object convertFromNoSql(byte[] data) {
		return StandardConverters.convertFromBytes(this.javaType, data);
	}
	public byte[] convertToNoSql(Object o) {
		return StandardConverters.convertToBytes(o);
	}

	public StorageTypeEnum getStoredType() {
		if(this == StorageTypeEnum.BOOLEAN)
			return StorageTypeEnum.INTEGER;
		return this;
	}
}
