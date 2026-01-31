package com.devcommunity.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.devcommunity.entity.User;
import com.devcommunity.entity.UserPrincipal;
import com.devcommunity.repository.IAuthRepo;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	    private IAuthRepo authRepo;
	    
	    public UserDetailsServiceImpl(IAuthRepo authRepo) {
	    	this.authRepo = authRepo;
	    }


	    @Override
	    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	        Optional<User> user = authRepo.findByUsername(username);
	        if (user.isEmpty()) {
	            throw new UsernameNotFoundException("user not found");
	        }
	        
	        return new UserPrincipal(user.get());
	    }

}
