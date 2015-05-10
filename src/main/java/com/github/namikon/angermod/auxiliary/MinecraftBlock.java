package com.github.namikon.angermod.auxiliary;

import java.util.logging.LogManager;

import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;

/**
 * MinecraftBlock definition helper to compare blocks broken while an player break-event
 * @author Namikon
 *
 */
public class MinecraftBlock {
	private String _mFQBN;
	private String _mBlockDomain;
	private String _mBlockName;
	private int _mBlockMetaData;
	private int _mDimensionID;
	
	public MinecraftBlock(String pFullQualifiedBlockName, int pDimensionID)
	{
		_mFQBN = pFullQualifiedBlockName;
		_mDimensionID = pDimensionID;
		
		String tSplitFQBN[] = _mFQBN.split(":");
		if (tSplitFQBN.length < 2)
		{
			LogHelper.error("BlockName " + _mFQBN + " in config is invalid. Make sure you use full [domain]:[blockname] notation!");
			throw new IllegalArgumentException(pFullQualifiedBlockName);
		}
		else
		{
			_mBlockDomain = tSplitFQBN[0];
			_mBlockName = tSplitFQBN[1];
			
			if (tSplitFQBN.length > 2)
			{
				String tMetaData = tSplitFQBN[2];

				if (IntHelper.tryParse(tMetaData))
					_mBlockMetaData = Integer.parseInt((String) tMetaData);
				else
					_mBlockMetaData = -1;
			}
		}
	}
	
	/**
	 * Compare eventData of blockbreak to this class
	 * @param pEventData 
	 * @return true/false if the block equals to this instance
	 */
	public boolean isEqualTo(BreakEvent pEventData)
	{
		boolean tResult = false;
		int tPlayerDIM = pEventData.getPlayer().dimension;
		
		LogHelper.debug("BlockCompare begun ");
		
		try
		{
			if (tPlayerDIM == _mDimensionID)
			{
				LogHelper.debug("DimensionID match");
				UniqueIdentifier tBlockDomain = GameRegistry.findUniqueIdentifierFor(pEventData.block);
				
				if (tBlockDomain.modId.equalsIgnoreCase(_mBlockDomain)) // BlockDomain ( ModID ) matches
				{
					LogHelper.debug("_mBlockDomain match");
					if(tBlockDomain.name.equalsIgnoreCase(_mBlockName)) // BlockName matches
					{
						LogHelper.debug("_mBlockName match");
						if(_mBlockMetaData > 0) // Do we have additional MetaData ?
						{
							if(_mBlockMetaData == pEventData.blockMetadata) // MetaData matches
							{
								LogHelper.debug("FullMatch");
								tResult = true; // we have a hit (with metadata)
							}	
						}
						else
						{
							LogHelper.debug("FullMatch (w/o meta)");
							tResult = true; // we have a hit (without metadata)
						}
					}
					else
						LogHelper.debug("_mBlockName match");
				}
				else
					LogHelper.debug("_mBlockDomain mismatch");
			}
			else
				LogHelper.debug("DimensionID mismatch " + tPlayerDIM + " != " + _mDimensionID);
		}
		catch (Exception e)
		{
			LogHelper.error("MinecraftBlock.isEqualTo.Error", "Error while comparing Blockidentifier");
			LogHelper.DumpStack("MinecraftBlock.isEqualTo.Error.Exception", e);
		}
		
		return tResult;
	}
}
