package com.amandea.app.ws.service.impl;

import com.amandea.app.ws.io.entity.AddressEntity;
import com.amandea.app.ws.io.entity.UserEntity;
import com.amandea.app.ws.repository.AddressRepository;
import com.amandea.app.ws.repository.UserRepository;
import com.amandea.app.ws.service.AddressService;
import com.amandea.app.ws.shared.dto.AddressDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AddressRepository addressRepository;

    @Override
    public List<AddressDto> getAddresses(String userId) {
        List<AddressDto> returnValue = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null)
            return returnValue;
        List<AddressEntity> addresses= addressRepository.findAllByUserDetails(userEntity);
        for(AddressEntity addressEntity: addresses){
            returnValue.add(modelMapper.map(addressEntity,AddressDto.class));
        }
        return returnValue;
    }

    @Override
    public AddressDto getAddress(String addressId) {
        AddressDto returnValue = new AddressDto();

        AddressEntity addressEntity = addressRepository.findByAddressId(addressId);
        ModelMapper modelMapper = new ModelMapper();
        if (addressEntity!=null)
         returnValue = modelMapper.map(addressEntity,AddressDto.class);

        return returnValue;
    }
}
