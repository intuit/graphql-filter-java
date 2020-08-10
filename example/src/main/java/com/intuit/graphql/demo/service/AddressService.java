package com.intuit.graphql.demo.service;

import com.intuit.graphql.demo.persistence.entity.AddressEntity;
import com.intuit.graphql.demo.persistence.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Transactional
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    public  List<Map<String, String>> searchAddresses(Specification<AddressEntity> specification) {
        List<AddressEntity> addresses = addressRepository.findAll(specification);

        List<Map<String,String>> addressList = transform(addresses);
        return addressList;

    }

    private List<Map<String,String>> transform(List<AddressEntity> addressEntities) {
        List<Map<String,String>> addressList = new ArrayList<>();

        for (AddressEntity addressEntity: addressEntities) {
            Map<String, String> address = new HashMap<>();
            address.put("id", addressEntity.getId());
            address.put("street", addressEntity.getStreet());
            address.put("city", addressEntity.getCity());
            address.put("state", addressEntity.getState());
            address.put("zipCode", addressEntity.getZipCode().toString());
            addressList.add(address);
        }
        return addressList;
    }
}
