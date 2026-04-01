package com.drv.assetmanagement.service;

import com.drv.assetmanagement.dto.*;

public interface AssetService {
    
    Result<PageResult<AssetDTO>> listAssets(AssetQueryDTO queryDTO);
    
    Result<AssetDTO> getAssetById(Long id);
    
    Result<AssetDTO> getAssetByCode(String assetCode);
    
    Result<Void> createAsset(AssetDTO assetDTO);
    
    Result<Void> updateAsset(AssetDTO assetDTO);
    
    Result<Void> deleteAsset(Long id);
    
    Result<Void> assignAsset(AssignAssetDTO assignDTO);
    
    Result<Void> returnAsset(Long assetId, String description);
    
    Result<Void> transferAsset(AssignAssetDTO transferDTO);
}