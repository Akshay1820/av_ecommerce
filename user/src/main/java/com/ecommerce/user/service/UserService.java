package com.ecommerce.user.service;


import com.ecommerce.user.exceptions.ResourceNotFoundException;
import com.ecommerce.user.dto.AddressDTO;
import com.ecommerce.user.dto.UserRequest;
import com.ecommerce.user.dto.UserResponse;
import com.ecommerce.user.model.Address;
import com.ecommerce.user.model.User;
import com.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> fetchAllUsers(){
        return userRepository.findAll()
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    public void createUser(UserRequest userRequest){
        User user =  new User();
        mapToUser(user,userRequest);
        userRepository.save(user);
    }

    public UserResponse getUserById(String id) {
        return userRepository.findById(id).map(this::mapToUserResponse)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));
    }

    public UserResponse updateUser(String id, UserRequest userRequest) {
        User user= userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));
        mapToUser(user,userRequest);
        userRepository.save(user);
        return mapToUserResponse(user);
    }

    private UserResponse mapToUserResponse(User user){
        return UserResponse
                .builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .address(mapToAddressDto(user.getAddress()))
                .build();
    }


    private User mapToUser(User user, UserRequest userRequest){
       user.setFirstName(userRequest.getFirstName());
       user.setLastName(userRequest.getLastName());
       user.setEmail(userRequest.getEmail());
       user.setPhone(userRequest.getPhone());
       user.setAddress(mapToAddress(userRequest.getAddress()));
       return user;
    }


    private Address mapToAddress(AddressDTO addressDTO){
        Address address=new Address();
        address.setStreet(addressDTO.getStreet());
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setCountry(addressDTO.getCountry());
        address.setZipcode(addressDTO.getZipcode());
        return address;
    }
    private AddressDTO mapToAddressDto(Address address){
        return AddressDTO.builder()
                .street(address.getStreet())
                .city(address.getCity())
                .state(address.getState())
                .country(address.getCountry())
                .zipcode(address.getZipcode())
                .build();
    }
}
