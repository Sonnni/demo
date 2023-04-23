package com.example.demo.service;

import com.example.demo.models.Parent;
import com.example.demo.repository.ParentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class ParentDetailsServiceImpl implements ParentDetailsService {
    @Autowired
    ParentRepository parentRepository;

    @Override
    public ParentDetails loadParentByUsername(String username) throws UsernameNotFoundException {
        Parent parent = parentRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
        return ParentDetailsImpl.build(parent);
    }
}
