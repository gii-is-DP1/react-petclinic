package org.springframework.samples.petclinic.clinicowner;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClinicOwnerService {
    private ClinicOwnerRepository clinicOwnerRepository;

    @Autowired
	public ClinicOwnerService(ClinicOwnerRepository clinicOwnerRepository) {
		this.clinicOwnerRepository = clinicOwnerRepository;
	}

	@Transactional(readOnly = true)
	public Iterable<ClinicOwner> findAll() throws DataAccessException {
		return clinicOwnerRepository.findAll();
	}

	@Transactional(readOnly = true)
	public ClinicOwner findById(int clinicOwnerId) throws DataAccessException {
		return clinicOwnerRepository.findById(clinicOwnerId).get();
	}

	@Transactional(readOnly = true)
	public ClinicOwner findByUserId(int userId) throws DataAccessException {
		
		Optional<ClinicOwner> clinicOwner = clinicOwnerRepository.findByUserId(userId);
		
		if(clinicOwner.isPresent()) {
			return clinicOwner.get();
		}else{
			return null;
		}
	}

    @Transactional
	public ClinicOwner saveClinicOwner(ClinicOwner clinicOwner) throws DataAccessException {
		clinicOwnerRepository.save(clinicOwner);
		return clinicOwner;
	}

	@Transactional
	public void deleteById(int clinicOwnerId) throws DataAccessException {
		clinicOwnerRepository.deleteById(clinicOwnerId);
	}
}
