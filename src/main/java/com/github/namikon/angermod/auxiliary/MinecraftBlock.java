package com.github.namikon.angermod.auxiliary;

import java.util.logging.LogManager;

import com.github.namikon.angermod.AngerMod;

import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import eu.usrv.yamcore.auxiliary.IntHelper;

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
			AngerMod.Logger.error(String.format("BlockName %s in config is invalid. Make sure you use full [domain]:[blockname] notation!", _mFQBN));
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
		
		AngerMod.Logger.debug("BlockCompare begun ");
		
		try
		{
			if (tPlayerDIM == _mDimensionID)
			{
				AngerMod.Logger.debug("DimensionID match");
				UniqueIdentifier tBlockDomain = GameRegistry.findUniqueIdentifierFor(pEventData.block);
				
				if (tBlockDomain.modId.equalsIgnoreCase(_mBlockDomain)) // BlockDomain ( ModID ) matches
				{
					AngerMod.Logger.debug("_mBlockDomain match");
					if(tBlockDomain.name.equalsIgnoreCase(_mBlockName)) // BlockName matches
					{
						AngerMod.Logger.debug("_mBlockName match");
						if(_mBlockMetaData > 0) // Do we have additional MetaData ?
						{
							if(_mBlockMetaData == pEventData.blockMetadata) // MetaData matches
							{
								AngerMod.Logger.debug("FullMatch");
								tResult = true; // we have a hit (with metadata)
							}	
						}
						else
						{
							AngerMod.Logger.debug("FullMatch (w/o meta)");
							tResult = true; // we have a hit (without metadata)
						}
					}
					else
						AngerMod.Logger.debug("_mBlockName match");
				}
				else
					AngerMod.Logger.debug("_mBlockDomain mismatch");
			}
			else
				AngerMod.Logger.debug(String.format("DimensionID mismatch %d != %d", tPlayerDIM, _mDimensionID));
		}
		catch (Exception e)
		{
			AngerMod.Logger.error("MinecraftBlock.isEqualTo.Error", "Error while comparing Blockidentifier");
			AngerMod.Logger.DumpStack("MinecraftBlock.isEqualTo.Error.Exception", e);
		}
		
		return tResult;
	}
}
