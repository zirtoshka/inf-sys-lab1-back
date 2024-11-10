package org.zir.dragonieze;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.zir.dragonieze.dragon.Coordinates;
import org.zir.dragonieze.dragon.Person;
import org.zir.dragonieze.dragon.repo.PersonRepository;
import org.zir.dragonieze.dto.CoordinatesDTO;
import org.zir.dragonieze.dto.PersonDTO;
import org.zir.dragonieze.user.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/dragon/user/pers")
public class PersonController extends Controller {
    private final PersonRepository personRepository;

    @Transactional
    @PostMapping("/addPerson")
    public ResponseEntity<String> addPerson(
            @RequestHeader(HEADER_AUTH) String header,
            @Valid @RequestBody Person person
    ) throws JsonProcessingException {
        String username = getUsername(header, jwtUtil);
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        User user = userOptional.get();
        person.setUser(user);
        personRepository.save(person);
        String json = getJson(new PersonDTO(person));
        return ResponseEntity.ok(json);
    }

    @GetMapping("/getPersons")
    public ResponseEntity<String> getPersons(
            @RequestHeader(HEADER_AUTH) String header
    ) throws JsonProcessingException {
        String username = getUsername(header, jwtUtil);
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (!userOptional.isPresent()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        User user = userOptional.get();
        List<Person> personList = personRepository.findByUserId(user.getId());
        List<PersonDTO> personDTOS = personList.stream()
                .map(PersonDTO::new)
                .toList();
        String json = getJson(personDTOS);
        return ResponseEntity.ok(json);
    }

}