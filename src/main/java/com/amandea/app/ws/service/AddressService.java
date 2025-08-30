package com.amandea.app.ws.service;

import com.amandea.app.ws.io.entity.AddressEntity;
import com.amandea.app.ws.shared.dto.AddressDto;

import java.util.List;

public interface AddressService {
    List<AddressDto> getAddresses(String userId);
    AddressDto getAddress(String addressId);
}
