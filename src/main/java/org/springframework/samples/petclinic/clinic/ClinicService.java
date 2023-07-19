package org.springframework.samples.petclinic.clinic;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClinicService {
    private ClinicRepository clinicRepository;

    @Autowired
	public ClinicService(ClinicRepository clinicRepository) {
		this.clinicRepository = clinicRepository;
	}

	@Transactional(readOnly = true)
	public List<Clinic> findAll() throws DataAccessException {
		
		return (List<Clinic>) clinicRepository.findAll();
	}

	@Transactional(readOnly = true)
	public List<Clinic> findClinicsByUserId(int userId) throws DataAccessException {
		return clinicRepository.findClinicsByUserId(userId);
	}

	@Transactional(readOnly = true)
	public Clinic findClinicById(int clinicId) throws DataAccessException {
		
		Optional<Clinic> clinic = clinicRepository.findById(clinicId);
		
		if(clinic.isPresent()) {
			return clinic.get();
		}else{
			return null;
		}
	}

	@Transactional(readOnly = true)
	public List<Owner> findOwnersOfUserClinics(int userId) throws DataAccessException {
		return clinicRepository.findOwnersOfUserClinics(userId);
	}

	@Transactional(readOnly = true)
	public List<Vet> findVetsOfUserClinics(int userId) throws DataAccessException {
		return clinicRepository.findVetsOfUserClinics(userId);
	}

    @Transactional
	public Clinic save(Clinic clinic) throws DataAccessException {
		clinicRepository.save(clinic);
		return clinic;
	}

	@Transactional
	public Clinic update(Clinic clinic, int clinicId) throws DataAccessException {
		
		Clinic clinicToUpdate = clinicRepository.findById(clinicId).get();
		if (clinic.getClinicOwner() != null){
			BeanUtils.copyProperties(clinic, clinicToUpdate, "id", "owners");
		}else{
			BeanUtils.copyProperties(clinic, clinicToUpdate, "id", "clinicOwner", "owners");
		}

		return save(clinicToUpdate);
	}

	@Transactional
	public void delete(int clinicId) throws DataAccessException {
		clinicRepository.deleteById(clinicId);
	}
}
