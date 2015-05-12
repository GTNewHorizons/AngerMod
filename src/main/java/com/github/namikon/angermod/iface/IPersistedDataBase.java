package com.github.namikon.angermod.iface;

public interface IPersistedDataBase {

	public abstract void setValue(String pConfigName, String pConfigValue);

	public abstract void setValue(String pConfigName, int pConfigValue);

	public abstract int getValueAsInt(String pConfigName,
			int pDefaultValueOnNull);

	public abstract String getValue(String pConfigName);

	public abstract boolean Load();

	public abstract void Save();

}