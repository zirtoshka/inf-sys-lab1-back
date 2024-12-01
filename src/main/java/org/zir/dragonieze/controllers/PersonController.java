package org.zir.dragonieze.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.zir.dragonieze.dragon.*;
import org.zir.dragonieze.dragon.repo.LocationRepository;
import org.zir.dragonieze.dragon.repo.PersonRepository;
import org.zir.dragonieze.dto.PersonDTO;
import org.zir.dragonieze.log.Auditable;
import org.zir.dragonieze.services.BaseService;
import org.zir.dragonieze.sort.LocationSort;
import org.zir.dragonieze.sort.specifications.PersonSpecifications;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/dragon/user/person")
public class PersonController extends Controller {
    private final PersonRepository personRepository;
    private final LocationRepository locationRepository;

    public PersonController(BaseService service, PersonRepository personRepository, LocationRepository locationRepository) {
        super(service);
        this.personRepository = personRepository;
        this.locationRepository = locationRepository;
    }

    @Transactional
    @PostMapping("/add")
    public ResponseEntity<String> addPerson(
            @RequestHeader(HEADER_AUTH) String header,
            @Valid @RequestBody Person person
    ) throws JsonProcessingException {
        if (person.getLocation() != null) {
            Location location = service.validateAndGetEntity(person.getLocation().getId(), locationRepository, "Location");
            person.setLocation(location);
        } else {
            person.setLocation(null);
        }
        Person savedPerson = service.saveEntityWithUser(header, person, Person::setUser, personRepository);
        String json = service.convertToJson(new PersonDTO(savedPerson));
        return ResponseEntity.ok(json);
    }


    @Transactional
    @DeleteMapping("/delete/{id}")
    @Auditable(action = "DELETE", entity = "Person")
    public ResponseEntity<String> deletePerson(
            @RequestHeader(HEADER_AUTH) String header,
            @PathVariable Long id
    ) {
        service.deleteEntityWithCondition(
                header,
                id,
                Person::getUser,
                personRepository
        );
        return ResponseEntity.ok("удалилось ура");
    }

    @GetMapping("/get")
    public Page<PersonDTO> getPersons(
            @RequestHeader(HEADER_AUTH) String header,
            @RequestParam(value = "offset", defaultValue = "0") @Min(0) Integer offset,
            @RequestParam(value = "limit", defaultValue = "5") @Min(0) @Max(100) Integer limit,
            @RequestParam(value = "sort", defaultValue = "ID_ASC") LocationSort sort,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "canEdit", required = false) boolean canEdit,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "hairColor", required = false) Color hair,
            @RequestParam(value = "eyeColor", required = false) Color eye,
            @RequestParam(value = "locationId", required = false) Long locationId,
            @RequestParam(value = "height", required = false) int height,
            @RequestParam(value = "passportID", required = false) String passportID,
            @RequestParam(value = "nationality", required = false) Country nationality
    ) {
        Specification<Person> specification = Specification.where(
                PersonSpecifications.hasId(id)
                        .and(PersonSpecifications.hasName(name))
                        .and(PersonSpecifications.hasUserId(userId))
                        .and(PersonSpecifications.hasHair(hair))
                        .and(PersonSpecifications.hasCanEdit(canEdit))
                        .and(PersonSpecifications.hasLocation(locationId))
                        .and(PersonSpecifications.hasHeight(height))
                        .and(PersonSpecifications.hasPassportID(passportID))
                        .and(PersonSpecifications.hasNationality(nationality))
                        .and(PersonSpecifications.hasEyes(eye))
        );
        return personRepository.findAll(specification,
                        PageRequest.of(offset, limit, sort.getSortValue()))
                .map(PersonDTO::new);
    }


    @Transactional
    @PostMapping("/update")
    @Auditable(action = "UPDATE", entity = "Person")
    public ResponseEntity<String> updatePerson(
            @RequestHeader(HEADER_AUTH) String header,
            @Valid @RequestBody Person person
    ) throws JsonProcessingException {
        Person updatePerson = service.updateEntityWithUser(
                header,
                person,
                person.getId(),
                personRepository::findById,
                Person::getUser,
                (old, updated) -> {
                    old.setName(updated.getName());
                    old.setEyeColor(updated.getEyeColor());
                    old.setHairColor(updated.getHairColor());
                    old.setLocation(validateAndRetrieveLocation(updated.getLocation()));
                    old.setHeight(updated.getHeight());
                    old.setPassportID(updated.getPassportID());
                    old.setNationality(updated.getNationality());
                    old.setCanEdit(updated.getCanEdit());
                },
                personRepository
        );
        String json = service.convertToJson(new PersonDTO(updatePerson));
        return ResponseEntity.ok(json);
    }

    private Location validateAndRetrieveLocation(Location location) {
        if (location == null) {
            return null;
        }
        return service.validateAndGetEntity(location.getId(), locationRepository, "Location");
    }

}