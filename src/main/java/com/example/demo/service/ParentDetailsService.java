package com.example.demo.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface ParentDetailsService {

    ParentDetails loadParentByUsername(String username) throws UsernameNotFoundException;
}
