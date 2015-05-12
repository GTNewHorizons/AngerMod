package com.github.namikon.angermod.persisteddata;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.github.namikon.angermod.auxiliary.IntHelper;
import com.github.namikon.angermod.auxiliary.LogHelper;
import com.github.namikon.angermod.auxiliary.Reference;
import com.github.namikon.angermod.auxiliary.TimeHelper;
import com.github.namikon.angermod.iface.IPersistedDataBase;
import com.google.common.io.Files;

/**
 * An attempt to create a persisted storage location for all kinds of values that need to be stored somewhere,
 * without wasting space in the NBT-Files if not necessary
 * @author Namikon
 *
 */
public class PersistedDataBase implements IPersistedDataBase {
	private Map<String, String> _mDataStorage;
	private String _mBufferFileName;
	private File _mConfigBaseDirectory;
	private long _mLastSave;
	
	
	/**
	 * Init persistent storage file. It needs the base directory and a filename 
	 * @param pConfigBaseDirectory
	 * @param pBufferFileName
	 */
	public PersistedDataBase(File pConfigBaseDirectory, String pBufferFileName)
	{
		_mConfigBaseDirectory = pConfigBaseDirectory;
		_mBufferFileName = pBufferFileName;
		_mDataStorage = new HashMap<String, String>();
		
		_mLastSave = TimeHelper.GetCurrentTimestamp();
	}
	

	/* (non-Javadoc)
	 * @see com.github.namikon.angermod.persisteddata.IPersistedDataBase#setValue(java.lang.String, java.lang.String)
	 */
	@Override
	public void setValue(String pConfigName, String pConfigValue)
	{
		LogHelper.debug("Setting persisted config [" + pConfigName + "] to value [" + pConfigValue + "]");
		
		_mDataStorage.put(pConfigName, pConfigValue);
		
		long tCurrent = TimeHelper.GetCurrentTimestamp();
		if (tCurrent - _mLastSave > 30) // Save every 30 seconds, if changes occour
		{
			LogHelper.info("Saving local storage file as 30 seconds have passed");
			Save();
			_mLastSave = TimeHelper.GetCurrentTimestamp();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.github.namikon.angermod.persisteddata.IPersistedDataBase#setValue(java.lang.String, int)
	 */
	@Override
	public void setValue(String pConfigName, int pConfigValue)
	{
		setValue(pConfigName, String.format("%d", pConfigValue));
	}
	
	/* (non-Javadoc)
	 * @see com.github.namikon.angermod.persisteddata.IPersistedDataBase#getValueAsInt(java.lang.String, int)
	 */
	@Override
	public int getValueAsInt(String pConfigName, int pDefaultValueOnNull)
	{
		String tCfgValue = getValue(pConfigName);
		if(tCfgValue == null)
			return pDefaultValueOnNull;
		else
			if(IntHelper.tryParse(tCfgValue))
				return Integer.parseInt(tCfgValue);
			else
				return pDefaultValueOnNull;
	}
	
	/* (non-Javadoc)
	 * @see com.github.namikon.angermod.persisteddata.IPersistedDataBase#getValue(java.lang.String)
	 */
	@Override
	public String getValue(String pConfigName)
	{
		return _mDataStorage.get(pConfigName);
	}
	
	// TODO: Move this to some global location!
	private String getStorageFileName()
	{
		return _mConfigBaseDirectory + "\\" + Reference.COLLECTIONNAME + "\\" + _mBufferFileName;
	}
	
	/* (non-Javadoc)
	 * @see com.github.namikon.angermod.persisteddata.IPersistedDataBase#Load()
	 */
	@Override
	public boolean Load()
	{
		File tInputFile = new File(getStorageFileName());
		boolean tResult = LoadFile(tInputFile);
		
		if (tResult) // Successful load, so we have a working file
		{
			if (tInputFile.exists())
			{
				LogHelper.info("Creating backup of currently working storage file...");
				File tBackupFile = new File(tInputFile.getAbsolutePath() + ".backup");
				if (tBackupFile.exists())
					tBackupFile.delete();
				
				try {
					Files.copy(tInputFile, tBackupFile);
				} catch (IOException e) {
					LogHelper.error("Unable to create backup of storage file");
					e.printStackTrace();
				}
			}
		}
		
		return tResult;
	}
	
	private boolean LoadFile(File pInputFile)
	{
		boolean tResult = false;
		LogHelper.debug("Loading persisted storage file...");
		
		if (!pInputFile.exists())
		{
			LogHelper.info("Persisted storage file does not exist. Creating a new one");
			Save();
		}
		
		try {
			InputStream file = new FileInputStream(pInputFile);
			InputStream buffer = new BufferedInputStream(file);
			ObjectInput input = new ObjectInputStream(buffer);
			
			try {
				_mDataStorage = (HashMap<String, String>) input.readObject();
				
				tResult = true;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			finally
			{
				input.close();
			}
		} catch (FileNotFoundException e) {

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return tResult;
	}
	

	/* (non-Javadoc)
	 * @see com.github.namikon.angermod.iface.IPersistedDataBase#Save()
	 */
	@Override
	public void Save()
	{
		File tInputFile = new File(getStorageFileName());
		
		try {
			OutputStream file = new FileOutputStream(tInputFile);
			OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(buffer);
			
			try {
				output.writeObject(_mDataStorage);
			} catch (IOException e) {
				e.printStackTrace();
			}
			finally
			{
				output.close();
			}
		} catch (FileNotFoundException e) {

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
