package com.drv.assetmanagement.controller;

import com.drv.assetmanagement.dto.*;
import com.drv.assetmanagement.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AssetController {

    private final AssetService assetService;

    @PostMapping("/list")
    public Result<PageResult<AssetDTO>> listAssets(@RequestBody AssetQueryDTO queryDTO) {
        return assetService.listAssets(queryDTO);
    }

    @GetMapping("/{id}")
    public Result<AssetDTO> getAssetById(@PathVariable Long id) {
        return assetService.getAssetById(id);
    }

    @GetMapping("/code/{assetCode}")
    public Result<AssetDTO> getAssetByCode(@PathVariable String assetCode) {
        return assetService.getAssetByCode(assetCode);
    }

    @PostMapping
    public Result<Void> createAsset(@RequestBody AssetDTO assetDTO) {
        return assetService.createAsset(assetDTO);
    }

    @PutMapping("/{id}")
    public Result<Void> updateAsset(@PathVariable Long id, @RequestBody AssetDTO assetDTO) {
        assetDTO.setId(id);
        return assetService.updateAsset(assetDTO);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteAsset(@PathVariable Long id) {
        return assetService.deleteAsset(id);
    }

    @PostMapping("/{id}/assign")
    public Result<Void> assignAsset(@PathVariable Long id, @RequestBody AssignAssetDTO assignDTO) {
        assignDTO.setAssetId(id);
        return assetService.assignAsset(assignDTO);
    }

    @PostMapping("/{id}/return")
    public Result<Void> returnAsset(@PathVariable Long id, @RequestParam String description) {
        return assetService.returnAsset(id, description);
    }

    @PostMapping("/{id}/transfer")
    public Result<Void> transferAsset(@PathVariable Long id, @RequestBody AssignAssetDTO transferDTO) {
        transferDTO.setAssetId(id);
        return assetService.transferAsset(transferDTO);
    }
}