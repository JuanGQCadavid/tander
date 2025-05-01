package com.tander.profile.repository;

import org.springframework.data.repository.CrudRepository;

import com.tander.profile.model.Profile;

public interface ProfileRepository extends CrudRepository<Profile, Long> {

}
